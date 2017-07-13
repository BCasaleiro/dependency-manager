package com.feedzai.app;

import java.lang.InterruptedException;

/**
 * Dummy Test Service
 * Service B
 */
public class ServiceB extends Service
{
    public ServiceB (int id, String master)
    {
        super(id, master);
    }

    public void start()
    {
        System.out.println("[B] I am here! I am helping!");
    }

    public void stop()
    {
        System.out.println("[B] Stopping. Bye!");
        synchronized (monitor) {
            monitor.notify();
            running = false;
        }
    }
}
