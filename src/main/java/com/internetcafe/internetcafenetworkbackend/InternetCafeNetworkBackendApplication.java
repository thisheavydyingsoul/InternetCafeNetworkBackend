package com.internetcafe.repository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.internetcafe")
@EnableJpaAuditing
public class InternetCafeNetworkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternetCafeNetworkBackendApplication.class, args);
    }

}
