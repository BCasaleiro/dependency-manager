package com.feedzai.app;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Hashtable;

/**
 * Dependency Console
 *
 */
public class DepManagerConsole
{
    private static final boolean DEBUG = false;
    private static final String DELIMITER = " ";
    private static final Hashtable<String, Integer> AVAILABLE_SERVICES = new Hashtable<String, Integer>() {{
        put("a", 0);
        put("b", 1);
        put("c", 2);
        put("d", 3);
    }};

    private static void printMenu() {
        System.out.println("Available Services: [a, b, c, d]");
        System.out.println("MENU:");
        System.out.println("start [service]");
        System.out.println("startall");
        System.out.println("stop [service]");
        System.out.println("stopall");
        System.out.println("kill [service]");
        System.out.println("info");
        System.out.println("exit");
        System.out.println("|----------------------------------|");
    }

    private static void printRunningServices( ArrayList<Service> runningServices ) {
        int len = runningServices.size();

        System.out.println("|-----------LIST-----------|");
        for (int i = 0; i < len; i++) {
            System.out.println("ID: " + runningServices.get(i).getId());
        }
        System.out.println("|--------------------------|");
    }

    public static void main( String[] args )
    {
        DepManager dm = new DepManager();
        Scanner scanner;
        String aux;
        String[] cmd;

        dm.instantiateServices();
        if ( DEBUG ) System.out.println("[DEBUG] Instantiated Services.");

        if (dm.loadDependencies()) {
            if ( DEBUG ) System.out.println("[DEBUG] Dependencies Loaded.");

            printMenu();

            scanner = new Scanner(System.in);
            while (!(aux = scanner.nextLine()).equals("exit")) {
                cmd = aux.split(DELIMITER);
                if( cmd[0].equals("start") ) {
                    if ( AVAILABLE_SERVICES.containsKey(cmd[1]) ) {
                        dm.start( AVAILABLE_SERVICES.get(cmd[1]), true );
                    } else {
                        System.out.println("Not an available Service.");
                    }
                } else if ( cmd[0].equals("startall") ) {
                    dm.startAll();
                } else if ( cmd[0].equals("stop") ) {
                    if ( AVAILABLE_SERVICES.containsKey(cmd[1]) ) {
                        dm.stop( AVAILABLE_SERVICES.get(cmd[1]), true );
                    } else {
                        System.out.println("Not an available Service.");
                    }
                } else if ( cmd[0].equals("stopall") ) {
                    dm.stopAll();
                } else if ( cmd[0].equals("kill") ) {
                    if ( AVAILABLE_SERVICES.containsKey(cmd[1]) ) {
                        dm.kill( AVAILABLE_SERVICES.get(cmd[1]) );
                    } else {
                        System.out.println("Not an available Service.");
                    }
                } else if ( cmd[0].equals("info") ) {
                    printMenu();
                } else if ( cmd[0].equals("list") ) {
                    printRunningServices( dm.getRunningServices() );
                } else {
                    System.out.println("Not a valid command.");
                }
            }

            if ( DEBUG ) System.out.println("[DEBUG] Cleaning Up.");
            dm.stopAll();
        } else {
            System.out.println("[ERROR] Invalid dependencies file.");
        }
    }
}
