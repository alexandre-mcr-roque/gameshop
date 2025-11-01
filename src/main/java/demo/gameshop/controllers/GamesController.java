package demo.gameshop.controllers;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import demo.gameshop.documents.Game;
import demo.gameshop.helpers.ModelMapper;
import demo.gameshop.models.GameDetails;
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
		model.addAttribute(ModelMapper.fromDocuments(games, GameDetails::new));
		return () -> "games/list";
	}

	// TODO : Implement game detail display logic and view
	// (view found in resources/templates/games/detail.html)
	@GetMapping("/{title}")
	public Callable<String> displayGame(
			@PathVariable("title") String titleNormalized,
			Model model) {
		// (get game from normalized name)
		Game game = new Game("Test Game");
		
		model.addAttribute("game", ModelMapper.fromDocument(game, GameDetails::new));
		return () -> "games/detail";
	}
}
