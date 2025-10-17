package demo.gameshop.entities;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

public class Customer {

	@Id
	private @Getter String id;
	
	private @Getter @Setter String firstName;
	private @Getter @Setter String lastName;
	private @Getter @Setter String email;
	
	public Customer() {}
	
	public Customer(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	// Print customer details
	@Override
	public String toString() {
		return String.format(
				"Customer[id=%s, firstName='%s', lastName='%s', email='%s']",
				id, firstName, lastName, email);
	}
}
