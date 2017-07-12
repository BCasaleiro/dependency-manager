package com.feedzai.app;

/**
 * Dummy Test Service
 * Service C
 */
public class ServiceC extends Service
{
    public ServiceC ()
    {

    }

    public void start()
    {
        System.out.println("[C] I am here! I am helping!");
        while (!shutdown) {

        }
    }

    public void stop()
    {
        System.out.println("[C] Stoping. Bye!");
        shutdown = true;
    }
}
