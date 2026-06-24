package com.edupanel.controller;

import com.edupanel.auth.AuthService;
import com.edupanel.auth.RegistroUsuarioService;
import com.edupanel.auth.SesionUsuario;
import com.edupanel.model.UsuarioPendiente;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final RegistroUsuarioService registroUsuarioService;
    private final AuthService authService;

    public AuthController(RegistroUsuarioService registroUsuarioService, AuthService authService) {
        this.registroUsuarioService = registroUsuarioService;
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        try {
            SesionUsuario sesionUsuario = authService.autenticar(email, password);

            session.setAttribute(AuthService.SESION_USUARIO, sesionUsuario);

            return "redirect:" + authService.obtenerRutaPrincipal(sesionUsuario);

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("nuevoUsuario", new UsuarioPendiente());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute UsuarioPendiente nuevoUsuario, Model model) {
        try {
            registroUsuarioService.registrarUsuarioPendiente(nuevoUsuario);
            return "redirect:/pendiente";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nuevoUsuario", nuevoUsuario);
            return "registro";
        }
    }
}
