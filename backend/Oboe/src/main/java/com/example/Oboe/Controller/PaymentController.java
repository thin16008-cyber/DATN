package com.example.Oboe.Controller;
import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.CustomWebhookDTO;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.Payment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.PaymentRepository;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.annotation.PremiumOnly;
import org.springframework.security.core.Authentication;
import vn.payos.type.Webhook;
import com.example.Oboe.Service.MomoService;
import com.example.Oboe.Service.PayOsService;
import com.example.Oboe.DTOs.CustomWebhookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PayOsService payOsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MomoService momoService;
    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/momo")
    public ResponseEntity<?> payWithMomo(@RequestParam UUID userId) {
        try {
            Map<String, String> paymentResult = momoService.createPayment(userId);
            return ResponseEntity.ok(paymentResult); // Trả về full thông tin: payUrl, orderId, requestId
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Lỗi tạo thanh toán",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/momo-notify")
    public ResponseEntity<String> handleMomoCallback(@RequestBody Map<String, String> payload) {
        momoService.handleMomoCallback(payload);
        return ResponseEntity.ok("success");
    }


//     @PostMapping("/payos")
//     public ResponseEntity<?> payWithPayOS(@RequestParam(required = false) Integer amount,
//                                           Authentication authentication) {
//         try {
//             CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
//             UUID userId = customUser.getUserID();

//             var result = payOsService.createPayment(99000, "Pay Oboeru", userId);

//             if (result == null || result.getCheckoutUrl() == null) {
//                 return ResponseEntity.status(500).body(Map.of(
//                         "error", "Lỗi tạo thanh toán PayOS",
//                         "message", "Không có dữ liệu trả về từ PayOS hoặc thiếu checkoutUrl"
//                 ));
//             }

//             // Lấy lại payment từ orderCode
//             long orderCode = result.getOrderCode();
//             Payment payment = paymentRepository.findByOrderCode(orderCode);
//             if (payment == null) {
//                 return ResponseEntity.status(500).body(Map.of("error", "Không tìm thấy đơn hàng vừa tạo"));
//             }

//             String encodedCheckoutUrl = URLEncoder.encode(result.getCheckoutUrl(), StandardCharsets.UTF_8);
// //            String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + result.getCheckoutUrl();

//             String qrUrl = result.getQrCode();

//             Map<String, Object> body = new HashMap<>();
//             body.put("amount", payment.getAmount());
//             body.put("orderCode", payment.getOrderCode());
//             body.put("status", payment.getStatus());
//             body.put("paid_at", payment.getPaidAt());
//             body.put("qrUrl", qrUrl);
//             body.put("checkoutUrl", result.getCheckoutUrl());

//             System.out.println("Checkout result: " + result);
//             return ResponseEntity.ok(body);

//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(500).body(Map.of(
//                     "error", "Lỗi PayOS",
//                     "message", e.getMessage()
//             ));
//         }
//     }



//     @PostMapping("/payos-notify")
//     public ResponseEntity<?> handlePayOsCallback(@RequestBody String rawJson) {
//         try {
//             System.out.println("Webhook Received:\n" + rawJson);

//             var data = payOsService.handleWebhook(rawJson);

//             System.out.println("Payment status updated for orderCode: " + data.getOrderCode() +
//                     ", status code: " + data.getCode());


//             return ResponseEntity.ok("Received");
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(500).body("Failed: " + e.getMessage());
//         }
//     }

        @PostMapping("/payos")
        public ResponseEntity<?> payWithPayOS(
                @RequestParam(required = false) Integer amount,
                Authentication authentication
        ) {
            try {
                CustomUserDetails customUser =
                        (CustomUserDetails) authentication.getPrincipal();
                UUID userId = customUser.getUserID();

                // 👉 gọi service REST PayOS
                Map<String, Object> result = payOsService.createPayment(99000, userId);

                if (result == null || !result.containsKey("data")) {
                    return ResponseEntity.status(500).body(Map.of(
                            "error", "Lỗi tạo thanh toán PayOS",
                            "message", "Không có dữ liệu trả về từ PayOS"
                    ));
                }

                Map<String, Object> data =
                        (Map<String, Object>) result.get("data");

                String checkoutUrl = (String) data.get("checkoutUrl");
                Long orderCode = Long.valueOf(data.get("orderCode").toString());
                String qrCode = (String) data.get("qrCode");

                if (checkoutUrl == null) {
                    return ResponseEntity.status(500).body(Map.of(
                            "error", "Thiếu checkoutUrl từ PayOS"
                    ));
                }

                // Lấy payment từ DB
                Payment payment = paymentRepository.findByOrderCode(orderCode);
                if (payment == null) {
                    return ResponseEntity.status(500).body(
                            Map.of("error", "Không tìm thấy đơn hàng vừa tạo")
                    );
                }

                Map<String, Object> body = new HashMap<>();
                body.put("amount", payment.getAmount());
                body.put("orderCode", payment.getOrderCode());
                body.put("status", payment.getStatus());
                body.put("paid_at", payment.getPaidAt());
                body.put("qrUrl", qrCode);
                body.put("checkoutUrl", checkoutUrl);

                System.out.println("Checkout result: " + data);

                return ResponseEntity.ok(body);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of(
                        "error", "Lỗi PayOS",
                        "message", e.getMessage()
                ));
            }
        }

    @PostMapping("/payos-webhook")
    public ResponseEntity<?> webhook(@RequestBody String rawJson) {
        try {
            payOsService.handleWebhook(rawJson);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }




    @GetMapping("/payment-success")
    public ResponseEntity<?> handlePaymentSuccess(
            @RequestParam String code,
            @RequestParam String cancel,
            @RequestParam String status,
            @RequestParam String orderCode) {
        return ResponseEntity.ok(Map.of(
                "message", "Cảm ơn bạn đã thanh toán! Hệ thống sẽ tự động xác nhận.",
                "orderCode", orderCode,
                "status", status,
                "cancel", cancel
        ));
    }

    @GetMapping("/payment-cancel")
    public ResponseEntity<?> handleCancel(@RequestParam(required = false) String orderCode) {
        if (orderCode == null || orderCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu orderCode"));
        }

        try {
            Long orderCodeLong = Long.parseLong(orderCode);
            Payment payment = paymentRepository.findByOrderCode(orderCodeLong);

            if (payment != null) {
                payment.setStatus("CANCELLED");
                paymentRepository.save(payment);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Thanh toán đã bị hủy",
                    "orderCode", orderCodeLong
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "orderCode không hợp lệ"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getPaymentStatus(@RequestParam Long orderCode) {
        Payment payment = paymentRepository.findByOrderCode(orderCode);
        if (payment == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy đơn hàng"));
        }

        return ResponseEntity.ok(Map.of(
                "orderCode", payment.getOrderCode(),
                "status", payment.getStatus(),
                "amount", payment.getAmount(),
                "user", payment.getUser().getUser_id()
        ));
    }
}
