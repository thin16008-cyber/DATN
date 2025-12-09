package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import com.example.Oboe.Service.SampleSentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sample-sentences")
public class SampleSentenceController {

    @Autowired
    private SampleSentenceService service;

    /**
     * Tạo một câu ví dụ mới.
     * POST /api/sample-sentences
     */
    @PostMapping
    public SampleSentenceDTO create(@RequestBody SampleSentenceDTO dto) {
        return service.create(dto);
    }

    /**
     * Cập nhật câu ví dụ theo ID.
     * PUT /api/sample-sentences/{id}
     */
    @PutMapping("/{id}")
    public SampleSentenceDTO update(@PathVariable UUID id, @RequestBody SampleSentenceDTO dto) {
        // Đảm bảo Controller sử dụng UUID, đồng bộ với Service
        return service.update(id, dto);
    }

    /**
     * Xóa câu ví dụ theo ID.
     * DELETE /api/sample-sentences/{id}
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        // Đảm bảo Controller sử dụng UUID, đồng bộ với Service
        service.delete(id);
    }


    /**
     * Lấy câu ví dụ theo ID.
     * GET /api/sample-sentences/{id}
     */
    @GetMapping("/{id}")
    public SampleSentenceDTO getById(@PathVariable UUID id) {
        // Đảm bảo Controller sử dụng UUID, đồng bộ với Service
        return service.getById(id);
    }

    /**
     * Lấy tất cả câu ví dụ có phân trang.
     * GET /api/sample-sentences?page=0&size=10
     */
    @GetMapping
    public Map<String, Object> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getAll(pageable);
    }

}