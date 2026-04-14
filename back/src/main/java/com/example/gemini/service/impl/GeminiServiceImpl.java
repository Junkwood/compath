package com.example.gemini.service.impl;

import com.example.gemini.dto.GeminiResponse;
import com.example.gemini.service.GeminiService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiServiceImpl implements GeminiService {

    @Autowired
    private WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model.name}")
    private String modelName;

    @Autowired
    private Gson gson;

    private String createFullPrompt(String content) {
        // 음성 파일의 경우 content가 비어있을 수 있으므로 기본 문구를 설정합니다.
        String targetContent = (content == null || content.isBlank()) ? "제공된 음성 데이터를 바탕으로 분석해줘." : content;

        return "너는 노션(Notion)이나 지라(Jira)처럼 깔끔하게 업무를 정리하는 프로젝트 매니저이자 개발자야.\n" +
                "다음 내용을 분석해서 아래 '반드시' 정해진 형식으로만 답변해줘.\n\n" +
                "1. 한 줄 핵심 요약: (회의의 전체 목적을 한 줄로 요약)\n" +
                "2. 주요 논의 사항: (논의된 핵심 내용을 불렛포인트로 정리)\n" +
                "3. 결정된 사항: (회의에서 확정된 결론)\n" +
                "4. 추천 업무(To-Do): (추천 업무 리스트는 반드시 아래 JSON 배열 형식으로만 작성하고, 담당자는 포함하지 마)\n" +
                " 형식: [{'업무제목1': '업무내용1'}, {'업무제목2': '업무내용2'}]\n\n" +
//                "⚠️ 주의사항:\n" +
                "- 추천 업무 리스트 작성 시 '```json' 같은 코드 블록 태그를 사용하지 마.\n" +
                "- 추천 업무작성시 4. 추천 업무(To-Do) 같은 제목 기재하지 말것.\n" +
                "- 추천 업무작성시 담당자는 절대 기재하지 말고 4가지 정도로 추천해줘.\n" +
                "- 만약 음성 데이터라면, 말실수나 중복된 단어는 문맥에 맞게 교정해서 요약해.\n\n" +
                "분석할 내용:\n" + targetContent;
    }

    @Override
    public Mono<String> getChatResponse(String prompt) {


        /*
         * { "contents": [{ "parts":[{"text": "Explain how AI works"}] }] }
         */
        // 요청의 body 구성하기
        // 위의 형식에 맞게 요청의 body 구성하기
        List<Map<String, Object>> parts = List.of(Map.of("text", createFullPrompt(prompt)));
        return executeRequest(parts);
    }

    @Override
    public Mono<String> getAudioResponse(MultipartFile file) {
        try {
            String base64Audio = Base64.getEncoder().encodeToString(file.getBytes());

            // 오디오 모드 전용 파트 구성 (지시문 + 오디오 데이터)
            List<Map<String, Object>> parts = List.of(
                    Map.of("text", createFullPrompt("이 음성 파일을 듣고 회의록 형식으로 요약해줘.")), // 오디오 분석용 지시
                    Map.of("inline_data", Map.of(
                            "mime_type", file.getContentType(),
                            "data", base64Audio
                    ))
            );
            return executeRequest(parts);
        } catch (IOException e) {
            return Mono.error(new RuntimeException("오디오 처리 실패", e));
        }
    }

    private Mono<String> executeRequest(List<Map<String, Object>> parts) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", parts))
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/models/" + modelName + ":generateContent")
                        .queryParam("key", apiKey)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseBody -> {
                    try {
                        return Mono.just(extractResponse(responseBody));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("JSON 파싱 오류", e));
                    }
                });
    }

    // 우리가 원하는 문자열만을 빼내는 과정
    private String extractResponse(String responseJson) {
        try {
            // 응답된 json 문자열을 파싱해서 GeminiResponse 객체로 변환
            GeminiResponse geminiResponse = gson.fromJson(responseJson, GeminiResponse.class);

            if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);

                if (firstCandidate.getContent() != null && firstCandidate.getContent().getParts() != null) {
                    return firstCandidate.getContent().getParts().stream()
                            .map(GeminiResponse.Part::getText)
                            .reduce((a, b) -> a + "\n" + b) // 여러 개의 응답을 합침
                            .orElse("응답 없음");
                }
            }
        } catch (JsonSyntaxException e) {
            return "JSON 파싱 오류 : " + e.getMessage();
        }
        return "응답 없음";
    }
}