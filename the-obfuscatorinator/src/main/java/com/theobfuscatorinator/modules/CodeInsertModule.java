package com.theobfuscatorinator.modules;

import java.util.ArrayList;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.StaticJavaParser;

public class CodeInsertModule implements IModule {
    @Override
    public String getName() {
        return "CodeInsert";
    }

    @Override
    public String getDescription() {
        return "Insert random junk code";
    }

    @Override
    public void execute(Context ctx) {
        CompilationUnit cu = ctx.currentCU;
        
        ArrayList<ClassOrInterfaceDeclaration> cs = ModuleUtils.findClassOrInterfaceDeclarations(cu);
        for (ClassOrInterfaceDeclaration c : cs) {
            addJunk(c);
        }
    }

    private void addJunk(ClassOrInterfaceDeclaration c) {
        MethodDeclaration method = c.addMethod("test");
        method.addMarkerAnnotation("Test");
        BlockStmt blockStmt = StaticJavaParser.parseBlock("System.out.println();");
        method.setBody(blockStmt);
    }
}