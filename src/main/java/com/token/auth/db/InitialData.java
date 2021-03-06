package com.token.auth.db;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.token.auth.model.User;
import com.token.auth.repository.UserRepository;

@Service
public class InitialData implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		userRepository.deleteAll();

		User chirag = new User("chirag", passwordEncoder.encode("chirag123"), "USER", "");
		User varun = new User("varun", passwordEncoder.encode("varun123"), "ADMIN", "ACCESS_TEST1");
		User radhe = new User("radhe", passwordEncoder.encode("radhe123"), "MANAGER", "ACCESS_TEST1,ACCESS_TEST2");

		List<User> users = Arrays.asList(chirag, varun, radhe);

		userRepository.saveAll(users);
	}

}
