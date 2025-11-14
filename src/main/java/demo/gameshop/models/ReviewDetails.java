package demo.gameshop.models;

import demo.gameshop.documents.Game;
import demo.gameshop.documents.GameReview;
import demo.gameshop.interfaces.models.Mappable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReviewDetails  implements Mappable<GameReview.UserGameReview, ReviewDetails> {
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    private int rating;
    private String review;
    private LocalDate reviewDate;
    private LocalDate editDate;

    @Override
    public ReviewDetails mapper(GameReview.@NonNull UserGameReview doc) {
        this.userId = doc.getUserId();
        this.username = doc.getUsername();
        this.firstName = doc.getFirstName();
        this.lastName = doc.getLastName();
        this.rating = doc.getRating();
        this.review = doc.getReview();
        this.reviewDate = doc.getReviewDate();
        this.editDate = doc.getEditDate();
        return this;
    }
}
