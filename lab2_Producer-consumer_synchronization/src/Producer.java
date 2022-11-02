import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//The first thread (producer) will compute the products of pairs of elements
// - one from each vector - and will feed the second thread.
public class Producer extends Thread {
    private ReentrantLock lock;
    private Condition flag;
    private ArrayList<Integer> vector1;
    private ArrayList<Integer> vector2;
    private Queue<Integer> queue;
    private AtomicBoolean ready;
    private int queueSize = 5;


    public Producer(ReentrantLock lock, Condition f, ArrayList v1, ArrayList v2, Queue com, AtomicBoolean ready) {
        this.lock = lock;
        this.flag = f;
        this.vector1 = v1;
        this.vector2 = v2;
        this.queue = com;
        this.ready = ready;
    }

    @Override
    public void run() {


        for (int index = 0; index < vector1.size(); index++) {
            put(index);

        }

    }

    public void put(int index) {
        this.lock.lock();
        try {

            int value = 0;

            while (this.queue.size() >= this.queueSize) {
                System.out.println("The queue is full. Producer is waiting. Size: " + this.queueSize);
                this.flag.await();
            }
            value = vector2.get(index) * vector1.get(index);
            System.out.println("Producer produced- " + value);
            //insert jobs in the list
            queue.add(value);
            this.flag.signalAll();

            //ready.set(true);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            this.lock.unlock();
        }

    }
//        lock.lock();
//        try {
//            for (int i = 0; i < vector2.size(); i++) {
//                System.out.println(vector1.get(i) * vector2.get(i));
//                int prod = vector1.get(i) * vector2.get(i);
//                com.add(prod);
//            }
//            ready = true;
//            this.flag.signal();
//        }finally {
//            lock.unlock();
//        }


}



