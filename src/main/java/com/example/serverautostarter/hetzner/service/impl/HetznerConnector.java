package com.example.serverautostarter.hetzner.service.impl;

import io.github.sinuscosinustan.hetznercloud.HetznerCloudAPI;
import io.github.sinuscosinustan.hetznercloud.objects.enums.APIType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HetznerConnector {

    @Value("${HETZNER_API}")
    private String API;
    @Bean
    public HetznerCloudAPI getClient() {
        return new HetznerCloudAPI(API, APIType.CLOUD);
    }
}
