package com.example.Oboe.Service;

import com.example.Oboe.DTOs.GrammarDTO;
import com.example.Oboe.Entity.Grammar;
import com.example.Oboe.Repository.GrammarRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GrammarService {

    private final GrammarRepository grammarRepository;
    
    // Cập nhật Constructor (chỉ còn GrammarRepository)
    public GrammarService(GrammarRepository grammarRepository) {
        this.grammarRepository = grammarRepository;
    }

    // --- Phương thức CRUD ---

    // Get all grammar with pagination
    public Map<String, Object> getAllGrammar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grammar> grammarPage = grammarRepository.findAll(pageable);

        List<GrammarDTO> grammarDTOs = grammarPage.getContent()
                .stream()
                .map(this::grammarToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("grammars", grammarDTOs);
        response.put("currentPage", grammarPage.getNumber());
        response.put("pageSize", grammarPage.getSize());
        response.put("totalElements", grammarPage.getTotalElements());
        response.put("totalPages", grammarPage.getTotalPages());
        response.put("isLastPage", grammarPage.isLast());

        return response;
    }

    // Create new grammar (ROLE_ADMIN)
    public GrammarDTO createGrammar(GrammarDTO dto) {
        checkAdminAccess();

        Grammar grammar = new Grammar();
        grammar.setStructure(dto.getStructure());
        grammar.setExplanation(dto.getExplanation());
        
        // Cập nhật: Ánh xạ trường mới detailContent
        if (dto.getDetailContent() != null) {
            grammar.setDetailContent(dto.getDetailContent());
        }
        
        // Cập nhật: Ánh xạ trường mới topicTag (thay thế grammarType cũ)
        if (dto.getTopicTag() != null) {
            grammar.setTopicTag(dto.getTopicTag());
        }
        
        // LOẠI BỎ: grammar.setExample(dto.getExample()); vì trường 'example' đã bị xóa khỏi Entity Grammar

        Grammar saved = grammarRepository.save(grammar);

        return grammarToDTO(saved);
    }

    // Get grammar by ID
    public GrammarDTO getGrammarById(UUID id) {
        Grammar grammar = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Grammar với ID: " + id));
        return grammarToDTO(grammar);
    }

    // Update grammar (ROLE_ADMIN)
    public GrammarDTO updateGrammar(UUID id, GrammarDTO dto) {
        checkAdminAccess();

        Grammar grammar = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar không tồn tại"));

        if (dto.getStructure() != null) grammar.setStructure(dto.getStructure());
        if (dto.getExplanation() != null) grammar.setExplanation(dto.getExplanation());
        // LOẠI BỎ: if (dto.getExample() != null) grammar.setExample(dto.getExample());
        
        // Cập nhật: Ánh xạ trường mới detailContent
        if (dto.getDetailContent() != null) grammar.setDetailContent(dto.getDetailContent());
        
        // Cập nhật: Ánh xạ trường mới topicTag
        if (dto.getTopicTag() != null) grammar.setTopicTag(dto.getTopicTag());

        Grammar updated = grammarRepository.save(grammar);
        return grammarToDTO(updated);
    }

    // Delete grammar (ROLE_ADMIN)
    public void deleteGrammar(UUID id) {
        checkAdminAccess();

        Grammar grammar = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar không tồn tại"));

        grammarRepository.delete(grammar);
    }

    // Search grammar
    public List<GrammarDTO> searchGrammar(String keyword) {
        // Giả định GrammarRepository có phương thức searchGrammar() hoạt động
        List<Grammar> grammars = grammarRepository.searchGrammar(keyword); 
        return grammars.stream()
                .map(this::grammarToDTO)
                .collect(Collectors.toList());
    }

    // --- Hàm chuyển đổi ---

    // Convert Grammar → DTO
    private GrammarDTO grammarToDTO(Grammar grammar) {
        GrammarDTO dto = new GrammarDTO();
        
        dto.setGrammarId(grammar.getGrammarId()); 
        
        dto.setStructure(grammar.getStructure());
        dto.setExplanation(grammar.getExplanation());
        
        // LOẠI BỎ: dto.setExample(grammar.getExample());
        
        // Cập nhật: Ánh xạ trường mới detailContent
        dto.setDetailContent(grammar.getDetailContent());
        
        // Cập nhật: Ánh xạ trường mới topicTag (thay thế tenseAspect/grammarType cũ)
        dto.setTopicTag(grammar.getTopicTag()); 
        
        // Nếu DTO vẫn giữ trường tenseAspect, cần ánh xạ:
        // dto.setTenseAspect(grammar.getGrammarType()); 
        
        return dto;
    }

    // Check role
    private void checkAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
        }
    }
}