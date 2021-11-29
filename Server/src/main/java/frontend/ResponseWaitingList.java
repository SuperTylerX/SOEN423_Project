package frontend;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResponseWaitingList {

    public static Integer identifier = 0;
    public static ConcurrentHashMap<String, CopyOnWriteArrayList<ConcurrentHashMap<String, String>>> responseMap = new ConcurrentHashMap<>();


    synchronized public static Integer assignIdentifier(){
        return identifier++;
    }
}
