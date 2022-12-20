import java.util.concurrent.Callable;

public class MultiplicationTaskCallable implements Callable<Integer> {

    private final int threadNo;

    public MultiplicationTaskCallable(int threadNo) {

        this.threadNo = threadNo;
    }

    @Override
    public Integer call() {

        for (Integer[] point : Main.distribution.get(threadNo)) {

            Main.computeElement(point[0], point[1]);
        }

        return null;
    }
}
