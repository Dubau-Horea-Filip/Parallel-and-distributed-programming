import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//Create two threads, a producer and a consumer, with the producer feeding the consumer.
//S
//Requirement: Compute the scalar product of two vectors.
//
//Create two threads. The first thread (producer) will compute the products of pairs of elements - one from each vector -
// and will feed the second thread. The second thread (consumer) will sum up the products computed by the first one.
// The two threads will behind synchronized with a condition variable and a mutex.
// The consumer will be cleared to use each product as soon as it is computed by the producer thread.

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        ReentrantLock mutex = new ReentrantLock();
        final Condition flag  = mutex.newCondition();

        ArrayList v1 = new ArrayList(List.of(3,5,4));
        ArrayList v2 = new ArrayList(List.of(2,7,5));
        ArrayList<Integer> c = new ArrayList<>();
        boolean ready = new Boolean(false);
        Thread producer = new Producer(mutex,flag,v1,v2,c,ready);
        Thread consumer = new Consumer(mutex,flag,c,ready);
        producer.run();
        consumer.run();


    }
}