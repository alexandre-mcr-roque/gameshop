package demo.gameshop;

import demo.gameshop.helpers.PasswordGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordGeneratorTests {
    @Test
    public void testPasswordGenerator() {
        assertThat(PasswordGenerator.generatePassword().length()).isEqualTo(8);
    }
}