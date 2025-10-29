package demo.gameshop.controllers;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import demo.gameshop.entities.Game;
import demo.gameshop.repositories.GameRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/games")
@RequiredArgsConstructor
public class GamesController {

	private final GameRepository gameRepository;
	
	// TODO : Complete view
	// (view found in resources/templates/games/list.html)
	@GetMapping
	public Callable<String> listGames(Model model) {
		List<Game> games = gameRepository.findAll();
		model.addAttribute(games);
		return () -> "games/list";
	}

	// TODO : Implement game detail display logic and view
	// (view found in resources/templates/games/detail.html)
	@GetMapping("/{name-normalized}")
	public Callable<String> displayGame(
			@PathVariable("name-normalized") String name,
			Model model) {
		// (get game from normalized name)
		
		// (store values in model)
		
		return () -> "games/detail";
	}
}
