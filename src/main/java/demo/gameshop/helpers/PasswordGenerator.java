package demo.gameshop.helpers;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Based off of <a href="https://www.baeldung.com/java-generate-secure-password">https://www.baeldung.com/java-generate-secure-password</a> */
public class PasswordGenerator {
	private PasswordGenerator()
	{}

    private final static int NUMBER_LOWERCASE = 2;
    private final static int NUMBER_UPPERCASE = 2;
    private final static int NUMBER_DIGITS = 2;
    private final static int NUMBER_SPECIAL = 2;

	private static String generateCharacters(int length, int minCodePoint, int maxCodePoint) {
	    return RandomStringUtils.secure().next(length, minCodePoint, maxCodePoint, false, false);
	}

	private static String generateRandomLowercase() {
	    return generateCharacters(NUMBER_UPPERCASE, 'a', 'z');
	}

	private static String generateRandomUppercase() {
		return generateCharacters(NUMBER_DIGITS, 'A', 'Z');
	}

	private static String generateRandomDigits() {
		return generateCharacters(NUMBER_SPECIAL, '0', '9');
	}

	private static String generateRandomSpecialCharacters() {
	    return generateCharacters(NUMBER_LOWERCASE, 33, 45);
	}

	public static String generatePassword() {
		String pwString = generateRandomLowercase()
				.concat(generateRandomUppercase())
				.concat(generateRandomDigits())
                .concat(generateRandomSpecialCharacters());
	    List<Character> pwChars = pwString.chars()
	    		.mapToObj(data -> (char) data)
	    		.collect(Collectors.toList());
	    Collections.shuffle(pwChars);
        return pwChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
	}
}