package ch.example.terminkalender.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    // Login-Seite anzeigen
    @GetMapping("/login")
    public String showLogin() {
        return "login";  // templates/login.html
    }

    // Registrierung-Seite anzeigen
    @GetMapping("/register")
    public String showRegister() {
        return "register";  // templates/register.html
    }

    // Login verarbeiten (Platzhalter)
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password, Model model) {
        // Hier würdest du User-Prüfung machen
        // Für Demo: einfach auf Startseite weiterleiten
        return "redirect:/";
    }

    // Registrierung verarbeiten (Platzhalter)
    @PostMapping("/register")
    public String processRegister(@RequestParam String username, @RequestParam String password, Model model) {
        // Hier würdest du User anlegen
        // Für Demo: einfach auf Login-Seite weiterleiten
        return "redirect:/login";
    }
}
