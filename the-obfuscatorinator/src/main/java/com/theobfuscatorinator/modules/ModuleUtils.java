package com.theobfuscatorinator.modules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

final class ModuleUtils {
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
}