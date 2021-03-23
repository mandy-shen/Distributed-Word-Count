package rest;

import node.Node;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class GateController {

    @GetMapping(value="/gate")
    public String gate(@RequestParam String file) {
        // URL: http://localhost:8080/gate?file=https://www.gutenberg.org/cache/epub/19033/pg19033.txt
        String list = "";
        RestTemplate restTemplate = new RestTemplate();

        try {
            System.out.println("Node.leader="+Node.leader);
            list = restTemplate.getForObject("http://" + Node.leader + ":8080/count?file=" + file, String.class);
        } catch (Exception e) {
            Node.leader = "gate";
            list = restTemplate.getForObject("http://gate:8080/count?file=" + file, String.class);
        }
        return list;
    }

}