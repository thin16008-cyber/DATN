package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.IdiomDTO;
import com.example.Oboe.Service.IdiomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


// @RequestMapping("/api/sample-sentences")

@RestController
// 2. Cập nhật Request Mapping
@RequestMapping("/api/idioms")
public class IdiomController {

    @Autowired
    private IdiomService service;

    @PostMapping
    public IdiomDTO create(@RequestBody IdiomDTO dto) { // Cập nhật DTO
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public IdiomDTO update(@PathVariable UUID id, @RequestBody IdiomDTO dto) { // Cập nhật DTO
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }


    @GetMapping("/{id}")
    public IdiomDTO getById(@PathVariable UUID id) { // Cập nhật DTO
        return service.getById(id);
    }

    @GetMapping
    public Map<String, Object> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getAll(pageable);
    }

}
