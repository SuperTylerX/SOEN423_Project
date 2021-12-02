package frontend;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResponseWaitingList {

    public static Integer identifier = 0;
    public static ConcurrentHashMap<String, CopyOnWriteArrayList<ConcurrentHashMap<String, String>>> responseMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Integer> error_count = new ConcurrentHashMap<>();


    synchronized public static Integer assignIdentifier(){
        return identifier++;
    }

    public static void initialize_error_count() {
        error_count.put("R1", 0);
        error_count.put("R2", 0);
        error_count.put("R3", 0);
        error_count.put("R4", 0);
    }


}
