package com.theobfuscatorinator.modules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

final class ModuleUtils {
    private static Random rnd = new Random();

    /*
        Resolve a path, either relative (if path starts with $) or absolute.
        Relative meaning relative to the maven project.
    */
    static Path resolvePath(String path) {
        return !path.startsWith("$")
            ? Paths.get(path)
            : CodeGenerationUtils.mavenModuleRoot(ModuleObfuscator.class).resolve(path.substring(1));
    }

    /*
        Find a list of class or interface declarations. There could be multiple
        in theory if the user has two top-level classes. This is a useful utility
        for when we want to modify classes by adding methods, etc.
    */
    static ArrayList<ClassOrInterfaceDeclaration> findClassOrInterfaceDeclarations(CompilationUnit cu) {
        ArrayList<ClassOrInterfaceDeclaration> nodes = new ArrayList<>();
        for (Node node : cu.getChildNodes()) {
            if (node instanceof ClassOrInterfaceDeclaration) {
                nodes.add((ClassOrInterfaceDeclaration)node);
            }
        }
        return nodes;
    }

    /**
     * Get a random number from [0, bound)
     */
    static int randInt(int bound) {
        return rnd.nextInt(bound);
    }
    
    /**
     * Get a random element from the given array.
     */
    static <T> T randElem(T[] arr) {
        return arr[rnd.nextInt(arr.length)];
    }

    /**
     * Generate random name for methods, variables, and classes.
     * <br/><br/>
     * 
     * Generate a name with a target length. All generated names
     * start with a lower case letter. The rest of the name is made up of random letters, upper or
     * lower case, and numbers between 0 and 9.
     * 
     * @return A random name.
     */
    static String randName(int length) {
        String charactersToChooseFrom =
         "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String basicLetters = "abcdefghijklmnopqrstuvwxyz";

        // Names must start with a letter
        StringBuilder name = new StringBuilder();
        name.append(basicLetters.charAt(randInt(basicLetters.length())));

        for (int i = 0; i < length - 1; i++) {
            name.append(charactersToChooseFrom.charAt(randInt(charactersToChooseFrom.length())));
        }
        return name.toString();
    }
}