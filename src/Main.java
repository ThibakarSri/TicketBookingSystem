import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static volatile boolean isRunning = true; // Flag to control thread execution
    private static Scanner scanner = new Scanner(System.in);
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILENAME = "configs.json";

    public static void main(String[] args) {
        // Load or Create Configuration
        Configuration configuration = loadOrCreateConfiguration();

        // Initialize TicketPool
        TicketPool ticketPool = new TicketPool(configuration.getMaxTicketCapacity());

        // Create threads for vendors and customers
        List<Thread> threads = createThreads(configuration, ticketPool);

        // Control the simulation interactively
        controlSimulation(threads);

        System.out.println("Ticketing system terminated.");
    }

    // Section 1: Load or Create Configuration
    private static Configuration loadOrCreateConfiguration() {
        List<Configuration> configurations = new ArrayList<>();
        Configuration configuration = new Configuration();

        // Prompt to load existing configurations
        System.out.println("Load existing configurations? (yes/no):");
        String loadConfig = scanner.nextLine().trim().toLowerCase();

        if (loadConfig.equals("yes")) {
            try {
                configurations = Configuration.loadConfig(FILENAME, gson);
                System.out.println("Available configurations:");
                for (int i = 0; i < configurations.size(); i++) {
                    System.out.println((i + 1) + ": " + configurations.get(i));
                }
                System.out.println("Select a configuration to load (1-" + configurations.size() + "):");
                int choice = promptInt("", 1, configurations.size());
                configuration = configurations.get(choice - 1);
                System.out.println("Loaded configuration:\n" + configuration);
                return configuration; // Return the loaded configuration
            } catch (Exception e) {
                System.out.println("Failed to load configurations. Starting fresh.");
            }
        }

        // Prompt for user input to create a new configuration
        System.out.println("Please configure the system:");
        configuration.setTotalTickets(promptInt("Enter total number of tickets:", 1, 1000));
        configuration.setTicketReleaseRate(promptInt("Enter ticket release rate (seconds):", 1, 10));
        configuration.setCustomerRetrievalRate(promptInt("Enter customer retrieval rate (seconds):", 1, 10));
        configuration.setMaxTicketCapacity(promptInt("Enter maximum ticket capacity:", 1, 1000));
        configuration.setNumVendors(promptInt("Enter the number of vendors:", 1, 10));
        configuration.setNumCustomers(promptInt("Enter the number of customers:", 1, 10));
        configuration.setMaxTicketsPerCustomer(promptInt("Enter the maximum number of tickets each customer can buy:", 1, configuration.getTotalTickets()));

        // Ask if the user wants to save the configuration
        System.out.println("Do you want to save the configuration? (yes/no):");
        String saveConfig = scanner.nextLine().trim().toLowerCase();
        if (saveConfig.equals("yes")) {
            try {
                configurations = Configuration.loadConfig(FILENAME, gson);
            } catch (Exception ignored) {
                // If the file doesn't exist, start with an empty list
            }
            configurations.add(configuration);
            saveConfigurations(configurations);
        }

        return configuration;
    }

    // Section 2: Create Threads
    private static List<Thread> createThreads(Configuration configuration, TicketPool ticketPool) {
        List<Thread> threads = new ArrayList<>();

        // Create vendor threads
        for (int i = 1; i <= configuration.getNumVendors(); i++) {
            Thread vendorThread = new Thread(new Vendor(
                    configuration.getTotalTickets() / configuration.getNumVendors(),
                    configuration.getTicketReleaseRate(),
                    ticketPool), "Vendor-" + i);
            threads.add(vendorThread);
        }

        // Create customer threads
        for (int i = 1; i <= configuration.getNumCustomers(); i++) {
            Thread customerThread = new Thread(new Customer(
                    ticketPool,
                    configuration.getCustomerRetrievalRate(),
                    configuration.getMaxTicketsPerCustomer()), // Maximum tickets per customer
                    "Customer-" + i);
            threads.add(customerThread);
        }

        return threads;
    }

    // Section 3: Control Simulation
    private static void controlSimulation(List<Thread> threads) {
        System.out.println("Enter 'start' to begin or 'stop' to terminate the process:");
        while (true) {
            String command = scanner.nextLine().trim().toLowerCase();
            if (command.equals("start")) {
                isRunning = true;
                System.out.println("Starting ticket handling process...");
                for (Thread thread : threads) {
                    thread.start();
                }
            } else if (command.equals("stop")) {
                isRunning = false;
                System.out.println("Stopping ticket handling process...");
                break;
            } else {
                System.out.println("Invalid command. Please enter 'start' or 'stop'.");
            }
        }

        // Wait for threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread execution interrupted: " + e.getMessage());
            }
        }
    }

    // Section 4: Save Configurations
    private static void saveConfigurations(List<Configuration> configurations) {
        try {
            Configuration.saveConfig(configurations, FILENAME, gson);
            System.out.println("Configuration saved successfully.");
        } catch (Exception e) {
            System.out.println("Failed to save configuration: " + e.getMessage());
        }
    }

    // Helper: Prompt for Integer Input
    private static int promptInt(String message, int min, int max) {
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

    // Helper: Check if System is Running
    public static boolean isRunning() {
        return isRunning;
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
//    private static volatile boolean isRunning = true; // Flag to control thread execution
//
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String filename = "configs.json";
//
//        List<Configuration> configurations = new ArrayList<>();
//
//        // Load existing configurations
//        System.out.println("Load existing configurations? (yes/no):");
//        String loadConfig = scanner.nextLine().trim().toLowerCase();
//        if (loadConfig.equals("yes")) {
//            try {
//                configurations = Configuration.loadConfig(filename, gson);
//                System.out.println("Available configurations:");
//                for (int i = 0; i < configurations.size(); i++) {
//                    System.out.println((i + 1) + ": " + configurations.get(i));
//                }
//                System.out.println("Select a configuration to load (1-" + configurations.size() + "):");
//                int choice = Integer.parseInt(scanner.nextLine().trim());
//                if (choice >= 1 && choice <= configurations.size()) {
//                    Configuration selectedConfig = configurations.get(choice - 1);
//                    System.out.println("Loaded configuration:\n" + selectedConfig);
//                } else {
//                    System.out.println("Invalid choice. Starting fresh.");
//                }
//            } catch (Exception e) {
//                System.out.println("Failed to load configurations. Starting fresh.");
//            }
//        }
//
//        // Create a new configuration
//        Configuration newConfig = new Configuration();
//        System.out.println("Please configure the system:");
//        newConfig.setTotalTickets(promptInt(scanner, "Enter total number of tickets:", 1, 1000));
//        newConfig.setTicketReleaseRate(promptInt(scanner, "Enter ticket release rate (seconds):", 1, 10));
//        newConfig.setCustomerRetrievalRate(promptInt(scanner, "Enter customer retrieval rate (seconds):", 1, 10));
//        newConfig.setMaxTicketCapacity(promptInt(scanner, "Enter maximum ticket capacity:", 1, 1000));
//
//        configurations.add(newConfig);
//
//        // Save configurations
//        try {
//            Configuration.saveConfig(configurations, filename, gson);
//            System.out.println("Configuration saved successfully.");
//        } catch (Exception e) {
//            System.out.println("Failed to save configuration: " + e.getMessage());
//        }
//
//        // Get the number of vendors and customers
//        int numVendors = promptInt(scanner, "Enter the number of vendors:", 1, 10);
//        int numCustomers = promptInt(scanner, "Enter the number of customers:", 1, 10);
//
//        // Get the maximum tickets each customer can buy
//        int maxTicketsPerCustomer = promptInt(scanner, "Enter the maximum number of tickets each customer can buy:", 1, newConfig.getTotalTickets());
//
//        // Initialize TicketPool
//        TicketPool ticketPool = new TicketPool(newConfig.getMaxTicketCapacity());
//
//        // Create threads for vendors and customers
//        List<Thread> threads = new ArrayList<>();
//
//        for (int i = 1; i <= numVendors; i++) {
//            Thread vendorThread = new Thread(new Vendor(
//                    newConfig.getTotalTickets() / numVendors,
//                    newConfig.getTicketReleaseRate(),
//                    ticketPool), "Vendor-" + i);
//            threads.add(vendorThread);
//        }
//
//        for (int i = 1; i <= numCustomers; i++) {
//            Thread customerThread = new Thread(new Customer(
//                    ticketPool,
//                    newConfig.getCustomerRetrievalRate(),
//                    maxTicketsPerCustomer), "Customer-" + i);
//            threads.add(customerThread);
//        }
//
//        // Interactive Start/Stop Commands
//        System.out.println("Enter 'start' to begin or 'stop' to terminate the process:");
//        while (true) {
//            String command = scanner.nextLine().trim().toLowerCase();
//            if (command.equals("start")) {
//                isRunning = true; // Signal threads to start
//                System.out.println("Starting ticket handling process...");
//                for (Thread thread : threads) {
//                    thread.start();
//                }
//            } else if (command.equals("stop")) {
//                isRunning = false; // Signal threads to stop
//                System.out.println("Stopping ticket handling process...");
//                break;
//            } else {
//                System.out.println("Invalid command. Please enter 'start' or 'stop'.");
//            }
//        }
//
//        // Wait for all threads to terminate
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                System.out.println("Thread execution interrupted: " + e.getMessage());
//            }
//        }
//
//        System.out.println("Ticketing system terminated.");
//    }
//
//    private static int promptInt(Scanner scanner, String message, int min, int max) {
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
//
//    public static boolean isRunning() {
//        return isRunning;
//    }
//}
//
//
