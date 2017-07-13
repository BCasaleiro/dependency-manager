package com.feedzai.app;

import java.lang.InterruptedException;

/**
 * Dummy Test Service
 * Service C
 */
public class ServiceC extends Service
{
    public ServiceC (int id, String master)
    {
        super(id, master);
    }

    public void start()
    {
        System.out.println("[C] I am here! I am helping!");
    }

    public void stop()
    {
        System.out.println("[C] Stopping. Bye!");
        synchronized (monitor) {
            monitor.notify();
            running = false;
        }
    }
}
