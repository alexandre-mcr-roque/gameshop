package demo.gameshop.models;

import org.springframework.web.multipart.MultipartFile;

import demo.gameshop.documents.Game;
import demo.gameshop.interfaces.models.Mappable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameForm implements Mappable<Game, GameForm> {
	// TODO : Validation annotations
	private String title;
	private String titleNormalized;
	private String genre;
	private String imageUrl;
	
	private MultipartFile imageFile;
	
	@Override
	public GameForm mapper(Game doc) {
		this.title = doc.getTitle();
		this.titleNormalized = doc.getTitleNormalized();
		this.genre = doc.getGenre();
		this.imageUrl = doc.getImageUrl();
		return this;
	}
}
