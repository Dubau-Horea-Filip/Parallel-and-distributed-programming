import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
//The first thread (producer) will compute the products of pairs of elements
// - one from each vector - and will feed the second thread.
public class Producer extends Thread{
    private ReentrantLock lock;
    private Condition flag;
    private ArrayList<Integer> vector1;
    private ArrayList<Integer> vector2;
    private ArrayList<Integer> com;
    private boolean ready;


    public Producer(ReentrantLock lock, Condition f, ArrayList v1, ArrayList v2, ArrayList com, boolean ready) {
        this.lock=lock;
        this.flag=f;
        this.vector1 = v1;
        this.vector2=v2;
        this.com=com;
        this.ready = ready;
    }

    @Override
    public void run() {

        lock.lock();
        for(int i =0;i<vector2.size();i++)
        {
            System.out.println(vector1.get(i) * vector2.get(i));
            int prod = vector1.get(i)*vector2.get(i);
            com.add(prod);
        }
        ready = true;
        this.flag.notify();
        lock.unlock();

    }




    // si sa folosesti conditia aia
    // gen cu lock.newCondition()
}
