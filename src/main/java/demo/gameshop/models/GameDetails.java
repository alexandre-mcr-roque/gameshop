package demo.gameshop.models;

import demo.gameshop.documents.Game;
import demo.gameshop.interfaces.models.Mappable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDetails implements Mappable<Game, GameDetails> {
	
	private String id;
	private String title;
	private String titleNormalized;
	private String genre;
	private String imageUrl;

	@Override
	public GameDetails mapper(Game doc) {
		this.id = doc.getId();
		this.title = doc.getTitle();
		this.titleNormalized = doc.getTitleNormalized();
		this.genre = doc.getGenre();
		this.imageUrl = doc.getImageUrl();
		return this;
	}
}
