package node;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class LeaderNode {

    int id;
    Path dirPath;
    String ip;
    DatagramSocket socket;
    Timer timer;

    public LeaderNode(Path dirPath, int id) {
        this.id = id;
        this.dirPath = dirPath;
        init();
        initTimer();
    }

    private void init() {
        System.out.printf("%s=leader\n", id);
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
            this.socket = new DatagramSocket();
            // test
            Files.write(Paths.get(dirPath + "/leader"), String.valueOf(id).getBytes()); // overwrite file
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            //System.out.println("sent="+msg);
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
