package com.example.gemini.controller;

import com.example.gemini.service.GeminiService;
import com.example.meeting.dto.MeetingDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GeminiController {

    private final GeminiService service;


    @PostMapping("/gemini/simple")
    public Mono<String> callGemini(@RequestPart(value = "files", required = false) MultipartFile files,
                                   @RequestPart(value = "prompt", required = false) String prompt) {
        if (files != null && !files.isEmpty()) {
            return service.getAudioResponse(files);
        }

      return service.getChatResponse(prompt);

    }

}