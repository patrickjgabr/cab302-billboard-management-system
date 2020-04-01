package ControlPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BillboardMethods {
    public static void importBillboard (File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        System.out.println(" ");
        System.out.println("Billboard: "  + doc.getElementsByTagName("billboard").item(0).getAttributes().getNamedItem("background").getNodeValue());
        System.out.println("Message Colour: "  + doc.getElementsByTagName("message").item(0).getAttributes().getNamedItem("colour").getNodeValue());
        System.out.println("Message : "  + doc.getElementsByTagName("message").item(0).getTextContent());
        System.out.println("Picture URL: "  + doc.getElementsByTagName("picture").item(0).getAttributes().getNamedItem("url").getNodeValue());
        System.out.println("Information Colour: "  + doc.getElementsByTagName("information").item(0).getAttributes().getNamedItem("colour").getNodeValue());
        System.out.println("Information : "  + doc.getElementsByTagName("information").item(0).getTextContent());
        System.out.println(" ");

    }
    //can you see this
}
