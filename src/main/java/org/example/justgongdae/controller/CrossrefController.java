package org.example.justgongdae.controller;

import org.example.justgongdae.data.Paper;
import org.example.justgongdae.data.crossref.CrossrefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrossrefController {

    private final CrossrefRepository crossrefRepository;

    @Autowired
    public CrossrefController(CrossrefRepository crossrefRepository) {
        this.crossrefRepository = crossrefRepository;
    }

    @GetMapping("/")
    public String showPaper(Model model) {
        Paper paper = crossrefRepository.fetchRandomPaper();
        if (paper != null) {
            model.addAttribute("paper", paper);
            return "index";
        } else {
            return "error";
        }
    }
}
