public class MultiplicationTaskThread extends Thread {

    private final int threadNo;

    public MultiplicationTaskThread(int threadNo) {
        this.threadNo = threadNo;
    }

    @Override
    public void run() {

        for (Integer[] point : Main.distribution.get(threadNo)) {

            Main.computeElement(point[0], point[1]);
        }
    }
}
