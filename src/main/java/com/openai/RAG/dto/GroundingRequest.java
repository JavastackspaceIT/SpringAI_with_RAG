package com.openai.RAG.dto;

import jakarta.validation.constraints.NotBlank;

public record GroundingRequest(@NotBlank String prompt) {
}
