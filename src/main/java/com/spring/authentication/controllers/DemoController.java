package com.spring.authentication.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/v1")
public class DemoController {
    @GetMapping(value = "/demo_controller")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello World");
    }
}
