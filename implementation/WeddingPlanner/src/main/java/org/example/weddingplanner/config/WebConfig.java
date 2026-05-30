package org.example.weddingplanner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the local "uploads" directory to be accessible via the /uploads/** URL pattern
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
