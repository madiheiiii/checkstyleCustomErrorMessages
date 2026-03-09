package com.gradify.checks;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import java.io.File;

public class IndentationAuditListener implements AuditListener {
    private String projectName = "unknown";
    private final ViolationCollector data;

    public IndentationAuditListener() {
        this.data = new ViolationCollector();
    }

    public ViolationCollector getViolationData() {
        return data;
    }

    @Override
    public void addError(AuditEvent event) {
        String source = event.getSourceName();

        if (source != null && source.contains("TabIndentationCheck")) {
            String message = event.getMessage();
            int line = event.getLine();

            String fullMessage = reconstructFullMessage(message, line);
            String violationType = message;
            data.addViolation(line, fullMessage, source);
            data.addDeduction(1.0);
        }
    }
    private String reconstructFullMessage(String messageKey, int line) {
        switch(messageKey) {
            case "spaces.instead":
                return "Spaces are not allowed for indentation - use tabs only";
            case "mixed.indent":
                return "Mixed tabs and spaces in indentation - use tabs only";
            case "wrong.tab.count":
                // This one is trickier - you'd need the expected/found counts
                return "Wrong tab count for indentation";
            default:
                return messageKey;
        }
    }
    @Override
    public void auditStarted(AuditEvent e) {
        data.setProjectName(System.getProperty("user.dir"));
        String fullPath = data.getProjectName();
        int lastSep = fullPath.lastIndexOf(File.separator);
        if (lastSep > 0) {
            data.setProjectName(fullPath.substring(lastSep + 1));
        }
    }
    @Override
    public void auditFinished(AuditEvent e) {}
    @Override
    public void fileStarted(AuditEvent e) {}
    @Override
    public void fileFinished(AuditEvent e) {}
    @Override
    public void addException(AuditEvent e, Throwable t) {}
}