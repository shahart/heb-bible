package edu.hebbible;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
public class HebBible extends SpringBootServletInitializer { // todo why this is needed on external Tomcat?!

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HebBible.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(HebBible.class, args);
    }

}
