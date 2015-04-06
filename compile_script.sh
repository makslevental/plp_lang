#!/bin/bash
if [ -z "$CLASSPATH" ]; then 
    export CLASSPATH=/home/maksim/Desktop/plp_lang/parser/:/home/maksim/Desktop/plp_lang/ast/:/home/maksim/Desktop/plp_lang/scanner/:/usr/share/java/junit-4.12.jar:/usr/share/java/hamcrest-core-1.3.jar:/home/maksim/Desktop/plp_lang/type_checking_code_generation/asm-5.0.3.jar:/home/maksim/Desktop/plp_lang/type_checking_code_generation/asm-util-5.0.3.jar:/home/maksim/Desktop/plp_lang/type_checking_code_generation/;
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
    java -cp $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestSimpleParser
elif [ "$1" = "DEBUGAST" ]; then
    javac -g -cp $CLASSPATH ast/TestParser.java ast/Parser.java
    mv ast/*.class ast/cop5555sp15/
    jdb -classpath $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestParser 
elif [ "$1" = "TESTAST" ]; then
    javac -cp $CLASSPATH ast/TestParser.java ast/Parser.java
    mv ast/*.class ast/cop5555sp15/
    java -cp $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestParser 
elif [ "$1" = "DEBUGASTEX" ]; then
    javac -g -cp $CLASSPATH ast/TestParserErrorHandling.java ast/Parser.java
    mv ast/*.class ast/cop5555sp15/
    jdb -classpath $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestParserErrorHandling 
elif [ "$1" = "TESTASTEX" ]; then
    javac -cp $CLASSPATH ast/TestParserErrorHandling.java ast/Parser.java
    mv ast/*.class ast/cop5555sp15/
    java -cp $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.TestParserErrorHandling 
elif [ "$1" = "TESTCODETYPE" ]; then
    javac -cp $CLASSPATH type_checking_code_generation/cop5555sp15/Assignment4Tests.java
    #mv ast/*.class type_checking_code_generation/cop5555sp15/
    java -cp $CLASSPATH org.junit.runner.JUnitCore cop5555sp15.Assignment4Tests 
fi
