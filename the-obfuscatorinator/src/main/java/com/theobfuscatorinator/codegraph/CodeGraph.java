package com.theobfuscatorinator.codegraph;

import com.theobfuscatorinator.codeInterpreter.ClassStructure;
import com.theobfuscatorinator.codeInterpreter.CodeStructure;
import com.theobfuscatorinator.codeInterpreter.MethodStructure;
import com.theobfuscatorinator.graph.Edge;
import com.theobfuscatorinator.graph.Graph;
import com.theobfuscatorinator.graph.Node;

import java.util.ArrayList;

/**
 * This class represents a directed graph of the file structure of a java project.
 * 
 * @author Thomas Andrasek
 */
public class CodeGraph {
    public static final int CLASS_OWN_METHOD = 0;
    public static final int CLASS_OWN_CLASS = 1;

    private Graph graph;

    /**
     * Construct a new graph from the given file path.
     * 
     * The start of the graph will be from the first file found with a main method.
     */
    public CodeGraph(ArrayList<CodeStructure> code) {
        this.graph = new Graph();

        for (CodeStructure codeStruct : code) {
            ArrayList<Node<ClassStructure>> classStructureNodes = new ArrayList<>();
            for (ClassStructure classStruct : codeStruct.getClasses()) {
                classStructureNodes.add(new Node<ClassStructure>(classStruct));
            }
            while (classStructureNodes.size() > 0) {
                Node<ClassStructure> classStructNode = classStructureNodes.remove(0);

                this.graph.addNode(classStructNode);

                for (ClassStructure classStruct : classStructNode.getValue().getClasses()) {
                    Node<ClassStructure> n = new Node<>(classStruct);
                    classStructureNodes.add(n);
                    this.graph.addEdge(classStructNode, n, CLASS_OWN_CLASS);
                }

                for (MethodStructure methodStruct : classStructNode.getValue().getMethods()) {
                    Node<MethodStructure> methodNode = new Node<>(methodStruct);

                    this.graph.addNode(methodNode);
                    this.graph.addEdge(classStructNode, methodNode, CLASS_OWN_METHOD);
                }
            }
        }


        for (Node<?> node : this.graph.getNodes()) {
            if (!(node.getValue() instanceof ClassStructure)) {
                continue;
            }

            ClassStructure classStruct = (ClassStructure) node.getValue();
            System.out.println(classStruct.getClassName());

            for (Edge edge : node.getEdges()) {
                if (edge.getEnd().getValue() instanceof MethodStructure) {
                    MethodStructure methodStruct = (MethodStructure) edge.getEnd().getValue();
                    System.out.println("\t" + methodStruct.getMethodName());
                }
                else {
                    ClassStructure innerClassStruct = (ClassStructure) edge.getEnd().getValue();
                    System.out.println("\t" + innerClassStruct.getClassName());
                }
                
                System.out.println("\t" + edge.getType());
            }
        }
    }

    /**
     * Finds the CodeStructure object of a project that contains the main method, if one exists.
     * @param code Set of codestructures that represents a project's source code
     * @return The CodeStructure object in the HashSet that contains the main method. Null if no main method exists.
     */
    public static CodeStructure findMainMethod(ArrayList<CodeStructure> code){
        for(CodeStructure file : code){
            if(file.containsMainMethod()) return file;
        }
        return null;
    }
}
