package demo.gameshop.documents;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
public class Customer {

	@Id
	private @Getter String id;
	
	private @Getter @Setter String firstName;
	private @Getter @Setter String lastName;
	private @Getter @Setter String email;
	private @Getter @Setter LocalDate dateOfBirth;
	private @Getter Set<String> roles = Set.of();
	
	public Customer() {}
	
	public Customer(String firstName, String lastName, String email, LocalDate dateOfBirth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.dateOfBirth = dateOfBirth;
	}
	
	public Customer(String firstName, String lastName, String email, LocalDate dateOfBirth, String... roles) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.dateOfBirth = dateOfBirth;
		this.roles = Set.of(roles);
	}
	
	// Print customer details
	@Override
	public String toString() {
		return String.format(
				"Customer[id=%s, firstName='%s', lastName='%s', email='%s']",
				id, firstName, lastName, email);
	}
}
