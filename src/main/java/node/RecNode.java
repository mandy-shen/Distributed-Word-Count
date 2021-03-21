package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.util.Random;

public class RecNode extends Thread {

    int id;
    Path dirPath;
    String ip;
    DatagramSocket socket;
    boolean isLoop;
    Random random;

    public RecNode(Path dirPath, int id) {
        this.id = id;
        this.dirPath = dirPath;
        this.isLoop = true;
        this.random = new Random();
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
            this.socket = new DatagramSocket(4445);
            socket.setSoTimeout(3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
        this.start();
    }

    private void init() {
        System.out.printf("%s=rec\n", id);
    }

    @Override
    public void run() {
        try {
            isLoop = true;
            byte[] rec = new byte[8];
            DatagramPacket recPkt = new DatagramPacket(rec, rec.length);

            while(isLoop) {
                socket.receive(recPkt);
                String msg = new String(recPkt.getData(), 0, recPkt.getLength());
                System.out.printf("**** r%s_REC=%s\n",id, msg);
                isLoop = "alive".equals(msg);
                if (!isLoop)
                    System.out.printf("**** r%s_REC=%s\n",id, msg);
            }
        } catch (SocketTimeoutException e) {
            int sleep = Math.abs(random.nextInt(300));
            System.out.printf("**** r%s_Timeout\n **** sleep=", id, sleep);
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            new LeaderNode(dirPath, id); // change to leader
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket!=null)
                socket.close();
            System.out.printf("**** r%s_CLOSED\n",id);
        }
    }
}
