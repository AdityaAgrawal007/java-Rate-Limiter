package com.cerberus.rateLimiter;

import com.cerberus.rateLimiter.core.RateLimiter;
import com.cerberus.rateLimiter.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/gime_tad_good_stuff")
    String getResourceAPI() {
        String resource = "good_stuff";
        return resource;
    }
}
