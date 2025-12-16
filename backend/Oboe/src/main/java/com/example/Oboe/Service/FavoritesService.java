package com.example.Oboe.Service;

import com.example.Oboe.DTOs.FavoritesDTO;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; 

@Service
public class FavoritesService {

    private final UserRepository userRepository;
    private final FavoritesRepository favoritesRepository;
    private final GrammarRepository grammarRepository;
    private final VocabularyRepository vocabularyRepository;
    
    // Đã chuẩn hóa tên biến
    private final IdiomRepository idiomRepository; 

    // Cập nhật Constructor (Dependency Injection)
    public FavoritesService(
                            UserRepository userRepository,
                            FavoritesRepository favoritesRepository,
                            GrammarRepository grammarRepository,
                            VocabularyRepository vocabularyRepository, 
                            IdiomRepository idiomRepository // Đã chuẩn hóa tên
                           ) {
        this.userRepository = userRepository;
        this.favoritesRepository = favoritesRepository;
        this.grammarRepository = grammarRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.idiomRepository = idiomRepository; 
    }

    // --- Phương thức logic ---
    
    /**
     * Bật/Tắt yêu thích (thêm hoặc xóa mục yêu thích).
     */
    public FavoritesDTO toggleFavorite(FavoritesDTO dto, UUID userId) {
        Favorites existing = null;
        
        // LOẠI BỎ logic Phonetics và cập nhật tên ID/Repository cho Idiom
        if (dto.getGrammarId() != null) {
            existing = favoritesRepository.findFavoriteGrammar(userId, dto.getGrammarId()).orElse(null);
        } else if (dto.getVocabularyId() != null) {
            existing = favoritesRepository.findFavoriteVocabulary(userId, dto.getVocabularyId()).orElse(null);
        } else if (dto.getIdiomId() != null) { 
            existing = favoritesRepository.findFavoriteIdiom(userId, dto.getIdiomId()).orElse(null); 
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải cung cấp ít nhất 1 loại nội dung.");
        }
        
        // Logic thêm/xóa
        if (existing != null) {
            favoritesRepository.delete(existing);
            return null; 
        }
        return createFavorite(dto, userId);
    }
    
    /**
     * Kiểm tra xem một mục đã được yêu thích chưa.
     */
    public boolean isFavorited(UUID userId, String type, UUID targetId) {
        return switch (type.toLowerCase()) {
            case "grammar" -> favoritesRepository.findFavoriteGrammar(userId, targetId).isPresent();
            case "vocabulary" -> favoritesRepository.findFavoriteVocabulary(userId, targetId).isPresent();
            case "idiom" -> favoritesRepository.findFavoriteIdiom(userId, targetId).isPresent(); 
            default -> throw new IllegalArgumentException("Loại nội dung không hợp lệ: " + type);
        };
    }

    /**
     * Tạo mục yêu thích mới.
     */
    public FavoritesDTO createFavorite(FavoritesDTO dto, UUID userId) {
        Favorites favorites = new Favorites();

        favorites.setFavoritedAt(dto.getFavoritedAt() != null ? dto.getFavoritedAt() : java.time.LocalDate.now());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        favorites.setUser(user);

        // LOẠI BỎ logic Phonetics và cập nhật logic Idiom
        if (dto.getGrammarId() != null) {
            Grammar grammar = grammarRepository.findById(dto.getGrammarId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grammar not found"));
            
            favorites.setGrammar(grammar);
            favorites.setTitle(grammar.getStructure());
            favorites.setContent(grammar.getExplanation());
            
        } else if (dto.getVocabularyId() != null) {
            Vocabulary vocabulary = vocabularyRepository.findById(dto.getVocabularyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vocabulary not found"));
            
            favorites.setVocabulary(vocabulary);
            favorites.setTitle(vocabulary.getWord());
            favorites.setContent(vocabulary.getVietnameseMeaning()); 
            
        } else if (dto.getIdiomId() != null) { 
            Idiom idiom = idiomRepository.findById(dto.getIdiomId()) 
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Idiom not found"));
            
            favorites.setIdiom(idiom); 
            favorites.setTitle(idiom.getEnglishPhrase());
            favorites.setContent(idiom.getVietnameseMeaning());
            
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải cung cấp ít nhất 1 loại nội dung yêu thích.");
        }

        Favorites savedFavorite = favoritesRepository.save(favorites);
        return toDTO(savedFavorite);
    }

    /**
     * Xóa mục yêu thích theo ID và kiểm tra quyền sở hữu.
     */
    public void deleteFavorite(UUID favoritesId, UUID userId) {
         Favorites favorite = favoritesRepository.findById(favoritesId)
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mục yêu thích không tồn tại."));

        // Chỉ người sở hữu mới được xóa
        // LỖI CÚ PHÁP: favorite.getUser().getUser_id() -> favorite.getUser().getUserId()
        if (!favorite.getUser().getUser_id().equals(userId)) { 
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa mục yêu thích này.");
        }

        favoritesRepository.delete(favorite);
    }
    
    /**
     * Lấy tất cả các mục yêu thích theo User ID và Type
     */
    public List<FavoritesDTO> getFavoritesByUserIdAndType(UUID userId, String type) {
        List<Favorites> list = favoritesRepository.findByUserId(userId);

        return list.stream()
                .filter(fav -> {
                    return switch (type.toLowerCase()) {
                        case "grammar" -> fav.getGrammar() != null;
                        case "vocabulary" -> fav.getVocabulary() != null;
                        case "idiom" -> fav.getIdiom() != null; 
                        default -> false;
                    };
                })
                .map(fav -> {
                    FavoritesDTO dto = toDTO(fav);
                    dto.setType(type.toLowerCase());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy tất cả các mục yêu thích theo User ID
     */
    public List<FavoritesDTO> getAllFavoritesByUserId(UUID userId) {
        List<Favorites> list = favoritesRepository.findByUserId(userId);

        return list.stream()
                .map(FavoritesService::toDTO) // Gọi hàm toDTO tĩnh
                .collect(Collectors.toList());
    }

    /**
     * Hàm chuyển đổi Entity Favorites sang FavoritesDTO (Đã sửa lỗi constructor)
     */
    public static FavoritesDTO toDTO(Favorites favorites) {
        if (favorites == null) return null;
        
        FavoritesDTO dto = new FavoritesDTO();
        
        // 1. Ánh xạ thuộc tính cơ bản
        dto.setFavoritesId(favorites.getFavoritesId());
        dto.setTitle(favorites.getTitle());
        dto.setContent(favorites.getContent());
        dto.setFavoritedAt(favorites.getFavoritedAt());
        
        // 2. Ánh xạ User ID
        if (favorites.getUser() != null) {
            // LỖI CÚ PHÁP: favorites.getUser().getUser_id() -> favorites.getUser().getUserId()
            dto.setUserId(favorites.getUser().getUser_id()); 
        }
        
        // 3. Ánh xạ ID của các đối tượng liên quan
        if (favorites.getGrammar() != null) {
            dto.setGrammarId(favorites.getGrammar().getGrammarId());
            dto.setType("grammar");
        } else if (favorites.getVocabulary() != null) {
            dto.setVocabularyId(favorites.getVocabulary().getVocabularyId());
            dto.setType("vocabulary");
        } else if (favorites.getIdiom() != null) { 
            dto.setIdiomId(favorites.getIdiom().getIdiomId());
            dto.setType("idiom");
        } else {
             dto.setType("unknown");
        }
        
        // Nếu bạn muốn bao gồm FlashCards:
        // if (favorites.getFlashCards() != null) {
        //     // dto.setCardId(favorites.getFlashCards().getCardId()); 
        //     dto.setType("flashcard");
        // }


        return dto;
    }
}