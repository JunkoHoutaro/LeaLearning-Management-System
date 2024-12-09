//package com.example.Mini_Project1.controller;
//
//import com.example.Mini_Project1.response.file.FileResponse;
//import com.example.Mini_Project1.service.FileService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("v1/file")
//public class FileController {
//    private final FileService fileService;
//
//    @PostMapping
//    public ResponseEntity<FileResponse> uploadFile(@RequestParam String cloudFolderPath, @RequestParam String filePath) throws IOException {
//        return ResponseEntity.ok(fileService.uploadResource(cloudFolderPath, filePath));
//    }
//
//    @DeleteMapping
//    public ResponseEntity<FileResponse> deleteFile(@RequestParam String url) throws Exception {
//        return ResponseEntity.ok(fileService.removeResource(url));
//    }
//}
