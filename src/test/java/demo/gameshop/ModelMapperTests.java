package demo.gameshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import demo.gameshop.documents.Game;
import demo.gameshop.helpers.ModelMapper;
import demo.gameshop.models.GameDetails;
import demo.gameshop.repositories.GameRepository;

@DataMongoTest
@ActiveProfiles(profiles = {"test"})
@TestInstance(Lifecycle.PER_CLASS)
public class ModelMapperTests {

	@Autowired
	private GameRepository gameRepository;
	private Game game;

	@BeforeAll
	public void createDocument() {
		gameRepository.deleteAll();
		game = new Game("Test Game");
		game = gameRepository.save(game);
	}
	
	private final class GameDetailsId extends GameDetails {
		private String id;
		
		public String getId() { return id; }
		
		@Override
		public GameDetailsId mapper(Game doc) {
			super.mapper(doc);
			this.id = doc.getId();
			return this;
		}
	}
	
	public void testFromDocument() {
		// Should throw NullPointerException due to document being nutll
		assertThrowsExactly(NullPointerException.class,
				() -> ModelMapper.fromDocument(null, GameDetails::new));
		// Should map the data
		GameDetails details = ModelMapper.fromDocument(game, GameDetails::new);
		assertThat(details.getTitle().equals(game.getTitle())
				&& details.getTitleNormalized().equals(game.getTitleNormalized()))
		.isTrue();
		// Should map the data from itself and it's inherited class
		GameDetailsId form = (GameDetailsId) ModelMapper.fromDocument(game, GameDetailsId::new);
		assertThat(form.getTitle().equals(game.getTitle())
				&& form.getTitleNormalized().equals(game.getTitleNormalized())
				&& form.getId().equals(game.getId()))
		.isTrue();
	}
	
	@Test
	public void testFromDocuments() {
		List<Game> games = List.of(
				game,
				new Game("Test Game 2"),
				new Game("Test Game 3"));
		final int SIZE = games.size();
		
		// Should map all data
		List<GameDetails> detailz = ModelMapper.fromDocuments(games, GameDetails::new);
		assertThat(detailz.size()).isEqualTo(SIZE);
		for (int i = 0; i < SIZE; i++) {
			GameDetails details = detailz.get(i);
			Game game = games.get(i);
			assertThat(details.getTitle().equals(game.getTitle())
					&& details.getTitleNormalized().equals(game.getTitleNormalized()));
		}
		// Should map all data
		Game[] gamesArr = games.toArray(new Game[0]);
		detailz = ModelMapper.fromDocuments(gamesArr, GameDetails::new);
		assertThat(detailz.size()).isEqualTo(3);
		for (int i = 0; i < SIZE; i++) {
			GameDetails details = detailz.get(i);
			Game game = gamesArr[i];
			assertThat(details.getTitle().equals(game.getTitle())
					&& details.getTitleNormalized().equals(game.getTitleNormalized()));
		}
		// Should throw NullPointerException
		gamesArr[SIZE / 2] = null;
		assertThatExceptionOfType(NullPointerException.class)
			.isThrownBy(() -> ModelMapper.fromDocuments(gamesArr, GameDetails::new));
	}
}
