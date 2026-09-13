package in.bbabca.wallah.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService service() {
        return new FileStorageService(
                tempDir.toString(),
                "local",
                "",
                "us-east-1",
                "",
                "",
                "",
                false,
                "resources",
                15
        );
    }

    @Test
    void rejectsPdfExtensionWhenContentIsNotPdf() {
        MockMultipartFile fakePdf = new MockMultipartFile(
                "file",
                "notes.pdf",
                "application/pdf",
                "this is not a pdf".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> service().store(fakePdf));
    }

    @Test
    void acceptsPdfWithPdfSignature() throws Exception {
        byte[] pdf = "%PDF-1.7\n1 0 obj\n<<>>\nendobj\n%%EOF".getBytes();
        MockMultipartFile validPdf = new MockMultipartFile(
                "file",
                "notes.pdf",
                "application/pdf",
                pdf
        );

        String storedName = service().store(validPdf);

        assertTrue(storedName.endsWith(".pdf"));
        assertTrue(Files.exists(tempDir.resolve(storedName)));
    }

    @Test
    void rejectsBinaryContentDisguisedAsText() {
        MockMultipartFile fakeText = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/plain",
                new byte[]{0x00, 0x01, 0x02, 0x03}
        );

        assertThrows(IllegalArgumentException.class, () -> service().store(fakeText));
    }
}
