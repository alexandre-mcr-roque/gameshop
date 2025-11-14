package demo.gameshop.controllers;

import demo.gameshop.documents.Game;
import demo.gameshop.documents.GameReview;
import demo.gameshop.helpers.ModelMapper;
import demo.gameshop.models.GameDetails;
import demo.gameshop.models.ReviewDetails;
import demo.gameshop.repositories.GameRepository;
import demo.gameshop.repositories.GameReviewRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

@Controller
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

	private final GameRepository gameRepository;
	private final GameReviewRepository gameReviewRepository;

	@GetMapping
	public Callable<String> listGames(Model model) {
		return () -> {
			List<Game> games = gameRepository.findAll();
			// Ensure the view receives the list under the name 'games'
			model.addAttribute(
                    "games",
                    ModelMapper.fromDocuments(games, GameDetails::new));
			return "games/list";
		};
	}

	@GetMapping("/{title}")
	public Callable<String> displayGame(
			@PathVariable("title") String titleNormalized,
			Model model) {
		return () -> {
            Optional<Game> gameOptional  = gameRepository.findByTitleNormalized(titleNormalized);
            if (gameOptional.isEmpty()) return "games/gameNotFound";
			Game game = gameOptional.get();
            // Get or create game review document
            String gameId = game.getId();
            Optional<GameReview> gameReviewOptional = gameReviewRepository.findById(gameId);
            GameReview gameReview = gameReviewOptional.orElse(gameReviewRepository.save(new GameReview(gameId)));
            // Add game details and rating to the model
            model.addAttribute("game", ModelMapper.fromDocument(game, GameDetails::new));
            model.addAttribute("rating", gameReview.getRating());
			return "games/detail";
		};
	}

    @ResponseBody
    @GetMapping("{id}/reviews")
    public List<ReviewDetails> getGameReviews(
            @PathVariable("id") String gameId,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            HttpServletResponse response) {
        // Get or create game review document
        Optional<GameReview> gameReviewOptional = gameReviewRepository.findById(gameId);
        GameReview gameReview = gameReviewOptional.orElse(gameReviewRepository.save(new GameReview(gameId)));
        Map<String, GameReview.UserGameReview> reviews = gameReview.getReviews();
        int size = reviews.size();
        // Set HTTP status to 204 No Content if there are no reviews
        if (size == 0) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return List.of();
        }
        // Set HTTP status to 206 Partial Content if it reaches the end of the reviews
        if (size <= offset + count) response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        return ModelMapper.fromDocuments(
                reviews.values().stream().skip(offset).limit(count).toList(),
                ReviewDetails::new);
    }
}
