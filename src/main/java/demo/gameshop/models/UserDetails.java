package demo.gameshop.models;

import java.time.LocalDate;
import java.util.Set;

import demo.gameshop.documents.User;
import demo.gameshop.interfaces.models.Mappable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetails implements Mappable<User, UserDetails> {
	
	private String username;
	private String email;
	
	private Set<String> roles;
	
	private String firstName;
	private String lastName;
	private LocalDate dateOfBirth;
	private String phoneNumber;
	private String address;

	private boolean disabled;

	@Override
	public UserDetails mapper(User doc) {
		this.username = doc.getUsername();
		this.email = doc.getEmail();
		this.roles = doc.getRoles();
		this.firstName = doc.getFirstName();
		this.lastName = doc.getLastName();
		this.dateOfBirth = doc.getDateOfBirth();
		this.phoneNumber = doc.getPhoneNumber();
		this.address = doc.getAddress();
		this.disabled = doc.isDisabled();
		return this;
	}
}
