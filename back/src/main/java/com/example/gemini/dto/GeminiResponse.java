package com.example.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponse {
    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        // 2단계: candidates 안을 열기 위한 열쇠
        private Content content;
    }

    @Data
    public static class Content {
        // 3단계: content 안을 열기 위한 열쇠
        private List<Part> parts;
    }

    @Data
    public static class Part {
        // 4단계: 우리가 찾는 진짜 알맹이
        private String text;
    }
}
