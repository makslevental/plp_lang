#!/bin/bash
if [ -z "$CLASSPATH" ]; then 
    export CLASSPATH=/home/maksim/Desktop/plp_lang/parser/:/home/maksim/Desktop/plp_lang/scanner/:/usr/share/java/junit-4.12.jar:/usr/share/java/hamcrest-core-1.3.jar;
fi


if [ "$1" = "DEBUGSCANNER" ]; then
    javac -g -cp $CLASSPATH scanner/TestScanner.java scanner/Scanner.java scanner/TokenStream.java
    mv scanner/*.class scanner/cop5555sp15/
    jdb -classpath $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestScanner 
elif [ "$1" = "TESTSCANNER" ]; then
    javac -cp $CLASSPATH scanner/TestScanner.java scanner/Scanner.java scanner/TokenStream.java
    mv scanner/*.class scanner/cop5555sp15/
    java -cp $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestScanner;
elif [ "$1" = "DEBUGPARSER" ]; then
    javac -g -cp $CLASSPATH parser/TestSimpleParser.java parser/SimpleParser.java
    mv parser/*.class parser/cop5555sp15/
    jdb -classpath $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestSimpleParser 
elif [ "$1" = "TESTPARSER" ]; then
    javac -cp $CLASSPATH parser/TestSimpleParser.java parser/SimpleParser.java
    mv parser/*.class parser/cop5555sp15/
    java -cp $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestSimpleParser;
fi
