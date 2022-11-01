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
    private int queueSize=5;
    private int summ;


    public Consumer(ReentrantLock lock, Condition f, Queue com, AtomicBoolean ready) {
        this.flag = f;
        this.lock = lock;
        this.queue = com;
        this.ready = ready;
    }

    public int getSumm() {
        return summ;
    }

    @Override
    public void run() {
        this.lock.lock();
        int sum = 0;

                try {
                    for(int index=0;index<3;index++) {
                        while (this.queue.isEmpty()) {
                            System.out.println("The queue is empty. Consumer is waiting");
                            this.flag.await();
                        }
                        int val = queue.remove();
                        sum += val;
                        System.out.println("Consumer consumed -" + sum);
                        //wake up producer thread
                        this.flag.signal();

                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);

                } finally {
                    this.lock.unlock();
                }
            this.summ=sum;

    }
}
