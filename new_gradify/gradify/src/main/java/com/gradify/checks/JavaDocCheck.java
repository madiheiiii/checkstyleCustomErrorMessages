package com.gradify.checks;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

public class JavaDocCheck {
//    private final ViolationCollector data;
//
//    public JavaDocCheck(){
//        this.data = new ViolationCollector();
//    }
//    public ViolationCollector getViolationData(){
//        return data;
//    }
//    @Override
//    public void addError(AuditEvent event) {
//        String source = event.getSourceName();
//        String message = event.getMessage();
//        int line = event.getLine();
//
//        if (source == null) return;
//
//        data.addViolation(line, message);
//        data.addDeduction(1.0);
//        }
//    }
//    @Override
//    public void auditStarted(AuditEvent e) {
//        data.setProjectName(System.getProperty("user.dir"));
//        String fullPath = data.getProjectName();
//        int lastSep = fullPath.lastIndexOf(java.io.File.separator);
//        if (lastSep > 0) {
//            data.setProjectName(fullPath.substring(lastSep + 1));
//        }
//    }
//    @Override public void auditFinished(AuditEvent e) {}
//    @Override public void fileStarted(AuditEvent e) {}
//    @Override public void fileFinished(AuditEvent e) {}
//    @Override public void addException(AuditEvent e, Throwable t) {}
}
