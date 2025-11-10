package demo.gameshop.models;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import demo.gameshop.documents.User;
import demo.gameshop.interfaces.models.Mappable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class UserForm implements Mappable<User, UserForm> {
	private String id;
	
	@NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past
    private LocalDate dateOfBirth;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 255)
    private String address;
    
    @NotBlank
    private Set<String> roles;

	@Override
	public UserForm mapper(@NonNull User doc) {
		this.id = doc.getId();
		this.username = doc.getUsername();
		this.email = doc.getEmail();
		this.firstName = doc.getFirstName();
		this.lastName = doc.getLastName();
		this.dateOfBirth = doc.getDateOfBirth();
		this.phoneNumber = doc.getPhoneNumber();
		this.address = doc.getAddress();
		this.roles = doc.getRoles();
		return this;
	}
}
