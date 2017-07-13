package com.feedzai.app;

import java.lang.InterruptedException;

/**
 * Dummy Test Service
 * Service B
 */
public class ServiceB extends Service
{
    public ServiceB (int id)
    {
        super(id);
    }

    public void start()
    {
        System.out.println("[B] I am here! I am helping!");
        try {
            synchronized (monitor) {
                monitor.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void stop()
    {
        System.out.println("[B] Stopping. Bye!");
        synchronized (monitor) {
            monitor.notify();
        }
    }
}
