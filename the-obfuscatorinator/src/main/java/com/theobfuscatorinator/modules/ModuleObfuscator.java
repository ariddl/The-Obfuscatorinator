package com.theobfuscatorinator.modules;

import java.util.ArrayList;
import java.nio.file.Paths;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

public class ModuleObfuscator {
    private ArrayList<IModule> modules;

    public ModuleObfuscator() {
        modules = new ArrayList<>();
        modules.add(new StringModule());
    }

    public void run() {        
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(ModuleObfuscator.class).resolve("src/main/res"));
        
        CompilationUnit cu = sourceRoot.parse("test", "Main.java");

        Log.info("running");

        cu.accept((ModifierVisitor<Void>)modules.get(0), null);

        cu.accept(new ModifierVisitor<Void>() {
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
        }, null);

        sourceRoot.saveAll(
                // The path of the Maven module/project which contains the ModuleObfuscator class.
                CodeGenerationUtils.mavenModuleRoot(ModuleObfuscator.class)
                        // appended with a path to "output"
                        .resolve(Paths.get("output")));
    }

    public static void main(String[] args) {
        ModuleObfuscator obfs = new ModuleObfuscator();
        obfs.run();
    }
}