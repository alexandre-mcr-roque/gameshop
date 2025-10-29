package demo.gameshop.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import demo.gameshop.repositories.GameRepository;
import demo.gameshop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final GameRepository gameRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	// TODO : Make admin panel (games CRUD, user management)
	@GetMapping
	public String adminPanel() {
		return "admin/panel";
	}
}
