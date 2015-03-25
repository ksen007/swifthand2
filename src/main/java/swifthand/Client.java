package swifthand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

class Pair {
    String appPackageName = null;
    LinkedList<String> elist = new LinkedList<String>();
}

class State {
    int id;
    int nTransitions;
    LinkedHashSet<State> transitions[];
    private static ArrayList<State> states = new ArrayList<State>();
    private static Random rand = new Random();
    private static HashSet<Stack<Integer>> banned = new HashSet<Stack<Integer>>();

    State(int id, int nTransitions) {
        this.id = id;
        this.nTransitions = nTransitions;
        this.transitions = new LinkedHashSet[nTransitions];
        states.add(this);
    }

    private boolean addTransition(int tid, State q) {
        assert tid >=0 && tid <nTransitions;
        LinkedHashSet<State> next = transitions[tid];
        if (next == null) {
            next = transitions[tid] = new LinkedHashSet<State>();
        }
        if (next.contains(q)) {
            return false;
        } else {
            next.add(q);
            return true;
        }
    }

    public State addTransition(int tid, int sid, int nTransitions) {
        State tmp;
        if (sid < states.size()) {
            addTransition(tid, tmp = states.get(sid));
        } else {
            assert sid == states.size();
            tmp = new State(sid, nTransitions);
            addTransition(tid, tmp);
        }
        return tmp;
    }

    public static void print() {
        System.out.println("Model");
        System.out.println("-----");
        for (State s: states) {
            System.out.print(s.id+"("+s.nTransitions+")");
            System.out.print(" -> ");
            for(int i=0; i<s.nTransitions;i++) {
                if (s.transitions[i] != null) {
                    if (s.transitions[i].size()>1) {
                        System.out.print("**");
                    }
                    for (State t : s.transitions[i]) {
                        System.out.print("(" + i + "," + t.id + ")");
                    }
                }
            }
            System.out.println();
        }
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    public static int[] generateRandomPermutation (int n){
        int[] array = new int[n];
        for(int i = 0; i < array.length; i++)
            array[i] = i;

        for(int i = 0; i < n; i++){
            int ran = i + rand.nextInt (n-i);
            int temp = array[i];
            array[i] = array[ran];
            array[ran] = temp;
        }
        return array;
    }


    private boolean getSequence(int i, int k, Stack<Integer> trace) {
        if (i < k) {
            int[] randArr = generateRandomPermutation(nTransitions);
            for(int l=0; l<nTransitions; l++) {
                int j = randArr[l];
                if (transitions[j] == null) {
                    trace.push(j);
                    if (banned.contains(trace)) {
                        System.err.println("************************************************");
                        System.err.println("Avoiding sequence "+trace);
                        System.err.println("************************************************");
                        return false;
                    } else
                        return true;
                } else {
                    int length = transitions[j].size();
                    if (length > 1) {
                        int[] randArr2 = generateRandomPermutation(length);
                        Object[] states = transitions[j].toArray();
                        for (int m=0; m < length; m++) {
                            State q = (State)states[randArr2[m]];
                            trace.push(j);
                            trace.push(q.id);
                            if (q.getSequence(i + 1, k, trace)) {
                                return true;
                            } else {
                                trace.pop();
                                trace.pop();
                            }
                        }
                    } else {
                        for (State q:transitions[j]) {
                            trace.push(j);
                            trace.push(q.id);
                            if (q.getSequence(i + 1, k, trace)) {
                                return true;
                            } else {
                                trace.pop();
                                trace.pop();
                            }
                        }
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public Stack<Integer> getNextSequence(int maxLength, Stack<Integer> bad) {
        if (bad != null) {
            banned.add(bad);
        }
        Stack<Integer> tmp = new Stack<Integer>();
        for (int i=1; i<=maxLength; i++) {
            tmp.clear();
            if (getSequence(0, i, tmp)) {
                return tmp;
            }
        }
        return null;
    }
}

interface Strategy {
    public String getNextEvent(LinkedList<String> elist);
}

class ModelBasedStrategy implements  Strategy {
    State root;
    State current;
    Stack<Integer> nextSeq = null;
    int index = -1;
    int lastTid;

    private HashMap<String, Integer> appStateToID = new HashMap<String, Integer>();
    private ArrayList<String> IDToAppState = new ArrayList<String>();


    private int getUniqueAppStateID(LinkedList<String> elist) {
        StringBuilder sb = new StringBuilder();
        for (String next : elist) {
            sb.append(next);
        }
        String state = sb.toString();
        Integer id = appStateToID.get(state);
        if (id == null) {
            id = IDToAppState.size();
            appStateToID.put(state, id);
            IDToAppState.add(state);
        }
        return id;
    }

    public String getNextEvent(LinkedList<String> elist) {
        int id = getUniqueAppStateID(elist);
        int nTransitions = elist.size();
        int i;

        if (root == null) {
            current = root = new State(id, nTransitions);
            index = 1;
            nextSeq = current.getNextSequence(10, null);
            System.out.println("Next sequence "+nextSeq);
            i = nextSeq.get(index-1);
        } else {
            current = current.addTransition(lastTid, id, nTransitions);
            if (index == -1 || index == nextSeq.size() || nextSeq.get(index) != current.id) {
                if (index != -1 && index < nextSeq.size() && nextSeq.get(index) != current.id) {
                    System.err.println("***********************************************************************************************************");
                    System.err.println("Failed to follow sequence = " + nextSeq + ", index = " + index + ", current = " + current.id + ", lastTid = " + lastTid);
                    System.err.println("***********************************************************************************************************");
                    nextSeq = current.getNextSequence(10, nextSeq);
                } else {
                    nextSeq = current.getNextSequence(10, null);
                }
                index = 1;
                System.out.println("Next sequence "+nextSeq);
                i = nextSeq.get(index-1);
            } else {
                index += 2;
                i = nextSeq.get(index-1);
            }
        }
        lastTid = i;
        System.out.println("Picked i = "+i);
        State.print();
        System.err.println("Sequence = " + nextSeq + ", index = " + index + ", current = " + current.id + ", lastTid = " + lastTid);
        return elist.get(i);
    }

}

class RandomStrategy implements Strategy {
    private static long time = System.currentTimeMillis();
    static {
        System.out.println("Seed "+ time);
    }
    private static Random rand = new Random(100);


    public String getNextEvent(LinkedList elist) {
        String evt;
        int i = rand.nextInt(elist.size());
        while ((evt = (String) elist.get(i)).startsWith("text")) {
            i = rand.nextInt(i);
        }
        System.out.println("Picked i = "+i);
        return evt;
    }


}

public class Client {
    Strategy strategy = new ModelBasedStrategy();

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
        for (int i=0; ; i++) {
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
                String event = strategy.getNextEvent(data.elist);
                sendEvent(out, event);
            } else if (data.appPackageName.equals(currentPackageName)) {
                String event = strategy.getNextEvent(data.elist);
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
