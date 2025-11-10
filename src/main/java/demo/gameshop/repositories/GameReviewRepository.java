package demo.gameshop.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import demo.gameshop.documents.GameReview;

public interface GameReviewRepository extends MongoRepository<GameReview, String>
{}
