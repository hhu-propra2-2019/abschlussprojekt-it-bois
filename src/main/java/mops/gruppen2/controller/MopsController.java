package mops.gruppen2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MopsController {

    @GetMapping("")
    public String redirect() {
        return "redirect:/gruppen2/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        request.logout();
        return "redirect:/gruppen2/";
    }

    @GetMapping("*")
    public String defaultLink() {
        return "error";
    }
}
