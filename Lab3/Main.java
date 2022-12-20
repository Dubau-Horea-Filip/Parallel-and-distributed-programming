import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;


/*
 * The following approach will be employed (e.g. 9x9 matrix, 4 tasks):
 * Each task takes every k-th element (where k is the number of tasks), going row by row.
 * So, task 0 takes elements (0,0), (0,4), (0,8), (1,3), (1,7), (2,2), (2,6), (3,1), (3,5), (4,0), etc.
 */
public class Main {

    static final int threadCount = 15;

    static final int w1 = 1000;
    static final int h1 = 1000;
    static final int w2 = 1000;
    static final int h2 = 1000;

    static int[][] m1 = new int[h1][w1];
    static int[][] m2 = new int[h2][w2];
    static int[][] mdp = new int[h1][w2];

    public static HashMap<Integer, List<Integer[]>> distribution = new HashMap<>();


    // This function computes the element on line l, column c of the dot product.
    public static void computeElement(int l, int c) {

        mdp[l][c] = 0;

        for (int i = 0; i < w1; i++) {
            mdp[l][c] += m1[l][i] * m2[i][c];
        }
    }

    static void multiplicationThreads() throws InterruptedException {
// Create an actual thread for each task (use the low-level thread mechanism from the programming language);
        long ts1, ts2, delta;

        ts1 = System.currentTimeMillis();

        int i;
        MultiplicationTaskThread[] threads = new MultiplicationTaskThread[threadCount];

        for (i = 0; i < threadCount; i++) {

            MultiplicationTaskThread thread = new MultiplicationTaskThread(i);
            threads[i] = thread;
            thread.start();
        }

        for (i = 0; i < threadCount; i++) {

            threads[i].join();
        }

        ts2 = System.currentTimeMillis();

        delta = ts2 - ts1;

        System.out.println("Execution time with actual threads: " + delta + "ms\n");
    }

    static void multiplicationThreadPool() throws InterruptedException {

        long ts1, ts2, delta;
        int i;

        ts1 = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<MultiplicationTaskCallable> callables = new ArrayList<>(threadCount);

        for (i = 0; i < threadCount; i++) {

            callables.add(new MultiplicationTaskCallable(i));
        }

        executor.invokeAll(callables);
        executor.shutdown();

        ts2 = System.currentTimeMillis();

        delta = ts2 - ts1;

        System.out.println("\nExecution time with thread pool: " + delta + "ms\n");
    }

    static void printMDP() {

        int i, j;
        for (i = 0; i < h1; i++) {

            for (j = 0; j < w2; j++) {

                System.out.print(mdp[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        assert(w1 == h2);
        assert(threadCount < h1*w2);

        int i, j;

        for (i = 0; i < h1; i++) {
            for (j = 0; j < w1; j++) {
                //m1[i][j] = ThreadLocalRandom.current().nextInt(1, 10);
                m1[i][j] = 1;
            }
        }

        for (i = 0; i < h2; i++) {
            for (j = 0; j < w2; j++) {
                //m2[i][j] = ThreadLocalRandom.current().nextInt(1, 10);
                m2[i][j] = 1;
            }
        }

        for (i = 0; i < threadCount; i++) {

            distribution.put(i, new ArrayList<>(2));
        }


        //Each task takes every k-th element (where k is the number of tasks), going row by row.
        // So, task 0 takes elements (0,0), (0,4), (0,8), (1,3), (1,7), (2,2), (2,6), (3,1), (3,5), (4,0), etc.

//        int count = 0;
//        for (i = 0; i < h1; i++) {
//
//            for (j = 0; j < w2; j++) {
//
//                distribution.get(count % threadCount).add(new Integer[]{i ,j});
//                count++;
//            }
//        }

        //Each task computes consecutive elements, going row after row. So, task 0 computes rows 0 and 1, plus elements 0-1 of row 2 (20 elements in total);
        // task 1 computes the remainder of row 2, row 3, and elements 0-3 of row 4 (20 elements); task 2 computes the remainder of row 4, row 5, and elements 0-5 of row 6 (20 elements); finally, task 3 computes the remaining elements (21 elements).

//        int count1 = h1/threadCount;
//        int count2 = 0;
//        int count3 = 0;
//        int cthread = 0;
//        for (i = 0; i < h1; i++) {
//
//            for (j = 0; j < w2; j++) {
//
//                distribution.get(cthread).add(new Integer[]{i ,j});
//                count2++;
//                if(count2 == count1)
//                {
//                    cthread++;
//                    count2 =0;
//                }
//            }
//        }

        //Each task computes consecutive elements, going column after column.
        // This is like the previous example, but interchanging the rows with the columns:
        // task 0 takes columns 0 and 1, plus elements 0 and 1 from column 2, and so on.


        int count11 = w1/threadCount;
        int count22 = 0;
        int count33 = 0;
        int cthread1 = 0;
         for (j = 0; j < w2; j++){

             for (i = 0; i < h1; i++) {

                distribution.get(cthread1).add(new Integer[]{i ,j});
                count22++;
                if(count22 == count11)
                {
                    cthread1++;
                    count22=0;
                }
            }
        }


        multiplicationThreads();

        //printMDP();

        multiplicationThreadPool();

         //printMDP();
    }
}
