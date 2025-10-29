package demo.gameshop.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CurrentUserAdvice {
	
    @ModelAttribute("currentUser")
    public String currentUserEmail(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userDetails.getUsername();
    }
    
    @ModelAttribute("isAdmin")
    public boolean userIsAdmin(HttpServletRequest request) {
    	return request.isUserInRole("ROLE_ADMIN");
    }
}
