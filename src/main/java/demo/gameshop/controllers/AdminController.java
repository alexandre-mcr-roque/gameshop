package demo.gameshop.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import demo.gameshop.documents.Game;
import demo.gameshop.documents.User;
import demo.gameshop.helpers.ModelMapper;
import demo.gameshop.models.GameDetails;
import demo.gameshop.models.GameForm;
import demo.gameshop.models.UserDetails;
import demo.gameshop.repositories.GameRepository;
import demo.gameshop.repositories.UserRepository;
import demo.gameshop.services.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final GameRepository gameRepository;
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
	
	public Callable<String> listUsers(Model model) {
		List<User> users = userRepository.findAll();
		model.addAttribute(
				"users",
				ModelMapper.fromDocuments(users, UserDetails::new));
		return () -> "admin/listUsers";
	}
	
	//===============================================
	// Games CRUD
	//===============================================
	@GetMapping("/games")
	public Callable<String> listGames(Model model) {
		List<Game> games = gameRepository.findAll();
		model.addAttribute(
				"games",
				ModelMapper.fromDocuments(games, GameDetails::new));
		return () -> "admin/listGames";
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
		// If any field-level validation failed, return to form and display errors
		if (bindingResult.hasErrors()) {
			model.addAttribute("registerForm", gameForm);
			return () -> "admin/newGame";
		}
		Game game = new Game(gameForm.getTitle());
		if (gameRepository.findByTitleNormalized(game.getTitleNormalized()).isPresent()) {
			bindingResult.rejectValue("name", "name.exists", "Game already exists");
			model.addAttribute("gameForm", gameForm);
			return () -> "admin/newGame";
		}
		game.setGenre(gameForm.getGenre());
		saveAndSetImage(gameForm, game);
		gameRepository.save(game);
		
		//=====================================================
		// In case of error
		//model.addAttribute("gameForm", gameForm);
		//=====================================================
		return () -> "redirect:/admin/games";
	}
	
	@GetMapping("/games/edit/{title}")
	public Callable<String> editGame(
			@PathVariable("title") String nameNormalized,
			Model model) {
		// Check if game exists
		Optional<Game> gameOptional = gameRepository.findByTitleNormalized(nameNormalized);
		if (gameOptional.isEmpty()) return () -> "admin/gameNotFound";
		Game game = gameOptional.get();
		
		model.addAttribute(
				"gameForm",
				ModelMapper.fromDocument(game, GameForm::new));
		return () -> "admin/editGame";
	}
	
	@PostMapping("/games/edit/{title}")
	public Callable<String> editGameSubmit(
			@PathVariable("title") String titleNormalized,
			@Valid @ModelAttribute GameForm gameForm,
			BindingResult bindingResult,
			Model model) {
		// Check if game exists
		Optional<Game> gameOptional = gameRepository.findByTitleNormalized(titleNormalized);
		if (gameOptional.isEmpty()) return () -> "admin/gameNotFound";
		Game game = gameOptional.get();
		// Normalized title is not sent on post, reset it
		gameForm.setTitleNormalized(game.getTitleNormalized());
		
		// If any field-level validation failed, return to form and display errors
		if (bindingResult.hasErrors()) {
			gameForm.setTitleNormalized(game.getTitleNormalized());
			model.addAttribute("registerForm", gameForm);
			return () -> "admin/newGame";
		}
				
		game.setTitle(gameForm.getTitle());
		game.setGenre(gameForm.getGenre());
		saveAndSetImage(gameForm, game);
		gameRepository.save(game);
		return () -> "redirect:/admin/games";
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
			if (ogImageUrl != null) {
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
