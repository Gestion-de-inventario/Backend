package com.comedor.backend.infrastructure.adapters.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class SecretController {

        @GetMapping
        public String health() {
            return "OK";
        }

}
