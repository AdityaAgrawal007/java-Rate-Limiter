package com.cerberus.rateLimiter;

import com.cerberus.rateLimiter.core.RateLimiter;
import com.cerberus.rateLimiter.core.RateLimiterImpl;
import com.cerberus.rateLimiter.extractor.IpKeyExtractor;
import com.cerberus.rateLimiter.interceptor.RateLimitInterceptor;
import com.cerberus.rateLimiter.store.InMemoryStateStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class RateLimiterConfig implements WebMvcConfigurer {

    @Bean
    public RateLimiter rateLimiter() {
        return new RateLimiterImpl(10, Duration.ofMinutes(1), new InMemoryStateStore());
    }

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor(rateLimiter(), new IpKeyExtractor());
    }

    // this method register interceptor with spring, and hence every incomming request is passed to interceptor
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor());
    }
}