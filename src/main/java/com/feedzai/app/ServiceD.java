package com.feedzai.app;

/**
 * Dummy Test Service
 * Service D
 */
public class ServiceD extends Service
{
    public ServiceD ()
    {

    }

    public void start()
    {
        System.out.println("[D] I am here! I am helping!");
        while (!shutdown) {

        }
    }

    public void stop()
    {
        System.out.println("[D] Stopping. Bye!");
        shutdown = true;
    }
}
