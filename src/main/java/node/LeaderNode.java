package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class LeaderNode {

    int id;
    String ip;
    public DatagramSocket socket;
    public Timer timer;

    public LeaderNode(int id) {
        this.id = id;
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
            this.socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
        initTimer();
    }

    private void init() {
        System.out.printf("%s=leader\n", id);
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

    public void stop() {
        //broadcast("end");
        if(timer!=null)
            timer.cancel();
        if(socket!=null)
            socket.close();
        System.out.printf("leader%s_stopped\n", id);
    }
}
