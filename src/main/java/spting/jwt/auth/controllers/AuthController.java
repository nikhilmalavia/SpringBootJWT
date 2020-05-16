package spting.jwt.auth.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import spting.jwt.auth.model.AuthRequest;
import spting.jwt.auth.service.AuthService;

@RestController
public class AuthController {

	@Autowired
	AuthService auth;

	@PostMapping("/authenticate")
	public String generateToken(@RequestBody AuthRequest authRequest, HttpServletRequest request) throws Exception {
		return auth.authentication(authRequest, request);
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome !! ";
	}

	@GetMapping("/logout")
	public Object logout(HttpServletRequest headers) {
		return auth.logout(headers);
	}

}