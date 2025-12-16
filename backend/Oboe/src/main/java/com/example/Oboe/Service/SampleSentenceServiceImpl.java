package com.example.Oboe.Service;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import com.example.Oboe.Entity.SampleSentence;
import com.example.Oboe.Repository.SampleSentenceRepository;
import jakarta.transaction.Transactional; // Thêm @Transactional để đảm bảo tính toàn vẹn
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors; // Thêm import cho Collectors

@Service
@Transactional // Đảm bảo các thao tác với DB chạy trong transaction
public class SampleSentenceServiceImpl implements SampleSentenceService {

    // Sử dụng Constructor Injection thay vì @Autowired trên trường
    private final SampleSentenceRepository repository;

    public SampleSentenceServiceImpl(SampleSentenceRepository repository) {
        this.repository = repository;
    }

    // --- Hàm chuyển đổi DTO <-> Entity ---

    // Đã cập nhật để ánh xạ các trường mới và tên trường mới
    private SampleSentenceDTO convertToDTO(SampleSentence entity) {
        SampleSentenceDTO dto = new SampleSentenceDTO();
        

        dto.setSentenceId(entity.getSentenceId());
        dto.setEnglishSentence(entity.getEnglishSentence());
        dto.setVietnameseTranslation(entity.getVietnameseTranslation());
        dto.setTopicTag(entity.getTopicTag());
        dto.setUsageFrequency(entity.getUsageFrequency());
        
        
        return dto;
    }


    private SampleSentence convertToEntity(SampleSentenceDTO dto) {
        SampleSentence entity = new SampleSentence();
        
        // KHÔNG set ID cho thao tác CREATE. ID (UUID) sẽ được Hibernate tự động sinh.
        
        // Cần sử dụng các Getter mới: getEnglishSentence, getVietnameseTranslation
        entity.setEnglishSentence(dto.getEnglishSentence());
        entity.setVietnameseTranslation(dto.getVietnameseTranslation());
        entity.setTopicTag(dto.getTopicTag());
        entity.setUsageFrequency(dto.getUsageFrequency());

        return entity;
    }

    // --- CRUD Implementation ---

    @Override
    public SampleSentenceDTO create(SampleSentenceDTO dto) {
        // Tái sử dụng logic toEntity, nhưng không set ID
        SampleSentence entity = convertToEntity(dto); 
        return convertToDTO(repository.save(entity));
    }

    @Override
    public SampleSentenceDTO update(UUID id, SampleSentenceDTO dto) {
        SampleSentence entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SampleSentence not found with ID: " + id));
        
        // Cập nhật các trường
        if (dto.getEnglishSentence() != null) entity.setEnglishSentence(dto.getEnglishSentence());
        if (dto.getVietnameseTranslation() != null) entity.setVietnameseTranslation(dto.getVietnameseTranslation());
        if (dto.getUsageFrequency() != null) entity.setUsageFrequency(dto.getUsageFrequency());
        if (dto.getTopicTag() != null) entity.setTopicTag(dto.getTopicTag());
        
        
        return convertToDTO(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("SampleSentence not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public SampleSentenceDTO getById(UUID id) {
        SampleSentence entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SampleSentence not found with ID: " + id));
        return convertToDTO(entity);
    }

    @Override
    public Map<String, Object> getAll(Pageable pageable) {
        Page<SampleSentence> pageResult = repository.findAll(pageable);
        

        Page<SampleSentenceDTO> dtoPage = pageResult.map(this::convertToDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtoPage.getContent());
        response.put("currentPage", dtoPage.getNumber());
        response.put("totalItems", dtoPage.getTotalElements());
        response.put("totalPages", dtoPage.getTotalPages());
        response.put("isLastPage", dtoPage.isLast());

        return response;
    }
}