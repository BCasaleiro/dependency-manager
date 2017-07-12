package com.feedzai.app;

/**
 * Dummy Test Service
 * Service B
 */
public class ServiceB extends Service
{
    public ServiceB ()
    {

    }

    public void start()
    {
        System.out.println("[B] I am here! I am helping!");
        while (!shutdown) {

        }
    }

    public void stop()
    {
        System.out.println("[B] Stopping. Bye!");
        shutdown = true;
    }
}
