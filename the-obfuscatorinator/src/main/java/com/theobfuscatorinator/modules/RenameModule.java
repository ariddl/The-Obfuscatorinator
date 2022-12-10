package com.theobfuscatorinator.modules;

import java.util.Random;
import java.util.HashMap;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class RenameModule implements IModule {
    @Override
    public String getName() {
        return "Rename";
    }

    @Override
    public String getDescription() {
        return "Rename functions and classes to garbage";
    }

    @Override
    public void execute(Context ctx) {
        HashMap<String, String> renamedMethods = new HashMap<>();

        for (Node node : ctx.currentCU.getChildNodes()) {
            if (node instanceof ClassOrInterfaceDeclaration) {
            }
        }

        // Apply a visitor to update the references to the things we renamed (e.g., methods)
        ctx.currentCU.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(MethodCallExpr n, Void arg) {
                SimpleName name = new SimpleName();
                name.setIdentifier(ModuleUtils.randName(8));
                n.setName(name);
                return super.visit(n, arg);
            }
        }, null);
    }
}