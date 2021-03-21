package com.mandy.wordcount;

import node.LeaderNode;
import node.RecNode;
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
            Path dirPath = Paths.get("./volume");
            if (!Files.exists(dirPath))
                Files.createDirectories(dirPath);

            // log id
            int id = 1;
            Path idpath = Paths.get(dirPath + "/id");
            if (Files.exists(idpath))
                id = Integer.valueOf(Files.readAllLines(idpath).get(0)) + 1;
            Files.write(idpath, String.valueOf(id).getBytes()); // overwrite file

            // for test socket
            if (id == 1)
                new LeaderNode(dirPath, id);
            else
                new RecNode(dirPath, id);

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
