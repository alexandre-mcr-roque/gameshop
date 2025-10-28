package demo.gameshop.controllers;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;

import demo.gameshop.entities.User;
import demo.gameshop.models.LoginForm;
import demo.gameshop.models.RegisterForm;
import demo.gameshop.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("registerForm", new RegisterForm());
		return "account/register";
	}

	@PostMapping("/register")
	public String registerSubmit(
			@Valid @ModelAttribute RegisterForm registerForm,
			BindingResult bindingResult,
			Model model) {
		// If any field-level validation failed, return to form and display errors
		if (bindingResult.hasErrors()) {
			model.addAttribute("registerForm", registerForm);
			return "account/register";
		}

		// Check username uniqueness
		if (userRepository.findByUsername(registerForm.getUsername()).isPresent()) {
			bindingResult.rejectValue("username", "username.exists", "Username already exists");
			model.addAttribute("registerForm", registerForm);
			return "account/register";
		}

		User user = new User(registerForm.getUsername(),
					 passwordEncoder.encode(registerForm.getPassword()),
					 "USER");
		user.setEmail(registerForm.getEmail());
		user.setFirstName(registerForm.getFirstName());
		user.setLastName(registerForm.getLastName());
		user.setDateOfBirth(registerForm.getDateOfBirth());
		user.setPhoneNumber(registerForm.getPhoneNumber());
		user.setAddress(registerForm.getAddress());
		userRepository.save(user);
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("loginForm", new LoginForm());
		return "account/login";
	}
	
	@GetMapping("/profile")
	public String profile(
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {
		Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			model.addAttribute("user", user);
		}
		return "account/profile";
	}
	
	// Utility endpoint to create a test user
	@GetMapping("/create-test-user")
	public String createTestUser(Model model) {
		if (userRepository.findByUsername("testuser").isPresent()) {
			return "redirect:/login";
		}
		User user = new User("testuser",
							 passwordEncoder.encode("password"),
							 "USER");
		user.setFirstName("Test");
		user.setLastName("User");
		user.setEmail("test.user@testmail.com");
		user.setDateOfBirth(LocalDate.of(1990, 1, 1));
		user.setPhoneNumber("123-456-7890");
		userRepository.save(user);
		return "redirect:/login";
	}
}