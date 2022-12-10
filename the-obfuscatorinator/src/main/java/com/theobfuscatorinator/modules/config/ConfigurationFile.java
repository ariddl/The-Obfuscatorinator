package com.theobfuscatorinator.modules.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
  
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigurationFile {
    private String sourceRoot;
    private ArrayList<InputFileEntry> inputFiles;
    private ArrayList<String> modules;

    public ConfigurationFile(String file) throws ParserConfigurationException,
          SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document document = builder.parse(new File(file));
        load(document);
    }

    private void load(Document document) {
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                System.out.println(elem.getTagName());
            }
        }
    }

    public String getSourceRoot() {
        return sourceRoot;
    }

    public ArrayList<InputFileEntry> getInputFiles() {
        return inputFiles;
    }

    public ArrayList<String> getModules() {
        return modules;
    }
}