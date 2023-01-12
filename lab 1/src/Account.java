import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

//Also, each account has an associated log (the list of records of operations performed on that account).
//We have concurrently run transfer operations, to be executer on multiple threads.
// Each operation transfers a given amount of money from one account to someother account,
// and also appends the information about the transfer to the logs of both accounts.
public class Account {


      ReentrantLock mutex;

    public List<Operation> log;
    private int accountId;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);


    public Account(int balance, ReentrantLock mutex) {
        this.balance = balance;
        this.log = new ArrayList<Operation>();
        accountId= atomicInteger.incrementAndGet();
        this.mutex=mutex;
    }


    public int getAccountId() {
        return accountId;
    }

    private int balance;

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }



    public String log()
    {
        return log.toString() + "\n";
    }

    public boolean checksum(int firstsum)
    {

        for(int i = 0; i<log.size();i++)
        {
            if (this.log.get(i).sender.equals(this))
            {
                firstsum-=this.log.get(i).sum;
            }
            else {
                firstsum+=this.log.get(i).sum;
            }
        }
        if(firstsum==this.balance) return true;
        else return false;
    }




}
