package in.bbabca.wallah.controller;

import in.bbabca.wallah.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

@Controller
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<?> download(@PathVariable String filename) {
        if (fileStorageService.isCloudStorage()) {
            URI signedUrl = URI.create(fileStorageService.createSignedDownloadUrl(filename).toString());
            return ResponseEntity.status(302).location(signedUrl).build();
        }

        Resource resource = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
