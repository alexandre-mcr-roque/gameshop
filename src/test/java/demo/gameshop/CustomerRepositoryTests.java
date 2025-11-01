package demo.gameshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import demo.gameshop.documents.Customer;
import demo.gameshop.repositories.CustomerRepository;

@DataMongoTest
@ActiveProfiles(profiles = {"test"})
public class CustomerRepositoryTests {

	@Autowired
	private CustomerRepository customerRepository;

	@BeforeEach
	public void setUp() {
		customerRepository.deleteAll();
	}

	@Test
	public void saveAndFindByEmail() {
		Customer c = new Customer("Alice", "Smith", "alice@example.com", LocalDate.of(1990, 1, 1));
		
		Customer saved = customerRepository.save(c);
		assertThat(saved.getId()).isNotNull();
		
		Optional<Customer> found = customerRepository.findByEmail("alice@example.com");
		assertThat(found).isPresent();
		assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
		assertThat(found.get().getFirstName()).isEqualTo("Alice");
	}

	@Test
	public void findByFirstNameAndLastName() {
		Customer c1 = new Customer("Bob", "Jones", "bob.jones@example.com", LocalDate.of(1988, 2, 10));
		Customer c2 = new Customer("Bob", "Brown", "bob.brown@example.com", LocalDate.of(1989, 3, 15));
		Customer c3 = new Customer("Carol", "Jones", "carol.jones@example.com", LocalDate.of(1991, 4, 20));
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
	
	@Test
    public void defaultRolesIsEmpty() {
        Customer c = new Customer("Eve", "Adams", "eve@example.com", LocalDate.of(1992, 6, 5));
        Customer saved = customerRepository.save(c);
        assertThat(saved.getId()).isNotNull();

        Optional<Customer> found = customerRepository.findByEmail("eve@example.com");
        assertThat(found).isPresent();
        Set<String> roles = found.get().getRoles();
        assertThat(roles).isNotNull();
        assertThat(roles).isEmpty();
    }

    @Test
    public void saveAndRetrieveRoles() {
        Customer c = new Customer("Dan", "Miller", "dan@example.com", LocalDate.of(1985, 5, 20), "ROLE_USER", "ROLE_ADMIN");
        Customer saved = customerRepository.save(c);
        assertThat(saved.getId()).isNotNull();

        Optional<Customer> found = customerRepository.findByEmail("dan@example.com");
        assertThat(found).isPresent();
        Set<String> roles = found.get().getRoles();
        assertThat(roles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}