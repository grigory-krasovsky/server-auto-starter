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
        return new HetznerCloudAPI("JwRiWf9cc6FIBH4oAAdFIkhkZur7uvxZr0sqscB495hD0Ep43j18NFmQxb0mHse3", APIType.CLOUD);
    }
}
