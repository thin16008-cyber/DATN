package com.example.Oboe.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOsBeanConfig {

    private final PayOsConfig payOsConfig;

    public PayOsBeanConfig(PayOsConfig payOsConfig) {
        this.payOsConfig = payOsConfig;
    }
    


    @Bean
    public PayOS payOS() {
        System.out.println("CLIENT_ID=" + payOsConfig.getClientId());
        System.out.println("API_KEY=" + payOsConfig.getApiKey());
        System.out.println("CHECKSUM_KEY=" + payOsConfig.getChecksumKey());
        System.out.println("CHECKSUM length = " + payOsConfig.getChecksumKey().length());

        return new PayOS(
                payOsConfig.getClientId(),
                payOsConfig.getApiKey(),
                payOsConfig.getChecksumKey()
            );
    }
}