# The CodeSparks Framework

The CodeSparks framework aims to facilitate the integration of software analysis tools into the IDE such that their
results are shown as glyph-based visualizations in the source code.

## Build

Prerequisites:

1. Apache Ant (>=1.10)
2. JDK 11
3. IntelliJ IDEA Community Edition 2022.2.4 (or comparable)
4. Specify the path to the IntelliJ IDEA installation directory in the `idea.properties` file.

To create the CodeSparks framework libraries, namely `codesparks-core`, `codesparks-java` and `codesparks-python` run
`ant` (default target=`jar`) in the terminal.

## Use

Add the required JAR files as module library of the IDE Plugin module in your IntelliJ Platform Plugin project. This
particularly includes the JAR file `codesparks-core`. Depending on the target programming language, you can also use the
`codesparks-java` or `codesparks-python` JAR file for Java or Python, respectively.

## Tested under

Operating systems:

1. Windows 10 22H2 64-bit, Build 19045.2364
2. Linux Mint 21 Vanessa 64-bit, Kernel 5.15.0-56-generic x86_64

IntelliJ IDEA versions:

1. 2022.2.4 Community Edition (Build #IC-222.4459.24, built on November 22, 2022)

## Demo Implementation

A CodeSparks Demo Plugin that targets the Java programming language is available here:
[CodeSparks Demo Plugin](https://github.com/segroup-uni-trier/codesparks-pmd-demo)

## License

The CodeSparks Framework is Open Source software released under the
[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0). Note, some parts of this software, such as the
compilation and runtime dependencies, may have different licenses (see NOTICE.txt).