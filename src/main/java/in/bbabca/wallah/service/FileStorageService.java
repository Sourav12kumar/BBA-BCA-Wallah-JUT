package in.bbabca.wallah.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt"
    );

    private final Path uploadDirectory;
    private final String provider;
    private final String bucket;
    private final String keyPrefix;
    private final Duration presignDuration;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public FileStorageService(
            @Value("${app.upload.dir:uploads}") String uploadDir,
            @Value("${app.storage.provider:local}") String provider,
            @Value("${app.storage.s3.bucket:}") String bucket,
            @Value("${app.storage.s3.region:us-east-1}") String region,
            @Value("${app.storage.s3.endpoint:}") String endpoint,
            @Value("${app.storage.s3.access-key:}") String accessKey,
            @Value("${app.storage.s3.secret-key:}") String secretKey,
            @Value("${app.storage.s3.path-style:false}") boolean pathStyle,
            @Value("${app.storage.s3.key-prefix:resources}") String keyPrefix,
            @Value("${app.storage.s3.presign-minutes:15}") long presignMinutes) {

        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.provider = provider == null ? "local" : provider.trim().toLowerCase(Locale.ROOT);
        this.bucket = bucket == null ? "" : bucket.trim();
        this.keyPrefix = sanitizePrefix(keyPrefix);
        this.presignDuration = Duration.ofMinutes(Math.max(1, presignMinutes));

        if (isCloudStorage()) {
            if (this.bucket.isBlank()) {
                throw new IllegalStateException("S3 storage is enabled but no bucket is configured.");
            }

            AwsCredentialsProvider credentialsProvider = (!accessKey.isBlank() && !secretKey.isBlank())
                    ? StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                    : DefaultCredentialsProvider.create();

            S3Configuration s3Configuration = S3Configuration.builder()
                    .pathStyleAccessEnabled(pathStyle)
                    .build();

            S3ClientBuilder clientBuilder = S3Client.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.of(region))
                    .serviceConfiguration(s3Configuration);

            S3Presigner.Builder presignerBuilder = S3Presigner.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.of(region))
                    .serviceConfiguration(s3Configuration);

            if (endpoint != null && !endpoint.isBlank()) {
                URI endpointUri = URI.create(endpoint.trim());
                clientBuilder.endpointOverride(endpointUri);
                presignerBuilder.endpointOverride(endpointUri);
            }

            this.s3Client = clientBuilder.build();
            this.s3Presigner = presignerBuilder.build();
        } else {
            this.s3Client = null;
            this.s3Presigner = null;
            try {
                Files.createDirectories(this.uploadDirectory);
            } catch (IOException e) {
                throw new IllegalStateException("Could not create upload directory", e);
            }
        }
    }

    public String store(MultipartFile file) {
        validateFile(file);

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "resource" : file.getOriginalFilename()
        );
        String extension = getExtension(originalName);
        String storedName = UUID.randomUUID() + "." + extension;

        if (isCloudStorage()) {
            String key = objectKey(storedName);
            try {
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build();
                s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                return storedName;
            } catch (IOException | RuntimeException e) {
                throw new IllegalStateException("Could not store uploaded file in object storage.", e);
            }
        }

        Path target = uploadDirectory.resolve(storedName).normalize();
        if (!target.getParent().equals(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid upload path.");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return storedName;
        } catch (IOException e) {
            throw new IllegalStateException("Could not store uploaded file.", e);
        }
    }

    public Resource load(String filename) {
        if (isCloudStorage()) {
            throw new IllegalStateException("Cloud files must be accessed through a signed URL.");
        }

        try {
            Path file = uploadDirectory.resolve(filename).normalize();
            if (!file.getParent().equals(uploadDirectory)) {
                throw new IllegalArgumentException("Invalid file path.");
            }

            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("File not found.");
            }
            return resource;
        } catch (IOException e) {
            throw new IllegalArgumentException("File not found.", e);
        }
    }

    public URL createSignedDownloadUrl(String filename) {
        if (!isCloudStorage()) {
            throw new IllegalStateException("Signed URLs are only available for cloud storage.");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey(filename))
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(presignDuration)
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url();
    }

    public boolean isCloudStorage() {
        return "s3".equals(provider);
    }

    public String getProvider() {
        return provider;
    }

    public void deleteByPublicUrl(String publicUrl) {
        if (publicUrl == null || !publicUrl.startsWith("/files/")) {
            return;
        }

        String filename = publicUrl.substring("/files/".length());
        if (filename.isBlank() || filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            return;
        }

        if (isCloudStorage()) {
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey(filename))
                        .build());
            } catch (RuntimeException ignored) {
                // Resource metadata deletion should not fail only because object deletion failed.
            }
            return;
        }

        try {
            Path file = uploadDirectory.resolve(filename).normalize();
            if (file.getParent().equals(uploadDirectory)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ignored) {
            // Database deletion should not fail only because a stored file could not be removed.
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please choose a file to upload.");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "resource" : file.getOriginalFilename()
        );

        if (originalName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        String extension = getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Unsupported file type. Allowed: PDF, Word, PowerPoint, Excel and TXT."
            );
        }
    }

    private String objectKey(String filename) {
        return keyPrefix.isBlank() ? filename : keyPrefix + "/" + filename;
    }

    private static String sanitizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) return "";
        return prefix.trim().replaceAll("^/+|/+$", "");
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
