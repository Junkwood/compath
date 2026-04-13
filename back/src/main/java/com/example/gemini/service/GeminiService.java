package com.example.gemini.service;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface GeminiService {

    public Mono<String> getChatResponse(String prompt);

    public Mono<String> getAudioResponse(MultipartFile file);
}
