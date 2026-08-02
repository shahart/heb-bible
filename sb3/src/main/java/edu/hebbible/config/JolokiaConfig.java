package edu.hebbible.config;

import org.jolokia.server.core.http.AgentServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class JolokiaConfig {

    @Bean
    ServletRegistrationBean<AgentServlet> jolokiaServlet() {
        ServletRegistrationBean<AgentServlet> registration =
                new ServletRegistrationBean<>(new AgentServlet(), "/actuator/jolokia/*");
        registration.setName("jolokia");
        registration.setLoadOnStartup(0);
        registration.setAsyncSupported(true);
        return registration;
    }
}
