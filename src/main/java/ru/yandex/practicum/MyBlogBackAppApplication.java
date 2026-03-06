package ru.yandex.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;

@SpringBootApplication
public class MyBlogBackAppApplication implements ApplicationRunner {

    @Autowired
    private BuildProperties buildProperties;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("Aplication build time — " + buildProperties.getTime());
    }

    public static void main(String[] args) {
        SpringApplication.run(MyBlogBackAppApplication.class, args);
    }

}
