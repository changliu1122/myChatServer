package org.mychat.mychat_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(value = "org.mychat.mychat_server.mapper")
@ComponentScan(basePackages = {"org.mychat.mychat_server","IdGenerator"})
public class MyChatServerApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyChatServerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MyChatServerApplication.class, args);
    }

}
