package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.VocabularyDTOs;
import com.example.Oboe.Service.VocabularyService;
import com.example.Oboe.Service.DictionaryAudioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/vocabulary")
public class VocabularyController {

    private final VocabularyService vocabularyService;
    private final DictionaryAudioService dictionaryAudioService;

    public VocabularyController(VocabularyService vocabularyService, DictionaryAudioService dictionaryAudioService) {
        this.vocabularyService = vocabularyService;
        this.dictionaryAudioService = dictionaryAudioService;
    }

    // GET /api/vocabulary?page=0&size=10
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllVocabulary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(vocabularyService.getAllVocabulary(page, size));
    }

    // GET /api/vocabulary/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VocabularyDTOs> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vocabularyService.getVocabularyById(id));
    }

    // POST /api/vocabulary (ROLE_ADMIN)
    @PostMapping
    public ResponseEntity<VocabularyDTOs> create(@RequestBody VocabularyDTOs dto) {
        return ResponseEntity.ok(vocabularyService.createVocabulary(dto));
    }

    // PUT /api/vocabulary/{id} (ROLE_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<VocabularyDTOs> update(
            @PathVariable UUID id,
            @RequestBody VocabularyDTOs dto
    ) {
        return ResponseEntity.ok(vocabularyService.updateVocabulary(id, dto));
    }

    // DELETE /api/vocabulary/{id} (ROLE_ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vocabularyService.deleteVocabulary(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/vocabulary/search?keyword=sakura
    @GetMapping("/search")
    public ResponseEntity<List<VocabularyDTOs>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(vocabularyService.searchVocabulary(keyword));
    }
    // POST /api/vocabulary/fill-audio (ROLE_ADMIN)
    @PostMapping("/fill-audio")
    public ResponseEntity<Map<String, Object>> fillMissingAudio() {
        int updated = vocabularyService.fillMissingAudioForVocabulary();

        Map<String, Object> response = new HashMap<>();
        response.put("updated", updated);
        response.put("message", "Đã cập nhật audio cho từ vựng chưa có audio");

        return ResponseEntity.ok(response);
    }
    // @GetMapping("/{id}/pronounce")
    // public ResponseEntity<Map<String, String>> pronounce(@PathVariable UUID id) {
    //     String audioUrl =  dictionaryAudioService.fetchAudioUrl(W);

    //     if (audioUrl == null) {
    //         return ResponseEntity.notFound().build();
    //     }

    //     return ResponseEntity.ok(Map.of("audioUrl", audioUrl));
    // }


}
