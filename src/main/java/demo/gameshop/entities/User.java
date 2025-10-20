package demo.gameshop.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Getter;
import lombok.Setter;

public class User {

	@Id
	private @Getter String id;
	
	@Indexed(unique = true)
	private @Getter String username;
	private @Getter @Setter String password;
	
	private @Getter @Setter List<String> authorities;

	private @Getter @Setter boolean disabled;

//  Other fields in UserDetails that are not used currently
//	private @Getter @Setter boolean accountExpired;
//	private @Getter @Setter boolean credentialsExpired;
//	private @Getter @Setter boolean accountLocked;
}
