package com.example.mymedrec.controllers;

import com.example.mymedrec.models.MedicSelectForm;
import com.example.mymedrec.models.Reteta;
import com.example.mymedrec.models.SimptomeSelectForm;
import com.example.mymedrec.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Controller
public class LoggedController {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "db";
    private static final String OPENAI_API_KEY = "";

    private final Connection con;

    public LoggedController() throws SQLException {
        this.con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
            String queryRetete = "SELECT * FROM MEDRECRETETE WHERE NUMEPACIENT = ?";
            PreparedStatement psRetete = con.prepareStatement(queryRetete);
            psRetete.setString(1, user.getNumeUtilizator());
            ResultSet rsRetete = psRetete.executeQuery();

            List<Reteta> listaRetete = new ArrayList<>();
            while (rsRetete.next()) {
                Reteta reteta = new Reteta(
                        rsRetete.getString("NUMEPACIENT"),
                        rsRetete.getString("AFECTIUNE"),
                        rsRetete.getString("TRATAMENT"),
                        rsRetete.getString("STATUS"),
                        rsRetete.getString("MEDICCONSTATATOR")
                );
                listaRetete.add(reteta);
            }

            model.addAttribute("listaRetete", listaRetete);
            model.addAttribute("medicSelectForm", new MedicSelectForm());
            String queryPacient = "SELECT NUMEMEDIC FROM MEDRECPACIENTI WHERE NUMEPACIENT = ?";
            PreparedStatement psPacient = con.prepareStatement(queryPacient);
            psPacient.setString(1, user.getNumeUtilizator());
            ResultSet rsPacient = psPacient.executeQuery();
            if (rsPacient.next()) {
                String numeMedic = rsPacient.getString("NUMEMEDIC");
                model.addAttribute("inscris", true);
                model.addAttribute("numeMedic", numeMedic);
                model.addAttribute("showExcludeButton", true);
            } else {
                model.addAttribute("inscris", false);
                model.addAttribute("showExcludeButton", false);

                String queryMedici = "SELECT * FROM MEDRECUSERS WHERE STATUT = 'medic'";
                PreparedStatement psMedici = con.prepareStatement(queryMedici);
                ResultSet rsMedici = psMedici.executeQuery();

                List<User> listaMedici = new ArrayList<>();
                while (rsMedici.next()) {
                    User medic = new User(
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

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "logged";
    }
    @PostMapping("/alege-medic")
    public String alegeMedic(
            @SessionAttribute("user") User user,
            @ModelAttribute("medicSelectForm") MedicSelectForm medicSelectForm,
            Model model
    ) {
        try {
            String queryInsert = "INSERT INTO MEDRECPACIENTI (NUMEPACIENT, NUMEMEDIC) VALUES (?, ?)";
            PreparedStatement psInsert = con.prepareStatement(queryInsert);
            psInsert.setString(1, user.getNumeUtilizator());
            psInsert.setString(2, medicSelectForm.getMedic());
            psInsert.executeUpdate();

            return "redirect:/logged";
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/login";
    }

    @RequestMapping("/exclude-medic")
    public String excludeMedic(
            @SessionAttribute("user") User user,
            Model model
    ) {
        try {
            String queryDelete = "DELETE FROM MEDRECPACIENTI WHERE NUMEPACIENT = ?";
            PreparedStatement psDelete = con.prepareStatement(queryDelete);
            psDelete.setString(1, user.getNumeUtilizator());
            psDelete.executeUpdate();

            return "redirect:/logged";
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/login";
    }
    @GetMapping("/logout")
    public String logout(Model model) {
        return "redirect:/login";
    }
    @PostMapping("/chat")
    @ResponseBody
    public String chat(@RequestBody String prompt) {
        try {
            String openaiEndpoint = "https://api.openai.com/v1/chat/completions";
            HttpClient client = HttpClient.newHttpClient();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBodyMap = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(
                            Map.of("role", "system", "content", prompt)
                    )
            );
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(openaiEndpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + OPENAI_API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Error: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
                return "Error: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}