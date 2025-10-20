package demo.gameshop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
	@GetMapping("/register")
	public String register(Model model) {
		return "register";
	}

	@GetMapping("/login")
	public String login(Model model) {
		return "login";
	}

}