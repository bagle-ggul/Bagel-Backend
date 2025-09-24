package com.suhsaechan.dongbanza.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "springdoc")
public class SwaggerProperties {

    private List<Server> servers = new ArrayList<>();

    @Getter
    @Setter
    public static class Server {
        private String url;
        private String description;
    }
}