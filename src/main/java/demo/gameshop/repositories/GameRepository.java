package demo.gameshop.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import demo.gameshop.documents.Game;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
	Optional<Game> findByTitleNormalized(String titleNormalized);
}
