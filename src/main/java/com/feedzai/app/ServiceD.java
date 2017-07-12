package com.feedzai.app;

import java.lang.InterruptedException;

/**
 * Dummy Test Service
 * Service D
 */
public class ServiceD extends Service
{
    public ServiceD (String monitor)
    {
        this.monitor = monitor;
    }

    public void start()
    {
        System.out.println("[D] I am here! I am helping!");
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
        System.out.println("[D] Stopping. Bye!");
        synchronized (monitor) {
            monitor.notify();
        }
    }
}
