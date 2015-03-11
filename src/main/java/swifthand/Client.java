package swifthand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

class Pair {
    String appPackageName = null;
    LinkedList elist = new LinkedList();
}

public class Client {
    private static long time = System.currentTimeMillis();
    static {
        System.out.println("Seed "+ time);
    }
    private static Random rand = new Random(time);


    private  String getRandomEvent(LinkedList elist) {
        String evt;
        int i = rand.nextInt(elist.size());
        while ((evt = (String) elist.get(i)).startsWith("text")) {
            i = rand.nextInt(i);
        }
        System.out.println("Picked i = "+i);
        return evt;
    }

    public Pair getDataFromDevice(BufferedReader in) throws IOException {
        String fromServer;
        Pair ret = new Pair();
        boolean first = true;
        while ((fromServer = in.readLine())!= null) {
            System.out.println(fromServer);
            if (fromServer.equals("end"))
                break;
            if (first) {
                first = false;
                ret.appPackageName = fromServer;
            } else {
                ret.elist.addLast(fromServer);
            }
        }
        return ret;
    }

    public void sendEvent(PrintWriter out, String event) {
        System.out.println("Sending:"+event);
        out.println(event);
    }

    public void randomTest(int N, String appName) throws IOException {
        Socket kkSocket = new Socket(Constants.hostName, Constants.SERVERPORT);
        PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));

        int iter = 0;
        String currentPackageName = null;
        for (int i=0; i<N; i++) {
            Pair data = getDataFromDevice(in);
            if (data.appPackageName == null || data.elist.isEmpty()) {
                break;
            }

            if (iter == 0) {
                iter++;
                sendEvent(out, "launch:"+appName);
            } else if (iter == 1) {
                iter++;
                currentPackageName = data.appPackageName;
                String event = getRandomEvent(data.elist);
                sendEvent(out, event);
            } else if (data.appPackageName.equals(currentPackageName)) {
                String event = getRandomEvent(data.elist);
                sendEvent(out, event);
            } else {
                sendEvent(out, "launch:"+appName);
            }
        }
        sendEvent(out, "end");
    }

    public static void main(String[] args) throws IOException {
        Client c = new Client();
        c.randomTest(100, "Settings");
    }

}
