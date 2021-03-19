package com.mandy.wordcount;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@SpringBootApplication(scanBasePackages =  { "rest" })
public class WordcountApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WordcountApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        InetAddress localhost = InetAddress.getLocalHost();
        String ip = String.format("%s %s\n", localhost.getHostAddress(), localhost.getHostName());
        System.out.println(ip);

        Path dirPath = Paths.get("/volume");
        if (!Files.exists(dirPath))
            Files.createDirectories(dirPath);
        Files.write(Paths.get("/volume/hosts"), ip.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

}
