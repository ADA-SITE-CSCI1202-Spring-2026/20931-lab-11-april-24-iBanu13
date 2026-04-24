package WaitNotify;

class SharedResource {
    private int data;
    private boolean bChanged = false;

    public synchronized int get() {
        while (!bChanged) {
            try {
                System.out.println(Thread.currentThread().getName() + " is waiting for data...");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " consumer is interrupted");
            }
        }

        int consumedData = this.data;
        bChanged = false; 
        System.out.println(Thread.currentThread().getName() + " consumed: " + consumedData);
        notify();

        return consumedData;
    }

    public synchronized void set(int value) {
        while (bChanged) {
            try {
                wait(); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        this.data = value;
        bChanged = true;

        notify();
    }
}

class Producer implements Runnable {
    private final SharedResource resource;

    public Producer(SharedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            resource.set(i);
            System.out.println("Produced: " + i);
        }
    }
}

class Consumer implements Runnable {
    private final SharedResource resource;

    public Consumer(SharedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            resource.get();
        }
    }
}

public class task2 {
    public static void main(String[] args) {
        SharedResource resource = new SharedResource();

        Thread producer = new Thread(new Producer(resource));
        Thread consumer = new Thread(new Consumer(resource));

        producer.start();
        consumer.start();
    }
}