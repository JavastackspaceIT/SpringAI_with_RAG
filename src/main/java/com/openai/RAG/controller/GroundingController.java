package com.openai.RAG.controller;

import com.openai.RAG.dto.GroundingRequest;
import com.openai.RAG.dto.GroundingResponse;
import com.openai.RAG.service.GroundingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroundingController {

    private final GroundingService groundingService;

    public GroundingController(GroundingService groundingService) {
        this.groundingService = groundingService;
    }

    @PostMapping("/api/v1/grounding")
    public GroundingResponse getGrounding(@RequestBody GroundingRequest groundingRequest) {
        return groundingService.grounding(groundingRequest);
    }

}
