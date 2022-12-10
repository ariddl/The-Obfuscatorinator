package com.theobfuscatorinator.modules.config;

public enum FormatFlags {
    NONE(0),
    WHITESPACE(1),
    LINES(2);

    private final int id;
    FormatFlags(int id) { this.id = id; }
    public int getValue() { return id; }
}