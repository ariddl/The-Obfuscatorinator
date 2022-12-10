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
    private ArrayList<InputFileEntry> inputFiles = new ArrayList<InputFileEntry>();
    private ArrayList<String> modules = new ArrayList<String>();

    public ConfigurationFile(String file) throws ParserConfigurationException,
          SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document document = builder.parse(new File(file));
        load(document);
    }

    /*
        Load top-level config nodes
    */
    private void load(Document document) {
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                System.out.println(elem.getTagName());
                
                switch (elem.getTagName()) {
                    case "ClassPath": loadClassPath(node); break;
                    case "Files": loadInputFiles(elem); break;
                    case "Modules": loadModules(elem); break;
                    default: break;
                }
            }
        }
    }

    /*
        Parse source root/class path from XML
    */
    private void loadClassPath(Node node) {
        sourceRoot = node.getAttributes().getNamedItem("path").getNodeValue();
        System.out.println("Using source root: " + sourceRoot);
    }

    /*
        Parse input source files from XML
    */
    private void loadInputFiles(Element elem) {
        NodeList nodes = elem.getElementsByTagName("File");
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            InputFileEntry e = new InputFileEntry();
            e.packageName = node.getAttributes().getNamedItem("package").getNodeValue();
            e.name = node.getAttributes().getNamedItem("name").getNodeValue();
            inputFiles.add(e);
            System.out.println("Input file: [" + e.packageName + ", " + e.name + "]");
        }
    }

    /*
        Parse enabled modules from XML
    */
    private void loadModules(Element elem) {
        NodeList nodes = elem.getElementsByTagName("Module");
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            String enabled = node.getAttributes().getNamedItem("enabled").getNodeValue();
            if (enabled.equalsIgnoreCase("true")) {
                modules.add(node.getAttributes().getNamedItem("name").getNodeValue());
                System.out.println("Using module: " + modules.get(modules.size() - 1));
            }
        }
        if (modules.size() == 0) {
            System.out.println("Using all modules by default");
        }
    }

    /*
        The project root directory where all Java packages and classes are located.
    */
    public String getSourceRoot() {
        return sourceRoot;
    }

    /*
        Get files to be obfuscated.
    */
    public ArrayList<InputFileEntry> getInputFiles() {
        return inputFiles;
    }

    /*
        Get obfuscation modules to use. If none provided, all are used.
    */
    public ArrayList<String> getModules() {
        return modules;
    }
}