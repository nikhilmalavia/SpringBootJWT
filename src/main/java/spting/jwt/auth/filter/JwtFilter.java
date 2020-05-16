package spting.jwt.auth.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import spting.jwt.auth.config.JwtUtils;
import spting.jwt.auth.service.CustomUserDetailsService;
import spting.jwt.auth.system.Crypto;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private static String AUTHORIZATION = "Authorization";
	private static String BEARER = "Bearer ";

	@Autowired
	private JwtUtils jwtUtil;

	@Autowired
	Crypto crypto;

	@Autowired
	private CustomUserDetailsService service;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final Optional<String> jwt = extractAuthToken(request);

		jwt.ifPresent(token -> {
			try {
				String userName = jwtUtil.extractUsername(token);
				if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

					UserDetails userDetails = service.loadUserByUsername(userName);

					if (jwtUtil.validateToken(token, userDetails, request)) {

						setSecurityContext(new WebAuthenticationDetailsSource().buildDetails(request), token,
								userDetails);
					}
				}

			} catch (IllegalArgumentException | MalformedJwtException | ExpiredJwtException e) {
				System.err.println("Unable to get JWT Token or JWT Token has expired");
			}
		});

		filterChain.doFilter(request, response);

	}

	private void setSecurityContext(WebAuthenticationDetails authDetails, String token, UserDetails userDetails) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
				null, userDetails.getAuthorities());
		authenticationToken.setDetails(authDetails);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	private Optional<String> extractAuthToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {

			return Optional.of(crypto.decrypt(bearerToken.substring(7)));
		}
		return Optional.empty();
	}

}
