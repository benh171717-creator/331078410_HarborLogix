package harborlogix.tools;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * PROVIDED TOOL - DO NOT MODIFY.
 *
 * Run:  java harborlogix.tools.OpenClosedCheck src/harborlogix/ops/Yard.java
 *
 * Scans a source file for the things that break the open/closed rule:
 * concrete subclass names, instanceof, and casts to a cargo type.
 * Your Yard.java must come back clean.
 */
public class OpenClosedCheck {

    private static final List<String> BANNED_NAMES = Arrays.asList(
            "StandardContainer", "RefrigeratedContainer", "HazmatContainer",
            "LiquidTank", "OversizedCargo",
            "ContractClient", "GovernmentClient");

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("usage: java harborlogix.tools.OpenClosedCheck <file.java> [more files]");
            return;
        }

        int totalProblems = 0;

        for (String arg : args) {
            Path path = Paths.get(arg);
            if (!Files.exists(path)) {
                System.out.println("NOT FOUND: " + arg);
                totalProblems++;
                continue;
            }

            List<String> lines = Files.readAllLines(path);
            int problems = 0;
            System.out.println("--- checking " + path.getFileName() + " ---");

            for (int i = 0; i < lines.size(); i++) {
                String raw = lines.get(i);
                String code = stripComment(raw);
                int lineNo = i + 1;

                for (String banned : BANNED_NAMES) {
                    if (code.contains(banned)) {
                        System.out.printf("  line %-4d mentions concrete type '%s'%n", lineNo, banned);
                        problems++;
                    }
                }
                if (code.contains("instanceof")) {
                    System.out.printf("  line %-4d uses instanceof%n", lineNo);
                    problems++;
                }
                if (code.matches(".*\\(\\s*(CargoUnit|Client)\\s*\\).*")) {
                    System.out.printf("  line %-4d contains a cast to a hierarchy type%n", lineNo);
                    problems++;
                }
            }

            if (problems == 0) {
                System.out.println("  CLEAN - this file respects the open/closed rule.");
            } else {
                System.out.println("  " + problems + " problem(s) found.");
            }
            totalProblems += problems;
        }

        System.out.println();
        System.out.println(totalProblems == 0
                ? "OPEN/CLOSED CHECK PASSED"
                : "OPEN/CLOSED CHECK FAILED (" + totalProblems + " problems)");
    }

    private static String stripComment(String line) {
        int idx = line.indexOf("//");
        String s = (idx >= 0) ? line.substring(0, idx) : line;
        s = s.replaceAll("/\\*.*?\\*/", "");
        String trimmed = s.trim();
        if (trimmed.startsWith("*")) {
            return "";
        }
        return s;
    }
}
