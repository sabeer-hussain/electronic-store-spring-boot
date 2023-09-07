package com.sabeer.electronic.store;

import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class ElectronicStoreApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ElectronicStoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		List<User> users = userRepository.findAll();
//		users.stream().forEach(user -> {
//			String encodedPassword = passwordEncoder.encode(user.getPassword());
//			user.setPassword(encodedPassword);
//			userRepository.save(user);
//		});
	}
}
