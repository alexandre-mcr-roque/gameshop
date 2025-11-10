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
	
	private String id;
	private String username;
	private String email;
	
	private Set<String> roles;
	
	private String firstName;
	private String lastName;
	private LocalDate dateOfBirth;
	private String phoneNumber;
	private String address;
	
	// Unused for now
	private String imageUrl;

	private boolean disabled;

	public String getInitials() {
		return new StringBuilder(2)
				.append(this.firstName.charAt(0))
				.append(this.lastName.charAt(0))
				.toString().toUpperCase();
	}
	
	@Override
	public UserDetails mapper(User doc) {
		this.id = doc.getId();
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
