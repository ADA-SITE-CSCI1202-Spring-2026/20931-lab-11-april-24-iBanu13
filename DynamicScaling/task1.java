package DynamicScaling;

class MathTask implements Runnable {
    private final int start;
    private final int end;

    public MathTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        double sum = 0;

        for (int i = start; i < end; i++) {
            for (int j = 0; j < 100; j++) {
                sum += i * i * i + i * j;
            }
        }

        task1.dummysink += sum;
    }
}

public class task1 {
    public static volatile double dummysink = 0;
    public static void main(String[] args) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available cores: " + cores);

        int totalWork = 10_000_000;

        long start1 = System.currentTimeMillis();

        Thread singleThread = new Thread(new MathTask(0, totalWork));
        singleThread.start();
        singleThread.join();

        long end1 = System.currentTimeMillis();
        System.out.println("Time with 1 thread: " + (end1 - start1) + " ms");

        Thread[] threads = new Thread[cores];

        int chunk = totalWork / cores;

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < cores; i++) {
            int start = i * chunk;
            int end = (i == cores - 1) ? totalWork : start + chunk;

            threads[i] = new Thread(new MathTask(start, end));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long end2 = System.currentTimeMillis();
        System.out.println("Time with " + cores + " threads: " + (end2 - start2) + " ms");
    }
}