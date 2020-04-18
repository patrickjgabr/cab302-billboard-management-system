package Viewer;

//import ControlPanel.CustomFont;
import Shared.Billboard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GenerateBillboardFromXML{
    public static Billboard XMLToBillboard(File XMLFile, String name){
        String background = "";
        String message = "";
        String info = "";
        String msgColour = "";
        String infoColour = "";
        String URL = "Not done yet";
        try {
            File input = XMLFile;
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = fac.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();
            NodeList n = doc.getElementsByTagName("billboard");

            Node node = n.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                if (el.hasAttribute("background")) {
                    background = el.getAttribute("background");
                }

                if(el.getElementsByTagName("message").item(0) != null){                         //message and colour
                    message = el.getElementsByTagName("message").item(0).getTextContent();
                    if (el.getElementsByTagName("message").item(0).hasAttributes()){
                        msgColour = el.getElementsByTagName("message").item(0).getAttributes().getNamedItem("colour").getNodeValue();
                    }
                }
                if(el.getElementsByTagName("information").item(0) != null){                     //info text and colour
                    info = el.getElementsByTagName("information").item(0).getTextContent();
                    if (el.getElementsByTagName("information").item(0).hasAttributes()){
                        infoColour = el.getElementsByTagName("information").item(0).getAttributes().getNamedItem("colour").getNodeValue();
                    }
                }
                if(el.getElementsByTagName("picture").item(0) != null){
                    ;//URL = el.getElementsByTagName("picture").item(0).getAttributes().getNamedItem("URL").getNodeValue();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Billboard("Test Creator Name", name, URL, message, msgColour, background, info, infoColour);
    }
}
