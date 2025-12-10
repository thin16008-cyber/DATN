package com.example.Oboe.Repository;

import com.example.Oboe.Entity.SampleSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
// Đã đổi Key ID từ UUID sang Long
public interface SampleSentenceRepository extends JpaRepository<SampleSentence, UUID> {
    
    /**
     * Tìm kiếm câu ví dụ theo nghĩa tiếng Việt hoặc câu tiếng Anh.
     * Đã loại bỏ vietnamesePronunciation.
     */
    @Query("SELECT s FROM SampleSentence s WHERE LOWER(s.vietnameseTranslation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.englishSentence) LIKE LOWER(CONCAT('%', :keyword, '%'))")
            // Đã loại bỏ OR LOWER(s.vietnamesePronunciation) LIKE ... và OR LOWER(s.japaneseText) LIKE ...
    List<SampleSentence> searchByKeyword(@Param("keyword") String keyword);
    
    // Thêm các phương thức tìm kiếm theo khóa ngoại (tùy chọn)
    

}