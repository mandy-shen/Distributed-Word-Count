package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class RecNode extends Thread {

    int id;
    String ip;
    public DatagramSocket socket;
    boolean isLoop;

    public RecNode(int id) {
        this.id = id;
        this.isLoop = true;
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
            }
        } catch (SocketTimeoutException e) {
            System.out.printf("**** r%s_Timeout\n",id);
            new CandiNode(id, ip);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket!=null)
                socket.close();
            System.out.printf("**** r%s_CLOSED\n",id);
        }
    }

    class CandiNode {
        int id;
        String ip;
        public CandiNode(int id, String ip) {
            this.id = id;
            this.ip = ip;
            System.out.printf("%s(%s)=candi\n", id, ip);
        }
    }
}
