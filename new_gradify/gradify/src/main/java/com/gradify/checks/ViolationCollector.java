package com.gradify.checks;

import java.util.*;

/**
 * A data collector for storing, organizing, and retrieving code violations.
 *
 * The collector maintains:
 *   A map of violations organized by the name of the check that detected them</li>
 *   A running total of deductions applied</li>
 *   A count of total violations recorded</li>
 *   The name of the project being analyzed</li>
 *
 * @see CurlyBracketCheck
 * @see ViolationCollector.Violation
 */
public class ViolationCollector {
    /**A map which stores the list of all violations */
    private final Map<String, List<Violation>> violations = new HashMap<>();
    /**A double counting for all the points deducted. */
    private double totalDeduction = 0.0;
    /**The number of violations which is recorded across all checks.*/
    private int totalViolations = 0;
    private String projectName = "unknown";

    /**This class includes the location, description, and Type of each violation*/
    public static class Violation {
        public final int line;
        public final String message;

        /**The name of the check which detected this violation. For instance if source contains "LeftCurly" then
         * the checkName is "LeftCurlyCheck" */
        public final String checkName;


        /**
         * This constructor creates a new violation, in case checkName is not available.
         * (might not be needed)
         *
         * @param line the line number which the violation has occured
         * @param message the description of the violation
         * */
        public Violation(int line, String message){
            this(line, message, "unknown");
        }

        /**
         * This constructor, creates a new violation and stores its details.
         * @param line the line number which the violation has occured
         * @param message
         * @param checkName
         */
        public Violation(int line, String message, String checkName) {
            this.line = line;
            this.message = message;
            this.checkName = checkName;
        }
    }

    /**
     * a method that calls the addViolation with three parameters and give the third parameter as unknown so that it
     * could be stored in the list.
     *
     * @param line the line where the violation occurs
     * @param message the description of the violation
     */
    public void addViolation(int line, String message){
        addViolation(line, message, "unknown");
    }

    /**
     * a method that takes each violation and adds them to lists, separated by their violation type.
     *
     * @param line the line where the violation occurs
     * @param message the description of the violation
     * @param checkName the violation type
     */
    public void addViolation(int line, String message, String checkName) {
        /** checks if there's a list available for each violation type and if not, it creates one for it to add to it. */
        List<Violation> list = violations.get(checkName);
        if (list == null) {
            list = new ArrayList<>();
            violations.put(checkName, list);
        }
        list.add(new Violation(line, message, checkName));
        totalViolations++;
    }

    /**
     * adds the weight of each violation to totalDeduction.
     * @param amount the weight of each violation
     */
    public void addDeduction(double amount) {
        totalDeduction += amount;
    }

    /**
     * a getter method which returns a list of all violations stored by {@link #addViolation(int, String, String)} in
     * {@link #violations}
     *
     * @return a list of all violations
     */
    public List<Violation> getViolations() {
        List<Violation> allViolations = new ArrayList<>();
        for (List<Violation> list : violations.values()) {
            allViolations.addAll(list);
        }
        return allViolations;
    }

    /**
     * a getter method which creates a copy map of {@link #violations} which results in a tidy grouped violation view.
     *
     * @return a map of all violations
     */
    public Map<String, List<Violation>> getAllViolations() {
        Map<String, List<Violation>> copy = new HashMap<>();
        /**entrySet() is a Hashmap method which returns a set to iterate through. It saves us loops to iterate through
         * both keys and values*/
        for (Map.Entry<String, List<Violation>> entry : violations.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * a method which returns violations by their type.
     *
     * @param checkName the violation types
     * @return a List of violations divided by their type
     */
    public List<Violation> getViolationsByCheck(String checkName) {
        return violations.getOrDefault(checkName, Collections.emptyList());
    }

    /**
     * @return int for total number of violations
     */
    public int getTotalViolations() {
        return totalViolations;
    }

    /**
     * @return double for total weight of deduction
     */
    public double getTotalDeduction() {
        return totalDeduction;
    }

    /**
     * @return a Map of total number of violation per violation type.
     */
    public Map<String, Integer> getViolationCounts() {
        Map<String, Integer> counts = new HashMap<>();
        violations.forEach((check, list) -> counts.put(check, list.size()));
        return counts;
    }

    /**
     * sets the project name to user.dir
     * @param name a string given to extract the name off of it
     */
    public void setProjectName(String name) {
        this.projectName = name;
    }

    /**
     * @return a String representing the project name
     */
    public String getProjectName() {
        return projectName;
    }
}