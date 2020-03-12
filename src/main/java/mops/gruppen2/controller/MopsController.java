package mops.gruppen2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MopsController {

    @GetMapping("")
    public String redirect(){
        return "redirect:/gruppen2/";
    }
}
