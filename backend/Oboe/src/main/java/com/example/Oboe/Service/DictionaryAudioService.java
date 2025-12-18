package com.example.Oboe.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class DictionaryAudioService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchAudioUrl(String word) {
        try {
            String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

            List<Map<String, Object>> response =
                    restTemplate.getForObject(url, List.class);

            if (response == null || response.isEmpty()) return null;

            List<Map<String, Object>> phonetics =
                    (List<Map<String, Object>>) response.get(0).get("phonetics");

            if (phonetics == null) return null;

            // Ưu tiên audio US
            for (Map<String, Object> p : phonetics) {
                String audio = (String) p.get("audio");
                if (audio != null && audio.contains("-us")) {
                    return audio;
                }
            }

            // Fallback
            for (Map<String, Object> p : phonetics) {
                String audio = (String) p.get("audio");
                if (audio != null && !audio.isEmpty()) {
                    return audio;
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }
}