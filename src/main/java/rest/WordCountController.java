package rest;

import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countwords")
public class WordCountController {

    @GetMapping
    public Iterable countwords(@RequestParam String file) {
        // URL: http://localhost:18080/countwords?file=https://www.gutenberg.org/cache/epub/19033/pg19033.txt
        // System.out.printf("file=%S\n", file);
        // String file = "https://www.gutenberg.org/cache/epub/19033/pg19033.txt";
        return getFrequencyResult(file);
    }

    private List<String> getFrequencyResult(String path) {
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

        return List.of( "Word(s) Found Most: " + mostWords,
                "Word(s) Found Least: " + leastWords);
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