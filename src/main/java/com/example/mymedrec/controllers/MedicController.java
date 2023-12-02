package com.example.mymedrec.controllers;

import com.example.mymedrec.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MedicController {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";

    private Connection con;

    public MedicController() throws SQLException {
        con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @GetMapping("/medic")
    public String medic(
            @SessionAttribute("user") User user,
            Model model
    ) {
        model.addAttribute("username", user.getNumeUtilizator());
        model.addAttribute("prenume", user.getPrenume());
        model.addAttribute("nume", user.getNume());
        model.addAttribute("parola", user.getParola());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("annastere", user.getAnNastere());
        model.addAttribute("lunanastere", user.getLunaNastere());
        model.addAttribute("zinastere", user.getZiNastere());

        List<User> pacienti = new ArrayList<>();
        try {
            String query = "SELECT * FROM MEDRECUSERS WHERE NUMEUTILIZATOR IN (SELECT NUMEPACIENT FROM MEDRECPACIENTI WHERE NUMEMEDIC = ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, user.getNumeUtilizator());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                pacienti.add(new User(
                        rs.getString("NUMEUTILIZATOR"),
                        rs.getString("PRENUME"),
                        rs.getString("NUME"),
                        rs.getString("PAROLA"),
                        rs.getString("EMAIL"),
                        rs.getInt("ANNASTERE"),
                        rs.getInt("LUNANASTERE"),
                        rs.getInt("ZINASTERE")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("pacienti", pacienti);
        return "medic";
    }
}