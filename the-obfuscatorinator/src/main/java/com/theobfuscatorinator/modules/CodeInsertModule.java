package com.theobfuscatorinator.modules;

import java.util.ArrayList;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

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
        MethodDeclaration method = c.addMethod(ModuleUtils.randName(ModuleUtils.randInt(6) + 4));
        BlockStmt blockStmt = StaticJavaParser.parseBlock("{double n = 10;double y = 0.0;for (double k = 0; k <= n; k++) {y = y + ((Math.pow(16, (n - k))) % (8 * k + 1)) / (8 * k + 1);}double z = 0;for (double k = (n + 1); k <= 100; k++) {z = z + ((Math.pow(16, (100 - k))) / ((8 * k) + 1));}y = y + z;y = 4 * y;double y2 = 0;for (double k = 0; k <= n; k++) {y2 = y2 + ((Math.pow(16, (n - k))) % (8 * k + 4)) / (8 * k + 4);}z = 0;for (double k = (n + 1); k <= 100; k++) {z = z + ((Math.pow(16, (100 - k))) / ((8 * k) + 4));}y2 = y2 + z;y2 = 2 * y2;y = y - y2;double y3 = 0;for (double k = 0; k <= n; k++) {y3 = y3 + ((Math.pow(16, (n - k))) % (8 * k + 5)) / (8 * k + 5);}z = 0;for (double k = (n + 1); k <= 100; k++) {z = z + ((Math.pow(16, (100 - k))) / ((8 * k) + 5));}y3 = y3 + z;y = y - y3;double y4 = 0;for (double k = 0; k <= n; k++) {y4 = y4 + ((Math.pow(16, (n - k))) % (8 * k + 6)) / (8 * k + 6);}z = 0;for (double k = (n + 1); k <= 100; k++) {z = z + ((Math.pow(16, (100 - k))) / ((8 * k) + 1));}y4 = y4 + z;y = y - y4;}");
        method.setBody(blockStmt);
        
        // Randomize all numbers in the generated body
        blockStmt.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(IntegerLiteralExpr n, Void arg) {
                n.setInt(ModuleUtils.randInt(2147483647));
                return super.visit(n, arg);
            }
        }, null);
    }
}