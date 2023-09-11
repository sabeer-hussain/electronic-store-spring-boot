package com.sabeer.electronic.store;

import com.sabeer.electronic.store.entities.Role;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.repositories.RoleRepository;
import com.sabeer.electronic.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableWebMvc
public class ElectronicStoreApplication implements CommandLineRunner {

	@Value("${admin.role.id}")
	private String adminRoleId;

	@Value("${normal.role.id}")
	private String normalRoleId;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ElectronicStoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// for encoding password for existing users
//		List<User> users = userRepository.findAll();
//		users.stream().forEach(user -> {
//			String encodedPassword = passwordEncoder.encode(user.getPassword());
//			user.setPassword(encodedPassword);
//			userRepository.save(user);
//		});

		// for creating roles when application starts
//		try {
//			Role roleAdmin = Role.builder().roleId(adminRoleId).roleName("ROLE_ADMIN").build();
//			Role roleNormal = Role.builder().roleId(normalRoleId).roleName("ROLE_NORMAL").build();
//			roleRepository.saveAll(Arrays.asList(roleAdmin, roleNormal));
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}
}
