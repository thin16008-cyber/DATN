package com.example.Oboe.Service;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID; 

public interface SampleSentenceService {
    
    SampleSentenceDTO create(SampleSentenceDTO dto);
    
    // Đã cập nhật: Sử dụng UUID cho ID
    SampleSentenceDTO update(UUID id, SampleSentenceDTO dto);
    
    // Đã cập nhật: Sử dụng UUID cho ID
    void delete(UUID id);
    
    // Đã cập nhật: Sử dụng UUID cho ID
    SampleSentenceDTO getById(UUID id);
    
    Map<String, Object> getAll(Pageable pageable);
}