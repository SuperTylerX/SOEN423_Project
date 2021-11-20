package repicas.replica1;

import packet.Packet;

import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {

    private int replicaSequenceNumber;
    final public CopyOnWriteArrayList<Packet> tasks;

    public Server() {
        replicaSequenceNumber = 0;
        tasks = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        // Initialized Admin and Student Service
        AdminService adminServiceDVL = new AdminService();
        adminServiceDVL.campusCode = "DVL";
        AdminService adminServiceKKL = new AdminService();
        adminServiceKKL.campusCode = "KKL";
        AdminService adminServiceWST = new AdminService();
        adminServiceWST.campusCode = "WST";

        StudentService studentServiceDVL = new StudentService();
        studentServiceDVL.campusCode = "DVL";
        StudentService studentServiceKKL = new StudentService();
        studentServiceKKL.campusCode = "KKL";
        StudentService studentServiceWST = new StudentService();
        studentServiceWST.campusCode = "WST";

        while (true) {

            for (Packet task : tasks) {
                if (task.getSequenceNumber() == replicaSequenceNumber) {
                    // handle task
                    System.out.println(task);
                    // done!
                    tasks.remove(task);
                    replicaSequenceNumber++;
                }
            }
        }

    }
}
