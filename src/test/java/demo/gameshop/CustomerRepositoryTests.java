package demo.gameshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import demo.gameshop.entities.Customer;
import demo.gameshop.repositories.CustomerRepository;

@DataMongoTest
public class CustomerRepositoryTests {

	@Autowired
	private CustomerRepository customerRepository;

	@BeforeEach
	public void setUp() {
		customerRepository.deleteAll();
	}

	@Test
	public void saveAndFindByEmail() {
		Customer c = new Customer("Alice", "Smith", "alice@example.com");
		
		Customer saved = customerRepository.save(c);
		assertThat(saved.getId()).isNotNull();
		
		Optional<Customer> found = customerRepository.findByEmail("alice@example.com");
		assertThat(found).isPresent();
		assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
		assertThat(found.get().getFirstName()).isEqualTo("Alice");
	}

	@Test
	public void findByFirstNameAndLastName() {
		Customer c1 = new Customer("Bob", "Jones", "bob.jones@example.com");
		Customer c2 = new Customer("Bob", "Brown", "bob.brown@example.com");
		Customer c3 = new Customer("Carol", "Jones", "carol.jones@example.com");
		customerRepository.save(c1);
		customerRepository.save(c2);
		customerRepository.save(c3);

		List<Customer> byFirst = customerRepository.findByFirstName("Bob");
		assertThat(byFirst).hasSize(2).extracting(Customer::getEmail).containsExactlyInAnyOrder(
			"bob.jones@example.com", "bob.brown@example.com");

		List<Customer> byLast = customerRepository.findByLastName("Jones");
		assertThat(byLast).hasSize(2).extracting(Customer::getEmail).containsExactlyInAnyOrder(
			"bob.jones@example.com", "carol.jones@example.com");
	}

	@Test
	public void findByEmailNotFound() {
		Optional<Customer> found = customerRepository.findByEmail("noone@example.com");
		assertThat(found).isNotPresent();
	}
}