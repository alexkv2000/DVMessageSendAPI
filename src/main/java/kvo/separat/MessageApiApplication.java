package kvo.separat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessageApiApplication {

    @Value("${config.path}")
    private String configPath;
    public static void main(String[] args) {
        SpringApplication.run(MessageApiApplication.class, args);
    }
    public String getConfigPath() {
        return configPath;
    }
}
