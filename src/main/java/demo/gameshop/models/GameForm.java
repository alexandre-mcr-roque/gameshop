package demo.gameshop.models;

import org.springframework.web.multipart.MultipartFile;

import demo.gameshop.annotations.FileValidation;
import demo.gameshop.documents.Game;
import demo.gameshop.interfaces.models.Mappable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class GameForm implements Mappable<Game, GameForm> {
	private String id;
	
	@NotBlank
	@Size(min = 3, max = 100)
	private String title;

	/** Set programatically */
	private String titleNormalized;
	
	@NotBlank
	@Size(min = 1, max = 200)
	private String genre;
	
	// Set programatically
	private String imageUrl;
	
	@FileValidation(max = "32MB")
	private MultipartFile imageFile;

	@Override
	public GameForm mapper(@NonNull Game doc) {
		this.id = doc.getId();
		this.title = doc.getTitle();
		this.titleNormalized = doc.getTitleNormalized();
		this.genre = doc.getGenre();
		this.imageUrl = doc.getImageUrl();
		return this;
	}
}
