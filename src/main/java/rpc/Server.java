package rpc;

import rpc.service.WordCountImpl;

import javax.xml.ws.Endpoint;

public class Server extends Thread {

    public volatile boolean active = true;
    private Endpoint ep;
    private String hostname = "";

    public Server(String hostname) {
        this.hostname = hostname;
    }

    public void run(){
        ep = Endpoint.publish("http://" + hostname + ":7779/ws/count",  new WordCountImpl());
        init();
    }

    private void init(){
        synchronized(this){
            try {
                while (active) {
                    System.out.println(hostname+": http://" + hostname + ":7779/");
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if(!active) {
                    ep.stop();
                    System.out.println(hostname+" webService stopped!");
                }
            }
        }
    }
}
