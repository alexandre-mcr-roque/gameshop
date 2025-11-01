package demo.gameshop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import demo.gameshop.documents.Customer;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
	List<Customer> findByFirstName(String firstName);
	List<Customer> findByLastName(String lastName);
	Optional<Customer> findByEmail(String email);
}
