package com.example.mymedrec.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class LoginController {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";

    private Connection con;

    public LoginController() {
        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM MEDRECUSERS WHERE NUMEUTILIZATOR=? AND PAROLA=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Utilizator autentificat: " + username);
                // Aici poți adăuga logica adițională după autentificare, de exemplu, să salvezi username într-o variabilă de sesiune
                return "redirect:/home";
            } else {
                System.out.println("Nume de utilizator sau parolă incorecte.");
                return "redirect:/login";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Eroare la autentificare.");
            return "redirect:/login";
        }
    }
}
