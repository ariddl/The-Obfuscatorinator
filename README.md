# The-Obfuscatorinator

## Overview

The Obfuscatorinator is a Java Obfuscator that reads in a Java project, copies it, constructs a relevant graph, and obfuscates it. Ultimately, it will remove any unused code, comments, and whitespace and will add dummy comments and dummy code. All methods, class names, and variables shall be replaced and strings and variables shall be encrypted. The code will be reordered and spaghettified and opaque predicates will be inserted. 

## Tools

This project is written in Java, uses JUnit testing, and Maven.

## Build and Running

### Minimum Requirements
- Java 11+
- Maven 3.8+

First change to the route directory.

    cd the-obfuscatorinator

To clean install:

    mvn clean install

To build

    mvn package

To run

    java -cp "./target/the-obfuscatorinator-1.0.0-shaded.jar" com.theobfuscatorinator.modules.ModuleObfuscator <config file> [options]

## Options

- --list-modules | List all available obfuscation modules

## Sample configuration
```
<Configuration>
    <ClassPath path="$src/main/res" outPath="$output" />
    <Format removeWhitespace="false" removeLines="false" />
    <Files>
        <File package="test" name="Main.java" />
    </Files>
    <Modules>
        <Module name="String" enabled="false" />
        <Module name="Misc" enabled="true" />
        <Module name="Rename" enabled="false" />
        <Module name="CodeInsert" enabled="false" />
    </Modules>
</Configuration>
```

## Code of Conduct

Find our code of conduct at: https://github.com/ThomasAndrasek/The-Obfuscatorinator/blob/main/CODE_OF_CONDUCT.md

## License

MIT License
