package demo.gameshop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import demo.gameshop.models.GreetingModel;

@Controller
public class GreetingController {
	
	private static final String PARAM_NAME = "name";
	private static final String MODELNAME_MODEL = "model";
	private static final String MODELNAME_M2 = "m2";
	
	@GetMapping("/greeting/no-model")
	public String greeting(@RequestParam(name=PARAM_NAME, required=false, defaultValue="World")
							String name, Model model) {
		model.addAttribute(PARAM_NAME, name);
		return "greeting/greeting";
	}
	
	@GetMapping("/greeting/with-model")
	public String greeting(@RequestParam(name=PARAM_NAME, required=false, defaultValue="World") String name,
							@ModelAttribute(MODELNAME_MODEL) GreetingModel model,
							@ModelAttribute(MODELNAME_M2) GreetingModel m2) {
		model.setName(name);
		m2.setName("John Doe");
		return "greeting/greetingWithModel";
	}
}
