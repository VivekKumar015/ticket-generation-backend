package com.ticketsystem;

import com.ticketsystem.entity.Role;
import com.ticketsystem.enums.RoleName;
import com.ticketsystem.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class TicketSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            for (RoleName rn : RoleName.values()) {
                if (!roleRepository.existsByName(rn)) {
                    roleRepository.save(new Role(null, rn));
                }
            }
        };
    }
}