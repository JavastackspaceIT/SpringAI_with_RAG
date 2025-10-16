package com.openai.RAG.service;


import com.openai.RAG.dto.GroundingRequest;
import com.openai.RAG.dto.GroundingResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroundingService {


    private static final Logger log = LoggerFactory.getLogger(GroundingService.class);
    private PgVectorStore pgVectorStore;

    @Autowired
    private ChatClient chatClient;

    @Value("classpath:/prompt-templates/RAG-QA-Prompt.st")
    private Resource ragQAPrompt;

    public GroundingService(@Qualifier("qaVectorStore") PgVectorStore vectorStore) {
        this.pgVectorStore = vectorStore;
    }

    public GroundingResponse grounding(GroundingRequest groundingRequest) {

        var response = pgVectorStore.doSimilaritySearch(SearchRequest.builder().query(groundingRequest.prompt()).build());
        pgVectorStore.
        log.info("result size {} \n and response {} ", response.size(), response);
        var contextResponse = response.stream()
                .filter(Objects::nonNull)
                .filter(result -> result.getScore() > 0.8)
                .limit(2)
                .map(result -> result.getText())
                .collect(Collectors.joining("\n"));

        if (StringUtils.isNotEmpty(contextResponse)) {
            log.info("matched contextResponse {}", contextResponse);

            log.info("resource Values {}", ragQAPrompt);
            log.info("Input request {}", groundingRequest.prompt());

            PromptTemplate promptTemplate = new PromptTemplate(ragQAPrompt);
            var promptMessage = promptTemplate.
                    createMessage(Map.of("context", contextResponse,
                            "input", groundingRequest.prompt()));

            var prompt = new Prompt(List.of(promptMessage));
            var contentResponse = chatClient.prompt(prompt)
                    .call()
                    .content();

            log.info("final response from chat model {}", contentResponse);
            return new GroundingResponse(contentResponse);
        } else {
            log.info(" no relevant context found, returning original prompt");
            return new GroundingResponse("No relevant context found for the query: " + groundingRequest.prompt());
        }

    }
}
