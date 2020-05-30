package Server.ConsoleMessage;

/**
 *Console Message class formats specific types of console messages into a standard format. Producing clean console messages
 */
public abstract class ConsoleMessage {

    /**
     * Default constructor used to instantiate a ConsoleMessage Object
     */
    public ConsoleMessage() {}

    /**
     * Formats and prints a general message over a specific line length
     * @param header Header of the message
     * @param message Message body
     * @param lineSize Message line size
     */
    public void printGeneral(String header, String message, Integer lineSize) {

        //Print top of the message box
        startMidEndLine(lineSize);

        //Format main message line
        String line = "| " + header + ": ";

        //Print line
        line = iterateLine(line, message, "|" + " ".repeat(line.length()),lineSize);

        //Finish line
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

        //Print line
        if (line.length() <= lineSize) {
            finishLine(line, lineSize);
        }

        //Print bottom of message box
        startMidEndLine(lineSize);
    }

    /**
     * Formats and prints a error message which includes a error code, description and suggested fix over a specific line length
     * @param errorCode Error code
     * @param errorName Error name
     * @param description Message body
     * @param suggestedFix Suggested fix
     * @param lineSize Message line size
     */
    public void printError(Integer errorCode, String errorName, String description, String suggestedFix, Integer lineSize) {

        //Print top of the message box
        startMidEndLine(lineSize);

        //Format message title line
        String line = "| ERROR [" + errorCode + "]: ";
        line = iterateLine(line, errorName, "|               ", lineSize);

        //Print message title line
        if (line.length() <= lineSize) {
            finishLine(line, lineSize);
        }

        //Print mid message separator
        startMidEndLine(lineSize);

        //Format message body line
        line = "| ";

        //Print line
        line = iterateLine(line, description, "| ", lineSize);

        //Finish line
        finishLine(line, lineSize);

        //Print mid message separator
        startMidEndLine(lineSize);

        //Format suggested fix line
        line = "| HINT: ";

        //Print line
        line = iterateLine(line, suggestedFix, "|       ", lineSize);

        //Finish line
        finishLine(line, lineSize);

        //Print end of message box
        startMidEndLine(lineSize);
    }

    //Function which takes the line input, message preBuffer and line length and formats the console prints required
    private String iterateLine(String line, String message, String preLineBuffer, Integer lineLength) {

        //Instantiates a StringBuilder
        StringBuilder lineBuilder = new StringBuilder(line);

        //Iterates through message and adds characters to the lineBuilder
        for(int i = 0; i < message.length(); i++) {

            //If line isn't at the maximum length then add character
            if(lineBuilder.length() < lineLength) {
                lineBuilder.append(message.charAt(i));

            //If line is at the maximum length then print the line and add the character to a blank StringBuilder
            } else {
                lineBuilder.append(" |");
                System.out.println(lineBuilder);
                lineBuilder = new StringBuilder(preLineBuffer + message.charAt(i));
            }
        }

        //Returns what left of the current line
        line = lineBuilder.toString();
        return line;
    }

    //Function which takes an unfinished line that hasn't been printed and prints it
    private void finishLine(String line, Integer lineLength) {

        //Determines the current length
        int currentLength = line.length();

        //Add the number of what white space characters required to fill the rest of the line
        line = line + " ".repeat(Math.max(0, lineLength - currentLength + 1));

        //Add the end of line character
        line = line + "|";

        //Print line
        System.out.println(line);
    }

    //Function which prints out a line of "-" character for the given line size
    private void startMidEndLine (Integer lineSize) {
        System.out.println("--" + "-".repeat(Math.max(0, lineSize)));
    }
}
