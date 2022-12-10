package com.theobfuscatorinator.modules;

import com.github.javaparser.ast.CompilationUnit;

// We may want to have a CodeGraph reference here for project-wide replacements
// (e.g., for method and class renaming)
class Context {
    public CompilationUnit currentCU;
}