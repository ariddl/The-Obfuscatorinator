package com.theobfuscatorinator.insertcode;

import java.util.ArrayList;
import java.util.Random;

import com.theobfuscatorinator.codeInterpreter.CodeStructure;

/** 
 * This class will insert dummy code throughout a java code file.
 * It will add one dummy class and dummy print statements within methods.
 */

public class InsertCode {
	private static Random rnd = new Random();
    
    /**
     * getRandomString generates a random string of length 50 made up of letters and digits
     * @return A randomly generated string
     */
    
    public static String getRandomString() {
        String alphabetNumbers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder str = new StringBuilder();
        
        int index = (int) (rnd.nextFloat() * alphabet.length());
        str.append(alphabet.charAt(index));
        while (str.length() < 50) {
            index = (int) (rnd.nextFloat() * alphabetNumbers.length());
            str.append(alphabetNumbers.charAt(index));
        }
        String randomString = str.toString();
        return randomString;
    }

    /** 
     * Gets a randomly generated string and formats it properly into a java print statement
     * @return String that represents a print statement with a random string
     */
    public static String generateDummyString() {
        String randomString = getRandomString();
        String finalString = String.format("System.out.println(\"%s\");", randomString);
        return finalString;
    }
    
    /**
     * Builds a properly-made dummy class that includes random java print statements and variable sets.
     * Adds 30 lines of dummy code
     * @return a String that represents an entire class
     */

    public static String generateDummyClass() {
    	String className = getRandomString();
        String varName = getRandomString();
    	String startClassCode = String.format("public static class %s {", className);
    	String dummyPrint = "";
    	String dummyLine = String.format("\t\t\tint %s = 0;\n", varName);
    	String tempLine = "";
    	int maxLines = 30;
    	for (int i = 1; i <= maxLines; i++) {
    		dummyPrint = String.format("System.out.println(\"%s\");", getRandomString());
    		dummyLine += "\t\t\t" + dummyPrint + "\n";
    		tempLine = String.format("%s = %d;", varName, i);
    		dummyLine += "\t\t\t" + tempLine + "\n";
    	}
    	
    	String allDummyCode = "\n\t" + startClassCode + "\n" + "\n";
    	allDummyCode += "\t\tpublic static void main(String[] args) {";
    	allDummyCode += String.format("\n\n%s\n\t\t}\n\t}\n", dummyLine);
    	
    	return allDummyCode;
    }
    
    /**
     * Injects dummy strings throughout an inputted java file and modifies the file
     * @param file Java file to have dummy strings inserted to
     * @param codeStructure Code that represents the java code of the file
     */
    public static String insertStrings(CodeStructure codeStructure) {
    	String code = codeStructure.getUnCommentedCode();
        //Inserting dummy strings throughout the code
        String[] codeByLines = code.split("\n");
        for (int i = 0; i < codeByLines.length; i++) {
            if (codeByLines[i].endsWith("}") || codeByLines[i].endsWith("}\n")) {
                continue;
            }
	    for (String accessor : new String[] {"private", "public"}) {
	        if (codeByLines[i].contains(accessor) && codeByLines[i].endsWith("{") && codeByLines[i].indexOf(accessor) != 0) {
                    codeByLines[i] = codeByLines[i] + "\n" + " ".repeat(codeByLines[i].indexOf(accessor) + 4) + generateDummyString();
                }
	    }
        }

        //Converts the entire code file back into a string
        StringBuilder newCodeBuilder = new StringBuilder();
        for (String line : codeByLines) {
            newCodeBuilder.append(line + "\n");
        }
	String newCode = newCodeBuilder.toString();

        codeStructure.setUnCommentedCode(newCode);
        return newCode;
    }
    /**
     * Injects dummy class throughout an inputted java file and modifies the file
     * @param file Java file to have dummy class inserted to
     * @param codeStructure Code that represents the java code of the file
     */
    public static String insertClass(CodeStructure codeStructure) {
    	String code = codeStructure.getUnCommentedCode();
        String newCode = new String();

        // Inserting Dummy Class
         int index = code.indexOf("{");
         String dummyCode = generateDummyClass();
         newCode = code.substring(0, index + 1) + dummyCode + code.substring(index + 1);
 
        codeStructure.setUnCommentedCode(newCode);
        return newCode;
    }

    /**
     * This function calls both insert dummy class and dummy strings function.
     * @param codeStructures Array List of code structures that represent the java code
     * */
    public static void insertCode(ArrayList<CodeStructure> codeStructures) {
        String tempClassDummy = new String();
        String tempClassString = new String();
        for (CodeStructure codeStructure : codeStructures) {
            tempClassString = insertStrings(codeStructure);
            codeStructure.setUnCommentedCode(tempClassString);
            tempClassDummy = insertClass(codeStructure);
            codeStructure.setUnCommentedCode(tempClassDummy);
        }
    }
}
