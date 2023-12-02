package com.example.mymedrec.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class LoggedController {

    @GetMapping("/logged")
    public String logged(@SessionAttribute("username") String username, Model model) {
        model.addAttribute("username", username);
        return "logged";
    }
}