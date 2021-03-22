package node;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Node {

    public String hostname;
    public String ip;
    DatagramSocket socket;
    Random random;

    Receiver rec;
    Leader leader;

    public Node() {
        this.random = new Random();
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
            this.ip = InetAddress.getLocalHost().getHostAddress();
            this.socket = new DatagramSocket(4445);
            socket.setSoTimeout(3000);
            if (!"gate".equals(hostname))
                rec = new Receiver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Receiver extends Thread {

        boolean isLoop;

        public Receiver() {
            this.isLoop = true;
            this.start();
        }

        @Override
        public void run() {
            try {
                System.out.printf("%s=rec\n", ip);
                isLoop = true;
                byte[] rec = new byte[8];
                DatagramPacket recPkt = new DatagramPacket(rec, rec.length);

                while(isLoop) {
                    socket.receive(recPkt);
                    String msg = new String(recPkt.getData(), 0, recPkt.getLength());
                    System.out.printf("**** r%s_REC=%s\n", ip, msg);
                    isLoop = "alive".equals(msg);
                    if (!isLoop)
                        System.out.printf("**** r%s_REC=%s\n", ip, msg);
                }
            } catch (SocketTimeoutException e) {
                int sleep = Math.abs(random.nextInt(1000));
                System.out.printf("**** r%s_Timeout **** sleep=%s\n", ip, sleep);
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                System.out.printf("wake_up_leader=%s\n", ip);
                leader = new Leader(); // change to leader
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Leader {
        Timer timer;

        public Leader() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject("http://gate:8080/chgleader?hostname=" + hostname, String.class);
            System.out.printf("%s=leader\n", ip);
            initTimer();
        }

        private void initTimer() {
            this.timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcast("alive");
                }
            }, 0, 100); // every 0.1s broadcast heartbeat
        }

    }

    private void broadcast(String msg) {
        try {
            socket.setBroadcast(true);

            byte[] buffer = msg.getBytes();
            InetAddress addr = InetAddress.getByName("255.255.255.255");

            System.out.println("sent="+msg);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, 4445);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
