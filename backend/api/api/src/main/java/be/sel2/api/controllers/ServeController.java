package be.sel2.api.controllers;

import be.sel2.api.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/uploads")
public class ServeController {

    @Value("${file-location}")
    private String uploadedFolder;

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) {
        try {
            byte[] file = Files.readAllBytes(Path.of(uploadedFolder, filename));
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"").body(file);
        } catch (IOException ex) {
            throw new ForbiddenException("File access not allowed");
        }
    }
}
