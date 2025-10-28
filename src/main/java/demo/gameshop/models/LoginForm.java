package demo.gameshop.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
	@NotBlank
    @Size(min = 3, max = 50)
    private String username;
	
    @NotBlank
    @Size(max = 100)
    private String password;
}
