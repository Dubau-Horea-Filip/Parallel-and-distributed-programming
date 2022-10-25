// Each operation record shall have a unique serial number,
// that is incremented for each operation performed in the bank.

import java.util.concurrent.atomic.AtomicInteger;

public class Operation {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    OperationUsingSemaphore mutex;
    private int serialNumber;
    private Account sender;
    private Account reciver;
    private int sum;

    @Override
    public String toString() {
        return
                "{serialNumber: " + serialNumber +
                ", sender=" + sender.getAccountId() +
                ", reciver=" + reciver.getAccountId() +
                ", sum=" + sum+"}" ;
    }

    public Operation(Account reciver, Account sender, int sum,OperationUsingSemaphore mutex) {
        this.sender = sender;
        this.reciver = reciver;
        this.serialNumber = atomicInteger.incrementAndGet();
        this.sum = sum;
        this.mutex=mutex;
    }
    public Operation(Account reciver, Account sender, int sum ) {
        this.sender = sender;
        this.reciver = reciver;
        this.serialNumber = atomicInteger.incrementAndGet();
        this.sum = sum;
        this.mutex=mutex;
    }

    public void runOperation() {
        sender.setBalance(sender.getBalance() - this.sum);
        reciver.setBalance(reciver.getBalance() + this.sum);
        sender.log.add(this);
        reciver.log.add(this);
    }

    public void runOperationMutex() {
        boolean ok=false;
        while (!ok){
        if (mutex.tryOp())
        {
            sender.setBalance(sender.getBalance() - this.sum);
            reciver.setBalance(reciver.getBalance() + this.sum);
            sender.log.add(this);
            reciver.log.add(this);
            ok = true;
            mutex.finishOp();
        }}

    }


}
