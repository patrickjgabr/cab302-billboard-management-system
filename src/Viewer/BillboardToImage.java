package Viewer;
import Shared.Billboard;
import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Iterator;

public class BillboardToImage {
    public static void main(String[] args) {
        int fn = 10;
        File xml = new File("C:/Users/harry/OneDrive - Queensland University of Technology/Documents/Year2/CAB302 - Software Development/Assignment1/ExampleBillboards/" + fn + ".xml");
        Billboard test = GenerateBillboardFromXML.XMLToBillboard(xml, "HARRY");
        Generate(test);
    }

    public static JPanel Generate(Billboard billboard) {
        //setting defaults
        Color bg = Color.white, mt = Color.black, it = Color.black;                            //bg = background colour, mt = message text colour, it = info text colour

        Boolean info = billboard.getInformationText() != "", message = billboard.getMessageText() != "";    //boolean determining whether text is present.

        JFrame f = new JFrame();
        BufferedImage bi = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        Rectangle screen = new Rectangle(bi.getWidth(),bi.getHeight());
        Graphics gr = bi.getGraphics();
        Font text = gr.getFont();

        //------------------Background Colouring-----------------//
        if (!billboard.getBackgroundColour().equals("")) {                                     //check for background colour tag
            bg = Color.decode(billboard.getBackgroundColour()); }
        gr.setColor(bg);                                                                       //set background colour to default or specified.
        gr.fillRect(0, 0, bi.getWidth(), bi.getHeight());                               //fill background colour


        //----------------Message---------------------------//
        if (!billboard.getMessageTextColour().equals("")) {                                     //check for message text colour tag
            mt = Color.decode(billboard.getMessageTextColour()); }
        gr.setColor(mt);                                                                        //set message text colour to default or specified

        int txtWidth = gr.getFontMetrics(text).stringWidth(billboard.getMessageText());         //calculating font size
        int imageWidth = bi.getWidth();
        double messageWidthRatio = (double) imageWidth / (double)txtWidth;
        int newMessageFontSize = (int) (text.getSize() * messageWidthRatio);

        gr.setFont(new Font("Dialogue", Font.PLAIN, newMessageFontSize));
        if (info){drawCenteredText(gr, billboard.getMessageText(), new Rectangle(bi.getWidth(), bi.getHeight() / 2));}          //position and draw centred text
        else drawCenteredText(gr, billboard.getMessageText(), screen);


        //---------------------Info text----------------------//
        if (!billboard.getInformationTextColour().equals("")) {                                   //check if info text colour is specified and set colour
            it = Color.decode(billboard.getInformationTextColour()); }
        gr.setColor(it);

        if (!billboard.getInformationText().equals("")) {

            String wrapped = wrapString(billboard.getInformationText(), "\n", 70);
            String [] lines = wrapped.split("\n");

            int longestStringIndex = 0;
            int lineLength = lines[0].length();
            for(int i =0; i < lines.length; i++){
                if (lines[i].length() > lineLength){ longestStringIndex = i; lineLength = lines[i].length(); }
            }

            int infoTextWidth = gr.getFontMetrics(text).stringWidth(lines[longestStringIndex]);      //calculating max text size
            double infoWidthRatio = imageWidth / (double) infoTextWidth;
            int newInfoFontSize = (int) (text.getSize() * infoWidthRatio);

            if (newInfoFontSize >= 130){ newInfoFontSize = 130;}                                    //don't let info text size exceed 130
            gr.setFont(new Font("Dialogue", Font.PLAIN, newInfoFontSize));

            int imageHeight = bi.getHeight();
            int lineHeight = gr.getFontMetrics().getHeight();
            if (message){
                for (String line : wrapped.split("\n")){
                    drawCenteredText(gr, line, new Rectangle(bi.getWidth(), ((imageHeight += lineHeight)*2) - 600 ));
                }
            }
            else {
                for (String line : wrapped.split("\n")){
                    drawCenteredText(gr, line, new Rectangle(bi.getWidth(), imageHeight += (lineHeight + 30)));
                }
            }
        }
        //ImageIO.write(bi, "png", new File("."));

        JLabel l = new JLabel(new ImageIcon(bi));
        JPanel panel = new JPanel();
        panel.add(l);
        return panel;

        //.setSize(f.getWidth(), f.getHeight());

        //f.getContentPane().add(l);
        //f.pack();
        //f.setVisible(true);
    }

    /**
     * A very basic method to draw centred text to the provided
     * graphics inside a specified rectangular area.
     * @param g graphics to use
     * @param text String of text to be written to the graphics
     * @param rect Rectangle to bound and centre the text in
     */
    public static void drawCenteredText(Graphics g, String text, Rectangle rect) {
        Font font = g.getFont();
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;                          //x co-ordinate
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();       //y co-ordinate
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public static String wrapString(String s, String deliminator, int length) {
        String result = "";
        int previousDelimPos = 0;
        for (String i : s.split(" ", -1)) {
            if (result.length() - previousDelimPos + i.length() > length) {
                result = result + deliminator + i;
                previousDelimPos = result.length() + 1;
            }
            else result += (result.isEmpty() ? "" : " ") + i;
        }
        return result;
    }
}

