
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filename = "configs.json";

        List<Config> configurations = new ArrayList<>();

        // Load existing configurations
        System.out.println("Load existing configurations? (yes/no):");
        String loadConfig = scanner.nextLine().trim().toLowerCase();
        if (loadConfig.equals("yes")) {
            try {
                configurations = Config.loadConfig(filename, gson);
                System.out.println("Available configurations:");
                for (int i = 0; i < configurations.size(); i++) {
                    System.out.println((i + 1) + ": " + configurations.get(i));
                }
                System.out.println("Select a configuration to load (1-" + configurations.size() + "):");
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= configurations.size()) {
                    Config selectedConfig = configurations.get(choice - 1);
                    System.out.println("Loaded configuration:\n" + selectedConfig);
                } else {
                    System.out.println("Invalid choice. Starting fresh.");
                }
            } catch (Exception e) {
                System.out.println("Failed to load configurations. Starting fresh.");
            }
        }

        // Create a new configuration
        Config newConfig = new Config();
        System.out.println("Please configure the system:");
        newConfig.setTotalTickets(promptInt(scanner, "Enter total number of tickets:", 1, 1000));
        newConfig.setTicketReleaseRate(promptInt(scanner, "Enter ticket release rate (seconds):", 1, 10));
        newConfig.setCustomerRetrievalRate(promptInt(scanner, "Enter customer retrieval rate (seconds):", 1, 10));
        newConfig.setMaxTicketCapacity(promptInt(scanner, "Enter maximum ticket capacity:", 1, 1000));

        configurations.add(newConfig);

        // Save configurations
        try {
            Config.saveConfig(configurations, filename, gson);
            System.out.println("Configuration saved successfully.");
        } catch (Exception e) {
            System.out.println("Failed to save configuration: " + e.getMessage());
        }

        // Get the number of vendors and customers
        int numVendors = promptInt(scanner, "Enter the number of vendors:", 1, 10);
        int numCustomers = promptInt(scanner, "Enter the number of customers:", 1, 10);

        // Initialize TicketPool
        TicketPool ticketPool = new TicketPool(newConfig.getMaxTicketCapacity());

        // Create threads for vendors and customers
        List<Thread> threads = new ArrayList<>();

        // Create and start vendor threads
        for (int i = 1; i <= numVendors; i++) {
            Thread vendorThread = new Thread(new Vendor(
                    newConfig.getTotalTickets()/ numVendors, // Divide total tickets among vendors
                    newConfig.getTicketReleaseRate(),
                    ticketPool), "Vendor-" + i);
            threads.add(vendorThread);
            vendorThread.start();
        }

        // Create and start customer threads
        for (int i = 1; i <= numCustomers; i++) {
            Thread customerThread = new Thread(new Customer(
                    ticketPool,
                    newConfig.getCustomerRetrievalRate(),
                    newConfig.getTotalTickets() / (2 * numCustomers)), // Divide total purchases among customers
                    "Customer-" + i);
            threads.add(customerThread);
            customerThread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread execution interrupted: " + e.getMessage());
            }
        }

        System.out.println("Ticketing system execution completed.");
    }

    private static int promptInt(Scanner scanner, String message, int min, int max) {
        int value;
        while (true) {
            System.out.print(message + " ");
            try {
                value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) break;
                else System.out.println("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
        return value;
    }
}









//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String filename = "config.json";
//
//        // Configuration Initialization
//        List<Config> configurations  = new ArrayList<>();
//
//        // Load existing configuration
//        System.out.println("Load existing configuration? (yes/no):");
//        String loadConfig = scanner.nextLine().trim().toLowerCase();
//
//        if (loadConfig.equals("yes")) {
//            try {
//                configurations = Config.loadConfig("config.json", gson);
//                System.out.println("Available configurations:");
//                for(int i = 0; i < configurations.size(); i++) {
//                    System.out.println((i+1)+ ": " + configurations.get(i));
//                }
//                System.out.println("Select a configuration to load (1-" + configurations.size() + "):");
//                int choice = Integer.parseInt(scanner.nextLine().trim());
//                if (choice >= 1 && choice <= configurations.size()) {
//                    Config selectedConfig = configurations.get(choice - 1);
//                    System.out.println("Loaded configuration:\n" + selectedConfig);
//                } else {
//                    System.out.println("Invalid choice. Starting fresh.");
//                }
//            } catch (Exception e) {
//                System.out.println("Failed to load configuration. Starting fresh.");
//            }
//        }
//
//        // Create a new configurations
//        Config newConfig = new Config();
//        System.out.println("Please configure the system:");
//        newConfig.setTotalTickets(promptInt(scanner, "Enter total number of tickets:", 1, 1000));
//        newConfig.setTicketReleaseRate(promptInt(scanner, "Enter ticket release rate (seconds):", 1, 10));
//        newConfig.setCustomerRetrievalRate(promptInt(scanner, "Enter customer retrieval rate (seconds):", 1, 10));
//        newConfig.setMaxTicketCapacity(promptInt(scanner, "Enter maximum ticket capacity:", 1, 1000));
//
//        configurations.add(newConfig);
//
//        // Save Configuration
//        try {
//            Config.saveConfig(configurations,"config.json", gson);
//            System.out.println("Configuration saved successfully.");
//        } catch (Exception e) {
//            System.out.println("Failed to save configuration: " + e.getMessage());
//        }
//
//        // Initialize TicketPool
////        TicketPool ticketPool = new TicketPool(newConfig.getMaxTicketCapacity());
////
////        // Start Vendors and Customers
////        Thread vendorThread = new Thread(new Vendor(
////                newConfig.getTotalTickets(),
////                newConfig.getTicketReleaseRate(),
////                ticketPool), "Vendor-1");
////
////        Thread customerThread = new Thread(new Customer(
////                ticketPool,
////                newConfig.getCustomerRetrievalRate(),
////                newConfig.getTotalTickets() / 2), "Customer-1");
////
////        // Start Threads
////        vendorThread.start();
////        customerThread.start();
////
////        try {
////            vendorThread.join();
////            customerThread.join();
////        } catch (InterruptedException e) {
////            System.out.println("Thread execution interrupted: " + e.getMessage());
////        }
////
////        System.out.println("Ticketing system execution completed.");
////    }
//
//        // Initialize TicketPool
//        TicketPool ticketPool = new TicketPool(newConfig.getMaxTicketCapacity());
//
//    // Create a list to hold vendor and customer threads
//        List<Thread> threads = new ArrayList<>();
//
//// Create and start multiple vendor threads
//        int numVendors = 3; // Number of vendors
//        for (int i = 1; i <= numVendors; i++) {
//            Thread vendorThread = new Thread(new Vendor(
//                    newConfig.getTotalTickets() / numVendors, // Divide tickets among vendors
//                    newConfig.getTicketReleaseRate(),
//                    ticketPool), "Vendor-" + i);
//            threads.add(vendorThread);
//            vendorThread.start();
//        }
//
//// Create and start multiple customer threads
//        int numCustomers = 5; // Number of customers
//        for (int i = 1; i <= numCustomers; i++) {
//            Thread customerThread = new Thread(new Customer(
//                    ticketPool,
//                    newConfig.getCustomerRetrievalRate(),
//                    newConfig.getTotalTickets() / (2 * numCustomers)), // Divide purchase attempts
//                    "Customer-" + i);
//            threads.add(customerThread);
//            customerThread.start();
//        }
//
//// Wait for all threads to finish
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                System.out.println("Thread execution interrupted: " + e.getMessage());
//            }
//        }
//
//        System.out.println("Ticketing system execution completed.");
//
//
//        private static int promptInt(Scanner scanner, String message, int min, int max) {
//        int value;
//        while (true) {
//            System.out.print(message + " ");
//            try {
//                value = Integer.parseInt(scanner.nextLine().trim());
//                if (value >= min && value <= max) break;
//                else System.out.println("Value must be between " + min + " and " + max + ".");
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input. Please enter a valid integer.");
//            }
//        }
//        return value;
//    }
//}
//}

