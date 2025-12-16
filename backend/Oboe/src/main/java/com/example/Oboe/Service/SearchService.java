package com.example.Oboe.Service;

import com.example.Oboe.DTOs.*;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {

    // 1. LOẠI BỎ: PhoneticsRepository (vì Entity đã bị xóa)
    // @Autowired
    // private PhoneticsRepository phoneticsRepository; 

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private GrammarRepository grammarRepository;

    // 2. Cập nhật Autowired: Dùng IdiomRepository (vì PhraseIdiom đã đổi tên)
    @Autowired
    private IdiomRepository idiomRepository;

    // Giữ lại các Autowired khác (Quizzes, FlashCard, User) nếu vẫn được sử dụng ở các phương thức khác
    // Lưu ý: Các Repository này cần được kiểm tra để đảm bảo Entity của chúng vẫn tồn tại.
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizzesRepository quizzesRepository;

    @Autowired
    private FlashCardRepository flashCardRepository;


    // Gợi ý tất cả các loại học liệu (cho /suggest)
    public List<Map<String, String>> suggestAllTypes(String keyword) {
        List<Map<String, String>> suggestions = new ArrayList<>();

        suggestions.addAll(searchByType(keyword, "vocabulary"));
        // 3. LOẠI BỎ: phonetic (vì Entity đã bị xóa)
        // suggestions.addAll(searchByType(keyword, "phonetic")); 
        suggestions.addAll(searchByType(keyword, "grammar"));
        // 3. Cập nhật tên type: phrase -> idiom
        suggestions.addAll(searchByType(keyword, "idiom")); 

        return suggestions;
    }

    // Tìm theo type cụ thể (học liệu)
    public List<Map<String, String>> searchByType(String keyword, String type) {
        List<Map<String, String>> suggestions = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "vocabulary":
                for (Vocabulary v : vocabularyRepository.searchVocabulary(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "vocabulary");
                    item.put("word", v.getWord());
                    // 4a. Cập nhật trường reading: Dùng phoneticIpa
                    item.put("reading", v.getPhoneticIpa()); 
                    item.put("meaning", v.getVietnameseMeaning()); 
                    item.put("id", v.getVocabularyId().toString()); 
                    suggestions.add(item);
                }
                break;

            // 4b. LOẠI BỎ: case "phonetic" (Entity đã bị xóa)
            // case "phonetic": ... break;

            case "grammar":
                for (Grammar g : grammarRepository.searchGrammar(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "grammar");
                    item.put("word", g.getStructure());
                    // Giữ lại getVietnamesePronunciation nếu Grammar Entity còn trường này
                    item.put("meaning", g.getExplanation());
                    item.put("id", g.getGrammarId().toString()); 
                    suggestions.add(item);
                }
                break;

            // 4c. Cập nhật case phrase -> idiom (dùng tên Entity mới)
            case "idiom":
                // Sử dụng IdiomRepository và phương thức search mới
                for (Idiom s : idiomRepository.searchPhrasesAndIdioms(keyword)) { 
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "idiom");
                    item.put("word", s.getEnglishPhrase()); 
                    // Loại bỏ "reading" nếu Entity Idiom không có trường phát âm nào
                    // (Giả định Entity Idiom không có vietnamesePronunciation)
                    item.put("meaning", s.getVietnameseMeaning());
                    item.put("id", s.getIdiomId().toString()); 
                    suggestions.add(item);
                }
                break;

            default:
                throw new IllegalArgumentException("Type không hợp lệ: " + type);
        }

        return suggestions;
    }


}