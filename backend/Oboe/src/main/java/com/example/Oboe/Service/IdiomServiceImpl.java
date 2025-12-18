package com.example.Oboe.Service;

import com.example.Oboe.DTOs.IdiomDTO; 
import com.example.Oboe.Entity.Idiom; 
import com.example.Oboe.Repository.IdiomRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 1. Đổi tên lớp Service và Interface
@Service
public class IdiomServiceImpl implements IdiomService { 

    // 2. Đổi tên Autowired Repository
    @Autowired
    private IdiomRepository repository;

    // 3. Cập nhật phương thức chuyển đổi DTO (Tên Entity/DTO mới)
    private IdiomDTO convertToDTO(Idiom entity) {
        IdiomDTO dto = new IdiomDTO();
        
        // Chuẩn hóa tên Getter/Setter: ID
        dto.setId(entity.getIdiomId());
        
        // Chuẩn hóa tên Getter/Setter: Thuộc tính
        dto.setEnglishPhrase(entity.getEnglishPhrase());
        dto.setVietnameseMeaning(entity.getVietnameseMeaning());
        dto.setCategory(entity.getCategory()); // Thêm trường category mới
        dto.setAudioUrl(entity.getAudioUrl()); // Thêm trường audioUrl mới
        dto.setOrigin(entity.getOrigin()); // Thêm trường origin mới
        // Bổ sung: origin
        
        return dto;
    }

    // 4. Cập nhật phương thức chuyển đổi Entity (Tên Entity/DTO mới)
    private Idiom convertToEntity(IdiomDTO dto) {
        Idiom entity = new Idiom();
        
        // Cần phải set ID nếu là thao tác UPDATE (nhưng tạo mới thì không cần)
        if (dto.getId() != null) {
            entity.setIdiomId(dto.getId()); 
        }
        
        // Chuẩn hóa tên Getter/Setter: Thuộc tính
        entity.setEnglishPhrase(dto.getEnglishPhrase()); 
        entity.setVietnameseMeaning(dto.getVietnameseMeaning());
        entity.setCategory(dto.getCategory());
        entity.setAudioUrl(dto.getAudioUrl());
        entity.setOrigin(dto.getOrigin());
        // Bổ sung: origin

        return entity;
    }

    @Override
    public IdiomDTO create(IdiomDTO dto) {
        Idiom entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    @Override
    public IdiomDTO update(UUID id, IdiomDTO dto) {
        Idiom entity = repository.findById(id)
                // Cập nhật tên Entity trong Exception
                .orElseThrow(() -> new RuntimeException("Idiom not found with ID: " + id));
        
        // Cập nhật các trường
        entity.setEnglishPhrase(dto.getEnglishPhrase());
        entity.setVietnameseMeaning(dto.getVietnameseMeaning());
        entity.setCategory(dto.getCategory());
        entity.setAudioUrl(dto.getAudioUrl());
        entity.setOrigin(dto.getOrigin());
        // Bổ sung: origin

        return convertToDTO(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public IdiomDTO getById(UUID id) {
        Idiom entity = repository.findById(id)
                // Cập nhật tên Entity trong Exception
                .orElseThrow(() -> new RuntimeException("Idiom not found with ID: " + id)); 
        return convertToDTO(entity);
    }

    @Override
    public Map<String, Object> getAll(Pageable pageable) {
        Page<Idiom> pageResult = repository.findAll(pageable);
        Page<IdiomDTO> dtoPage = pageResult.map(this::convertToDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtoPage.getContent());
        response.put("currentPage", dtoPage.getNumber());
        response.put("totalItems", dtoPage.getTotalElements());
        response.put("totalPages", dtoPage.getTotalPages());
        response.put("isLastPage", dtoPage.isLast());

        return response;
    }
}