package com.example.Oboe.Service;

import com.example.Oboe.DTOs.VocabularyDTOs;
import com.example.Oboe.Entity.Vocabulary;
import com.example.Oboe.Repository.VocabularyRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    
    // Constructor đã chuẩn hóa
    public VocabularyService(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    // Get all vocabularies with pagination
    public Map<String, Object> getAllVocabulary(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vocabulary> vocabPage = vocabularyRepository.findAll(pageable);

        // Sử dụng DTO đã chuẩn hóa
        List<VocabularyDTOs> vocabDTOs = vocabPage.getContent().stream()
                .map(this::vocabToDTO).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("vocabularies", vocabDTOs);
        response.put("currentPage", vocabPage.getNumber());
        response.put("pageSize", vocabPage.getSize());
        response.put("totalElements", vocabPage.getTotalElements());
        response.put("totalPages", vocabPage.getTotalPages());
        response.put("isLastPage", vocabPage.isLast());

        return response;
    }

    // Create new vocabulary
    public VocabularyDTOs createVocabulary(VocabularyDTOs dto) {
        checkAdminAccess();

        Vocabulary vocab = convertToEntity(dto); // Sử dụng hàm chuyển đổi chung
        
        Vocabulary saved = vocabularyRepository.save(vocab);
        return vocabToDTO(saved);
    }

    // Get by ID
    public VocabularyDTOs getVocabularyById(UUID id) {
        Vocabulary vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ vựng với ID: " + id));
        return vocabToDTO(vocab);
    }

    // Update
    public VocabularyDTOs updateVocabulary(UUID id, VocabularyDTOs dto) {
        checkAdminAccess();

        Vocabulary vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));

        // Cập nhật các thuộc tính từ DTO sang Entity (chỉ khi DTO cung cấp dữ liệu)
        if (dto.getWord() != null) vocab.setWord(dto.getWord());
        if (dto.getVietnameseMeaning() != null) vocab.setVietnameseMeaning(dto.getVietnameseMeaning()); 
        if (dto.getWordType() != null) vocab.setWordType(dto.getWordType());
        
        // Cập nhật các trường mới
        if (dto.getPhoneticIpa() != null) vocab.setPhoneticIpa(dto.getPhoneticIpa()); 
        if (dto.getAudioUrl() != null) vocab.setAudioUrl(dto.getAudioUrl());
        if (dto.getLevel() != null) vocab.setLevel(dto.getLevel());
        if (dto.getSynonyms() != null) vocab.setSynonyms(dto.getSynonyms());
        if (dto.getAntonyms() != null) vocab.setAntonyms(dto.getAntonyms());

        Vocabulary updated = vocabularyRepository.save(vocab);
        return vocabToDTO(updated);
    }

    // Delete
    public void deleteVocabulary(UUID id) {
        checkAdminAccess();

        Vocabulary vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));
        
        vocabularyRepository.delete(vocab);
    }

    // Search
    public List<VocabularyDTOs> searchVocabulary(String keyword) {
        List<Vocabulary> results = vocabularyRepository.searchVocabulary(keyword); 
        return results.stream().map(this::vocabToDTO).collect(Collectors.toList());
    }

    // 🔄 Entity → DTO (Đảm bảo ánh xạ các trường mới)
    private VocabularyDTOs vocabToDTO(Vocabulary vocab) {
        // Sử dụng constructor đầy đủ nếu có, hoặc setter nếu DTO dùng Lombok @Data
        VocabularyDTOs dto = new VocabularyDTOs();
        
        dto.setVocabularyId(vocab.getVocabularyId()); 
        dto.setWord(vocab.getWord());
        dto.setVietnameseMeaning(vocab.getVietnameseMeaning()); 
        dto.setWordType(vocab.getWordType());
        
        dto.setPhoneticIpa(vocab.getPhoneticIpa()); 
        dto.setAudioUrl(vocab.getAudioUrl());
        dto.setLevel(vocab.getLevel());
        dto.setSynonyms(vocab.getSynonyms());
        dto.setAntonyms(vocab.getAntonyms());
        
        return dto;
    }
    
    // 🔄 DTO → Entity
    private Vocabulary convertToEntity(VocabularyDTOs dto) {
        Vocabulary vocab = new Vocabulary();
        
        vocab.setWord(dto.getWord());
        vocab.setVietnameseMeaning(dto.getVietnameseMeaning());
        vocab.setWordType(dto.getWordType());
        
        vocab.setPhoneticIpa(dto.getPhoneticIpa());
        vocab.setAudioUrl(dto.getAudioUrl());
        vocab.setLevel(dto.getLevel());
        vocab.setSynonyms(dto.getSynonyms());
        vocab.setAntonyms(dto.getAntonyms());
        
        return vocab;
    }

    private void checkAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
        }
    }
}