package ru.Dovgan_Egor.NauJava.CONFIG_PCK;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    @PostConstruct
    public void printConfig() {
        System.out.println("=== App Config Initialized ===");
        System.out.println("Application Name: " + appName);
        System.out.println("Application Version: " + appVersion);
        System.out.println("==============================");
    }
}
