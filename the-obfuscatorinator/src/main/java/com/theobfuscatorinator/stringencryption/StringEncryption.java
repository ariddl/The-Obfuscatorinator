package com.theobfuscatorinator.stringencryption;

import java.util.ArrayList;
import java.util.Random;

import com.theobfuscatorinator.codeInterpreter.CodeStructure;
import com.theobfuscatorinator.codeInterpreter.Renamer;

public class StringEncryption {

    /**
     * Constants - TODO: Need better names
    */
    private static final int CONST_MULT = 42;
    private static final int CONST_OFFSET = 27000;
    private static final int CONST_MOD = 100;

    private static Random random = new Random();
    
    /**
     * Templated custom pair class, because there does not seem to be a reasonable way to do this 
     * with this version of the JDK.
     * @param <K> Type of the first element in the pair
     * @param <V> Type of the second element in the pair
     */
    static class Pair<K,V>{
        public K first;
        public V second;

        public Pair(K fst, V snd) {
            first = fst;
            second = snd;
        }
    }

    /**
     * Finds all the strings in the code structure and returns them as a list of pairs. The pairs
     * are the strings found match to their starting index.
     * 
     * @param codeStructure The code structure to search.
     * @return A list of pairs of strings and their starting index.
     */
    private static ArrayList<Pair<String, Integer>> findStrings(CodeStructure codeStructure) {
        ArrayList<Pair<String, Integer>> strings = new ArrayList<Pair<String, Integer>>();
        String code = codeStructure.getOriginalCode().substring(0);

        // Find all strings in the code
        int i = 0;
        while (i < code.length()) {
            if (code.charAt(i) == '"') {
                int j = i + 1;
                while (j < code.length()) {
                    // Make sure it is not an escaped quote.
                    if (code.charAt(j) == '"') {
                        if (j - 1 > i && code.charAt(j - 1) == '\\') {
                            if (j - 2 > i && code.charAt(j - 2) == '\\') {
                                break;
                            }
                            j++;
                            continue;
                        }
                        break;
                    }
                    j++;
                }
                strings.add(new Pair<String, Integer>(code.substring(i, j+1), i));
                i = j;
            }
            i++;
        }

        return strings;
    }

    /**
     * Encrypt each instance of a string.  
     * <br/><br/>
     * 
     * All strings in the code are replaced by an array of integers, each one representing a
     * character from the string. To convert the character to an integer, the character is
     * converted to its byte value, then the byte is converted to an integer. The integer is then
     * multiplied by 42 and increased by 27000. Then a random number is generated between 1 and
     * 100. The integer value is then multiplied by the randomly generated number, if the result
     * is not divisible by 100. The product of the integer and the random number then has the
     * random number added to it.   
     * <br/><br/>
     * 
     * The array of integers is then surrounded by the decryption method. Strings are replaced by
     * the decryption method and integer array.  
     * <br/><br/>
     * @param codeStructure The code structure to encrypt.
     * @param decryptionMethodName The name of the decryption method.
     * @return The encrypted code.
     */
    public static String encryptStrings(CodeStructure codeStructure, String decryptionMethodName) {
        ArrayList<Pair<String, Integer>> strings = findStrings(codeStructure);
        String code = codeStructure.getOriginalCode().substring(0);
        
        for (int i = strings.size() - 1; i >= 0; i--) {
            String string = strings.get(i).first.substring(1, strings.get(i).first.length() - 1);
            int index = strings.get(i).second;

            byte[] byteArrray = string.getBytes();
            StringBuilder byteStringBuilder = new StringBuilder();
            for (byte b : byteArrray) {
                int num = b * CONST_MULT + CONST_OFFSET;

                // Generate random number until it is not divisible by 100.
                int attached = random.nextInt(100) + 1;
                while ((num * attached + attached) % CONST_MOD == 0) {
                    attached = random.nextInt(100) + 1;
                }
                num *= attached;
                num *= CONST_MOD;
                num += attached;

                byteStringBuilder.append(num + ", ");
            }
            String byteString = byteStringBuilder.toString();
            
            // Replace the string with the encrypted value.
            if (byteString.length() != 0) {
                byteString = byteString.substring(0, byteString.length() - 2);
                String encrypted = String.format("%s(new int[]{%s})", decryptionMethodName, byteString);
                code = code.substring(0, index) + encrypted + code.substring(index + string.length() + 2);
            }
        }

        

        return code;
    }

    /**
     * Adds the decrypt method to the code structures.
     * <br/><br/>
     * 
     * The decrypt method takes an array of integers and returns a string. The array is decrypted
     * by first subtracted the attached value from the integer. Then the integer is divided by 100
     * and then devided by the subtracted value. The result of that is then subtracted by 27000 and
     * then devided by 42. The result is then converted to a byte and then to a character.
     * <br/><br/>
     * 
     * The decryption method is added to the very end of the code struucture.
     * 
     * @param codeStructures The code structures to add the decryption method to.
     */
    public static void addDecryptionMethod(ArrayList<CodeStructure> codeStructures) {
        String param = Renamer.generateClassName();
        String decrypted = Renamer.generateClassName();
        String iVar = Renamer.generateClassName();
        String second = Renamer.generateClassName();
        String first = Renamer.generateClassName();
        String completed = Renamer.generateClassName();
        // Build decryption method body.
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s) { ", param));
        builder.append(String.format("String %s = \"\"; ", decrypted));
        builder.append(String.format("for (int %s = 0; %s < %s.length; %s++) { ", iVar, iVar, param, iVar));
        builder.append(String.format("int %s = %s[%s] %% %d; ", first, param, iVar, CONST_MOD));
        builder.append(String.format("int %s = %s[%s] - %s; ", second, param, iVar, first));
        builder.append(String.format("%s /= 100; ", second));
        builder.append(String.format("%s /= %s; ", second, first));
        builder.append(String.format("int %s = %s - %d; ", completed, second, CONST_OFFSET));
        builder.append(String.format("%s /= %d; ", completed, CONST_MULT));
        builder.append(String.format("%s += (char) %s; ", decrypted, completed));
        builder.append(String.format("} return %s; }", decrypted));
        String decryptMethodBody = builder.toString();


        // Insert decryption method into each code structure.
        for (CodeStructure codeStructure : codeStructures) {
            int end = codeStructure.getUnCommentedCode().lastIndexOf("}");
            String code = codeStructure.getUnCommentedCode();
            String decryptMethod = "public static String " +
                                    codeStructure.getDecryptionMethodName() + "(int[] " +
                                    decryptMethodBody;
            code = code.substring(0, end) + decryptMethod + code.substring(end);
            codeStructure.setUnCommentedCode(code);
        }
    }

}
