import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
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
        Condition flag  = mutex.newCondition();

        ArrayList v1 = new ArrayList(List.of(3,5,4,1));
        ArrayList v2 = new ArrayList(List.of(2,7,5,2));

        ArrayList vector1 = new ArrayList<>();
        ArrayList vector2 = new ArrayList<>();
        for(int i=1;i<=100;i++)
        {
            vector1.add(i);
            vector2.add(i);
        }
        Queue<Integer> queue = new LinkedList<>();
        AtomicBoolean ready = new AtomicBoolean(false);
        Producer producer = new Producer(mutex,flag,vector1,vector2,queue,ready);
        Consumer consumer = new Consumer(mutex,flag,queue,ready,100);
        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch(InterruptedException ie) {
            System.out.println("Main: " + ie.getMessage());
        }
        System.out.println("Scalar Product is: " + consumer.getSumm() );

    }
}