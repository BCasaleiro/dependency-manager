package com.feedzai.app;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Service Interface
 *
 */
public abstract class Service implements Runnable
{
    private ArrayList<Integer> dependsOn= new ArrayList<Integer>();

    protected Boolean shutdown = false;
    protected Boolean running = false;

    public abstract void start();
    public abstract void stop();

    public void run()
    {
        // running = true;
        start();
    }

    public Iterator<Integer> getDependencies() {
        return dependsOn.iterator();
    }

    public int getNumberOfDependencies() {
        return dependsOn.size();
    }

    public void addDependency(Integer index) {
        dependsOn.add(index);
    }

    public void removeDependency(Integer index) {
        dependsOn.remove(index);
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public Boolean isRunning() {
        return running;
    }
}
