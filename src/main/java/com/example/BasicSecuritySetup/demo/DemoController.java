package com.example.BasicSecuritySetup.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    DemoService demoService;

    @GetMapping("/test")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello from secured page");
    }


}
