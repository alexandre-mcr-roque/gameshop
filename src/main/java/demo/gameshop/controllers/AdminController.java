package demo.gameshop.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import demo.gameshop.documents.Game;
import demo.gameshop.documents.GameReview;
import demo.gameshop.documents.User;
import demo.gameshop.helpers.ModelMapper;
import demo.gameshop.helpers.PasswordGenerator;
import demo.gameshop.models.GameDetails;
import demo.gameshop.models.GameForm;
import demo.gameshop.models.UserDetails;
import demo.gameshop.models.UserForm;
import demo.gameshop.repositories.GameRepository;
import demo.gameshop.repositories.GameReviewRepository;
import demo.gameshop.repositories.UserRepository;
import demo.gameshop.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final GameRepository gameRepository;
	private final GameReviewRepository gameReviewRepository;
	private final UserRepository userRepository;
	private final FileService fileService;
	private final PasswordEncoder passwordEncoder;
	
	// TODO : Make admin panel (games CRUD, user management)
	@GetMapping
	public String adminPanel() {
		return "admin/panel";
	}
	
	//===============================================
	// Users CRUD
	//===============================================
	@GetMapping("/users")
	public Callable<String> listUsers(Model model) {
		return () -> {
			List<User> users = userRepository.findAll();
			model.addAttribute(
					"users",
					ModelMapper.fromDocuments(users, UserDetails::new));
			return "admin/listUsers";
		};
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		model.addAttribute("userForm", new UserForm());
		return "admin/newUser";
	}
	
	@PostMapping("/users/new")
	public Callable<String> newUserSubmit(
			@Valid @ModelAttribute UserForm userForm,
			BindingResult bindingResult,
			Model model) {
		return () -> {
			// If any field-level validation failed, return to form and display errors
			if (bindingResult.hasErrors()) {
				model.addAttribute("userForm", userForm);
				return "admin/newUser";
			}
			User user = new User(userForm.getUsername(),
								 userForm.getEmail(),
								 passwordEncoder.encode(PasswordGenerator.generatePassword()),
								 userForm.getRoles());
			// Check if user already exists
			if (userRepository.findByUsername(user.getUsername()).isPresent()) {
				bindingResult.rejectValue("username", "username.exists", "Username already exists");
				model.addAttribute("userForm", userForm);
				return "admin/newUser";
			}
			if (userRepository.findByEmail(user.getEmail()).isPresent()) {
				bindingResult.rejectValue("email", "email.exists", "Email already exists");
				model.addAttribute("userForm", userForm);
				return "admin/newUser";
			}
			// Set values and save
			user.setFirstName(userForm.getFirstName());
			user.setLastName(userForm.getLastName());
			user.setDateOfBirth(userForm.getDateOfBirth());
			user.setPhoneNumber(userForm.getPhoneNumber());
			userRepository.save(user);
			//=====================================================
			// In case of error
			//model.addAttribute("userForm", userForm);
			//=====================================================
			return "redirect:/admin/users";
		};
	}
	
	@GetMapping("/users/edit/{id}")
	public Callable<String> editUser(
			@PathVariable String id,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
			Model model) {
		return () -> {
			// Check if user exists
			Optional<User> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) return "admin/userNotFound";
			User user = userOptional.get();
			// Prevent editing itself
			if (userDetails.getUsername().equals(user.getUsername())) return "admin/userNotFound";
			model.addAttribute(
					"userForm",
					ModelMapper.fromDocument(user, UserForm::new));
			return "admin/editUser";
		};
	}
	
	@PostMapping("/users/edit/{id}")
	public Callable<String> editUserSubmit(
			@PathVariable String id,
			@Valid @ModelAttribute UserForm userForm,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
			BindingResult bindingResult,
			Model model) {
		return () -> {
			// Check if user exists
			Optional<User> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) return "admin/userNotFound";
			User user = userOptional.get();
			// Check if found user coincides with form
			if (!user.getUsername().equals(userForm.getUsername())
				|| !user.getEmail().equals(userForm.getEmail()))
				return "admin/userNotFound";
			// Prevent editing itself
			if (userDetails.getUsername().equals(user.getUsername()))
				return "admin/userNotFound";
			// Set id in form as it is not sent on POST
			userForm.setId(id);
			// If any field-level validation failed, return to form and display errors
			if (bindingResult.hasErrors()) {
				model.addAttribute("userForm", userForm);
				return "admin/editUser";
			}
			// Set values and save	
			user.setFirstName(userForm.getFirstName());
			user.setLastName(userForm.getLastName());
			user.setDateOfBirth(userForm.getDateOfBirth());
			user.setAddress(userForm.getAddress());
			user.setRoles(userForm.getRoles());
			userRepository.save(user);
			
			return "redirect:/admin/users";
		};
	}
	
	@PostMapping("/users/disable/{id}")
	public void disableUser(
			@PathVariable String id,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
			HttpServletResponse response) {
		// Check if user exists
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		User user = userOptional.get();
		// Prevent disabling itself
		if (userDetails.getUsername().equals(user.getUsername())) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		user.setDisabled(true);
		try {
			userRepository.save(user);
		}
		catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
	
	@PostMapping("/users/enable/{id}")
	public void enableUser(
			@PathVariable String id,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
			HttpServletResponse response) {
		// Check if user exists
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		User user = userOptional.get();
		// Prevent enabling itself
		if (userDetails.getUsername().equals(user.getUsername())) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		user.setDisabled(false);
		try {
			userRepository.save(user);
		}
		catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
	
	@DeleteMapping("/users/{id}")
	public void deleteUser(
			@PathVariable String id,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
			HttpServletResponse response) {
		// Check if user exists
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		User user = userOptional.get();
		// Prevent deleting itself
		if (userDetails.getUsername().equals(user.getUsername())) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		try {
			userRepository.delete(user);
		}
		catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
	
	//===============================================
	// Games CRUD
	//===============================================
	@GetMapping("/games")
	public Callable<String> listGames(Model model) {
		return () -> {
			List<Game> games = gameRepository.findAll();
			model.addAttribute(
					"games",
					ModelMapper.fromDocuments(games, GameDetails::new));
			return "admin/listGames";
		};
	}
	
	@GetMapping("/games/new")
	public String newGame(Model model) {
		model.addAttribute("gameForm", new GameForm());
		return "admin/newGame";
	}
	
	@PostMapping("/games/new")
	public Callable<String> newGameSubmit(
			@Valid @ModelAttribute GameForm gameForm,
			BindingResult bindingResult,
			Model model) {
		return () -> {
			// If any field-level validation failed, return to form and display errors
			if (bindingResult.hasErrors()) {
				model.addAttribute("gameForm", gameForm);
				return "admin/newGame";
			}
			Game game = new Game(gameForm.getTitle());
			// Check if game already exists
			if (gameRepository.findByTitleNormalized(game.getTitleNormalized()).isPresent()) {
				bindingResult.rejectValue("name", "name.exists", "Game already exists");
				model.addAttribute("gameForm", gameForm);
				return "admin/newGame";
			}
			// Set values and save
			game.setGenre(gameForm.getGenre());
			saveAndSetImage(gameForm, game);
			game = gameRepository.save(game);
			gameReviewRepository.save(new GameReview(game.getId()));
			//=====================================================
			// In case of error
			//model.addAttribute("gameForm", gameForm);
			//=====================================================
			return "redirect:/admin/games";
		};
	}
	
	@GetMapping("/games/edit/{id}")
	public Callable<String> editGame(
			@PathVariable String id,
			Model model) {
		return () -> {
			// Check if game exists
			Optional<Game> gameOptional = gameRepository.findById(id);
			if (gameOptional.isEmpty()) return "admin/gameNotFound";
			Game game = gameOptional.get();
			
			model.addAttribute(
					"gameForm",
					ModelMapper.fromDocument(game, GameForm::new));
			return "admin/editGame";
		};
	}
	
	@PostMapping("/games/edit/{id}")
	public Callable<String> editGameSubmit(
			@PathVariable String id,
			@Valid @ModelAttribute GameForm gameForm,
			BindingResult bindingResult,
			Model model) {
		return () -> {
			// Check if game exists
			Optional<Game> gameOptional = gameRepository.findById(id);
			if (gameOptional.isEmpty()) return "admin/gameNotFound";
			Game game = gameOptional.get();
			// Check if found game coincides with form
			if (!game.getTitleNormalized().equals(gameForm.getTitleNormalized()))
				return "admin/gameNotFound";
			// Set id in form as it is not sent on POST
			gameForm.setId(id);
			// If any field-level validation failed, return to form and display errors
			if (bindingResult.hasErrors()) {
				model.addAttribute("gameForm", gameForm);
				return "admin/editGame";
			}
			// Set values and save
			game.setTitle(gameForm.getTitle());
			game.setGenre(gameForm.getGenre());
			saveAndSetImage(gameForm, game);
			gameRepository.save(game);
			
			return "redirect:/admin/games";
		};
	}
	
	@PostMapping("/games/disable/{id}")
	public void disableGame(
			@PathVariable String id,
			HttpServletResponse response) {
		response.setStatus(HttpStatus.NOT_IMPLEMENTED.value());
	}
	
	@PostMapping("/games/enable/{id}")
	public void enableGame(
			@PathVariable String id,
			HttpServletResponse response) {
		response.setStatus(HttpStatus.NOT_IMPLEMENTED.value());
	}
	
	@DeleteMapping("/games/{id}")
	public void deleteGame(
			@PathVariable String id,
			HttpServletResponse response) {
		// Check if game exists
		Optional<Game> gameOptional = gameRepository.findById(id);
		if (gameOptional.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		Game game = gameOptional.get();
		try {
			String imageUrl = game.getImageUrl();
			if (imageUrl != null && !imageUrl.isEmpty()) {
				fileService.deleteFile(imageUrl.substring(8));
			}
			gameRepository.delete(game);
		}
		catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
	
	private boolean saveAndSetImage(GameForm gameForm, Game game) {
		if (gameForm.getImageFile().isEmpty()) {
			return false;
		}
		// Build new image file name
		MultipartFile file = gameForm.getImageFile();
		String ogFileName = file.getOriginalFilename();
		StringBuilder fileName = new StringBuilder(game.getTitleNormalized());
        int extensionIdx = ogFileName.lastIndexOf(".");
		if (extensionIdx == -1) { // Has no file name extension
        	switch (file.getContentType()) {
        	case MediaType.IMAGE_PNG_VALUE:
        		fileName.append(".png");
        		break;
        	case MediaType.IMAGE_GIF_VALUE:
        		fileName.append(".gif");
        		break;
        	default: // Default to .jpeg extension
        		fileName.append(".jpeg");
        		break;
        	}
        } else fileName.append(ogFileName.substring(extensionIdx));
		// Save image
		try {
			String id = fileService.addFile(
					file,
					fileName.toString());
			// Successfully added new image, delete old image if it exists
			String ogImageUrl = game.getImageUrl();
			if (ogImageUrl != null && !ogImageUrl.isEmpty()) {
				fileService.deleteFile(ogImageUrl.substring(8));
			}
			game.setImageUrl("/images/"+id);
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
