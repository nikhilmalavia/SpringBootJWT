package spting.jwt.auth;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import spting.jwt.auth.model.UserModel;
import spting.jwt.auth.repo.UserRepository;

@SpringBootApplication
public class JwtAuthApplication {

	@Autowired
	private UserRepository repository;

	@PostConstruct
	public void initUsers() {
		List<UserModel> users = Stream.of(new UserModel(1, "admin", "password", "admin@test.com"),
				new UserModel(2, "user", "password", "user@test.com")).collect(Collectors.toList());
		repository.saveAll(users);
	}

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthApplication.class, args);
	}

}