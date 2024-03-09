//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: t2w <workingDirectory>");
            return;
        }

        if (new File(args[0] + "/website.site").exists()) {
            System.out.println("Configuration File Found: " + new File(args[0] + "/website.site").getAbsolutePath());
        }
        else {
            System.out.println(ANSI_RED + "No Configuration file Found! Exiting..." + ANSI_RESET);
            return;
        }

        if (new File(args[0] + "/main.txt").exists()) {
            System.out.println("Main File Found: " + new File(args[0] + "/main.txt").getAbsolutePath());
        }
        else {
            System.out.println(ANSI_RED + "No Main file Found! Exiting..." + ANSI_RESET);
            return;
        }

        if (new File(args[0] + "/style.txt").exists()) {
            System.out.println("Style File Found: " + new File(args[0] + "/style.txt").getAbsolutePath());
        }
        else {
            System.out.println(ANSI_RED + "No Style file Found! Exiting..." + ANSI_RESET);
            return;
        }

        String[] mainLines = new String[] {};
        String[] styleLines = new String[] {};
        String[] configLines = new String[] {};

        try (Stream<String> lines = Files.lines(Paths.get(args[0] + "/main.txt"))) {
            mainLines = lines.toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Stream<String> lines = Files.lines(Paths.get(args[0] + "/style.txt"))) {
            styleLines = lines.toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Stream<String> lines = Files.lines(Paths.get(args[0] + "/website.site"))) {
            configLines = lines.toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        buildSite(mainLines, styleLines, configLines, args[0]);
    }

    private static void buildSite(String[] mainLines, String[] styleLines, String[] configLines, String workDir) {
        StringBuilder htmlContent = new StringBuilder();
        StringBuilder cssContent = new StringBuilder();

        // Read Config
        String siteTitle = "Untitled";

        for (var ln : configLines) {
            var test = ln.split(": ")[0].toUpperCase();
            if (ln.split(": ")[0].toUpperCase().equals("TITLE")) {
                siteTitle = ln.split(": ")[1];
            }
        }

        // Head Basic HTML
        htmlContent.append("<!DOCTYPE html>\n");
        htmlContent.append("<html lang=\"en\">\n");
        htmlContent.append("<head>\n");
        htmlContent.append("\t<meta charset=\"UTF-8\">\n");
        htmlContent.append("\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");

        htmlContent.append("\n\t<title>").append(siteTitle).append("</title>\n");
        htmlContent.append("\n\t<link rel=\"stylesheet\" href=\"style.css\">");

        htmlContent.append("\n</head>\n<body>");

        // Body
        for (var ln : mainLines) {
            // h1
            if (ln.toUpperCase().startsWith("[TITLE]")) {
                htmlContent.append("\n\t<h1>").append(ln.split("] ")[1]).append("</h1>");
            } // h2
            else if (ln.toUpperCase().startsWith("[SUBTITLE]")) {
                htmlContent.append("\n\t<h3>").append(ln.split("] ")[1]).append("</h3>");
            } // p
            else if (!ln.isEmpty()) {
                htmlContent.append("\n\t<p>").append(ln).append("</p>");
            }
        }

        // End HTML
        htmlContent.append("\n</body>\n</html>");

        // Save HTML
        try {
            // Write the string content to the file
            Path filePath = Paths.get(workDir + "/site/index.html");
            Files.writeString(filePath, htmlContent);

            System.out.println(ANSI_GREEN + "HTML File Successfully Saved!" + ANSI_RESET);
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Failed Saving HTML File!" + ANSI_RESET);
            e.printStackTrace();
        }

        // Body Style
        cssContent.append("body {");

        for (var ln : styleLines) {
            // color
            if (ln.toUpperCase().trim().startsWith("TEXT COLOR")) {
                cssContent.append("\n\tcolor: ").append(ln.split(" is ")[1].toLowerCase()).append(';');
            }//background
            else if (ln.toUpperCase().trim().startsWith("BACKGROUND")) {
                cssContent.append("\n\tbackground: ").append(ln.split(" is ")[1].toLowerCase()).append(';');
            }
        }

        cssContent.append("\n}");

        // Save CSS
        try {
            // Write the string content to the file
            Path filePath = Paths.get(workDir + "/site/style.css");
            Files.writeString(filePath, cssContent);

            System.out.println(ANSI_GREEN + "CSS File Successfully Saved!" + ANSI_RESET);
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Failed Saving CSS File!" + ANSI_RESET);
            e.printStackTrace();
        }
    }
}