package com.finsync.config;

import com.finsync.security.BlockCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final BlockCheckInterceptor blockCheckInterceptor;

    public WebConfig(BlockCheckInterceptor blockCheckInterceptor) {
        this.blockCheckInterceptor = blockCheckInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(blockCheckInterceptor)
                .excludePathPatterns("/auth/**", "/css/**", "/js/**", "/*.css", "/*.js");
    }
}
