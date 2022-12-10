package com.theobfuscatorinator.modules;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.javaparser.utils.CodeGenerationUtils;

final class ModuleUtils {
    static Path resolvePath(String value) {
        return !value.startsWith("$")
            ? Paths.get(value)
            : CodeGenerationUtils.mavenModuleRoot(ModuleObfuscator.class).resolve(value.substring(1));
    }
}