package com.edupanel.controller;

import com.edupanel.model.UsuarioPendiente;
import com.edupanel.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("nuevoUsuario", new UsuarioPendiente());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute UsuarioPendiente nuevoUsuario, Model model) {
        try {
            usuarioService.registrarUsuarioPendiente(nuevoUsuario);
            return "redirect:/pendiente";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nuevoUsuario", nuevoUsuario);
            return "registro";
        }
    }
}