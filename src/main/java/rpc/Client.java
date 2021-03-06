package rpc;

import rpc.service.WordCount;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.TreeMap;

public class Client {
    QName qname;
    public Client() {
        qname = new QName("http://service.rpc/", "WordCountImplService");
    }

    public TreeMap<String, Integer> count(String server, byte[] bytes) {
        try {
            URL url = new URL("http://" + server + ":7779/ws/count?wsdl");
            Service service = Service.create(url, qname);
            WordCount wordCount = service.getPort(WordCount.class);

            return wordCount.count(bytes).getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new TreeMap<>();
    }

    public String output(String server, String path) {
        try {
            URL url = new URL("http://" + server + ":7779/ws/count?wsdl");
            Service service = Service.create(url, qname);
            WordCount wordCount = service.getPort(WordCount.class);

            return wordCount.output(server, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Boolean alive(String server) {
        try {
            URL url = new URL("http://"+server+":7779/ws/count?wsdl");
            Service service = Service.create(url, qname);
            WordCount wordCount = service.getPort(WordCount.class);

            return wordCount.alive(server);
        } catch (Exception e) {
            //e.printStackTrace();
            //System.out.println(server+"-failure");
        }
        return false;
    }
}
