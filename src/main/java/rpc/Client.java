package rpc;

import rpc.service.WordCount;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;

public class Client {

    public Client() {}

    public TreeMap<String, Integer> count(String server, byte[] bytes) {
        try {
            URL url = new URL("http://" + server + ":7779/ws/count?wsdl");
            QName qname = new QName("http://service.rpc/", "WordCountImplService");
            Service service = Service.create(url, qname);
            WordCount wordCount = service.getPort(WordCount.class);

            return wordCount.count(bytes).getMap();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new TreeMap<>();
    }

    public String output(String server, String path) {
        try {
            URL url = new URL("http://" + server + ":7779/ws/count?wsdl");
            QName qname = new QName("http://service.rpc/", "WordCountImplService");
            Service service = Service.create(url, qname);
            WordCount wordCount = service.getPort(WordCount.class);

            return wordCount.output(server, path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
