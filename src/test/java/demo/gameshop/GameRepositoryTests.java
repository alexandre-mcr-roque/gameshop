package demo.gameshop;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import demo.gameshop.documents.Game;
import demo.gameshop.repositories.GameRepository;

@DataMongoTest
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
	public void testNameNormalizer() {
		String normalizedName = Game.normalizeName("Test Game");
		assertThat(normalizedName).isEqualTo("test-game");
		
		normalizedName = Game.normalizeName("Test_Game-2!?");
		assertThat(normalizedName).isEqualTo("test-game-2");
		
		normalizedName = Game.normalizeName("Tést Gãme");
		assertThat(normalizedName).isEqualTo("test-game");
		
		normalizedName = Game.normalizeName("Test&Game");
		assertThat(normalizedName).isEqualTo("test-and-game");
		normalizedName = Game.normalizeName("Test & Game");
		assertThat(normalizedName).isEqualTo("test-and-game");

		normalizedName = Game.normalizeName("    Test Game");
		assertThat(normalizedName).isEqualTo("test-game");
		normalizedName = Game.normalizeName("Test Game			");
		assertThat(normalizedName).isEqualTo("test-game");
		normalizedName = Game.normalizeName("     Test\nGame			");
		assertThat(normalizedName).isEqualTo("test-game");
		
		normalizedName = Game.normalizeName("Test+Game!\"@#$€^~*¨'?+/\\[](){}");
		assertThat(normalizedName).isEqualTo("test-plus-game-plus");
		normalizedName = Game.normalizeName("Test+Game!\"@#$€^~*¨&'?&+/\\[](){}");
		assertThat(normalizedName).isEqualTo("test-plus-game-and-and-plus");
		
		normalizedName = Game.normalizeName("Alex's Test");
		assertThat(normalizedName).isEqualTo("alex-s-test");
		normalizedName = Game.normalizeName("Alex´s Test");
		assertThat(normalizedName).isEqualTo("alex-s-test");
	}
}
