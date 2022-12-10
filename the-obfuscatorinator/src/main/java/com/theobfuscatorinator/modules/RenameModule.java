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

    /**
     * Generate random class name.
     * <br/><br/>
     * 
     * Class names generated with a length of random characters between 10 and 110. All generated
     * names start with a capital letter. The rest of the name is made up of random letters, upper
     * or lower case, and numbers between 0 and 9.
     * 
     * @return A random class name.
     */
    public static String generateClassName() {
        String charactersToChooseFrom =
         "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        char[] letters = String.valueOf("abcdefghijklmnopqrstuvwxyz").toCharArray();

        StringBuilder className = new StringBuilder();
        className.append(Character.toUpperCase(letters[rnd.nextInt(letters.length)]));

        int numberOfLetters = (int) (Math.random() * 100) + 10;
        for (int i = 0; i < numberOfLetters; i++) {
            className.append(charactersToChooseFrom.charAt(rnd.nextInt(charactersToChooseFrom.length())));
        }

        return className.toString();
    }

    /**
     * Generate random name for methods or variables.
     * <br/><br/>
     * 
     * Names generated with a length of random characters between 10 and 110. All generated names
     * start with a lower case letter. The rest of the name is made up of random letters, upper or
     * lower case, and numbers between 0 and 9.
     * 
     * @return A random name.
     */
    public static String generateName() {
        String charactersToChooseFrom =
         "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String basicLetters = "abcdefghijklmnopqrstuvwxyz";

        StringBuilder className = new StringBuilder();

        className.append(basicLetters.charAt((int) (Math.random() * basicLetters.length())));

        int numberOfLetters = (int) (Math.random() * 100) + 10;
        for (int i = 0; i < numberOfLetters; i++) {
            className.append(charactersToChooseFrom.charAt(
                (int) (Math.random() * charactersToChooseFrom.length())));
        }

        return className.toString();
    }
}