package com.feedzai.app;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import java.io.IOException;

/**
 * Dependency Manager
 *
 */
public class DepManager
{
    private static final boolean DEBUG = true;
    private static final String DELIMITER = " ";

    private String monitor;
    private String file;

    private Hashtable<String, Integer> availableServices;
    private ArrayList<Service> services;
    private ArrayList<Service> allServicesOrder;
    private ArrayList<Thread> threads;

    /**
    * Basic Dependency Manager Constructor
    *
    */
    public DepManager(String monitor, String file)
    {
        this.monitor = monitor;
        this.file = file;
    }

    /**
    * Instantiate Available Services
    *
    */
    public void instantiateServices(ArrayList<Service> services, Hashtable<String, Integer> availableServices)
    {
        this.services = new ArrayList<Service>();
        this.services.addAll(services);

        this.availableServices = new Hashtable<String, Integer>();
        this.availableServices.putAll(availableServices);

        this.threads = new ArrayList<Thread>();
        for (int i = 0; i < services.size(); i++) {
            this.threads.add(new Thread());
        }
    }

    /**
    * Load dependencies
    * @return           if there are problems in the file
    */
    public Boolean loadDependencies()
    {
        FileReader fr = null;
        BufferedReader br = null;
        String line;
        String[] dependency;

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                dependency = line.split(DELIMITER);

                /* Check if the input is valid */
                if ( availableServices.containsKey(dependency[0]) && availableServices.containsKey(dependency[1]) ) {
                    /* Add dependency */
                    services.get( availableServices.get(dependency[0]) ).addDependency( availableServices.get(dependency[1]) );
                    /* Add requirement */
                    services.get( availableServices.get(dependency[1]) ).addRequirement( availableServices.get(dependency[0]) );
                } else {
                    return false;
                }
			}
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
        		if (br != null) {
                    br.close();
                }

        		if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
		}

        /* Compute the ranking of each Service */
        this.updateAllRankings();

        return true;
    }

    /**
    * Update Ranking of a Service and its dependencies
    *
    */
    private void updateRanking(int index)
    {
        Iterator<Integer> it = services.get(index).getDependencies();
        int serviceIndex;

        while ( it.hasNext() ) {
            serviceIndex = it.next();
            /* Compute every rank of the node descendents */
            if ( services.get(serviceIndex).getRanking() == 0 ) {
                updateRanking( serviceIndex );
            }
            /* The Service ranking is incremented by each of its dependencies ranking + 1 for each */
            services.get(index).incrementRanking( services.get(serviceIndex).getRanking() + 1 );
        }
    }

    /**
    * Update Rankings
    * Sort the Services in an Ascending order of ranking
    */
    private void updateAllRankings()
    {

        for (int i = 0; i < availableServices.size(); i++) {
            if ( services.get(i).getRanking() == 0 ) {
                updateRanking(i);
            }
        }

        /* Sort Services by ranking */
        allServicesOrder = new ArrayList<Service>(services);
        Collections.sort(allServicesOrder, new Comparator<Service>() {
            @Override
            public int compare(Service a, Service b)
            {
                return a.getRanking().compareTo(b.getRanking());
            }
        });
    }

    /**
    * Start Service
    * @param  index     receive Service index to start
    * @param  noWait    boolean indicating if the wait is needed
    */
    public void start( int index, boolean noWait )
    {
        Service service = services.get(index);
        Iterator<Integer> dependencies;
        Integer dependencyIndex;

        if (service.isRunning()) {
            return;
        }

        dependencies = service.getDependencies();
        while (dependencies.hasNext()) {
            dependencyIndex = dependencies.next();

            /* Guarantee that every dependency is met */
            if ( DEBUG ) System.out.println("[DEBUG] " + index + " depends on " + dependencyIndex + ".");
            if ( !services.get( dependencyIndex ).isRunning() ) {
                start(dependencyIndex, false);
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Starting service " + index + ".");
        threads.set(index,new Thread(service));
        /* Check if it can be preformed in parallel */
        if ( noWait ) {
            threads.get(index).start();
        } else {
            try {
                synchronized (monitor) {
                    threads.get(index).start();
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                System.out.println("Gotta go, can't wait!");
                Thread.currentThread().interrupt();
            }
        }


        if ( DEBUG ) System.out.println("[DEBUG] Service " + index + " started.");
    }

    /**
    * Start all Services
    *
    */
    public void startAll()
    {
        int servicesSize = allServicesOrder.size();

        if ( DEBUG ) System.out.println("[DEBUG] Starting all Services.");

        for (int i = 0; i < servicesSize; i++) {
            if ( !allServicesOrder.get(i).isRunning() ) {
                start(allServicesOrder.get(i).getId(), ( i > 0 && allServicesOrder.get(i - 1).getRanking() == allServicesOrder.get(i).getRanking() ) );
            }
        }
    }

    /**
    * Stop Service
    * @param  index     receive Service index to stop
    * @param  noWait    boolean indicating if the wait is needed
    */
    public void stop( int index, boolean noWait )
    {
        Service service = services.get(index);
        Iterator<Integer> requirements;
        Integer requirementIndex;

        requirements = service.getRequirements();
        while (requirements.hasNext()) {
            requirementIndex = requirements.next();

            /* Guarantee that every requirement is met */
            if ( DEBUG ) System.out.println("[DEBUG] " + index + " required by " + requirementIndex + ".");
            if ( services.get( requirementIndex ).isRunning() ) {
                stop(requirementIndex, false);
            }
        }

        /* Check if it can be preformed in parallel */
        if ( DEBUG ) System.out.println("[DEBUG] Stopping service " + index + ".");
        if (noWait) {
            service.stop();
        } else {
            try {
                synchronized (monitor) {
                    service.stop();
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                System.out.println("Gotta go, can't wait!");
                Thread.currentThread().interrupt();
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Service " + index + " stopped.");
    }

    /**
    * Stop all Services
    *
    */
    public void stopAll()
    {
        int servicesSize = allServicesOrder.size();

        if ( DEBUG ) System.out.println("[DEBUG] Stopping all Services.");

        for (int i = servicesSize - 1; i >= 0; i--) {
            if ( services.get(i).isRunning() ) {
                stop(allServicesOrder.get(i).getId(), ( i < (servicesSize - 1) && allServicesOrder.get(i + 1).getRanking() == allServicesOrder.get(i).getRanking() ) );
            }
        }
    }

    /**
    * Kill a Service
    * @param  index  receive Service index to kill
    */
    public void kill( int index )
    {
        Service service = services.get(index);
        Iterator<Integer> requirements;
        Integer requirementIndex;

        if ( !services.get(index).isRunning() ) {
            return;
        }

        /* kill all the requirements first */
        requirements = service.getRequirements();
        while (requirements.hasNext()) {
            requirementIndex = requirements.next();

            if ( DEBUG ) System.out.println("[DEBUG] " + index + " required by " + requirementIndex + ".");
            if ( services.get( requirementIndex ).isRunning() ) {
                kill(requirementIndex);
            }
        }

        /* kill the desired service */
        if ( DEBUG ) System.out.println("[DEBUG] Killing service " + index + ".");
        services.get(index).setRunning(false);
        threads.get(index).interrupt();
        if ( DEBUG ) System.out.println("[DEBUG] Service " + index + " killed.");
    }

    /**
    * List all the running Services
    * @return  list of running services
    */
    public ArrayList<Service> getRunningServices()
    {
        ArrayList<Service> output = new ArrayList<Service>();
        Service service;
        int len = services.size();

        for (int i = 0; i < len; i++) {
            service = services.get(i);
            if ( service.isRunning() ) {
                output.add(service);
            }
        }

        return output;
    }

}
