package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuestionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.url}")   
    private String geminiApiUrl;

    @Value("${gemini.api.key}")   // 👈 KEY TÁCH RIÊNG
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==========================
    // GENERATE LIST QUESTION
    // ==========================
    // public List<QuestionDTO> generateQuestion(String prompt) {

    //     RestTemplate restTemplate = new RestTemplate();

    //     // 👉 Luôn nối KEY tại đây
    //     String url = geminiApiUrl + "?key=" + apiKey;

    //     Map<String, Object> textMap = Map.of("text", prompt);
    //     Map<String, Object> contentMap = Map.of("parts", List.of(textMap));
    //     Map<String, Object> requestBody = Map.of("contents", List.of(contentMap));

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    //     try {
    //         ResponseEntity<Map> response = restTemplate.exchange(
    //                 url,
    //                 HttpMethod.POST,
    //                 entity,
    //                 Map.class
    //         );

    //         if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
    //             throw new RuntimeException("HTTP Error: " + response.getStatusCode());
    //         }

    //         Map<String, Object> body = response.getBody();
    //         List<Map<String, Object>> candidates =
    //                 (List<Map<String, Object>>) body.get("candidates");

    //         if (candidates == null || candidates.isEmpty()) {
    //             throw new RuntimeException("Empty candidates.");
    //         }

    //         Map<String, Object> content =
    //                 (Map<String, Object>) candidates.get(0).get("content");

    //         List<Map<String, Object>> parts =
    //                 (List<Map<String, Object>>) content.get("parts");

    //         String rawResponse = parts.get(0).get("text").toString().trim();

    //         int start = rawResponse.indexOf("[");
    //         int end = rawResponse.lastIndexOf("]");

    //         if (start >= 0 && end > start) {
    //             rawResponse = rawResponse.substring(start, end + 1);
    //         }

    //         List<Map<String, Object>> rawList =
    //                 objectMapper.readValue(rawResponse, new TypeReference<>() {});

    //         List<QuestionDTO> result = new ArrayList<>();

    //         for (Map<String, Object> item : rawList) {
    //             QuestionDTO dto = new QuestionDTO();
    //             dto.setQuestionID(UUID.randomUUID());
    //             dto.setQuestionName((String) item.get("question"));
    //             dto.setCorrectAnswer((String) item.get("answer"));
    //             dto.setOptions((List<String>) item.get("choices"));
    //             result.add(dto);
    //         }

    //         return result;

    //     } catch (Exception ex) {
    //         throw new RuntimeException("Gemini API Error: " + ex.getMessage());
    //     }
    // }

    public List<QuestionDTO> generateQuestion(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String url = geminiApiUrl + "?key=" + apiKey;

        Map<String, Object> textMap = Map.of("text", prompt);
        Map<String, Object> contentMap = Map.of("parts", List.of(textMap));
        Map<String, Object> requestBody = Map.of("contents", List.of(contentMap));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        Map.class
                );

                if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    attempt++;
                    Thread.sleep((long) Math.pow(2, attempt) * 1000); // exponential backoff
                    continue;
                }

                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    throw new RuntimeException("HTTP Error: " + response.getStatusCode());
                }

                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");

                if (candidates == null || candidates.isEmpty()) {
                    throw new RuntimeException("Empty candidates.");
                }

                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                String rawResponse = parts.get(0).get("text").toString().trim();

                int start = rawResponse.indexOf("[");
                int end = rawResponse.lastIndexOf("]");

                if (start >= 0 && end > start) {
                    rawResponse = rawResponse.substring(start, end + 1);
                }

                List<Map<String, Object>> rawList = objectMapper.readValue(rawResponse, new TypeReference<>() {});

                List<QuestionDTO> result = new ArrayList<>();
                for (Map<String, Object> item : rawList) {
                    QuestionDTO dto = new QuestionDTO();
                    dto.setQuestionID(UUID.randomUUID());
                    dto.setQuestionName((String) item.get("question"));
                    dto.setCorrectAnswer((String) item.get("answer"));
                    dto.setOptions((List<String>) item.get("choices"));
                    result.add(dto);
                }

                return result;

            } catch (Exception ex) {
                if (attempt >= maxRetries - 1) {
                    throw new RuntimeException("Gemini API Error after retries: " + ex.getMessage());
                }
                attempt++;
                try { Thread.sleep((long) Math.pow(2, attempt) * 1000); } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("Gemini API failed after max retries.");
    }


    // ==========================
    // GENERATE TEXT
    // ==========================
    // public String generateTextFromPrompt(String prompt) {

    //     RestTemplate restTemplate = new RestTemplate();

    //     // 👉 KEY nối vào URL
    //     String url = geminiApiUrl + "?key=" + apiKey;

    //     Map<String, Object> textMap = Map.of("text", prompt);
    //     Map<String, Object> partMap = Map.of("role", "user", "parts", List.of(textMap));
    //     Map<String, Object> requestBody = Map.of("contents", List.of(partMap));

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);

    //     HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    //     try {
    //         ResponseEntity<Map> response = restTemplate.exchange(
    //                 url,
    //                 HttpMethod.POST,
    //                 entity,
    //                 Map.class
    //         );

    //         if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
    //             throw new RuntimeException("HTTP Error: " + response.getStatusCode());
    //         }

    //         Map<String, Object> body = response.getBody();
    //         List<Map<String, Object>> candidates =
    //                 (List<Map<String, Object>>) body.get("candidates");

    //         Map<String, Object> content =
    //                 (Map<String, Object>) candidates.get(0).get("content");

    //         List<Map<String, Object>> parts =
    //                 (List<Map<String, Object>>) content.get("parts");

    //         String rawText = parts.get(0).get("text").toString().trim();

    //         if (rawText.startsWith("```json")) {
    //             rawText = rawText.substring(7).trim();
    //         } else if (rawText.startsWith("```")) {
    //             rawText = rawText.substring(3).trim();
    //         }
    //         if (rawText.endsWith("```")) {
    //             rawText = rawText.substring(0, rawText.length() - 3).trim();
    //         }

    //         return rawText;

    //     } catch (Exception ex) {
    //         throw new RuntimeException("Gemini API Error: " + ex.getMessage());
    //     }
    // }
    public String generateTextFromPrompt(String prompt) {
    RestTemplate restTemplate = new RestTemplate();
    String url = geminiApiUrl + "?key=" + apiKey;

    Map<String, Object> textMap = Map.of("text", prompt);
    Map<String, Object> partMap = Map.of("role", "user", "parts", List.of(textMap));
    Map<String, Object> requestBody = Map.of("contents", List.of(partMap));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    int maxRetries = 3;
    int attempt = 0;

    while (attempt < maxRetries) {
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                attempt++;
                Thread.sleep((long) Math.pow(2, attempt) * 1000); // exponential backoff
                continue;
            }

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("HTTP Error: " + response.getStatusCode());
            }

            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String rawText = parts.get(0).get("text").toString().trim();

            if (rawText.startsWith("```json")) rawText = rawText.substring(7).trim();
            else if (rawText.startsWith("```")) rawText = rawText.substring(3).trim();
            if (rawText.endsWith("```")) rawText = rawText.substring(0, rawText.length() - 3).trim();

            return rawText;

        } catch (Exception ex) {
            if (attempt >= maxRetries - 1) {
                throw new RuntimeException("Gemini API Error after retries: " + ex.getMessage());
            }
            attempt++;
            try { Thread.sleep((long) Math.pow(2, attempt) * 1000); } catch (InterruptedException ignored) {}
        }
    }
    throw new RuntimeException("Gemini API failed after max retries.");
    }

    public JsonNode parseJsonResponse(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phân tích JSON: " + e.getMessage());
        }
    }
}
