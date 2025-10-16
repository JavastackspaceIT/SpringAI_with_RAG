package com.openai.RAG.controller;


import com.openai.RAG.service.IngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class IngestionController {

    @Autowired
    private IngestionService ingestionService;

    @PostMapping("/api/v1/ingest")
    public ResponseEntity<String> fileUpload(@RequestParam("file") MultipartFile file,
                                             @RequestParam("ingestType") String ingestType) throws IOException {

        ingestionService.ingestFile(file, ingestType);

        return ResponseEntity.ok("File uploaded successfully");
    }


}
