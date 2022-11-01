import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

 class OperationUsingSemaphore {

    private Semaphore semaphore;

    public OperationUsingSemaphore(int slotLimit) {
        semaphore = new Semaphore(slotLimit);
    }

    boolean tryOp() {
        return semaphore.tryAcquire();
    }

    void finishOp() {
        semaphore.release();
    }

}

public class Main {
    public static void main(String[] args) {
        //System.out.println("unSincronised");
       // unSincronised();
       // System.out.println("Sincronised");
        syncronised();

    }

    public static void syncronised() {

        int nrThreads = 8;
        Thread[] threads = new Thread[nrThreads];

        OperationUsingSemaphore mutex = new OperationUsingSemaphore(1);


        Account A = new Account(20, new ReentrantLock());
        Account B = new Account(5, new ReentrantLock());
        Account C = new Account(40, new ReentrantLock());
        Account D = new Account(78, new ReentrantLock());
        List<Account> accounts = new ArrayList<Account>();
        accounts.add(A);
        accounts.add(B);
        accounts.add(C);
        accounts.add(D);


        for (int i = 0; i < nrThreads; ++i) {
            threads[i] = new Thread(new Runnable() {


                @Override
                public void run() {

                    Random rand = new Random();
                    int randomIndex = rand.nextInt(accounts.size());
                    Account sender = accounts.get(randomIndex);
                    randomIndex = rand.nextInt(accounts.size());
                    Account reciver = accounts.get(randomIndex);

                    while (sender.equals(reciver)) {
                        randomIndex = rand.nextInt(accounts.size());
                        reciver = accounts.get(randomIndex);
                    }
                    int random = rand.nextInt(6);

                    Operation op = new Operation(reciver,sender, random);
                    op.runOperationMutex();

                    if(random == 5 ){
                        boolean ok = A.checksum(20)||B.checksum(5)||C.checksum(40)||D.checksum(78);
                    if(ok == true)
                    {
                        System.out.println("cheksum done coreclty");
                    }
                    else {System.out.println("checksum failed");}
                }
                }


            });
        }


        for (int i = 0; i < nrThreads; ++i) {
            threads[i].start();
        }

        for (int i = 0; i < nrThreads; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(A.getBalance());
        System.out.println(B.getBalance());
        System.out.println(C.getBalance());
        System.out.println(D.getBalance());
        System.out.println(A.log());
        System.out.println(B.log());
        System.out.println(C.log());
        System.out.println(D.log());
        boolean ok = A.checksum(20)||B.checksum(5)||C.checksum(40)||D.checksum(78);
        if(ok == true)
        {
            System.out.println("cheksum done coreclty");
        }
        else {System.out.println("checksum failed");}

    }



}


//We have concurrently run transfer operations, to be executer on multiple threads.
// Each operation transfers a given amount of money from one account to someother account,
// and also appends the information about the transfer to the logs of both accounts.
//
//From time to time, as well as at the end of the program, a consistency check shall be executed.
// It shall verify that the amount of money in each account corresponds with the operations records associated to that account,
// and also that all operations on each account appear also in the logs of the source or destination of the transfer.
//
//Two transaction involving distinct accounts must be able to proceed independently (without having to wait for the same mutex).