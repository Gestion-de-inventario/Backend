package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
public class SecretController {

        @GetMapping
        public String health() {

            return PeruTime.now() +" OK";
        }

}
