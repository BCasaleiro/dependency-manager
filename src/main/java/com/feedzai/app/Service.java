package com.feedzai.app;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Service Abstract Class
 *
 */
public abstract class Service implements Runnable
{
    /**
     * List of Services that this Service depends on
     */
    private ArrayList<Integer> dependsOn= new ArrayList<Integer>();
    /**
     * List of Services that depend on this Service
     */
    private ArrayList<Integer> requiredBy= new ArrayList<Integer>();

    protected Boolean running = false;

    protected String master;
    protected String monitor;
    protected int id;
    private Integer ranking;

    /**
     * Service simple constructor
     * @param id        Service id
     * @param master    Lock
     */
    public Service(int id, String master) {
        this.monitor = Integer.toString(id);
        this.id = id;
        this.ranking = 0;
        this.master = master;
    }

    public abstract void start();
    public abstract void service();
    public abstract void stop();

    /**
     * Runnable run method
     * It contains the Service code workflow
     */
    public void run()
    {
        running = true;
        start();

        synchronized (master) {
            master.notify();
        }

        service();
        running = false;
        synchronized (master) {
            master.notify();
        }
    }

    /**
     * Get Service Id
     * @return id        Service id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get Dependencies
     * @return iterator of the dependencies
     */
    public Iterator<Integer> getDependencies()
    {
        return dependsOn.iterator();
    }

    /**
     * Get Number of Dependencies
     * @return size of depends on
     */
    public int getNumberOfDependencies() {
        return dependsOn.size();
    }

    /**
     * Add Dependency
     * @param index index of the new dependency
     */

    public void addDependency(Integer index)
    {
        dependsOn.add(index);
    }

    /**
     * Remove Dependency
     * @param index index of the dependency to remove
     */
    public void removeDependency(Integer index)
    {
        dependsOn.remove(index);
    }

    /**
     * Get Requirements
     * @return iterator of the requirements
     */
    public Iterator<Integer> getRequirements()
    {
        return requiredBy.iterator();
    }

    /**
     * Add Requirement
     * @param index index of the new requirement
     */
    public void addRequirement(Integer index)
    {
        requiredBy.add(index);
    }

    /**
     * Remove Requirement
     * @param index index of the requirement to remove
     */
    public void removeRequirement(Integer index)
    {
        requiredBy.remove(index);
    }

    /**
     * Set Running
     * @param running boolean
     */
    public void setRunning(Boolean running)
    {
        this.running = running;
    }

    /**
     * Is Running
     * @return if Service is running
     */
    public Boolean isRunning()
    {
        return running;
    }

    /**
     * Increment Ranking
     * @param ammount ammount to which ranking should be incremented
     */
    public void incrementRanking(int ammount)
    {
        this.ranking += ammount;
    }

    /**
     * Set Ranking
     * @param ranking ammount to which ranking should be set
     */
    public void setRanking(Integer ranking)
    {
        this.ranking = ranking;
    }

    /**
     * Get Ranking
     * @return ranking
     */
    public Integer getRanking()
    {
        return ranking;
    }
}
