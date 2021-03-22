package rest;

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
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class GateController {

    public String hostname;

    @GetMapping(value="/param")
    public void param(@RequestParam String hostname) {
        if(hostname!=null && hostname.startsWith("app"))
            this.hostname = hostname;
    }

    @GetMapping(value="/gate")
    public String gate(@RequestParam String file) {
        // URL: http://localhost/gate?file=https://www.gutenberg.org/cache/epub/19033/pg19033.txt
        String list = "";

        RestTemplate restTemplate = new RestTemplate();
        list = restTemplate.getForObject("http://" + hostname + ":8080/count?file=" + file, String.class);

        return list;
    }

}