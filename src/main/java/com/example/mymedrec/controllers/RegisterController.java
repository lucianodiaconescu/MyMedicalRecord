package com.example.mymedrec.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class RegisterController {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";

    private Connection con;

    public RegisterController() {
        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
    @PostMapping("/register")
    public String processRegister(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam String birthYear, @RequestParam String birthMonth, @RequestParam String birthDay, ModelMap model) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM MEDRECUSERS WHERE NUMEUTILIZATOR=? or EMAIL=?");
        ps.setString(1, username);
        ps.setString(2, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            model.addAttribute("message", "Numele de utilizator sau email-ul sunt folosite deja.");
            return "register";
        } else {
            PreparedStatement ps1 = con.prepareStatement("INSERT INTO MEDRECUSERS VALUES (?,?,?,?,?,?,?,?,?)");
            ps1.setString(1, firstName);
            ps1.setString(2, lastName);
            ps1.setString(3, username);
            ps1.setString(4, password);
            ps1.setString(5, email);
            ps1.setString(6, birthYear);
            ps1.setString(7, birthMonth);
            ps1.setString(8, birthDay);
            ps1.setString(9, "pacient");
            if (ps1.executeUpdate() != 0)
                model.addAttribute("message", "Te-ai inregistrat cu succes!");
            ps.close();
            return "register";
        }
    }
}