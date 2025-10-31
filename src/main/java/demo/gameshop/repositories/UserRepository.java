package demo.gameshop.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import demo.gameshop.documents.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	public Optional<User> findByUsername(String username);
	public Optional<User> findByEmail(String email);
}
