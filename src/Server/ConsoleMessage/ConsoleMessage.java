package Server.ConsoleMessage;

public abstract class ConsoleMessage {

    /**
     *Console Message class formats specific types of console messages into a standard format. Producing clean console messages
     */
    public ConsoleMessage() {}

    /**
     * Formats and prints a general message over a specific line length.
     * @param header Header of the message
     * @param message Message body
     * @param lineSize Message line size
     */
    public void printGeneral(String header, String message, Integer lineSize) {

        //Print top of the message box
        startMidEndLine(lineSize);

        //Format main message line
        String line = "| " + header + ": ";

        //Print message body
        line = iterateLine(line, message, "|" + " ".repeat(line.length()),lineSize);

        //Print end of message body
        finishLine(line, lineSize);

        //Print bottom of message box
        startMidEndLine(lineSize);
    }

    /**
     * Formats and prints a warning message over a specific line length
     * @param message Message body
     * @param lineSize Message line size
     */
    public void printWarning(String message, Integer lineSize) {

        //Print top of the message box
        startMidEndLine(lineSize);

        //Format start of message
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
