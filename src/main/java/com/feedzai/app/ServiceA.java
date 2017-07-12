package com.feedzai.app;

import java.lang.InterruptedException;

/**
 * Dummy Test Service
 * Service A
 */
public class ServiceA extends Service
{
    public ServiceA (String monitor)
    {
        this.monitor = monitor;
    }

    public void start()
    {
        System.out.println("[A] I am here! I am helping!");
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
        System.out.println("[A] Stopping. Bye!");
        synchronized (monitor) {
            monitor.notify();
        }
    }
}
