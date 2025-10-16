package com.openai.RAG.service;

import com.openai.RAG.dto.RagDocInsertionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class IngestionService implements CommandLineRunner {


    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    @Value("classpath:docs/Flexora_FAQ.pdf")
    private Resource faqPdf;

    @Value(value = "${ingestion.enabled}")
    private boolean ingestionEnabled;
    private final VectorStore vectorStore;

    public IngestionService(@Qualifier("qaVectorStore") PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {

        ingestPdfDocuments(faqPdf);
    }

    private void ingestPdfDocuments(Resource faqPdf) {

        if (ingestionEnabled) {
            var pdf = new PagePdfDocumentReader(faqPdf).get();
            vectorStore.add(pdf);
            log.info("documents {} ingested successfully", pdf.size());
        } else {
            log.info("document ingestion is disabled");
        }
    }

    public void ingestFile(MultipartFile file, String ingestType) {

        try {
            byte[] fileContent = file.getBytes();
            var fileSource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            switch (ingestType) {
                case "pdfFile":
                    var pdf = new PagePdfDocumentReader(fileSource).get();
                    vectorStore.add(pdf);
                    log.info("documents {} ingested successfully", pdf.size());
                    break;
                case "paragraph":
                    try {
                        ParagraphPdfDocumentReader reader = new ParagraphPdfDocumentReader(fileSource); // constructor can throw
                        var paragraphs = reader.get();
                        vectorStore.add(paragraphs);
                        log.info("documents ingested successfully {} ", paragraphs.size());
                    }
                    catch (IllegalArgumentException e) {
                        log.error("error while reading file content {}", e.getMessage());
                        throw new RagDocInsertionException("The uploaded PDF does not contain a Table of Contents.Please upload a PDF with a TOC or use another ingestion type");
                    }
                    break;
                default:
                    log.warn("unsupported file type {}", ingestType);
            }
        }
        catch (IllegalArgumentException e) {
            log.error("error while reading file content {}", e.getMessage());
            throw new RagDocInsertionException(e.getMessage());
        }
        catch (Exception  e) {
            log.error("error while reading file content {}", e.getMessage());
            throw new RagDocInsertionException(e.getMessage());
        }

    }
}