package mops.gruppen2.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Gruppen2Controller {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
