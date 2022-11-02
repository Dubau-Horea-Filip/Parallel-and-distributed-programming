import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//The second thread (consumer) will sum up the products computed by the first one.
public class Consumer extends Thread {
    private ReentrantLock lock;
    private Condition flag;
    private Queue<Integer> queue;
    private AtomicBoolean ready;
    private int queueSize = 5;
    private int summ;
    private int sum = 0;
    private int size;


    public Consumer(ReentrantLock lock, Condition f, Queue com, AtomicBoolean ready, int size) {
        this.flag = f;
        this.lock = lock;
        this.queue = com;
        this.ready = ready;
        this.size = size;
    }

    public int getSumm() {
        return summ;
    }

    @Override
    public void run() {

        for (int index = 0; index < this.size; index++) {
            get(index);
        }

    }

    public void get(int index) {
        this.lock.lock();


        try {

            while (this.queue.isEmpty()) {
                System.out.println("The queue is empty. Consumer is waiting");
                this.flag.await();
            }
            int val = queue.remove();
            sum += val;
            System.out.println("Consumer consumed -" + val);
            this.flag.signalAll();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } finally {
            this.lock.unlock();
        }
        this.summ = sum;
    }
}
