package com.tetkole.restservice;

import com.tetkole.restservice.models.Role;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class RestServiceApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;


	public static void main(String[] args) {
		SpringApplication.run(RestServiceApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (userRepository.existsByEmail("admin")) return;

		// create a default user
		User user = new User();
		user.setFirstname("admin");
		user.setLastname("admin");
		user.setPassword(passwordEncoder.encode("admin"));
		user.setEmail("admin");
		user.setRole(Role.ADMIN);
		userRepository.save(user);
	}

}
