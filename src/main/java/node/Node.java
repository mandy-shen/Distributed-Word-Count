package node;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Node {

    public String hostname;
    public String ip;
    DatagramSocket heartbeat;
    Random random;

    Receiver recv;
    Leader lead;

    public Node() {
        this.random = new Random();
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
            this.ip = InetAddress.getLocalHost().getHostAddress();
            this.heartbeat = new DatagramSocket(4445);
            heartbeat.setSoTimeout(600);

            if (!"gate".equals(hostname))
                recv = new Receiver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Receiver extends Thread {
        int acceptN;
        int acceptV;
        Timer timer;

        public Receiver() {
            initTimer();
            this.start();
        }

        @Override
        public void run() {
            accept();
        }

        private void initTimer() {
            this.timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    waitTobeLeader();
                }
            }, 200); // every 0.2s broadcast heartbeat
        }

        private void accept() {
            try {
                System.out.printf("%s=rec\n", hostname);
                byte[] rec = new byte[8];
                DatagramPacket recPkt = new DatagramPacket(rec, rec.length);

                while(true) {
                    heartbeat.receive(recPkt);
                    String msg = new String(recPkt.getData(), 0, recPkt.getLength());
                    System.out.printf("**** r%s_REC=%s\n", hostname, msg);

                    if (msg.startsWith("lv-")) {
                        // reset timer
                        timer.cancel();
                        initTimer();
                        if (lead != null)
                        lead.timer.cancel();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void waitTobeLeader() {
                // change to leader
                lead = new Leader();
        }
    }

    class Leader {
        public Timer timer;

        public Leader() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject("http://gate/param?hostname=" + hostname, String.class);
            System.out.printf("%s=candidate\n", hostname);
            initTimer();
        }

        private void initTimer() {
            this.timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.printf("%s=leader\n", hostname);
                    broadcast("lv-"+hostname);
                }
            }, Math.abs(random.nextInt(200)), 100); // every 0.1s broadcast heartbeat
        }

    }

    private void broadcast(String msg) {
        try {
            heartbeat.setBroadcast(true);

            byte[] buffer = msg.getBytes();
            InetAddress addr = InetAddress.getByName("255.255.255.255");

            System.out.println("sent="+msg);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, 4445);
            heartbeat.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
