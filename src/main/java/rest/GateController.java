package rest;

import node.Node;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import rpc.Client;

@RestController
@RequestMapping
public class GateController {

    @GetMapping(value="/app")
    public String gate(@RequestParam String file) {
        // URL: http://localhost:8080/app?file=https://www.gutenberg.org/cache/epub/19033/pg19033.txt
        String result = "";
        try {
            System.out.println("Node.leader="+Node.leader);
            result = new Client().output(Node.leader, file);
        } catch (Exception e) {
            Node.leader = "gate";
            result = new Client().output(Node.leader, file);
        }
        return result;
    }

}