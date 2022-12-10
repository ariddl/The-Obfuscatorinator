package com.theobfuscatorinator.modules;

public interface IModule {
    String getName();
    String getDescription();
    void execute(Context ctx);
}