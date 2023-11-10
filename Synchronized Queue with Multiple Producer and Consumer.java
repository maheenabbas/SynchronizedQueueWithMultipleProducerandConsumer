/*
 * Proj : Implement a Queue class whose enQ and deQ methods are synchronized. 
 * Submission date: May 13, 2023.
 * Project by: Maheen Naqvi
 */


// Importing libraries
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.locks.*;

// Define a class SynchronizedQueue
class SynchronizedQueue {
    // Creating a private variable myqueue
    private Queue<String> myqueue = new LinkedList<>();
    //var capacity for the maximum capacity of queue
    private int capacity;
    //to synchronize accessing to the queue
    private Lock mylock = new ReentrantLock();
    //conditions to tell threads waiting to enqueue or dequeue
    private Condition notFull = mylock.newCondition();
    private Condition notEmpty = mylock.newCondition();
    //keep track of size
    private int size = 0;
    //if the queue is terminated
    private boolean isTerminated = false;


    //constructor of the class
    public SynchronizedQueue(int capacity) {
        this.capacity = capacity;
    }

    //enQueue to add an element
    public void enQueue(String str) throws InterruptedException {
        mylock.lock();
        try {
            // Waiting until queue is not full or terminated
            while (size == capacity && !isTerminated) {
                notFull.await();
            } 
            //if queue is not terminated then remove an element from queue
            // update size, 
            // and notify waiting threads
            if (!isTerminated) {
                myqueue.offer(str);
                size++;
                System.out.println("Enqueued: " + str + " Queue size is: " + size);
                notEmpty.signalAll();
            }
        } finally {
            mylock.unlock();
        }
    }

    //to remove an element
    public String deQueue() throws InterruptedException {
        mylock.lock();
        try {
            // Waiting until queue is not full or terminated
            while (size == 0 && !isTerminated) {
                notEmpty.await();
            }
            //if queue is not terminated then remove an element from queue
            // update size, 
            // and notify waiting threads
            if (!isTerminated) {
                String str = myqueue.poll();
                size--;
                System.out.println("Dequeued: " + str + " Queue size is: " + size);
                notFull.signalAll();
                return str;
            }
            return null;
        } finally {
            mylock.unlock();
        }
    }

    //queue terminated and notify waiting threads
    public void terminate() {
        mylock.lock();
        try {
            isTerminated = true;
            notFull.signalAll();
            notEmpty.signalAll();
        } finally {
            mylock.unlock();
        }
    }

    public static void main(String[] args) {
        //scanner object to read input
        Scanner scanner = new Scanner(System.in);
    
        //ask user for # of producers and consumers
        System.out.print("Enter the number of producers: ");
        int numProducers = scanner.nextInt();
        System.out.print("Enter the number of consumers: ");
        int numConsumers = scanner.nextInt();
    
        //synchronizedQueue maximum capacity of 10
        SynchronizedQueue synchronizedQueue = new SynchronizedQueue(10);
    
        //creating an array of producer threads
        Thread[] producerThreads = new Thread[numProducers];
        //starting each producer thread
        for (int i = 0; i < numProducers; i++) {
            //creating a new producer thread that add 100 items to my queue
            producerThreads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    try {
                        //make a new string and add it to the queue
                        String str = new java.util.Date().toString();
                        synchronizedQueue.enQueue(str);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            producerThreads[i].start();
        }
    
        Thread[] consumerThreads = new Thread[numConsumers];
        //Starting each consumer thread
        for (int i = 0; i < numConsumers; i++) {
            consumerThreads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    try {
                        //removing an item from the queue
                        String str = synchronizedQueue.deQueue();
                        if (str == null) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            consumerThreads[i].start();
        }
    //waiting for both prod and cons threads to be complete
        try {
            for (Thread thread : producerThreads) {
                thread.join();
            }
            for (Thread thread : consumerThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scanner.nextLine();
        //terminate the queue
        synchronizedQueue.terminate();
        // close scanner
        scanner.close();
    }
        
    }
