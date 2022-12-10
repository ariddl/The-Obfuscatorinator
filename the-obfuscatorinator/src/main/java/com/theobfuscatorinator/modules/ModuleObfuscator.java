package com.theobfuscatorinator.modules;
import com.theobfuscatorinator.modules.config.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.Paths;
import java.util.function.Function;

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
                // Instantiate all possible modules
                IModule module = (IModule)c.newInstance();
                availableModules.put(module.getName(), module);
            } catch (Exception e) {}
        }
        activeModules = new ArrayList<>();
    }
    
    public boolean processArgs(String[] args) {
        if (args.length == 0) {
            Log.error("Config file or command required.");
            return false;
        }

        String configFile = null;
        for (int i = 0; i < args.length; ++i) {
            if (!args[i].startsWith("--")) {
                // Not a command, we'll assume this is our config file
                configFile = args[i];
                continue;
            }

            // Check for commands
            // If user does command, do not process output
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

        // We need stuff to obfuscate!
        if (config.getInputFiles().size() == 0) {
            Log.error("No input files.");
            return false;
        }

        // Add the modules we want
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

        // By default use everything if nothing is specified
        if (config.getModules().size() == 0) {
            Log.info("Using all obfuscation modules by default");
            for (String moduleName : availableModules.keySet()) {
                activeModules.add(availableModules.get(moduleName));
            }
        }

        // Nothing to do if no modules
        if (activeModules.size() == 0) {
            Log.error("No obfuscation modules selected.");
            return false;
        }

        return true;
    }

    // Get a custom printer for formatting output, depending on config
    private Function<CompilationUnit, String> getSourcePrinter(SourceRoot sourceRoot) {
        Function<CompilationUnit, String> defaultPrinter = sourceRoot.getPrinter();
        int formatFlags = config.getFormatFlags();
        
        // No formatting flags set means we can use default pretty printer
        if (formatFlags == 0) {
            return defaultPrinter;
        }

        // Return a custom printer
        return cu -> {
            // Get the default pretty printed output for this compilation unit
            String pretty = defaultPrinter.apply(cu);
            
            // Remove whitespace if we need to
            if ((formatFlags & FormatFlags.WHITESPACE.getValue()) != 0) {
                for (String ws : new String[] {"  ", "\t"}) {
                    pretty = pretty.replace(ws, "");
                }
            }

            // Remove lines if needed
            if ((formatFlags & FormatFlags.LINES.getValue()) != 0) {
                for (String c : new String[] {"\r", "\n"}) {
                    pretty = pretty.replace(c, "");
                }
            }

            // Return the prettified (or perhaps less pretty) source for this compilation unit
            return pretty;
        };
    }

    public void run() {
        // Get our SourceRoot, which will represent the root of the project we are obfuscating.
        SourceRoot sourceRoot = new SourceRoot(ModuleUtils.resolvePath(config.getSourceRoot()));
        sourceRoot.setPrinter(getSourcePrinter(sourceRoot));

        // Initialize an obfuscation context and execute all active modules on each compilation
        // unit, or file in the project (the ones specified in configuration file).
        Context ctx = new Context();
        for (InputFileEntry inputFile : config.getInputFiles()) {
            ctx.currentCU = sourceRoot.parse(inputFile.packageName, inputFile.name);
            for (int i = 0; i < activeModules.size(); ++i) {
                activeModules.get(i).execute(ctx);
            }
        }

        // Save all Java source files
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