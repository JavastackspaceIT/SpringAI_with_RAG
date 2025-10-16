package com.openai.RAG.controller;

import com.openai.RAG.dto.ErrorResponse;
import com.openai.RAG.dto.RagDocInsertionException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GroundingAdviseController {

    @ExceptionHandler(RagDocInsertionException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> errorResponse(String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("Invalid File");
        errorResponse.setErrorMessage(errorMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
