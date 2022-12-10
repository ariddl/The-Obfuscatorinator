package com.theobfuscatorinator.modules;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.StaticJavaParser;


public class StringModule implements IModule {

    private static final int CONST_MULT = 42;
    private static final int CONST_OFFSET = 27000;
    private static final int CONST_MOD = 100;

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
                SimpleName name = new SimpleName();
                name.setIdentifier(ModuleUtils.randName(8));

                NodeList nl = new NodeList();
                //nl.Add(n);
                MethodCallExpr mc = new MethodCallExpr(n, name, nl);
                
                n.setValue(ModuleUtils.randName(8));
                return super.visit(n, arg);
            }
        }, null);
    }

    private String generateDecryptBody() {
        String param = ModuleUtils.randName(8);
        String decrypted = ModuleUtils.randName(8);
        String iVar = ModuleUtils.randName(8);
        String second = ModuleUtils.randName(8);
        String first = ModuleUtils.randName(8);
        String completed = ModuleUtils.randName(8);
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
        return builder.toString();
    }
}