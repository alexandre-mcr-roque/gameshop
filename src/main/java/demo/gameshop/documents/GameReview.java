package demo.gameshop.documents;

import static lombok.AccessLevel.NONE;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
@Getter
@Setter
public class GameReview {

	/** Should be the same as a game id for a 1-1 relation */
	@Id
	@Setter(NONE)
	private String id;
	
	private float rating;
	
	@Setter(NONE)
	private Map<String, UserGameReview> reviews;
	
	/** Repository requires constructor with no arguments to work. */
	@SuppressWarnings("unused")
	private GameReview()
	{}
	
	public GameReview(String gameId) {
		this.id = gameId;
		reviews = Map.of();
	}
	
	/**
	 * Adds the review id for the user
	 * @return This game review
	 */
	public GameReview addReview(User user, int rating, String review) {
		user.addReview(this.id);
		return addReview(user.getId(), user.getUsername(),
				user.getFirstName(), user.getLastName(),
				rating, review);
	}

	/**
	 *  Does not remove the review id for the user
	 *  @return This game review
	 */
	public GameReview addReview(String id, String username,
								String firstName, String lastName,
								int rating, String review) {
		TreeMap<String, UserGameReview> mutableReviews = new TreeMap<>(this.reviews);
		UserGameReview userReview = new UserGameReview();
		userReview.userId = id;
		userReview.username = username;
		userReview.firstName = firstName;
		userReview.lastName = lastName;
		userReview.rating = rating;
		userReview.review = review;
		userReview.reviewDate = LocalDate.now();
		mutableReviews.put(userReview.userId, userReview);
		this.reviews = Collections.unmodifiableMap(mutableReviews);
		return this;
	}

	/**
	 *  Removes the review id for the user
	 *  @return This game review
	 */
	public GameReview removeReview(User user) {
		user.removeReview(this.id);
		return removeReview(user.getId());
	}
	
	/**
	 *  Does not remove the review id for the user
	 *  @return This game review
	 */
	public GameReview removeReview(String userId) {
		TreeMap<String, UserGameReview> mutableReviews = new TreeMap<>(this.reviews);
		mutableReviews.remove(userId);
		this.reviews = Collections.unmodifiableMap(mutableReviews);
		return this;
	}
	
	@Getter
	public static class UserGameReview {
		private String userId;
		private String username;
		private String firstName;
		private String lastName;
		
		private int rating;
		private String review;
		private LocalDate reviewDate;
		private LocalDate editDate;

        private UserGameReview() {}
    }
}
