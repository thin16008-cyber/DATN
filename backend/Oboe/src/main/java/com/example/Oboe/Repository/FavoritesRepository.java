package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, UUID> {
    
    // Lấy tất cả Favorites theo User ID
    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId")
    List<Favorites> findByUserId(@Param("userId") UUID userId);

    // --- LOẠI BỎ: findFavoritePhonetic (Vì Phonetic Entity đã bị xóa) ---
    // Loại bỏ: @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId AND f.phonetic.phoneticId = :phoneticId")

    // 1. Grammar
    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId AND f.grammar.grammarId = :grammarId")
    Optional<Favorites> findFavoriteGrammar(@Param("userId") UUID userId, @Param("grammarId") UUID grammarId);

    // 2. Vocabulary (Chuẩn hóa tham số ID thành vocabularyId)
    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId AND f.vocabulary.vocabularyId = :vocabularyId")
    Optional<Favorites> findFavoriteVocabulary(@Param("userId") UUID userId, @Param("vocabularyId") UUID vocabularyId);

    // 3. Idiom (Đã đổi tên Entity từ PhraseIdiom thành Idiom và ID thành idiomId)
    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId AND f.idiom.idiomId = :idiomId")
    Optional<Favorites> findFavoriteIdiom(@Param("userId") UUID userId, @Param("idiomId") UUID idiomId);
}