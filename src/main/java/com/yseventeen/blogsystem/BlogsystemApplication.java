package com.yseventeen.blogsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class BlogsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogsystemApplication.class, args);
    }
}
