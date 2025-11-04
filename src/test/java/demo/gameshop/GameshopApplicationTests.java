package demo.gameshop;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import demo.gameshop.controllers.GreetingController;

@WebMvcTest(controllers = GreetingController.class)
@WithMockUser
@ActiveProfiles(profiles = {"test"})
class GameshopApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void greeting() throws Exception {
		mockMvc.perform(get("/greeting/no-model"))
				.andExpect(content().string(containsString("Hello, World!")));
	}

	@Test
	public void greetingWithUser() throws Exception {
		mockMvc.perform(get("/greeting/no-model").param("name", "Greg"))
				.andExpect(content().string(containsString("Hello, Greg!")));
	}
}
