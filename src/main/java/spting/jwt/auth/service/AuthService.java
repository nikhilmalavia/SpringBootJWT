package spting.jwt.auth.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import spting.jwt.auth.config.JwtUtils;
import spting.jwt.auth.model.AuthRequest;
import spting.jwt.auth.model.Blacklisted;
import spting.jwt.auth.repo.BlacklistRepo;
import spting.jwt.auth.system.Crypto;

@Service
public class AuthService {

	@Autowired
	private BlacklistRepo blacklist;

	@Autowired
	Crypto crypto;

	@Autowired
	private JwtUtils jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	public String authentication(AuthRequest authRequest, HttpServletRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
		} catch (Exception ex) {
			// throw new Exception("inavalid username/password");
		}
		return crypto.encrypt(jwtUtil.generateToken(authRequest.getUserName(), request));
	}

	public Object logout(HttpServletRequest headers) {
		Blacklisted token = new Blacklisted();

		token.setToken(crypto.decrypt(headers.getHeader("Authorization").substring(7)));
		return blacklist.save(token);
	}

}
