package com.edupanel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping({"/","/index","index.html"})
    public String index() {
        return "index";
    }

    @GetMapping({"/informacion","/informacion.html"})
    public String informacion() {
        return "informacion";
    }

    @GetMapping("/pendiente")
    public String pendiente() {
        return "pendiente";
    }
    
    

}
