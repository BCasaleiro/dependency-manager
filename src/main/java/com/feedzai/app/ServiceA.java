package com.feedzai.app;

import java.lang.InterruptedException;

/**
 * Dummy Test Service
 * Service A
 */
public class ServiceA extends Service
{
    public ServiceA (int id, String master)
    {
        super(id, master);
    }

    public void start()
    {
        System.out.println("[A] I am here! I am helping!");
    }

    public void service() {
        try {
            synchronized (monitor) {
                monitor.wait();
            }
        } catch (InterruptedException e) {
            System.out.println("Ohhh noo!");
            Thread.currentThread().interrupt();
        }
    }

    public void stop()
    {
        System.out.println("[A] Stopping. Bye!");
        synchronized (monitor) {
            monitor.notify();
            running = false;
        }
    }
}
