package com.example.mymedrec.controllers;

import com.example.mymedrec.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoggedController {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";

    private Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

    public LoggedController() throws SQLException {
    }

    @GetMapping("/logged")
    public String logged(
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

        try {
            // Verifică dacă utilizatorul curent este pacientul unui medic
            String queryPacient = "SELECT NUMEMEDIC FROM MEDRECPACIENTI WHERE NUMEPACIENT = ?";
            PreparedStatement psPacient = con.prepareStatement(queryPacient);
            psPacient.setString(1, user.getNumeUtilizator());
            ResultSet rsPacient = psPacient.executeQuery();

            if (rsPacient.next()) {
                String numeMedic = rsPacient.getString("NUMEMEDIC");
                model.addAttribute("inscris", true);
                model.addAttribute("numeMedic", numeMedic);
            } else {
                model.addAttribute("inscris", false);
            }

            // Obține toți medicii din baza de date
            String queryMedici = "SELECT * FROM MEDRECUSERS WHERE STATUT = 'medic'";
            PreparedStatement psMedici = con.prepareStatement(queryMedici);
            ResultSet rsMedici = psMedici.executeQuery();

            List<User> listaMedici = new ArrayList<>();
            while (rsMedici.next()) { User medic = new User(
                    rsMedici.getString("PRENUME"),
                    rsMedici.getString("NUME"),
                    rsMedici.getString("NUMEUTILIZATOR"),
                    rsMedici.getString("PAROLA"),
                    rsMedici.getString("EMAIL"),
                    rsMedici.getInt("ANNASTERE"),
                    rsMedici.getInt("LUNANASTERE"),
                    rsMedici.getInt("ZINASTERE")
            );
                listaMedici.add(medic);
            }

            model.addAttribute("listaMedici", listaMedici);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "logged";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        return "redirect:/login";
    }
}