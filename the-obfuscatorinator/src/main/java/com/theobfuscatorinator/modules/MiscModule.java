package com.theobfuscatorinator.modules;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class MiscModule implements IModule {
    @Override
    public String getName() {
        return "Misc";
    }

    @Override
    public String getDescription() {
        return "Miscellaneous control flow modifications";
    }

    @Override
    public void execute(Context ctx) {
        ctx.currentCU.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(IfStmt n, Void arg) {
                // Process binary if expressions
                n.getCondition().ifBinaryExpr(binExpr -> {
                    // Need an else body as well
                    if (n.getElseStmt().isPresent()) {
                        // If this is an == or !=
                        if (binExpr.getOperator() == BinaryExpr.Operator.EQUALS ||
                            binExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS) {
                            // Flip the then and else code blocks
                            Statement thenStmt = n.getThenStmt().clone();
                            Statement elseStmt = n.getElseStmt().get().clone();
                            n.setThenStmt(elseStmt);
                            n.setElseStmt(thenStmt);

                            // Set operator to opposite of what it was
                            binExpr.setOperator(binExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS
                                                ? BinaryExpr.Operator.EQUALS
                                                : BinaryExpr.Operator.NOT_EQUALS);
                        }
                    }
                });
                return super.visit(n, arg);
            }
        }, null);
    }
}