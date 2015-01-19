#!/bin/bash
if [ -z "$CLASSPATH" ]; then 
    export CLASSPATH=/home/maksim/Desktop/plp_lang/scanner/:/usr/share/java/junit-4.12.jar:/usr/share/java/hamcrest-core-1.3.jar;
fi


if [ "$1" = "DEBUG" ]; then
    javac -g -cp $CLASSPATH TestScanner.java Scanner.java TokenStream.java
    mv *.class cop5555sp15/
    jdb -classpath $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestScanner 
else 
    javac -cp $CLASSPATH TestScanner.java Scanner.java TokenStream.java
    mv *.class cop5555sp15/
    java -cp $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestScanner;
fi
