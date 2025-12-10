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
        
        // Cần sử dụng các Getter mới: getSentenceId, getEnglishSentence, getVietnameseTranslation
        dto.setSentenceId(entity.getSentenceId());
        dto.setEnglishSentence(entity.getEnglishSentence());
        dto.setVietnameseTranslation(entity.getVietnameseTranslation());
        
        // Thêm các trường mới
        dto.setUsageFrequency(entity.getUsageFrequency());
        
        
        return dto;
    }

    // Đã cập nhật để ánh xạ các trường mới và tên trường mới
    private SampleSentence convertToEntity(SampleSentenceDTO dto) {
        SampleSentence entity = new SampleSentence();
        
        // KHÔNG set ID cho thao tác CREATE. ID (UUID) sẽ được Hibernate tự động sinh.
        
        // Cần sử dụng các Getter mới: getEnglishSentence, getVietnameseTranslation
        entity.setEnglishSentence(dto.getEnglishSentence());
        entity.setVietnameseTranslation(dto.getVietnameseTranslation());
        
        // Thêm các trường mới
        entity.setUsageFrequency(dto.getUsageFrequency());
        
        // KHÔNG xử lý khóa ngoại (Vocabulary, Idiom, Grammar) ở đây vì cần phải tìm Entity cha trước.
        // Logic xử lý khóa ngoại nên được thực hiện trong Service nếu cần.

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
        
        // Lưu ý: Cần logic phức tạp hơn nếu muốn cập nhật các khóa ngoại (related IDs)
        
        return convertToDTO(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        // Nên kiểm tra sự tồn tại trước khi xóa
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
        
        // Sử dụng stream().map(this::convertToDTO).collect(Collectors.toList())
        // thay vì pageResult.map(this::convertToDTO) để tránh lỗi nếu sử dụng Spring Data JPA cũ
        // Nhưng nếu sử dụng Spring Boot 3.x, pageResult.map() là hợp lệ. Tôi sẽ giữ pageResult.map().
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