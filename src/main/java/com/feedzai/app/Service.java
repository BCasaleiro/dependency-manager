package com.feedzai.app;

/**
 * Service Interface
 *
 */
public abstract class Service implements Runnable
{
    protected Boolean shutdown = false;
    public Boolean running = false;

    public abstract void start();
    public abstract void stop();

    public void run()
    {
        running = true;
        start();
    }
}
