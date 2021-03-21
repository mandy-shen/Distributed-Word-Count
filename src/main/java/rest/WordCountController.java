package rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/countwords")
public class WordCountController {

    @Value("${volume.path}")
    public String volpath;

    //@GetMapping(value="/gate")
    public String gate(@RequestParam String file) {

        String list = "Word(s) Found Most: \nWord(s) Found Least: ";

        try {
            Path leaderpath = Paths.get(volpath + "/leader");
            int id = Integer.valueOf(Files.readAllLines(leaderpath).get(0));
            System.out.printf("gate_leader=%s\n", id);

            RestTemplate restTemplate = new RestTemplate();
            switch (id) {
                case 1:
                    list = restTemplate.getForObject("http://localhost:18081/countwords?file="+file, String.class);
                    break;
                case 2:
                    list = restTemplate.getForObject("http://localhost:18082/countwords?file="+file, String.class);
                    break;
                case 3:
                    list = restTemplate.getForObject("http://localhost:18083/countwords?file="+file, String.class);
                    break;
                default:
                    list = getFrequencyResult(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    @GetMapping
    public String countwords(@RequestParam String file) {
        // URL: http://localhost:18080/countwords?file=https://www.gutenberg.org/cache/epub/19033/pg19033.txt
        // System.out.printf("file=%S\n", file);
        // String file = "https://www.gutenberg.org/cache/epub/19033/pg19033.txt";
        file = Paths.get("./pg19033.txt").toUri().toString();
        return getFrequencyResult(file);
    }

    private String getFrequencyResult(String path) {
        // map
        Map<String, Integer> map = getWordMap(path);
        // System.out.println("map size:"+ map.size());

        // get top3 words
        // sorted desc order by value
        String mostWords = map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())) // desc
                .limit(3)
                .map(e -> String.format("%s(%s)", e.getKey() , e.getValue()))
                .collect(Collectors.joining(", "));

        // get leastWords
        // swap key value => (key:int, val:wordA, wordB, ...)
        String leastWords = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey,
                        (v1, v2) -> (v1+", "+v2), TreeMap::new)) // collect to TreeMap
                .entrySet().stream()
                .map(e -> e.getValue())
                .findFirst().orElse("");

        /*
        String leastStr = map_swap.entrySet().stream()
                .map(e -> String.format("%s(%s)\n", e.getKey() , e.getValue()))
                .collect(Collectors.joining(", "));

        String sorted_Str = map.entrySet().stream()
                // .sorted(Map.Entry.comparingByValue()) // asc
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())) // desc
                .map(e -> String.format("%s(%s)", e.getKey() , e.getValue()))
                .collect(Collectors.joining(", "));
         */

        return "Word(s) Found Most: " + mostWords + "\nWord(s) Found Least: " + leastWords;
    }

    private Map<String, Integer> getWordMap(String path) {
        Map<String, Integer> map = new TreeMap<>(); // default: sorted key

        try {
            URL url = new URL(path);
            try (InputStreamReader reader = new InputStreamReader(url.openStream());
                 BufferedReader br = new BufferedReader(reader)) {

                String line;
                while((line = br.readLine()) != null) {
                    line = line.trim().toLowerCase(); // trim, to lowercase
                    if ("".equals(line))
                        continue;
                    String[] words = line.split("[^a-zA-Z]+"); // split words
                    for (String word: words) {
                        if ("".equals(word))
                            continue;
                        map.put(word, map.containsKey(word) ? map.get(word)+1 : 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

}