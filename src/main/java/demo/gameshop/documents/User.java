package demo.gameshop.documents;

import static lombok.AccessLevel.NONE;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;

@Document
@Getter
@Setter
public class User {
	
	@Id
	@Setter(NONE)
	private String id;
	
	/**
	 * This field is set on constructor
	 */
	@Indexed(unique = true)
	@Setter(NONE)
	private String username;
	
	/**
	 * This field is set on constructor
	 */
	@Indexed(unique = true)
	@Setter(NONE)
	private String email;
	
	private String password;
	private Set<String> roles;
	
	private String firstName;
	private String lastName;
	private LocalDate dateOfBirth;
	private String phoneNumber;
	private String address;
	
	/**
	 * This field is set on constructor
	 * The values are the ids of game reviews
	 */
	@Setter(NONE)
	private Set<String> reviews;

	private boolean disabled;

//  Other fields in UserDetails that are not used currently
//	private boolean accountExpired;
//	private boolean credentialsExpired;
//	private boolean accountLocked;

	/** Repository requires constructor with no arguments to work. */
	@SuppressWarnings("unused")
	private User()
	{}
	
	/**
	 * Creates a user with the role "USER"
	 */
	public User(String username, String email, String password) {
		this(username, email, password, "USER");
	}
	
	public User(String username, String email, String password, String... roles) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = Set.of(roles);
		this.reviews = Set.of();
	}
	
	public User(String username, String email, String password, Set<String> roles) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = Collections.unmodifiableSet(roles);
		this.reviews = Set.of();
	}

	/**
	 * Sets the roles to the user.
	 * @param roles The roles to set
	 */
	public void setRoles(Set<String> roles) {
		this.roles = Collections.unmodifiableSet(roles);
	}
	
	/**
	 * Add a role to the user. Duplicates are ignored and assumes the role is valid.
	 * @param role The role to add
	 * @return This User
	 */
	public User addRole(String role) {
		Set<String> mutableRoles = new TreeSet<>(this.roles);
		mutableRoles.add(role);
		this.roles = Collections.unmodifiableSet(mutableRoles);
		return this;
	}
	
	/**
	 * Add multiple roles to the user. Duplicates are ignored and assumes the roles are valid.
	 * @param roles The roles to add
	 * @return This User
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
	 * @param role The role to remove
	 * @return This User
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
	 * @param roles The roles to remove
	 * @return This User
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
	
	public User addReview(String reviewId) {
		TreeSet<String> mutableReviews = new TreeSet<>(this.reviews);
		mutableReviews.add(reviewId);
		this.reviews = Collections.unmodifiableSet(mutableReviews);
		return this;
	}
	
	public User removeReview(String reviewId) {
		TreeSet<String> mutableReviews = new TreeSet<>(this.reviews);
		mutableReviews.remove(reviewId);
		this.reviews = Collections.unmodifiableSet(mutableReviews);
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