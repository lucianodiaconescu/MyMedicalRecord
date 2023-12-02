package com.example.mymedrec.controllers;

import com.example.mymedrec.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
@SessionAttributes("user")
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
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            ModelMap model
    ) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM MEDRECUSERS WHERE NUMEUTILIZATOR=? AND PAROLA=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getString("PRENUME"),
                        rs.getString("NUME"),
                        username,
                        password,
                        rs.getString("EMAIL"),
                        rs.getInt("ANNASTERE"),
                        rs.getInt("LUNANASTERE"),
                        rs.getInt("ZINASTERE")
                );
                model.addAttribute("user", user);
                model.addAttribute("username", user.getNumeUtilizator());
                model.addAttribute("prenume", user.getPrenume());
                model.addAttribute("nume", user.getNume());
                model.addAttribute("parola", user.getParola());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("annastere", user.getAnNastere());
                model.addAttribute("lunanastere", user.getLunaNastere());
                model.addAttribute("zinastere", user.getZiNastere());
                if (rs.getString("STATUT").equalsIgnoreCase("pacient"))
                    return "redirect:/logged";
                else return "redirect:/medic";
            } else {
                model.addAttribute("message", "Numele de utilizator sau parola sunt incorecte!");
                return "login";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("message", "Eroare la autentificare.");
            return "login";
        }
    }
}
