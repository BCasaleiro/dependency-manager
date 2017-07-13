package com.feedzai.app;

import java.lang.InterruptedException;

/**
 * Dummy Test Service
 * Service D
 */
public class ServiceD extends Service
{
    public ServiceD (int id, String master)
    {
        super(id, master);
    }

    public void start()
    {
        System.out.println("[D] I am here! I am helping!");
    }

    public void stop()
    {
        System.out.println("[D] Stopping. Bye!");
        synchronized (monitor) {
            monitor.notify();
            running = false;
        }
    }
}
