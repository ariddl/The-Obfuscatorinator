package com.theobfuscatorinator.modules;

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
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

public class MiscModule extends ModifierVisitor<Void> implements IModule {
    @Override
    public String getName() {
        return "Misc";
    }

    @Override
    public String getDescription() {
        return "Miscellaneous control flow modifications";
    }

    @Override
    public Visitable visit(IfStmt n, Void arg) {
        n.getCondition().ifBinaryExpr(binaryExpr -> {
            if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
                Statement thenStmt = n.getThenStmt().clone();
                Statement elseStmt = n.getElseStmt().get().clone();
                n.setThenStmt(elseStmt);
                n.setElseStmt(thenStmt);
                binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
            }
        });
        return super.visit(n, arg);
    }
}