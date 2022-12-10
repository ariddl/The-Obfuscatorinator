package com.theobfuscatorinator.modules;
import com.theobfuscatorinator.modules.config.*;

import java.util.ArrayList;
import java.util.HashMap;
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

import java.lang.reflect.*;

public class ModuleObfuscator {
    private static final Class[] moduleTypes = new Class[] { StringModule.class };

    private HashMap<String, IModule> availableModules = new HashMap<String, IModule>();
    private ConfigurationFile config;
    private ArrayList<IModule> activeModules;

    public ModuleObfuscator() {
        for (Class c : moduleTypes) {
            try {
                IModule module = (IModule)c.newInstance();
                availableModules.put(module.getName(), module);
            } catch (Exception e) {}
        }
        activeModules = new ArrayList<>();
    }

    public boolean init(String[] args) {
        if (args.length == 0) {
            return false;
        }

        String configFile = null;
        for (int i = 0; i < args.length; ++i) {
            if (!args[i].startsWith("--")) {
                configFile = args[i];
                continue;
            }

            switch (args[i].substring(2)) {
                case "list-modules":
                {
                    break;
                }
            }
        }

        if (configFile == null) {
            return false;
        }

        try {
            config = new ConfigurationFile(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading configuration file.");
            return false;
        }

        if (config.getInputFiles().size() == 0) {
            System.out.println("No input files.");
            return false;
        }

        for (String moduleName : config.getModules()) {
            if (!availableModules.containsKey(moduleName)) {
                System.out.println("Skipping unknown module " + moduleName);
                continue;
            }
            
            IModule module = availableModules.get(moduleName);
            if (!activeModules.contains(module)) {
                activeModules.add(module);
            }
        }

        if (activeModules.size() == 0) {
            System.out.println("Using all modules by default");
            for (String moduleName : availableModules.keySet()) {
                activeModules.add(availableModules.get(moduleName));
            }
        }

        return true;
    }

    public void run() {
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(ModuleObfuscator.class).resolve("src/main/res"));
        
        CompilationUnit cu = sourceRoot.parse("test", "Main.java");

        Log.info("running");

        cu.accept((ModifierVisitor<Void>)activeModules.get(0), null);

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
        if (obfs.init(args)) {
            obfs.run();
        }
    }
}