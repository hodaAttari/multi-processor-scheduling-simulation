import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class ProcessorSimulation {
    static List<Processor> processors = new ArrayList<>();
    static PriorityQueue<Task> tasks = new PriorityQueue<>((t1, t2) -> Integer.compare(t2.value, t1.value)); // Priority queue to prioritize tasks by value
    static final ReentrantLock lock = new ReentrantLock();
    static final Semaphore mutex = new Semaphore(1);
    static boolean ready = false;
    static boolean stop = false;
    static int currentTime = 0;

    // Read processor data from a CSV file
    public static void readProcessors(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            boolean isFirstLine = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                String[] parts = line.split(",");
                Processor processor = new Processor(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])
                );
                processors.add(processor);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Read task data from a CSV file
    public static void readTasks(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            boolean isFirstLine = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                String[] parts = line.split(",");
                Task task = new Task(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]),
                        Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]),
                        Integer.parseInt(parts[6])
                );
                tasks.add(task);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Function to be run by each processor thread
    public static void processorFunction(Processor processor) {
        while (true) {
            try {
                mutex.acquire();

                // Try to assign new tasks to the processor
                List<Task> toRemove = new ArrayList<>();
                for (Task task : tasks) {
                    if (task.arrivalTime <= currentTime && task.deadline > currentTime &&
                            task.requiredCache <= processor.cache &&
                            task.requiredMemory <= processor.memory &&
                            task.requiredFrequency <= processor.frequency) {

                        processor.taskQueue.add(task);
                        toRemove.add(task);
                        break;
                    }
                }
                tasks.removeAll(toRemove);
                mutex.release();

                // Check if there are tasks to process
                if (!processor.taskQueue.isEmpty()) {
                    Task task = processor.taskQueue.poll();
                    System.out.println("Current time: " + currentTime + ", Processor " + processor.id + " is executing Task " + task.id);
                    Thread.sleep(10);  // Simulate task execution time
                } else {
                    //Thread.sleep(10);  // Simulate idle time
                }

                mutex.acquire();
                if (stop && processor.taskQueue.isEmpty()) {
                    mutex.release();
                    break;
                }
                mutex.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        readProcessors("C:\\Users\\user\\Desktop\\os\\src\\processors.csv");
        readTasks("C:\\Users\\user\\Desktop\\os\\src\\tasks.csv");

        List<Thread> threads = new ArrayList<>();
        for (Processor processor : processors) {
            Thread t = new Thread(() -> processorFunction(processor));
            System.out.println("Starting processor " + processor.id);
            threads.add(t);
            t.start();
        }

        while (true) {
            Thread.sleep(10);
            currentTime++;  // Increment the global time

            // Check if there are no more tasks to be processed
            mutex.acquire();
            if (tasks.isEmpty() && processors.stream().allMatch(p -> p.taskQueue.isEmpty())) {
                stop = true;
                mutex.release();
                break;
            }
            mutex.release();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All tasks have been processed and all processors have stopped.");
    }
}
