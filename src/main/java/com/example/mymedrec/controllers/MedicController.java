package com.example.mymedrec.controllers;

import com.example.mymedrec.models.Reteta;
import com.example.mymedrec.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
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

    public MedicController() {
        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, user.getNumeUtilizator());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        pacienti.add(new User(
                                rs.getString("PRENUME"),
                                rs.getString("NUME"),
                                rs.getString("NUMEUTILIZATOR"),
                                rs.getString("PAROLA"),
                                rs.getString("EMAIL"),
                                rs.getInt("ANNASTERE"),
                                rs.getInt("LUNANASTERE"),
                                rs.getInt("ZINASTERE")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("pacienti", pacienti);
        return "medic";
    }
    @GetMapping("/istoric/{numeUtilizator}")
    @ResponseBody
    public List<Reteta> getIstoricPacient(@PathVariable String numeUtilizator) {
        List<Reteta> istoric = new ArrayList<>();
        try {
            String query = "SELECT * FROM MEDRECRETETE WHERE NUMEPACIENT = ?";
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, numeUtilizator);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        istoric.add(new Reteta(
                                rs.getString("NUMEPACIENT"),
                                rs.getString("AFECTIUNE"),
                                rs.getString("TRATAMENT"),
                                rs.getString("STATUS"),
                                rs.getString("MEDICCONSTATATOR")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return istoric;
    }
}