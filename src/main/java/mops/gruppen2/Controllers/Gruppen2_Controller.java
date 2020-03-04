package mops.gruppen2.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Gruppen2_Controller {

	@GetMapping("/")
	public String test(){
		return "index";
	}
}
