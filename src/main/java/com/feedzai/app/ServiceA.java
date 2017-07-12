package com.feedzai.app;

/**
 * Dummy Test Service
 * Service A
 */
public class ServiceA extends Service
{
    public ServiceA ()
    {

    }

    public void start()
    {
        System.out.println("[A] I am here! I am helping!");
        while (!shutdown) {

        }
    }

    public void stop()
    {
        System.out.println("[A] Stopping. Bye!");
        shutdown = true;
    }
}
