package Server.ConsoleMessage;

public abstract class ConsoleMessage {

    public ConsoleMessage() {}

    public void printGeneral(String header, String message, Integer lineSize) {
        startMidEndLine(lineSize);

        String line = "| " + header + ": ";

        line = iterateLine(line, message, "|" + " ".repeat(line.length()),lineSize);
        finishLine(line, lineSize);
        startMidEndLine(lineSize);
    }

    public void printWarning(String message, Integer lineSize) {

        startMidEndLine(lineSize);
        String line = "| WARNING! ";
        line = iterateLine(line, message, "|          ", lineSize);

        if (line.length() <= lineSize) {
            finishLine(line, lineSize);
        }
        startMidEndLine(lineSize);
    }

    public void printError(Integer errorCode, String errorName, String description, String suggestedFix, Integer lineSize) {

        startMidEndLine(lineSize);
        String line = "| ERROR [" + errorCode + "]: ";
        line = iterateLine(line, errorName, "|               ", lineSize);

        if (line.length() <= lineSize) {
            finishLine(line, lineSize);
        }
        startMidEndLine(lineSize);

        line = "| ";
        line = iterateLine(line, description, "| ", lineSize);
        finishLine(line, lineSize);
        startMidEndLine(lineSize);
        line = "| HINT: ";
        line = iterateLine(line, suggestedFix, "|       ", lineSize);
        finishLine(line, lineSize);
        startMidEndLine(lineSize);
    }

    private String iterateLine(String line, String message, String preLineBuffer, Integer lineLength) {
        StringBuilder lineBuilder = new StringBuilder(line);
        for(int i = 0; i < message.length(); i++) {
            if(lineBuilder.length() < lineLength) {
                lineBuilder.append(message.charAt(i));
            } else {
                lineBuilder.append(" |");
                System.out.println(lineBuilder);
                lineBuilder = new StringBuilder(preLineBuffer + message.charAt(i));
            }
        }
        line = lineBuilder.toString();
        return line;
    }

    private void finishLine(String line, Integer lineLength) {
        int currentLength = line.length();
        line = line + " ".repeat(Math.max(0, lineLength - currentLength + 1));
        line = line + "|";
        System.out.println(line);
    }

    private void startMidEndLine (Integer lineSize) {
        System.out.println("--" + "-".repeat(Math.max(0, lineSize)));
    }
}
