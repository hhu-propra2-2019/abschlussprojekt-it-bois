package mops.gruppen2.controller;

import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.PageNotFoundException;
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
    public String defaultLink() throws EventException {
        throw new PageNotFoundException("\uD83D\uDE41");
    }
}
