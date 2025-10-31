package demo.gameshop.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import demo.gameshop.documents.User;
import demo.gameshop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * {@inheritDoc}<br>
	 * Will also search for users based on email if the given username contains <b>@</b>.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> userOptional = username.contains("@")
			? userRepository.findByEmail(username)
			: userRepository.findByUsername(username);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			return user.toUserDetails();
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}
}