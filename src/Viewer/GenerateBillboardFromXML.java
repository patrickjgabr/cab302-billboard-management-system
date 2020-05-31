package Viewer;

import Shared.Billboard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class GenerateBillboardFromXML{
    /**
     * Creates billboard object from XML file by scanning for certain tags and elements and assigning them to the
     * appropriate billboard elements. If tags are not present, that element is assigned to an empty string "" on
     * the billboard, and ignored when billboard is rendered.
     * @param XMLFile File to be converted to billboard, must be correct format with proper tags.
     * @param billboardName Name of billboard being created from the XML.
     * @param creatorName Name of creator.
     * @return Billboard object.
     */
    public static Billboard XMLToBillboard(File XMLFile, String billboardName, String creatorName){
        String background = "";             //every required billboard constructor. if they do not appear in
        String message = "";                //the XML file then they are assigned an empty string and ignored.
        String info = "";
        String msgColour = "";
        String infoColour = "";
        String picture="";
        String link = "";
        String data = "";
        try {
            File input = XMLFile;           //input XML file
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = fac.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();
            NodeList n = doc.getElementsByTagName("billboard");

            Node node = n.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                if (el.hasAttribute("background")) {                //checking for background attribute
                    background = el.getAttribute("background");     //and assigning if present.
                }
                if(el.getElementsByTagName("picture").item(0) != null){                 //checking for picture attribute
                    if(el.getElementsByTagName("picture").item(0).hasAttributes()){
                        try{
                            link = el.getElementsByTagName("picture").item(0).getAttributes().getNamedItem("url").getNodeValue();      //if URL format
                        }catch (Exception e){
                            try{
                                data = el.getElementsByTagName("picture").item(0).getAttributes().getNamedItem("data").getNodeValue();  //if base64 encoded
                            }catch (Exception j){e.printStackTrace();}
                        }

                    }
                };
                if(el.getElementsByTagName("message").item(0) != null){                         //checking for message tag
                    message = el.getElementsByTagName("message").item(0).getTextContent();
                    if (el.getElementsByTagName("message").item(0).hasAttributes()){
                        msgColour = el.getElementsByTagName("message").item(0).getAttributes().getNamedItem("colour").getNodeValue();   //checking for message colour tag
                    }
                }
                if(el.getElementsByTagName("information").item(0) != null){                     //checking for info text tag
                    info = el.getElementsByTagName("information").item(0).getTextContent();
                    if (el.getElementsByTagName("information").item(0).hasAttributes()){
                        infoColour = el.getElementsByTagName("information").item(0).getAttributes().getNamedItem("colour").getNodeValue();      //checking for info text colour tag
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        if(!link.equals("")){picture=link;}     //if url is not empty, set picture source as URL
        else if(!data.equals("")){ picture=data; }      //if data attribute is not empty, set picture source as data attribute

        return new Billboard(creatorName, billboardName, picture, message, msgColour, background, info, infoColour);  //new billboard returned. 
    }
}

