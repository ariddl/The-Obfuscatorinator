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
    private static final Class[] moduleTypes = new Class[] { StringModule.class, MiscModule.class, 
                                                             RenameModule.class, CodeInsertModule.class };

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
    
    public boolean processArgs(String[] args) {
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
                    System.out.println(String.format("There are (%d) obfuscation modules available.", availableModules.size()));
                    for (String moduleName : availableModules.keySet()) {
                        IModule module = availableModules.get(moduleName);
                        System.out.println(String.format("%s: %s", moduleName, module.getDescription()));
                    }
                    break;
                }
            }

            // A command was used
            return false;
        }

        return configFile != null ? init(configFile) : false;
    }

    private boolean init(String configFile) {
        try {
            config = new ConfigurationFile(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Error loading configuration file.");
            return false;
        }

        if (config.getInputFiles().size() == 0) {
            Log.error("No input files.");
            return false;
        }

        for (ModuleEntry me : config.getModules()) {
            if (!availableModules.containsKey(me.name)) {
                Log.info("Skipping unknown obfuscation module " + me.name);
                continue;
            }
            
            if (!me.enabled) {
                continue;
            }

            IModule module = availableModules.get(me.name);
            if (!activeModules.contains(module)) {
                activeModules.add(module);
            }
        }

        if (config.getModules().size() == 0) {
            Log.info("Using all obfuscation modules by default");
            for (String moduleName : availableModules.keySet()) {
                activeModules.add(availableModules.get(moduleName));
            }
        }

        if (activeModules.size() == 0) {
            Log.error("No obfuscation modules selected.");
            return false;
        }

        return true;
    }

    public void run() {
        SourceRoot sourceRoot = new SourceRoot(ModuleUtils.resolvePath(config.getSourceRoot()));

        Context ctx = new Context();
        for (InputFileEntry inputFile : config.getInputFiles()) {
            ctx.currentCU = sourceRoot.parse(inputFile.packageName, inputFile.name);
            for (int i = 0; i < activeModules.size(); ++i) {
                activeModules.get(i).execute(ctx);
            }
        }

        sourceRoot.saveAll(ModuleUtils.resolvePath(config.getOutputRoot()));
    }

    public static void main(String[] args) {
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        ModuleObfuscator obfs = new ModuleObfuscator();
        if (obfs.processArgs(args)) {
            obfs.run();
        }
    }
}