package com.mandy.wordcount;

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
            InetAddress localhost = InetAddress.getLocalHost();
            String ip = String.format("%s,%s,%s\n", System.getenv("id"),
                    localhost.getHostAddress(), localhost.getHostName());
            System.out.println(ip);

            Path dirPath = Paths.get("/volume");
            if (!Files.exists(dirPath))
                Files.createDirectories(dirPath);
            Files.write(Paths.get("/volume/hosts"), ip.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            Path idpath = Paths.get("/volume/id");
            if (Files.exists(idpath)) {
                Integer id = Integer.valueOf(Files.readAllLines(idpath).get(0));
                Files.write(idpath, String.valueOf(id++).getBytes());
            } else {
                Files.write(idpath, String.valueOf(1).getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
