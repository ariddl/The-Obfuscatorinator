package com.theobfuscatorinator.modules;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;


public class StringModule implements IModule {
    @Override
    public String getName() {
        return "String";
    }

    @Override
    public String getDescription() {
        return "Obfuscates string literals";
    }

    @Override
    public void execute(Context ctx) {
        ctx.currentCU.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(StringLiteralExpr n, Void arg) {
                n.setValue(n.getValue() + "_a");
                return super.visit(n, arg);
            }
        }, null);
    }
}