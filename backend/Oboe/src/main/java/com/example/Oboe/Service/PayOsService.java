package com.example.Oboe.Service;

import com.example.Oboe.Config.PayOsConfig;
import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.Payment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.PaymentRepository;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Util.HmacUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.google.api.client.http.HttpHeaders;

import lombok.RequiredArgsConstructor;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.WebhookData;

import java.time.LocalDateTime;

import org.springframework.http.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;









// @Service
// public class PayOsService {

//     private final PayOS payOS;
//     private final PayOsConfig config;
//     private final PaymentRepository paymentRepository;
//     private final UserRepository userRepository;

//     public PayOsService(PayOS payOS,
//                         PayOsConfig config,
//                         PaymentRepository paymentRepository,
//                         UserRepository userRepository) {
//         this.payOS = payOS;
//         this.config = config;
//         this.paymentRepository = paymentRepository;
//         this.userRepository = userRepository;
//     }

//     public CheckoutResponseData createPayment(int amount, String itemName, UUID userId) throws Exception {

//         // ItemData item = ItemData.builder()
//         //         .name(itemName)
//         //         .quantity(1)
//         //         .price(amount)
//         //         .build();

//         long orderCode = System.currentTimeMillis(); 

//         PaymentData paymentData = PaymentData.builder()
//                 .orderCode(orderCode)
//                 .amount(amount)
//                 .description("Pay Oboeru " + orderCode)
//                 .returnUrl(config.getReturnUrl())
//                 .cancelUrl(config.getCancelUrl())
//                 // .items(List.of(item))
//                 .build();

//         CheckoutResponseData response = payOS.createPaymentLink(paymentData);

//         Payment payment = new Payment();
//         payment.setOrderCode(orderCode);
//         payment.setAmount(amount);
//         payment.setUser(userRepository.findById(userId).orElse(null));
//         payment.setStatus("PENDING");
//         paymentRepository.save(payment);

//         return response;
//     }


//     public WebhookData handleWebhook(String rawJson) throws Exception {
//         ObjectMapper mapper = new ObjectMapper();
//         vn.payos.type.Webhook webhook = mapper.readValue(rawJson, vn.payos.type.Webhook.class);
//         WebhookData data = payOS.verifyPaymentWebhookData(webhook);

//         long orderCode = data.getOrderCode();
//         String code = data.getCode();

//         Payment payment = paymentRepository.findByOrderCode(orderCode);
//         if (payment != null) {
//             String status;

//             switch (code) {
//                 case "00" -> {
//                     status = "SUCCESS";
//                     payment.setPaidAt(LocalDateTime.now());
//                     User user = payment.getUser();
//                     if (user.getAccountType() != AccountType.PREMIUM) {
//                         user.setAccountType(AccountType.PREMIUM);

//                         userRepository.save(user);
//                     }
//                 }
//                 case "09" -> status = "CANCELLED";
//                 default -> status = "FAILED";
//             }


//             payment.setStatus(status);
//             paymentRepository.save(payment);
//         }

//         return data;
//     }

//     public void cancelPayment(long orderCode, String reason) throws Exception {
//         payOS.cancelPaymentLink(orderCode, reason);
//     }

//     public Object getPaymentInfo(long orderCode) throws Exception {
//         return payOS.getPaymentLinkInformation(orderCode);
//     }
// }
@Service
public class PayOsService {

    private final PayOsConfig config;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PayOsService(PayOsConfig config,
                        PaymentRepository paymentRepository,
                        UserRepository userRepository) {
        this.config = config;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    // ================= CREATE PAYMENT =================
    public Map<String, Object> createPayment(int amount, UUID userId) throws Exception {
        amount = 99000; // fixed amount

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long orderCode = System.currentTimeMillis();

        // ====== REQUEST BODY ======
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("orderCode", orderCode);
        body.put("amount", amount);
        body.put("description", "Pay Oboeru " + orderCode);
        body.put("returnUrl", config.getReturnUrl());
        body.put("cancelUrl", config.getCancelUrl());

        // ====== SIGNATURE ======
        String dataToSign =
                orderCode + "|" + amount + "|" +
                config.getReturnUrl() + "|" +
                config.getCancelUrl();

        String signature = HmacUtil.signSHA256(dataToSign, config.getChecksumKey());
        body.put("signature", signature);

        // ====== HEADERS ======
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", config.getClientId());
        headers.set("x-api-key", config.getApiKey());

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<Map<String, Object>>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        config.getBaseUrl() + "/v2/payment-requests",
                        request,
                        Map.class
                );

        Map<String, Object> resBody = response.getBody();
        Map<String, Object> data = (Map<String, Object>) resBody.get("data");

        // ====== SAVE DB ======
        Payment payment = new Payment();
        payment.setOrderCode(orderCode);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setPaymentMethod("PAYOS");
        payment.setTransactionId((String) data.get("paymentLinkId"));
        payment.setUser(user);

        paymentRepository.save(payment);

        // ====== RETURN FRONTEND ======
        Map<String, Object> result = new HashMap<>();
        result.put("orderCode", orderCode);
        result.put("checkoutUrl", data.get("checkoutUrl"));
        result.put("qrCode", data.get("qrCode"));
        result.put("amount", amount);
        result.put("status", "PENDING");

        return result;
    }

    // ================= WEBHOOK =================
    public void handleWebhook(String rawJson) throws Exception {

        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode data = root.get("data");

        String receivedSignature = root.get("signature").asText();

        String dataToSign =
                data.get("orderCode").asText() + "|" +
                data.get("amount").asText() + "|" +
                data.get("code").asText();

        String expectedSignature =
                HmacUtil.signSHA256(dataToSign, config.getChecksumKey());

        if (!expectedSignature.equals(receivedSignature)) {
            throw new RuntimeException("INVALID PAYOS SIGNATURE");
        }

        Long orderCode = data.get("orderCode").asLong();
        String code = data.get("code").asText();

        Payment payment = paymentRepository.findByOrderCode(orderCode);
        if (payment == null) return;

        switch (code) {
            case "00" -> {
                payment.setStatus("SUCCESS");
                payment.setPaidAt(LocalDateTime.now());
            }
            case "09" -> payment.setStatus("CANCELLED");
            default -> payment.setStatus("FAILED");
        }

        paymentRepository.save(payment);
    }
}

