package com.logiclab.documentcontrolsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.logiclab.documentcontrolsystem.domain")
@EnableJpaRepositories("com.logiclab.documentcontrolsystem.repository")
public class DocumentControlSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentControlSystemApplication.class, args);
    }
}