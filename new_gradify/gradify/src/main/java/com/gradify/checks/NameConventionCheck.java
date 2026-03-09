package com.gradify.checks;

import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;

/**
 * An implementation of AuditListener interface which records name convention-related violations from CheckStyle and
 * stores them in a ViolationCollector for grading.
 * Each violation automatically incurs a deduction of 1.0 point from the final grade.
 * The listener also extracts and stores the project name from the file path for reporting.
 *
 * @see AuditListener
 * @see ViolationCollector
 * @see ViolationCollector.Violation
 */
public class NameConventionCheck implements AuditListener{

    /**
     * Returns the ViolationCollector containing all captured name convention violations.
     * This collector can be used after analysis to return violation details, total counts, and total deductions.
     *
     * @return the ViolationCollector instance with all recorded violations
     */
    private final ViolationCollector data;

    public NameConventionCheck(){
        this.data = new ViolationCollector();
    }


    /**
     * Returns the ViolationCollector containing all captured name convention violations.
     * This collector can be used after analysis to return violation details, total counts, and total deductions.
     *
     * @return the ViolationCollector instance with all recorded violations
     */
    public ViolationCollector getViolationData(){
        return data;
    }

    /**
     * Processes a single error event from CheckStyle. This method is called for each violation detected during the audit.
     * It filters for bracket-related violations and records them in the ViolationCollector.
     *
     * For each relevant violation:
     *   The violation is added to the collector with line number, message, and type
     *   A deduction of 1.0 point is automatically applied
     *
     * @param e the AuditEvent containing violation details including source, message, and line number
     */
    @Override
    public void addError(AuditEvent e){
        String source = e.getSourceName();
        String message = e.getMessage();
        int line = e.getLine();
        if (source == null) return;

        /** Determine the specific type of name convention violation*/
        String ViolationType = null;

        if (source.contains("TypeName")){
            ViolationType = "Type Name";
        }
        if (source.contains("MemberName")){
            ViolationType = "Member Name";
        }
        if(source.contains("ParameterName")){
            ViolationType = "Parameter Name";
        }
        if(source.contains("CatchParameterName")) {
            ViolationType = "Catch Parameter";
        }
        if(source.contains("PatternVariableName")){
            ViolationType = "Pattern Variable";
        }
        if(source.contains("LocalVariableName")){
            ViolationType = "Local Variable";
        }
        if(source.contains("ClassTypeParameterName")){
            ViolationType  = "Generic Class Name";
        }
        if(source.contains("MethodTypeParameterName")){
            ViolationType = "Generic Method Name";
        }
        if(source.contains("InterfaceTypeParameterName")){
            ViolationType = "Interface parameter name";
        }
        if(source.contains("AbbreviationAsWordInName")){
            ViolationType = "Number of Capitals in a parameter";
        }

        if(ViolationType != null){
            data.addViolation(line, message, ViolationType);
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
     * @param e the AuditEvent at the end of audit
     */
    @Override public void auditFinished(AuditEvent e){}

    /**
     * Called when a file starts being analyzed.
     * @param e the AuditEvent when a new file starts
     */
    @Override public void fileStarted(AuditEvent e) {}

    /**
     * Called when a file finishes being analyzed.
     * @param e the AuditEvent when a file ends
     */
    @Override public void fileFinished(AuditEvent e){}

    /**
     * Called when an exception occurs during analysis. This is
     * only required by the interface.
     * @param e the AuditEvent associated with the exception
     * @param t the Throwable that was thrown
     */
    @Override public void addException(AuditEvent e, Throwable t){}
}
