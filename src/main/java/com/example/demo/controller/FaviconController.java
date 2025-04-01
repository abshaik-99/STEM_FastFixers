package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class FaviconController {
    @GetMapping("favicon.ico")
    @ResponseBody
    void disableFavicon() {
        // Do nothing and avoid error
    }
}
