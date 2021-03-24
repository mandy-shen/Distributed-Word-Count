package com.mandy.wordcount;

import node.Node;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"rest"})
public class WordcountApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordcountApplication.class, args);
        run();
    }

    public static void run() {
        Node node = new Node();
        Node.Nodes.add("app1");
        Node.Nodes.add("app2");
        Node.Nodes.add("app3");
        node.init();
    }

}
