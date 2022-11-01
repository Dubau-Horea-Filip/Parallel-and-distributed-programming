import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//The second thread (consumer) will sum up the products computed by the first one.
public class Consumer extends Thread {
    private ReentrantLock lock;
    private Condition flag;
    private ArrayList<Integer> com;
    private boolean ready;

    public Consumer(ReentrantLock lock, Condition f, ArrayList com, boolean ready) {
        this.flag = f;
        this.lock = lock;
        this.com = com;
        this.ready = ready;
    }

    @Override
    public void run() {
        int sum = 0;

        while (!ready) {

            try {
                this.flag.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        this.lock.lock();
        for (int i = 0; i < com.size(); i++) {
            sum += com.get(i);
        }
        System.out.println(sum);
        this.com.clear();


    }
}
