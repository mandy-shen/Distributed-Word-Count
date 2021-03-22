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
            // create volume path
            Path dirPath = Paths.get("/volume");
            if (!Files.exists(dirPath))
                Files.createDirectories(dirPath);

            // log id
            String id = System.getenv("id");
            System.out.println("id_test=" + id);

            // for test socket
            if ("0".equals(id)) {
                InetAddress localhost = InetAddress.getLocalHost();
                String ip = String.format("%s", localhost.getHostAddress());
                Files.write(Paths.get(dirPath + "/gate"), ip.getBytes());
            } else {
                new Node(dirPath);
            }

            // log hosts
            InetAddress localhost = InetAddress.getLocalHost();
            String ip = String.format("%s,%s,%s\n", id, localhost.getHostAddress(), localhost.getHostName());
            Files.write(Paths.get(dirPath + "/hosts"), ip.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
