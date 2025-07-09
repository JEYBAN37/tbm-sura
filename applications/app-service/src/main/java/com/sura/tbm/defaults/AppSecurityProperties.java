package com.sura.tbm.defaults;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app-security")
public class AppSecurityProperties {
    private String service;
    private String spEntityId;
    private boolean enable;
    private boolean authEnable;
    private List<String> publicResources;
    private Cors cors;

    @Data
    public static class Cors {
        private boolean allowCredentials;
        private List<String> allowedOrigins;
        private List<String> allowedMethods;
    }
}
