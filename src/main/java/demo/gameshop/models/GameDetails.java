package demo.gameshop.models;

import demo.gameshop.documents.Game;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDetails {
	
	private String name;
	
	public static GameDetails fromDocument(Game game) {
		GameDetails details = new GameDetails();
		// TODO : Fill fields
		details.name = game.getName();
		return details;
	}
}
