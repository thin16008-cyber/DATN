package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Idiom; // Đổi tên Entity từ PhraseIdiom thành Idiom
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
// Sửa lỗi: Interface phải extends JpaRepository<Entity, IdType> -> Idiom
public interface IdiomRepository extends JpaRepository<Idiom, UUID> { 
    

    @Query("SELECT p FROM Idiom p WHERE LOWER(p.vietnameseMeaning) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            // Loại bỏ: "OR LOWER(p.vietnamesePronunciation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.englishPhrase) LIKE LOWER(CONCAT('%', :keyword, '%'))")

    List<Idiom> searchPhrasesAndIdioms(@Param("keyword") String keyword); 

    // 2. Sửa Entity
    @Query("SELECT p FROM Idiom p WHERE LOWER(p.vietnameseMeaning) LIKE LOWER(CONCAT('%', :keyword, '%'))")

    List<Idiom> searchByVietnameseMeaning(@Param("keyword") String keyword);
    

    @Query("SELECT p FROM Idiom p WHERE p.category = :category AND (" + 
            "LOWER(p.vietnameseMeaning) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + 
            "LOWER(p.englishPhrase) LIKE LOWER(CONCAT('%', :keyword, '%')))")

    List<Idiom> findByCategoryAndKeyword(
            @Param("category") String category,
            @Param("keyword") String keyword);
}