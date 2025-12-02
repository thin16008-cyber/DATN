package com.example.Oboe.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {


    private final String FIREBASE_PROJECT_ID = "datn-931df"; // Project ID của bạn
    @PostConstruct
    public void initialize() {
        
        // Chỉ khởi tạo nếu chưa có FirebaseApp nào tồn tại
        if (FirebaseApp.getApps().isEmpty()) {
            
            // 1. Ưu tiên: Load Service Account Key từ resources
            try {
                ClassPathResource resource = new ClassPathResource("firebase/serviceAccountKey.json");
                
                if (resource.exists()) {
                    System.out.println("✅ FIREBASE INIT: Attempting to load Service Account Key...");
                    
                    try (InputStream serviceAccount = resource.getInputStream()) {
                        FirebaseOptions options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                .setProjectId(FIREBASE_PROJECT_ID) // Vẫn đặt Project ID để rõ ràng
                                .build();
                        
                        FirebaseApp.initializeApp(options);
                        System.out.println("✅ FIREBASE INIT: Successfully initialized using Service Account Key.");
                        return;
                    }
                } else {
                    System.out.println("⚠️ FIREBASE INIT: serviceAccountKey.json not found. Falling back to Application Default Credentials.");
                }
            } catch (Exception e) {
                // Bắt lỗi khi load file hoặc khởi tạo FirebaseOptions
                System.err.println("❌ FIREBASE INIT: Error during Service Account Key processing: " + e.getMessage());
            }

            // 2. Fallback: Sử dụng Google Application Default Credentials (tốt cho môi trường Cloud/Deployment)
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .setProjectId(FIREBASE_PROJECT_ID)
                        .build();
                
                FirebaseApp.initializeApp(options);
                System.out.println("⚠️ FIREBASE INIT: Initialized using Application Default Credentials.");
                return;
            } catch (IOException e) {
                System.err.println("❌ FIREBASE INIT: Application Default Credentials not available: " + e.getMessage());
            }
            
            // 3. Final Fallback: Chỉ sử dụng Project ID (KHÔNG ĐỦ cho xác thực Token)
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setProjectId(FIREBASE_PROJECT_ID)
                        .build();
                FirebaseApp.initializeApp(options);
                System.err.println("❌ FIREBASE INIT: Initialized with Project ID only. Authentication/Token Verification will likely FAIL due to missing credentials/keys.");
            } catch (Exception e) {
                System.err.println("CRITICAL ERROR: Firebase initialization failed completely: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}