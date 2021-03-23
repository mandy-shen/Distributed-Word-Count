package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Node {

    public static String leader = "gate";
    public static Integer term = 0;

    String hostname;
    String ip;
    DatagramSocket heartbeat;
    DatagramSocket chgleader;
    Random random;

    Receiver recv;
    Leader lead;

    public Node() {
        this.random = new Random();
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
            this.ip = InetAddress.getLocalHost().getHostAddress();
            heartbeat = new DatagramSocket(4445);
            chgleader = new DatagramSocket(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        recv = new Receiver();
        recv.accept();
    }

    class Receiver {

        public void accept() {
            try {
                System.out.printf("%s=rec\n", hostname);
                if (!"gate".equals(hostname))
                    heartbeat.setSoTimeout(1000);

                byte[] rec = new byte[16];
                DatagramPacket recPkt = new DatagramPacket(rec, rec.length);
                while (true) {
                    heartbeat.receive(recPkt);
                    String msg = new String(recPkt.getData(), 0, recPkt.getLength());
                    System.out.printf("**** r_%s_REC=%s\n", hostname, msg);

                    if (msg.startsWith("v-")) {
                        String[] arg = msg.split("-");
                        leader = arg[1];
                        term = Integer.parseInt(arg[2]);
                    } else if (msg.startsWith("-")) {
                        String[] arg = msg.split("-");
                        int nextTerm = Integer.parseInt(arg[2]);
                        if (term < nextTerm) {
                            leader = arg[1];
                            term = nextTerm;
                            broadcast(chgleader, leader, 2000);
                        }
                    }
                }
            } catch (SocketTimeoutException e) {
                candidate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void candidate() {
            int cnt = 0;
            try {
                System.out.printf("%s=candidate\n", hostname);
                broadcast(heartbeat,"-" + leader + "-" + (term+1), 4445);

                byte[] rec = new byte[8];
                DatagramPacket recPkt = new DatagramPacket(rec, rec.length);
                chgleader.setSoTimeout(1000);

                while(cnt<2) {
                    chgleader.receive(recPkt);
                    String msg = new String(recPkt.getData(), 0, recPkt.getLength());
                    System.out.printf("**** c_%s_candidate=%s\n", hostname, msg);
                    if (msg.equals(hostname))
                        cnt++;
                }
                lead = new Leader();
            } catch (Exception e) {
                accept();
            }
        }
    }

    class Leader {
        public Timer timer;

        public Leader() {
            initTimer();
        }

        private void initTimer() {
            this.timer = new Timer();
            System.out.printf("%s=leader\n", hostname);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcast(heartbeat, "v-"+hostname+"-"+term, 4445);
                }
            }, 0, 100); // every 0.1s broadcast heartbeat
        }

    }

    private void broadcast(DatagramSocket socket, String msg, int port) {
        try {
            //System.out.printf("%s=start_broadcast\n", hostname);
            socket.setBroadcast(true);

            //System.out.printf("%s=start_broadcast_setting\n", hostname);
            byte[] buffer = msg.getBytes();
            InetAddress addr = InetAddress.getByName("255.255.255.255");

            //System.out.println("sent="+msg);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
