// Each operation record shall have a unique serial number,
// that is incremented for each operation performed in the bank.

import java.util.concurrent.atomic.AtomicInteger;

public class Operation {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);


    private int serialNumber;
    public Account sender;
    public Account reciver;
    public int sum;

    @Override
    public String toString() {
        return
                "{serialNumber: " + serialNumber +
                        ", sender=" + sender.getAccountId() +
                        ", reciver=" + reciver.getAccountId() +
                        ", sum=" + sum + "}";
    }

    public Operation(Account reciver, Account sender, int sum) {
        this.sender = sender;
        this.reciver = reciver;
        this.serialNumber = atomicInteger.incrementAndGet();
        this.sum = sum;

    }


    public void runOperation() {
        sender.setBalance(sender.getBalance() - this.sum);
        reciver.setBalance(reciver.getBalance() + this.sum);
        sender.log.add(this);
        reciver.log.add(this);
    }

    public void runOperationMutex() {



        if (sender.getAccountId() < reciver.getAccountId()) {

            sender.mutex.lock();
            reciver.mutex.lock();
            sender.setBalance(sender.getBalance() - this.sum);
            reciver.setBalance(reciver.getBalance() + this.sum);
            sender.log.add(this);
            reciver.log.add(this);



        } else {
            reciver.mutex.lock();
            sender.mutex.lock();
            reciver.setBalance(reciver.getBalance() + this.sum);
            sender.setBalance(sender.getBalance() - this.sum);
            sender.log.add(this);
            reciver.log.add(this);
//
//            reciver.mutex.unlock();
//            sender.mutex.unlock();
        }
        sender.mutex.unlock();
        reciver.mutex.unlock();
    }


}
