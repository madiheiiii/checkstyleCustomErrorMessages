package com.gradify.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.*;

public class TabIndentationCheck extends AbstractCheck {

    private final List<IndentationViolation> capturedViolations = new ArrayList<>();
    private final Map<String, Double> violationWeights = new HashMap<>();
    private double totalDeduction = 0.0;
    private int totalViolations = 0;

    public static final String wrongTabCount = "wrong.tab.count";
    public static final String spacesInsteadOfTabs = "spaces.instead";
    public static final String mixedIndent = "mixed.indent";

    public TabIndentationCheck() {
        violationWeights.put(wrongTabCount, 1.0);
        violationWeights.put(spacesInsteadOfTabs, 1.0);
        violationWeights.put(mixedIndent, 1.0);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
                TokenTypes.LITERAL_IF,
                TokenTypes.LITERAL_FOR,
                TokenTypes.LITERAL_WHILE,
                TokenTypes.LITERAL_DO,
                TokenTypes.LITERAL_TRY,
                TokenTypes.LITERAL_CATCH,
                TokenTypes.LITERAL_FINALLY,
                TokenTypes.LITERAL_SWITCH,
                TokenTypes.LITERAL_CASE,
                TokenTypes.LITERAL_DEFAULT,
                TokenTypes.LITERAL_CLASS,
                TokenTypes.LITERAL_INTERFACE,
                TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[0];
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        capturedViolations.clear();
        totalDeduction = 0.0;
        totalViolations = 0;
    }

    @Override
    public void visitToken(DetailAST ast) {
        checkIndentation(ast);
    }

    public void printDetailedViolations() {
        for (IndentationViolation v : capturedViolations) {
            System.out.println(v.toString());  // This WILL show the full message
        }
    }

    private void checkIndentation(DetailAST ast) {
        int lineNo = ast.getLineNo();
        String line = getLine(lineNo - 1);

        if (line.trim().isEmpty()) return;

        int tabCount = 0;
        int spaceCount = 0;
        boolean hasMixed = false;
        int index = 0;

        while (index < line.length()) {
            char c = line.charAt(index);
            if (c == '\t') {
                if (spaceCount > 0) {
                    hasMixed = true; // Spaces then tabs
                }
                tabCount++;
                index++;
            } else if (c == ' ') {
                if (tabCount > 0) {
                    hasMixed = true;
                }
                spaceCount++;
                index++;
            } else {
                break;
            }
        }

        if (spaceCount > 0) {
            String message = "Spaces are not allowed for indentation - use tabs only";
            log(ast, spacesInsteadOfTabs, message);
            addViolation(lineNo, message, spacesInsteadOfTabs, spaceCount, 0);
            return; // Skip further checks - this line already has an error
        }

        if (hasMixed) {
            String message = "Mixed tabs and spaces in indentation - use tabs only";
            log(ast, mixedIndent, message);
            addViolation(lineNo, message, mixedIndent, tabCount + spaceCount, tabCount);
            return;
        }

        int expectedTabs = getNestingLevel(ast);

        if (tabCount != expectedTabs) {
            String message = String.format("Expected %d tab(s) for indentation, but found %d",
                    expectedTabs, tabCount);
            log(ast, wrongTabCount, message);
            addViolation(lineNo, message, wrongTabCount, tabCount, expectedTabs);
        }
    }

    /**
     * Calculate how many tabs should be at this nesting level
     */
    private int getNestingLevel(DetailAST ast) {
        int level = 0;
        DetailAST parent = ast.getParent();

        while (parent != null) {
            // Each block increases nesting level
            if (isBlock(parent)) {
                level++;
            }
            parent = parent.getParent();
        }

        return level;
    }

    /**
     * Determine if an AST node represents a block that should increase indentation
     */
    private boolean isBlock(DetailAST ast) {
        int type = ast.getType();
        return type == TokenTypes.LITERAL_IF ||
                type == TokenTypes.LITERAL_FOR ||
                type == TokenTypes.LITERAL_WHILE ||
                type == TokenTypes.LITERAL_DO ||
                type == TokenTypes.LITERAL_TRY ||
                type == TokenTypes.LITERAL_CATCH ||
                type == TokenTypes.LITERAL_FINALLY ||
                type == TokenTypes.LITERAL_SWITCH ||
                type == TokenTypes.LITERAL_CLASS ||
                type == TokenTypes.LITERAL_INTERFACE ||
                type == TokenTypes.METHOD_DEF ||
                type == TokenTypes.CTOR_DEF ||
                type == TokenTypes.OBJBLOCK ||
                type == TokenTypes.SLIST;
    }

    public void addViolation(int line, String message, String checkName) {
        capturedViolations.add(new IndentationViolation(line, message, checkName));
        double weight = violationWeights.getOrDefault(checkName, 1.0);
        totalDeduction += weight;
        totalViolations++;
    }

    public void addViolation(int line, String message, String checkName, int found, int expected) {
        capturedViolations.add(new IndentationViolation(line, message, checkName, found, expected));
        double weight = violationWeights.getOrDefault(checkName, 1.0);
        totalDeduction += weight;
        totalViolations++;
    }

    public double calculateGrade(double baseScore) {
        double maxDeduction = baseScore * 0.3;
        double actualDeduction = Math.min(totalDeduction, maxDeduction);
        return Math.max(0, baseScore - actualDeduction);
    }

    public Map<String, Integer> getViolationSummary() {
        Map<String, Integer> summary = new HashMap<>();
        for (IndentationViolation v : capturedViolations) {
            summary.merge(v.violationType, 1, Integer::sum);
        }
        return summary;
    }

    public List<IndentationViolation> getCapturedViolations() {
        return new ArrayList<>(capturedViolations);
    }

    public int getCapturedViolationCount() {
        return capturedViolations.size();
    }

    public double getTotalDeduction() {
        return totalDeduction;
    }

    public void clearCapturedViolations() {
        capturedViolations.clear();
        totalDeduction = 0.0;
        totalViolations = 0;
    }

    public Map<String, Double> getViolationWeights() {
        return violationWeights;
    }

    public static class IndentationViolation {
        public final int line;
        public final String message;
        public final String violationType;
        public final int found;
        public final int expected;
        public final boolean hasDetailedInfo;

        public IndentationViolation(int line, String message, String violationType) {
            this.line = line;
            this.message = message;
            this.violationType = violationType;
            this.found = 0;
            this.expected = 0;
            this.hasDetailedInfo = false;
        }

        public IndentationViolation(int line, String message, String violationType, int found, int expected) {
            this.line = line;
            this.message = message;
            this.violationType = violationType;
            this.found = found;
            this.expected = expected;
            this.hasDetailedInfo = true;
        }


        @Override
        public String toString() {
            if (hasDetailedInfo) {
                return String.format("Line %d: %s (found: %d, expected: %d)",
                        line, message, found, expected);
            }
            return String.format("Line %d: %s", line, message);
        }
    }
}