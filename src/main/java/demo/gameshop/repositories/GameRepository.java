package demo.gameshop.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import demo.gameshop.entities.Game;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
	public Optional<Game> findByNameNormalized(String nameNormalized);
}
