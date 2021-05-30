package com.scopic.auction.config;

import com.scopic.auction.api.RequestHeaderInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final RequestHeaderInterceptor requestHeaderInterceptor;

    @Autowired
    public InterceptorConfiguration(RequestHeaderInterceptor requestHeaderInterceptor) {
        this.requestHeaderInterceptor = requestHeaderInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestHeaderInterceptor);
    }

}