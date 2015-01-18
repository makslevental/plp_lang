javac -cp .:/usr/share/java/junit-4.12.jar TestScanner.java Scanner.java TokenStream.java
mv *.class cop5555sp15/
java -cp /home/maksim/Desktop/plp_lang/scanner/:/usr/share/java/junit-4.12.jar:/usr/share/java/hamcrest-core-1.3.jar org.junit.runner.JUnitCore cop5555sp15.TestScanner
