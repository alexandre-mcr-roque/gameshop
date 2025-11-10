package demo.gameshop.helpers;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.text.TextRandomProvider;

/** Based off of <a href="https://www.baeldung.com/java-generate-secure-password">https://www.baeldung.com/java-generate-secure-password</a> */
public class PasswordGenerator {
	private PasswordGenerator()
	{}
	
	private static String generateCharacters(int length, int minCodePoint, int maxCodePoint) {
		RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder()
	    		.withinRange(minCodePoint, maxCodePoint)
	    		.usingRandom(SecureTextRandomProvider.getInstance())
	    		.get();
	    return pwdGenerator.generate(length);
	}
	
	private static String generateRandomSpecialCharacters(int length) {
	    return generateCharacters(length, 33, 45);
	}
	
	private static String generateRandomLowercase(int length) {
	    return generateCharacters(length, 'a', 'z');
	}
	
	private static String generateRandomUppercase(int length) {
		return generateCharacters(length, 'A', 'Z');
	}
	
	private static String generateRandomNumbers(int length) {
		return generateCharacters(length, '0', '9');
	}
	
	public static String generatePassword() {
		String pwString = generateRandomSpecialCharacters(2)
				.concat(generateRandomLowercase(2))
				.concat(generateRandomUppercase(2))
				.concat(generateRandomNumbers(2));
	    List<Character> pwChars = pwString.chars()
	    		.mapToObj(data -> (char) data)
	    		.collect(Collectors.toList());
	    Collections.shuffle(pwChars);
	    String password = pwChars.stream()
	    		.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
	    		.toString();
	    return password;
	}
	
	private final static class SecureTextRandomProvider implements TextRandomProvider {
		private SecureRandom rng = new SecureRandom();
		@Override
		public int nextInt(int max) {
			return rng.nextInt(max);
		}
		
		public static SecureTextRandomProvider getInstance() {
			return new SecureTextRandomProvider();
		}
	}
}
