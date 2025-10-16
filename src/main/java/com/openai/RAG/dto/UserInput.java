package com.openai.RAG.dto;

import jakarta.validation.constraints.NotBlank;

public record UserInput(@NotBlank(message = "Prompt cannot be empty") String prompt) { }
