package in.bbabca.wallah.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt"
    );

    private final Path uploadDirectory;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    public String store(MultipartFile file) {
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

        String storedName = UUID.randomUUID() + "." + extension;
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

    public void deleteByPublicUrl(String publicUrl) {
        if (publicUrl == null || !publicUrl.startsWith("/files/")) {
            return;
        }
        String filename = publicUrl.substring("/files/".length());
        try {
            Path file = uploadDirectory.resolve(filename).normalize();
            if (file.getParent().equals(uploadDirectory)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ignored) {
            // Database deletion should not fail only because a stored file could not be removed.
        }
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
