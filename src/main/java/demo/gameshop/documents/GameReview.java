package demo.gameshop.documents;

import static lombok.AccessLevel.NONE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
@Getter
@Setter
public class GameReview {

	/** Should be the same as a game id for a 1-1 relation*/
	@Id
	@Setter(NONE)
	private String id;
	
	private float rating;
	
	@Setter(NONE)
	private List<UserGameReview> reviews;
	
	/** @deprecated Repository requires constructor with no arguments to work. */
	@SuppressWarnings("unused")
	private GameReview() {
		reviews = List.of();
	}
	
	public GameReview(String gameId) {
		this.id = gameId;
	}
	
	/**
	 * Returns the index of the newly added user review
	 */
	public int addReview(User user, int rating, String review) {
		List<UserGameReview> mutableList = new ArrayList<>(this.reviews);
		int idx = mutableList.size();
		UserGameReview userReview = new UserGameReview();
		userReview.userId = user.getId();
		userReview.username = user.getUsername();
		userReview.firstName = user.getFirstName();
		userReview.lastName = user.getLastName();
		userReview.rating = rating;
		userReview.review = review;
		userReview.reviewDate = LocalDate.now();
		mutableList.add(userReview);
		this.reviews = Collections.unmodifiableList(mutableList);
		return idx;
	}
	
	@Getter
	private static class UserGameReview {
		private String userId;
		private String username;
		private String firstName;
		private String lastName;
		
		private int rating;
		private String review;
		private LocalDate reviewDate;
		private LocalDate editDate;
	}
}
