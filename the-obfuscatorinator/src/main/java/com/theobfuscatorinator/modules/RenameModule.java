package com.theobfuscatorinator.modules;

import java.util.Random;

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

public class RenameModule extends ModifierVisitor<Void> implements IModule {
    private static Random rnd = new Random();

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
        ctx.currentCU.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(IfStmt n, Void arg) {
                n.getCondition().ifBinaryExpr(binExpr -> {
                    if (binExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
                        Statement thenStmt = n.getThenStmt().clone();
                        Statement elseStmt = n.getElseStmt().get().clone();
                        n.setThenStmt(elseStmt);
                        n.setElseStmt(thenStmt);
                        binExpr.setOperator(BinaryExpr.Operator.EQUALS);
                    }
                });
                return super.visit(n, arg);
            }
        }, null);
    }
}