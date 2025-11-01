package demo.gameshop;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import demo.gameshop.documents.Game;
import demo.gameshop.repositories.GameRepository;

@DataMongoTest
@ActiveProfiles(profiles = {"test"})
public class GameRepositoryTests {
	
	@Autowired
	private GameRepository gameRepository;

	@BeforeEach
	public void setUp() {
		gameRepository.deleteAll();
	}
	
	// TODO : Game repository tests (insert, update, delete)
	// (on update test make sure to check if 
	//  updated normalized name already exists)
	
	@Test
	public void testTitleNormalizer() {
		String normalizedTitle = Game.normalizeTitle("Test Game");
		assertThat(normalizedTitle).isEqualTo("test-game");
		
		normalizedTitle = Game.normalizeTitle("Test_Game-2!?");
		assertThat(normalizedTitle).isEqualTo("test-game-2");
		
		normalizedTitle = Game.normalizeTitle("Tést Gãme");
		assertThat(normalizedTitle).isEqualTo("test-game");
		
		normalizedTitle = Game.normalizeTitle("Test&Game");
		assertThat(normalizedTitle).isEqualTo("test-and-game");
		normalizedTitle = Game.normalizeTitle("Test & Game");
		assertThat(normalizedTitle).isEqualTo("test-and-game");

		normalizedTitle = Game.normalizeTitle("    Test Game");
		assertThat(normalizedTitle).isEqualTo("test-game");
		normalizedTitle = Game.normalizeTitle("Test Game			");
		assertThat(normalizedTitle).isEqualTo("test-game");
		normalizedTitle = Game.normalizeTitle("     Test\nGame			");
		assertThat(normalizedTitle).isEqualTo("test-game");
		
		normalizedTitle = Game.normalizeTitle("Test+Game!\"@#$€^~*¨'?+/\\[](){}");
		assertThat(normalizedTitle).isEqualTo("test-plus-game-plus");
		normalizedTitle = Game.normalizeTitle("Test+Game!\"@#$€^~*¨&'?&+/\\[](){}");
		assertThat(normalizedTitle).isEqualTo("test-plus-game-and-and-plus");
		
		normalizedTitle = Game.normalizeTitle("Alex's Test");
		assertThat(normalizedTitle).isEqualTo("alex-s-test");
		normalizedTitle = Game.normalizeTitle("Alex´s Test");
		assertThat(normalizedTitle).isEqualTo("alex-s-test");
	}
}
