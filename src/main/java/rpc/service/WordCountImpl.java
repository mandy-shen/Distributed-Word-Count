package rpc.service;

import node.Node;
import rpc.Client;

import javax.jws.WebService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@WebService(endpointInterface = "rpc.service.WordCount")
public class WordCountImpl implements WordCount {
    @Override
    public JAXBMap<String, Integer> count(byte[] bytes) {
        TreeMap<String, Integer> map = new TreeMap<>(); // default: sorted key

        String line = new String(bytes);
        line = line.trim().toLowerCase(); // trim, to lowercase

        String[] words = line.split("[^a-zA-Z]+"); // split words

        for (String word: words) {
            if ("".equals(word))
                continue;
            map.put(word, map.containsKey(word) ? map.get(word)+1 : 1);
        }

        return new JAXBMap(map);
    }

    @Override
    public String output(String leader, String path) {
        StringBuilder sb = new StringBuilder("Client connected to cluster.<br>");

        Set<String> nodes = Node.validNodes;
        String hosts = nodes.stream().collect(Collectors.joining(","));
        sb.append("Cluster servers: Server (").append(hosts).append(")");


        sb.append("<br><br>").append("Leader elected: Server "+leader).append("<br>");

        // live nodes number
        int num = (nodes.size()==0)? 1:nodes.size();
        byte[] src = readBytes(path);
        int len = src.length;

        List<TreeMap<String, Integer>> maps = new ArrayList<>();
        // distribute mission
        int pos = 0;
        int sublen = len/num;
        int cnt = num;
        Iterator<String> it = nodes.iterator();
        while(it.hasNext()) {
            cnt--;
            String server = it.next();
            byte[] dst = new byte[(cnt==0)?len:sublen];
            System.arraycopy(src, pos, dst, 0, dst.length);
            maps.add(new Client().count(server, dst));
            sb.append(server + " processed Chunk "+(num-cnt)+" ("+dst.length+" bytes)<br>");
            len -= sublen;
            pos += sublen;
        }

        // combine result
        TreeMap<String, Integer> rMap = maps.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> (v1+v2), TreeMap::new));

        // get top3 words
        // sorted desc order by value
        String mostWords = rMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())) // desc
                .limit(3)
                .map(e -> String.format("%s(%s)", e.getKey() , e.getValue()))
                .collect(Collectors.joining(", "));

        // get leastWords
        // swap key value => (key:int, val:wordA, wordB, ...)
        String leastWords = rMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey,
                        (v1, v2) -> (v1+", "+v2), TreeMap::new)) // collect to TreeMap
                .entrySet().stream()
                .map(e -> e.getValue())
                .findFirst().orElse("");

        sb.append("<br>Results provided by "+leader+" [leader]:<br>")
            .append("Word(s) Found Most: ").append(mostWords)
            .append("<br>Word(s) Found Least: ").append(leastWords);


        return sb.toString();
    }

    @Override
    public Boolean alive(String server) {
        return true;
    }

    private byte[] readBytes(String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (InputStream is = new URL(path).openStream()) {
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = is.read(byteChunk)) > 0 )
                baos.write(byteChunk, 0, n);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
