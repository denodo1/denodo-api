package com.denodo.platform.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.upload.serve-static:false}")
    private boolean serveStatic;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!serveStatic) return;

        String location = "file:///" + uploadDir.replace("\\", "/") + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
