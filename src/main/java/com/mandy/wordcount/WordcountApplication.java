package com.mandy.wordcount;

import node.Node;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@SpringBootApplication(scanBasePackages = {"rest"})
public class WordcountApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordcountApplication.class, args);
        run();
    }

    public static void run() {
        try {
            Node node = new Node();

            // create volume path
            Path dirPath = Paths.get("/volume");
            if (!Files.exists(dirPath))
                Files.createDirectories(dirPath);

            // log hosts
            String ip = String.format("%s,%s\n", node.hostname, node.ip);
            Files.write(Paths.get(dirPath + "/hosts"), ip.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
