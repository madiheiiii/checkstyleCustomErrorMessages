package com.gradify.checks;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

public class ClassDesignCheck implements AuditListener{
    /** Collector that stores all captured violations and manages deductions. */
    private final ViolationCollector data;

    /**
     * Initializes the ViolationCollector which will store all class design-related violations.
     */
    public ClassDesignCheck() {
        this.data = new ViolationCollector();
    }

    /**
     * Returns the ViolationCollector containing all captured class design violations.
     * This collector can be used after analysis to return violation details, total counts, and total deductions.
     *
     * @return the ViolationCollector instance with all recorded violations
     */
    public ViolationCollector getViolationData() {
        return data;
    }

    /**
     * Processes a single error event from CheckStyle. This method is called for each violation detected during the audit.
     * It filters for class design-related violations and records them in the ViolationCollector.
     *
     * For each relevant violation:
     *   The violation is added to the collector with line number, message, and type
     *   A deduction of 1.0 point is automatically applied
     *
     * @param event the AuditEvent containing violation details including source, message, and line number
     */
    @Override
    public void addError(AuditEvent event) {
        String source = event.getSourceName();
        String message = event.getMessage();
        int line = event.getLine();
        if (source == null) return;

        /** Determine the specific type of class design violation*/
        String checkName = null;
        if (source.contains("FinalClass")) {
            checkName = "FinalClass";
        }
        if (source.contains("ThrowsCount")){
            checkName = "Error regarding number of violations being thrown.";
        }
        if(source.contains("OneTopLevelClass")){
            checkName = "Number of class in each source file";
        }

        /** Store the violation if it matches a class design check. (filters unneeded checks) */
        if (checkName != null) {
            data.addViolation(line, message, checkName);
            data.addDeduction(1.0);
        }
    }

    /**
     * Called when the audit begins. Sets the project name in the ViolationCollector by extracting the final directory
     * name from the current working directory path.
     * @param e the AuditEvent at the start of audit (not typically used)
     */
    @Override
    public void auditStarted(AuditEvent e) {
        data.setProjectName(System.getProperty("user.dir"));
        String fullPath = data.getProjectName();
        int lastSep = fullPath.lastIndexOf(java.io.File.separator);
        if (lastSep > 0) {
            data.setProjectName(fullPath.substring(lastSep + 1));
        }
    }

    /**
     * Called when the audit finishes.
     *
     * @param e the AuditEvent at the end of audit
     */
    @Override public void auditFinished(AuditEvent e) {}

    /**
     * Called when a file starts being analyzed.
     *
     * @param e the AuditEvent when a new file starts
     */
    @Override public void fileStarted(AuditEvent e) {}

    /**
     * Called when a file finishes being analyzed.
     *
     * @param e the AuditEvent when a file ends
     */
    @Override public void fileFinished(AuditEvent e) {}

    /**
     * Called when an exception occurs during analysis. This is
     * only required by the interface.
     *
     * @param e the AuditEvent associated with the exception
     * @param t the Throwable that was thrown
     */
    @Override public void addException(AuditEvent e, Throwable t) {}
}

