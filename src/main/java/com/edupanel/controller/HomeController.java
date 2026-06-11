package com.edupanel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/pendiente")
    public String pendiente() {
        return "pendiente";
    }
    
    

}
