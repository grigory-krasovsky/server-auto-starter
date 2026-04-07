package com.example.serverautostarter.hetzner.service.impl;

import io.github.sinuscosinustan.hetznercloud.HetznerCloudAPI;
import io.github.sinuscosinustan.hetznercloud.objects.enums.APIType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HetznerConnector {

    @Bean
    public HetznerCloudAPI getClient() {
        return new HetznerCloudAPI("", APIType.CLOUD);
    }
}
