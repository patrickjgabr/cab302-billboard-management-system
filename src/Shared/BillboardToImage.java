package Shared;
import ControlPanel.Client;
import Shared.Billboard;
import org.w3c.dom.css.Rect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.io.ByteArrayInputStream;
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

    /**
     * Default billboard to image constructor
     * @param billboard Billboard to be converted for display
     * @param resolutionx image width resolution
     * @param resolutiony image height resolution
     */
    public BillboardToImage(Billboard billboard, Integer resolutionx, Integer resolutiony){
        this.billboard = billboard;
        this.resolutionx = resolutionx;
        this.resolutiony = resolutiony;
    }

    /**
     * Constructs JPanel for displaying image in. Used in billboard viewer.
     * @return JPanel containing a JLabel with an imageicon of the billboard.
     */
    public JPanel toJPanel() {
        JLabel l = new JLabel(Generate());
        JPanel panel = new JPanel();
        panel.add(l);
        return panel;
    }

    /**
     * For creating billboard as imageIcon. Used in control panel.
     * @return ImageIcon
     */
    public ImageIcon toImageIcon() {
        return Generate();
    }

    /**
     * method to handle creation of billboard. First checks for colour tags before drawing text in appropriate colours, then places text
     *  in the right position according to other present elements.
     * @return ImageIcon containing message text, info text and picture where applicable.
     */
    private ImageIcon Generate() {
        //setting defaults
        Color bg = Color.white, mt = Color.black, it = Color.black;                            //bg = background colour, mt = message text colour, it = info text colour

        boolean picture = !billboard.getPictureLink().equals("");
        boolean info = !billboard.getInformationText().equals("");
        boolean message = !billboard.getMessageText().equals("");    //boolean determining whether text is present.
        BufferedImage bi = new BufferedImage(resolutionx, resolutiony, BufferedImage.TYPE_INT_RGB);
        Rectangle screen = new Rectangle(bi.getWidth(),bi.getHeight());
        Graphics gr = bi.getGraphics();
        Font text = gr.getFont();

        //------------------Background Colouring-----------------//
        if (!billboard.getBackgroundColour().equals("")) {                                     //check for background colour tag
            if (billboard.getBackgroundColour().startsWith("#")) {                          //check for hex tag
                bg = Color.decode(billboard.getBackgroundColour());
            }
        }
        gr.setColor(bg);                                                                       //set background colour to default or specified.
        gr.fillRect(0, 0, bi.getWidth(), bi.getHeight());                               //fill background colour

        //----------------Message---------------------------//
        if (!billboard.getMessageTextColour().equals("")) {                                     //check for message text colour tag
            mt = Color.decode(billboard.getMessageTextColour());
        }
        gr.setColor(mt);                                                                        //set message text colour to default or specified

        int txtWidth = gr.getFontMetrics(text).stringWidth(billboard.getMessageText());         //calculating font size
        int imageWidth = bi.getWidth();
        double messageWidthRatio = (double) imageWidth / (double)txtWidth;
        int newMessageFontSize = (int) (text.getSize() * messageWidthRatio);
        if (newMessageFontSize >= 170){newMessageFontSize = 170;};
        gr.setFont(new Font("Dialogue", Font.PLAIN, newMessageFontSize));
        if (info && !picture){drawCenteredText(gr, billboard.getMessageText(), new Rectangle(bi.getWidth(), bi.getHeight() / 2));  //position and draw centred text
        }
        else if(picture || info){
            drawCenteredText(gr, billboard.getMessageText(), new Rectangle(bi.getWidth(), bi.getHeight() / 3));
        }
        else drawCenteredText(gr, billboard.getMessageText(), screen);


        //---------------------Info text----------------------//
        if (!billboard.getInformationTextColour().equals("")) {                                   //check if info text colour is specified and set colour
            it = Color.decode(billboard.getInformationTextColour()); }
        gr.setColor(it);

        if (info) {
            String wrapped = wrapString(billboard.getInformationText(), "\n", 60);
            String [] lines = wrapped.split("\n");

            int longestStringIndex = 0;
            int lineLength = lines[0].length();
            for(int i =0; i < lines.length; i++){
                if (lines[i].length() > lineLength){ longestStringIndex = i; lineLength = lines[i].length();
                }
            }

            int infoTextWidth = gr.getFontMetrics(text).stringWidth(lines[longestStringIndex]);      //calculating max text width
            double infoWidthRatio = imageWidth / (double) infoTextWidth;
            double newInfoFontSize = (text.getSize() * infoWidthRatio);

            if (newInfoFontSize >= 130){ newInfoFontSize = 130;}                                    //don't let info text size exceed 130

            if (newInfoFontSize >= newMessageFontSize){newInfoFontSize = newMessageFontSize-5;}
            gr.setFont(new Font("Dialogue", Font.PLAIN, (int) newInfoFontSize));

            int imageHeight = bi.getHeight();
            int lineHeight = gr.getFontMetrics().getHeight();

            //deciding where to draw info text based on which attributes are present

            //draw in bottom half of screen if only message is present.
            if (message && !picture){
                imageHeight = (imageHeight /2) - lineHeight;
                Rectangle singleLineRect = new Rectangle(10,(bi.getHeight()/2), bi.getWidth()-20, bi.getHeight()/2);
                if (lines.length == 1){                                                                                    //if info text is not split across multiple lines
                    drawCenteredText(gr, billboard.getInformationText(), singleLineRect);
                }
                else{                                                                   //if it is split into more than 1 line
                    for (String line : wrapped.split("\n")) {
                        Rectangle r = new Rectangle(20, (bi.getHeight() / 2)-lineHeight, bi.getWidth() - 40, imageHeight += (lineHeight+30));
                        drawCenteredText(gr, line, r);
                    }
                }
            }

            //draw in bottom third of screen if picture is present. (same for picture && message)
            else if(picture){
                imageHeight = (imageHeight /3) - lineHeight;
                Rectangle singleLineRect = new Rectangle(10,(bi.getHeight()/2), bi.getWidth(), bi.getHeight()/2);
                if (lines.length == 1){
                    drawCenteredText(gr, billboard.getInformationText(), singleLineRect);
                }
                else{
                    for (String line : wrapped.split("\n")){
                        Rectangle r = new Rectangle(20, ((bi.getHeight() / 3)*2) - lineHeight, bi.getWidth() - 40, imageHeight += (lineHeight+30));
                        drawCenteredText(gr, line, r);
                    }
                }
            }

            //draw in centre of screen if neither are present
            else {
                for (String line : wrapped.split("\n")){
                    drawCenteredText(gr, line, new Rectangle(bi.getWidth(), (imageHeight += (lineHeight + 20)*2) - (imageHeight/4) ));
                }
            }
        }

        //---------------------------IMAGE PROCESSING----------------------------------------------------------//
        if (picture){
            BufferedImage image = null;
            try{
                URL source = new URL(billboard.getPictureLink());                   //image display from URL
                image = ImageIO.read(source);
            } catch (IOException e){
                try{
                    String data = billboard.getPictureLink();
                    byte[] decoded = Base64.getDecoder().decode(data);
                    image = ImageIO.read(new ByteArrayInputStream(decoded));                //image displayed from picture data
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            assert image != null;
            drawScaledImage(gr, image, info, message);
        }


        return new ImageIcon(bi);
    }

    /**
     * calculates correct aspect ratios for images depending on their width, height, and presence of other elements. Image is then drawn to
     * graphics while maintaining their original aspect ratios.
     * @param g graphics for image to be drawn to.
     * @param image image to be scaled. must be in form of BufferedImage
     * @param infoText Boolean value indicating the presence of the 'infoText' attribute.  Image co-ordinates are decided by this.
     * @param messageText Boolean value indicating the presence of the 'messageText' attribute. Image co-ordinates are decided by this.
     */
    public void drawScaledImage(Graphics g, BufferedImage image, Boolean infoText, Boolean messageText){
        double imgHeight = image.getHeight(null), imgWidth = image.getWidth(null);
        int w = resolutionx, h = resolutiony, x=0, y=0, thirdY = h/3;
        double windowAspect, imageAspect = imgHeight / imgWidth, newHeight, newWidth;
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
     * A method to draw centred text to the provided
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


    /**
     * Takes a string and splits it with a deliminator based on a provided line length.
     * @param s String to be wrapped
     * @param deliminator character to insert at the end of a length. Usually '\n' in this application.
     * @param length length to split the string
     * @return string with inserted deliminator at every length
     */
    public static String wrapString(String s, String deliminator, int length) {
        StringBuilder result = new StringBuilder();
        int previousDelimPos = 0;
        for (String i : s.split(" ", -1)) {
            if (result.length() - previousDelimPos + i.length() > length) {
                result.append(deliminator).append(i);
                previousDelimPos = result.length() + 1;
            }
            else result.append((result.length() == 0) ? "" : " ").append(i);
        }
        return result.toString();
    }

    /**
     * Function for scaling images to a certain width and height. Does not retain aspect ratio, DrawScaledImage deals with aspect ratios.
     * @param imageToScale BufferedImage of the image that requires scaling
     * @param newWidth  new width of image
     * @param newHeight new height of image
     * @return image stretched to new width and heigh ready for use by DrawScaledImage function.
     */
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

