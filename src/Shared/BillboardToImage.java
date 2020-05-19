package Shared;
import Shared.Billboard;
import org.w3c.dom.css.Rect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Iterator;

public class BillboardToImage {
    private Billboard billboard;
    private Integer resolutionx, resolutiony;

    public BillboardToImage(Billboard billboard, Integer resolutionx, Integer resolutiony){
        this.billboard = billboard;
        this.resolutionx = resolutionx;
        this.resolutiony = resolutiony;
    }
    public ImageIcon Generate() {
        //setting defaults
        Color bg = Color.white, mt = Color.black, it = Color.black;                            //bg = background colour, mt = message text colour, it = info text colour
        Boolean picture = !billboard.getPictureLink().equals("");
        Boolean info = !billboard.getInformationText().equals(""), message = !billboard.getMessageText().equals("");    //boolean determining whether text is present.

        JFrame f = new JFrame();
        BufferedImage bi = new BufferedImage(resolutionx, resolutiony, BufferedImage.TYPE_INT_RGB);
        Rectangle screen = new Rectangle(bi.getWidth(),bi.getHeight());
        Graphics gr = bi.getGraphics();
        Font text = gr.getFont();

        //------------------Background Colouring-----------------//
        if (!billboard.getBackgroundColour().equals("")) {                                     //check for background colour tag
            if (billboard.getBackgroundColour().startsWith("#")) {                          //check for hex tag
                bg = Color.decode(billboard.getBackgroundColour()); }
        }
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
        if (newMessageFontSize >= 170){newMessageFontSize = 170;};
        gr.setFont(new Font("Dialogue", Font.PLAIN, newMessageFontSize));
        if (info && !picture){drawCenteredText(gr, billboard.getMessageText(), new Rectangle(bi.getWidth(), bi.getHeight() / 2));}          //position and draw centred text
        else if(picture || info){
            drawCenteredText(gr, billboard.getMessageText(), new Rectangle(bi.getWidth(), bi.getHeight() / 3)); }
        else drawCenteredText(gr, billboard.getMessageText(), screen);


        //---------------------Info text----------------------//
        if (!billboard.getInformationTextColour().equals("")) {                                   //check if info text colour is specified and set colour
            it = Color.decode(billboard.getInformationTextColour()); }
        gr.setColor(it);

        if (info) {
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
            if (newInfoFontSize >= newMessageFontSize){newInfoFontSize -= 5;}
            gr.setFont(new Font("Dialogue", Font.PLAIN, newInfoFontSize));

            int imageHeight = bi.getHeight();
            int lineHeight = gr.getFontMetrics().getHeight();

            if (message && !picture){                                               //deciding where to draw info text based on which attributes are present
                for (String line : wrapped.split("\n")){
                    drawCenteredText(gr, line, new Rectangle(bi.getWidth(), imageHeight += (lineHeight + 30))); }
            }
            else if(picture || (message && picture)){
                for (String line : wrapped.split("\n")){
                    drawCenteredText(gr, line, new Rectangle(bi.getWidth(), ((imageHeight += lineHeight)*2) - 450 ));}
            }
            else {
                for (String line : wrapped.split("\n")){
                    drawCenteredText(gr, line, new Rectangle(bi.getWidth(), (imageHeight += (lineHeight + 20)*2) - (imageHeight/4) )); }}
        }

        //---------------------------IMAGE PROCESSING----------------------------------------------------------//
        if (picture){
            BufferedImage image = null;
            try{
                URL source = new URL(billboard.getPictureLink());
                image = ImageIO.read(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawScaledImage(gr, image, info, message);
        }

        //ImageIO.write(bi, "png", new File("."));

        return new ImageIcon(bi);

        //.setSize(f.getWidth(), f.getHeight());

        //f.getContentPane().add(l);
        //f.pack();
        //f.setVisible(true);
    }
    public void drawScaledImage(Graphics g, BufferedImage image, Boolean infoText, Boolean messageText){
        double imgHeight = image.getHeight(null), imgWidth = image.getWidth(null);
        int w = resolutionx, h = resolutiony, x=0, y=0, thirdY = h/3;
        double windowAspect=h/w, imageAspect = imgHeight / imgWidth, newHeight, newWidth;
        Rectangle bounds = new Rectangle(0,0,w,h);
        BufferedImage newImage = image;

        if (!infoText && !messageText) {
            windowAspect = (double) h / (double) w;
            if(windowAspect >= imageAspect){
                newHeight = ((double) bounds.width / 2) * imageAspect;
                newImage = scaleImage(image, w/2, (int) newHeight);
                x = (w - newImage.getWidth())/2;
                y = (h - newImage.getHeight())/2;
            }
            else if(imageAspect > windowAspect){
                newWidth = (h/2) / imageAspect;
                newImage = scaleImage(image, (int) newWidth, h/2);
                x = (w - newImage.getWidth())/2;
                y = (h - newImage.getHeight())/2;
            }
        }
        else if(messageText && infoText){
            bounds = new Rectangle(0,thirdY, w, thirdY);
            windowAspect = (double) bounds.height / (double) bounds.width;
            if(windowAspect >= imageAspect){
                newHeight = ((double) bounds.width / 2) * imageAspect;
                newImage = scaleImage(image, w/2, (int) newHeight);
                x = (w - newImage.getWidth())/2;
                y = (bounds.height - newImage.getHeight())/2;
            }
            else if(imageAspect > windowAspect){
                newWidth = (double)bounds.height / imageAspect;
                newImage = scaleImage(image, (int) newWidth, bounds.height);
                x = (w - newImage.getWidth())/2;
                y = (bounds.height + newImage.getHeight())/2;
            }
        }
        else{
            bounds = new Rectangle(0, thirdY, w, thirdY*2);
            windowAspect = (double) bounds.height / (double) bounds.width;
            if(windowAspect >= imageAspect) {
                newHeight = ((double) bounds.width / 2) * imageAspect;
                newImage = scaleImage(image, w/2, (int) newHeight);
                x = (w - newImage.getWidth())/2;
                if(messageText){y = bounds.height - (newImage.getHeight()/2);}
                else if(infoText){
                    y = (bounds.height - newImage.getHeight())/2;
                }
            }
            else if(imageAspect > windowAspect){
                newWidth = (h/2) / imageAspect;
                newImage = scaleImage(image, (int) newWidth, h/2);
                x = (w - newImage.getWidth())/2;
                if(messageText){y = bounds.height - (newImage.getHeight()/2);}
                else if(infoText){
                    y = (bounds.height - newImage.getHeight())/2;
                }
            }
        }
        g.drawImage(newImage, x, y, null);
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

    public static BufferedImage scaleImage(BufferedImage imageToScale, int newWidth, int newHeight) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            scaledImage = new BufferedImage(newWidth, newHeight, imageToScale.getType());
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(imageToScale, 0, 0, newWidth, newHeight, null);
            graphics2D.dispose();
        }
        return scaledImage;
    }
}

