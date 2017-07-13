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
    private ArrayList<Integer> requiredBy= new ArrayList<Integer>();

    protected Boolean running = false;

    protected String monitor;
    protected int id;
    private Integer ranking;

    public Service(int id) {
        this.monitor = Integer.toString(id);
        this.id = id;
        this.ranking = 0;
    }

    public abstract void start();
    public abstract void stop();

    public void run()
    {
        start();
    }

    public int getId() {
        return id;
    }

    public Iterator<Integer> getDependencies() {
        return dependsOn.iterator();
    }

    public void addDependency(Integer index) {
        dependsOn.add(index);
    }

    public void removeDependency(Integer index) {
        dependsOn.remove(index);
    }

    public Iterator<Integer> getRequirements() {
        return requiredBy.iterator();
    }

    public void addRequirement(Integer index) {
        requiredBy.add(index);
    }

    public void removeRequirement(Integer index) {
        requiredBy.remove(index);
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public Boolean isRunning() {
        return running;
    }

    public void incrementRanking(int ammount) {
        this.ranking += ammount;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Integer getRanking() {
        return ranking;
    }
}
