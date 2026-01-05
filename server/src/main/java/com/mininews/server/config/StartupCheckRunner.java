package com.mininews.server.config;

import com.mininews.server.entity.User;
import com.mininews.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StartupCheckRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupCheckRunner.class);

    private final UserRepository userRepository;

    public StartupCheckRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        Optional<User> admin = userRepository.findByUsername("admin");
        if (admin.isPresent()) {
            User u = admin.get();
            log.info("[StartupCheck] Default admin found: id={}, username={}, role={}", u.getId(), u.getUsername(), u.getRole());
        } else {
            log.warn("[StartupCheck] Default admin NOT found. Please confirm db/init.sql has been executed.");
        }
    }
}
