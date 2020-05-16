package spting.jwt.auth.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import spting.jwt.auth.repo.BlacklistRepo;;

@Service
public class JwtUtils {

	@Autowired
	BlacklistRepo blacklist;

	private String secret = "SecretKey";

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateToken(String username, HttpServletRequest request) {
		Map<String, Object> claims = new HashMap<>();
		// claims.put("Ip", getClientIp(request));
		// claims.put("Agent", getUserAgent(request));
		return createToken(claims, username);
	}

	public Boolean validateToken(String token, UserDetails userDetails, HttpServletRequest request) {
		final String username = extractUsername(token);
		
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)
				&& blacklist.findByToken(token) == 0);
	}

	private String createToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private static String getClientIp(HttpServletRequest request) {
		String remoteAddr = "";
		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		return remoteAddr;
	}

	public static String getUserAgent(HttpServletRequest request) {
		String ua = "";
		if (request != null) {
			ua = request.getHeader("User-Agent");
		}
		return ua;
	}
}
