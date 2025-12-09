package com.example.Oboe.Service;

import com.example.Oboe.DTOs.IdiomDTO; // Đổi tên DTO
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.UUID;

// Đổi tên Interface Service
public interface IdiomService {
    
    // Cập nhật kiểu tham số và kiểu trả về thành IdiomDTO
    IdiomDTO create(IdiomDTO dto);
    
    IdiomDTO update(UUID id, IdiomDTO dto);
    
    void delete(UUID id);
    
    IdiomDTO getById(UUID id);
    
    Map<String, Object> getAll(Pageable pageable);
}