package demo.gameshop.entities;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	
	@Id
	@Setter(AccessLevel.NONE)
	private String id;
	
	@Indexed(unique = true)
	@Setter(AccessLevel.NONE)
	private String username;
	
	private String password;
	
	@Setter(AccessLevel.NONE)
	private Set<String> roles;
	
	private String firstName;
	private String lastName;
	private String email;
	private LocalDate dateOfBirth;
	private String phoneNumber;
	private String address;

	private boolean disabled;

//  Other fields in UserDetails that are not used currently
//	private boolean accountExpired;
//	private boolean credentialsExpired;
//	private boolean accountLocked;

	public User() {
		this.roles = Set.of();
	}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
		this.roles = Set.of();
	}
	
	public User(String username, String password, String... roles) {
		this.username = username;
		this.password = password;
		this.roles = Set.of(roles);
	}
	
	/**
	 * Add a role to the user. Duplicates are ignored and assumes the role is valid.
	 * @param role the role to add
	 * @return this User
	 */
	public User addRole(String role) {
		Set<String> mutableRoles = new TreeSet<>(this.roles);
		mutableRoles.add(role);
		this.roles = Collections.unmodifiableSet(mutableRoles);
		return this;
	}
	
	/**
	 * Add multiple roles to the user. Duplicates are ignored and assumes the roles are valid.
	 * @param roles the roles to add
	 * @return this User
	 */
	public User addRoles(String... roles) {
		Set<String> mutableRoles = new TreeSet<>(this.roles);
		for (String role : roles) {
			mutableRoles.add(role);
		}
		this.roles = Collections.unmodifiableSet(mutableRoles);
		return this;
	}
	
	/**
	 * Remove a role from the user. If the user does not have the role, nothing happens.<br>
	 * If the user only has one role, an exception is thrown.
	 * @param role the role to remove
	 * @return this User
	 */
	public User removeRole(String role) {
		Assert.isTrue(this.roles.size() > 1, "User must have at least one role.");
		Set<String> mutableRoles = new TreeSet<>(this.roles);
		mutableRoles.remove(role);
		this.roles = Collections.unmodifiableSet(mutableRoles);
		return this;
	}
	/**
	 * Remove multiple roles from the user. If the user does not have a role, it is ignored.<br>
	 * If removing the roles would leave the user with no roles, an exception is thrown. This does not account for duplicate parameters or non-existing roles in the list.
	 * @param roles the roles to remove
	 * @return this User
	 */
	public User removeRoles(String... roles) {
		Assert.isTrue(this.roles.size() > roles.length, "User must have at least one role.");
		Set<String> mutableRoles = new TreeSet<>(this.roles);
		for (String role : roles) {
			mutableRoles.remove(role);
		}
		this.roles = Collections.unmodifiableSet(mutableRoles);
		return this;
	}
	
	public UserDetails toUserDetails() {
		return org.springframework.security.core.userdetails.User.builder()
				.username(this.username)
				.password(this.password)
				.roles(this.roles.toArray(new String[0]))
				.disabled(this.disabled)
				.build();
	}
}