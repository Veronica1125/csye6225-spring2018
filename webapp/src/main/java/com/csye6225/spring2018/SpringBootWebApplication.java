package com.csye6225.spring2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories(basePackages = "com.csye6225.spring2018.repository")
@EntityScan(basePackages = "com.csye6225.spring2018.pojo")
@SpringBootApplication
public class SpringBootWebApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SpringBootWebApplication.class, args);
  }

}
