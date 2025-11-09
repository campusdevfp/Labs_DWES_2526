package com.example.uploadingfiles;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final Path rootLocation = Paths.get("uploads");

    public FileUploadController() throws IOException {
        Files.createDirectories(rootLocation);
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/form.html";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return "❌ Archivo vacío";
            Path destination = rootLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "✅ Archivo subido: " + file.getOriginalFilename();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error al subir archivo: " + e.getMessage();
        }
    }

    @GetMapping("/files")
    @ResponseBody
    public List<String> listUploadedFiles() throws IOException {
        return Files.list(rootLocation)
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        Path file = rootLocation.resolve(filename);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new org.springframework.core.io.UrlResource(file.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
