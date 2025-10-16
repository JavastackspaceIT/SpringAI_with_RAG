package com.openai.RAG.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {

    private final EmbeddingModel embeddingModel;

    public VectorStoreConfig(JdbcTemplate jdbcTemplate, EmbeddingModel openAiEmbeddingModel) {
        this.embeddingModel = openAiEmbeddingModel;
    }

    @Bean(name = "qaVectorStore")
    public PgVectorStore qaTableVectorStore(JdbcTemplate jdbcTemplate) {
        return PgVectorStore.builder(jdbcTemplate, this.embeddingModel)
                .initializeSchema(true)
                .schemaName("public")         // default schema
                .vectorTableName("qa_chunks") // clearer than "ragdocs"
                .build();
    }

}
