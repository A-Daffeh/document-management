package com.adoustar.documentmanagement;

import com.adoustar.documentmanagement.domain.RequestContext;
import com.adoustar.documentmanagement.entity.RoleEntity;
import com.adoustar.documentmanagement.enums.Authority;
import com.adoustar.documentmanagement.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return args -> {
//			RequestContext.setUserId(0L);
//			var userRole = new RoleEntity();
//			userRole.setName(Authority.USER.name());
//			userRole.setAuthorities(Authority.USER);
//			roleRepository.save(userRole);
//
//			var adminRole = new RoleEntity();
//			adminRole.setName(Authority.ADMIN.name());
//			adminRole.setAuthorities(Authority.ADMIN);
//			roleRepository.save(adminRole);
//
//			var superAdminRole = new RoleEntity();
//			superAdminRole.setName(Authority.SUPER_ADMIN.name());
//			superAdminRole.setAuthorities(Authority.SUPER_ADMIN);
//			roleRepository.save(superAdminRole);
//
//			var managerRole = new RoleEntity();
//			managerRole.setName(Authority.MANAGER.name());
//			managerRole.setAuthorities(Authority.MANAGER);
//			roleRepository.save(managerRole);
//			RequestContext.start();
		};
	}

}
