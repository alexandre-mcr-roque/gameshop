package demo.gameshop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import demo.gameshop.entities.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {
	public List<Customer> findByFirstName(String firstName);
	public List<Customer> findByLastName(String lastName);
	public Optional<Customer> findByEmail(String email);
}
