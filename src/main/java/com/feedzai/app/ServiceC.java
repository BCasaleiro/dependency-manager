package com.feedzai.app;

import java.lang.InterruptedException;

/**
 * Dummy Test Service
 * Service C
 */
public class ServiceC extends Service
{
    public ServiceC (String monitor)
    {
        this.monitor = monitor;
    }

    public void start()
    {
        System.out.println("[C] I am here! I am helping!");
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
        System.out.println("[C] Stopping. Bye!");
        synchronized (monitor) {
            monitor.notify();
        }
    }
}
