/*
 * Author: Allan Vang
 * Email: anvang6@wisc.edu
 * Course: CS200, Fall 2025
 * Assignment: Project 2
 * Citations: GitHub Copilot and ChatGPT
 * 
 */

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a food entry from the CORGIS Food dataset with macronutrients.
 * Calculates estimated calories using: protein*4 + carbs*4 + fat*9.
 *
 * @author (Allan Vang)
 */

class FoodItem {

    /** The name or description of the food item. */
    private String name;

    /** Grams of protein in this food. */
    private double protein;

    /** Grams of carbohydrates in this food. */
    private double carbs;

    /** Grams of fat (total lipid) in this food. */
    private double fat;

    /** Grams of dietary fiber. */
    private double fiber;

    /** Grams of sugar. */
    private double sugar;

    /**
     * Constructs a new FoodItem using nutrient data from the CSV file.
     *
     * @param name The name of the food
     * @param protein Grams of protein
     * @param carbs Grams of carbohydrates
     * @param fat Grams of fat
     * @param fiber Grams of fiber
     * @param sugar Grams of sugar
     */
    public FoodItem(String name, double protein, double carbs, double fat,
            double fiber, double sugar) {

        this.name = name;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.sugar = sugar;
    }

    /** @return The name of the food. */
    public String getName() {
        return name;
    }

    /** @return Grams of protein. */
    public double getProtein() {
        return protein;
    }

    /** @return Grams of carbohydrates. */
    public double getCarbs() {
        return carbs;
    }

    /** @return Grams of fat. */
    public double getFat() {
        return fat;
    }

    /** @return Grams of fiber. */
    public double getFiber() {
        return fiber;
    }

    /** @return Grams of sugar. */
    public double getSugar() {
        return sugar;
    }

    /**
     * Calculates estimated calories: protein*4 + carbs*4 + fat*9.
     *
     * @return Estimated calories for this food item
     */
    public double getEstimatedCalories() {
        double calories = (protein * 4) + (carbs * 4) + (fat * 9);
        return calories;
    }

    /**
     * Returns a String representation of the food with nutrients.
     *
     * @return String with name, protein, carbs, fat, and calories
     */
    public String toString() {
        return name + " (Protein: " + protein + " g, Carbs: " + carbs
                + " g, Fat: " + fat + " g, Calories≈ " + getEstimatedCalories()
                + ")";
    }
}


/**
 * Nutrition Tracker application for building daily meal lists.
 * Loads CORGIS food data and tracks nutrition totals.
 *
 * @author (Allan Vang)
 */
public class NutritionTracker {

    /**
     * Represents a food item with servings consumed today.
     */
    public static class Entry {
        private final FoodItem item;
        private final double servings;

        /**
         * Constructs an Entry.
         *
         * @param item The food item
         * @param servings Number of servings
         */
        public Entry(FoodItem item, double servings) {
            this.item = item;
            this.servings = servings;
        }

        /** @return The food item. */
        public FoodItem getItem() {
            return item;
        }

        /** @return The servings. */
        public double getServings() {
            return servings;
        }
    }

    /**
     * Main entry point. Loads CSV, displays menu, processes user choices.
     *
     * @param args Optional CSV filename
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Load food data from CSV
        String csvFile = "food.csv";
        ArrayList<FoodItem> allFoods = loadFoodData(csvFile);

        if (allFoods.isEmpty()) {
            System.out.println("Could not load food data from '" + csvFile + "'.");
            // Ask the user to provide a path to the CSV file
            System.out.print("Enter path to CSV file (or press Enter to cancel): ");
            String alt = input.nextLine().trim();
            if (alt.isEmpty()) {
                System.out.println("No CSV provided. Exiting.");
                return;
            }
            allFoods = loadFoodData(alt);
            if (allFoods.isEmpty()) {
                System.out.println("Still could not load CSV from: " + alt + ". Exiting.");
                return;
            }
        }

        System.out.println("Loaded " + allFoods.size() + " food items!\n");

        // Today's entries now include a servings multiplier so totals are accurate
        ArrayList<Entry> todaysFoods = new ArrayList<>();

        boolean running = true;

        while (running) {
            System.out.println("\n=== Nutrition Tracker Menu ===");
            System.out.println("1. Search for a food");
            System.out.println("2. Add food to today's list");
            System.out.println("3. View today's nutrition totals");
            System.out.println("4. Quit");
            // Use a safe prompt to read an integer option
            int choice = promptInt(input, "Choose an option: ", 1, 4);

            if (choice == 1) {
                searchFoods(allFoods, input);

            } else if (choice == 2) {
                addFoodToDay(allFoods, todaysFoods, input);

            } else if (choice == 3) {
                showNutritionTotals(todaysFoods);

            } else if (choice == 4) {
                running = false;

            } else {
                System.out.println("Invalid choice.");
            }
        }

        System.out.println("Goodbye!");
    }

    /**
     * Loads food data from CSV with header-aware parsing.
     *
     * @param filename Path to CSV file
     * @return List of FoodItem objects
     */
    public static ArrayList<FoodItem> loadFoodData(String filename) {
        ArrayList<FoodItem> list = new ArrayList<>();

        try {
            // Read header (if present) and determine whether CSV is long-form.
            Scanner file = new Scanner(new java.io.File(filename));
            String header;

            if (file.hasNextLine()) {
                header = file.nextLine();
            } else {
                header = "";
            }

            String[] headerCols = header.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            // Map simple names to indices by substring matching
            int idxName = 0;
            int idxProtein = -1;
            int idxCarbs = -1;
            int idxFat = -1;
            int idxFiber = -1;
            int idxSugar = -1;

            for (int i = 0; i < headerCols.length; i++) {
                String h = headerCols[i].replaceAll("\"", "").toLowerCase();
                if (h.contains("protein") && (idxProtein == -1)) {
                    idxProtein = i;
                }
                if ((h.contains("carbo") || h.contains("carbs")) && (idxCarbs == -1)) {
                    idxCarbs = i;
                }
                if (h.contains("fat") && (idxFat == -1)) {
                    idxFat = i;
                }
                if (h.contains("fiber") && (idxFiber == -1)) {
                    idxFiber = i;
                }
                if (h.contains("sugar") && (idxSugar == -1)) {
                    idxSugar = i;
                }
                if (h.contains("description") || h.contains("description")) {
                    idxName = i;
                }
            }

            // Simple fallback to the old fixed positions used by the course dataset
            if (idxProtein == -1) {
                idxProtein = 2;
            }
            if (idxCarbs == -1) {
                idxCarbs = 3;
            }
            if (idxFat == -1) {
                idxFat = 4;
            }
            if (idxFiber == -1) {
                idxFiber = 5;
            }
            if (idxSugar == -1) {
                idxSugar = 6;
            }

            while (file.hasNextLine()) {
                String line = file.nextLine();
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                int maxIdx = Math.max(idxName, 
                    Math.max(idxProtein, Math.max(idxCarbs, idxFat)));

                if (parts.length <= maxIdx) {
                    continue;
                }

                String name = parts[idxName].replaceAll("\"", "").trim();
                double protein = parseDoubleSafe(parts[idxProtein]);
                double carbs = parseDoubleSafe(parts[idxCarbs]);
                double fat = parseDoubleSafe(parts[idxFat]);
                double fiber;
                
                if (parts.length > idxFiber) {
                    fiber = parseDoubleSafe(parts[idxFiber]);
                } else {
                    fiber = parseDoubleSafe("");
                }
                double sugar;
                if (parts.length > idxSugar) {
                    sugar = parseDoubleSafe(parts[idxSugar]);
                } else {
                    sugar = parseDoubleSafe("");
                }

                // Heuristic: if a parsed protein is clearly an ID (very large),
                // try to pick better numeric columns
                if (protein > 1000) {
                    double[] numericCandidates = new double[parts.length];
                    int count = 0;
                    for (int i = 0; i < parts.length; i++) {
                        double v = parseDoubleSafe(parts[i]);
                        if ((v >= 0) && (v <= 500)) {
                            numericCandidates[count++] = v;
                        }
                    }
                    if (count >= 3) {
                        protein = numericCandidates[0];
                        carbs = numericCandidates[1];
                        fat = numericCandidates[2];
                    }
                }

                FoodItem item = new FoodItem(name, protein, carbs, fat, fiber, sugar);
                list.add(item);
            }
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("CSV file could not be opened: " + e.getMessage());
        }
        return list;
    }

    /**
     * Safely parses a CSV field to double. Returns 0.0 on error.
     *
     * @param token String to parse
     * @return Parsed double or 0.0
     */
    private static double parseDoubleSafe(String token) {
        if (token == null) {
            return 0.0;
        }

        String s = token.replaceAll("\"", "").trim();

        if (s.isEmpty()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Searches and displays foods matching user input.
     *
     * @param foods List of all foods
     * @param input Scanner for user input
     */
    public static void searchFoods(ArrayList<FoodItem> foods, Scanner input) {
        System.out.print("Enter a search term: ");
        String term = input.nextLine().toLowerCase();

        boolean found = false;

        for (FoodItem f : foods) {
            if (f.getName().toLowerCase().contains(term)) {
                System.out.println(f);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No foods matched your search.");
        }
    }

    /**
     * Prompts user to select food and servings, adds to daily log.
     *
     * @param allFoods List of all foods
     * @param today Today's entries
     * @param input Scanner for user input
     */
    public static void addFoodToDay(ArrayList<FoodItem> allFoods,
            ArrayList<Entry> today, Scanner input) {

        System.out.print("Enter a search term or exact name of the food: ");
        String term = input.nextLine().toLowerCase().trim();

        // Find all matching foods (case-insensitive contains)
        ArrayList<FoodItem> matches = new ArrayList<>();
        
        for (FoodItem f : allFoods) {
            if (f.getName().toLowerCase().contains(term)) {
                matches.add(f);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("No foods matched your search.");
            return;
        }

        // If multiple matches, let the user pick one
        FoodItem chosen;

        if (matches.size() == 1) {
            chosen = matches.get(0);
            System.out.println("Found: " + chosen.getName());
        } else {
            System.out.println("Multiple matches found:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + matches.get(i).getName());
            }
            int pick = promptInt(input, "Choose a number (1-" + matches.size() + "): ",
                    1, matches.size());
            chosen = matches.get(pick - 1);
        }

        // Ask for servings (allow fractional servings)
        double servings = promptDouble(input, "Enter servings (e.g., 1 or 0.5): ");

        today.add(new Entry(chosen, servings));
        System.out.println("Added: " + chosen.getName() + " (x" + servings + ")");
    }

    /**
     * Displays all foods consumed today with nutrition totals.
     *
     * @param today Today's entries
     */
    public static void showNutritionTotals(ArrayList<Entry> today) {
        if (today.isEmpty()) {
            System.out.println("No foods added today.");
            return;
        }

        double totalProtein = 0.0;
        double totalCarbs = 0.0;
        double totalFat = 0.0;
        double totalCalories = 0.0;

        System.out.println("\n=== Today's Foods ===");
        for (int i = 0; i < today.size(); i++) {
            Entry e = today.get(i);
            FoodItem f = e.getItem();
            double s = e.getServings();
            System.out.printf("%2d. %s  x%.2f\n", (i + 1), f.getName(), s);
            totalProtein += f.getProtein() * s;
            totalCarbs += f.getCarbs() * s;
            totalFat += f.getFat() * s;
            totalCalories += f.getEstimatedCalories() * s;
        }

        System.out.println("\n=== Daily Nutrition Totals ===");
        System.out.printf("Protein: %.2f g\n", totalProtein);
        System.out.printf("Carbs:   %.2f g\n", totalCarbs);
        System.out.printf("Fat:     %.2f g\n", totalFat);
        System.out.printf("Calories (estimated): %.2f\n", totalCalories);
    }

    /**
     * Prompts for integer between min and max, re-prompts on invalid input.
     *
     * @param sc Scanner to read from
     * @param prompt Prompt message
     * @param min Minimum valid value
     * @param max Maximum valid value
     * @return Valid integer in range
     */
    private static int promptInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if ((v >= min) && (v <= max)) {
                    return v;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format.");
            }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    /**
     * Prompts for positive double, re-prompts on invalid input.
     *
     * @param sc Scanner to read from
     * @param prompt Prompt message
     * @return Positive double value
     */
    private static double promptDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                double v = Double.parseDouble(line);
                if (v > 0.0) {
                    return v;
                }
            } catch (NumberFormatException e) {
                // continue
            }
            System.out.println("Please enter a positive number (for example: 1 or 0.5).");
        }
    }
}