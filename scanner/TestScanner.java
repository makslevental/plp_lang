package cop5555sp15;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import cop5555sp15.TokenStream;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import static cop5555sp15.TokenStream.Kind.*;

public class TestScanner {

    @Test
    public void emptyInput() {
	System.out.println("Test: emptyInput");
	String input = "";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(1, stream.tokens.size()); // creates EOF token
	assertEquals(EOF, stream.nextToken().kind);

    }

    @Test
    public void onlySpaces() {
	System.out.println("Test: onlySpaces");
	String input = "     "; // five spaces
	System.out.println("input is five spaces");
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	assertEquals(1, stream.tokens.size()); // creates EOF token
	Token t = stream.nextToken();
	System.out.println(stream);
	assertEquals(EOF, t.kind);
	assertEquals(5, t.beg);
    }
    @Test
    public void skipWhiteSpaceMax() {
	System.out.println("Test: skipWhiteSpace no other tokens");
	String input = "   \n  \r \t   \r\n";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	assertEquals(1, stream.tokens.size());
	System.out.println(stream);
	Token t = stream.nextToken();
	assertEquals(EOF, t.kind);
	assertEquals(14, t.beg);
	assertEquals(4,t.getLineNumber());
    }

    @Test
    public void onlyComment() {
    	System.out.println("Test: onlyComment");
    	String input = "/* */";
    	System.out.println(input);
    	TokenStream stream = new TokenStream(input);
    	Scanner scanner = new Scanner(stream);
    	scanner.scan();
    	System.out.println(stream);
    	Kind[] expectedKinds = { EOF };
    	String[] expectedTexts = { "" }; // need empty string for eof
    	assertArrayEquals(expectedKinds, makeKindArray(stream));
    	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	Token t = stream.nextToken();
	assertEquals(EOF, t.kind);
	assertEquals(5, t.beg);
	assertEquals(1,t.getLineNumber());
    }

    @Test
    public void commentWithWhiteSpace() {
    	System.out.println("Test: comments with whitespace");
    	String input = "/*  \t \t \r \n \r\n   */";
    	System.out.println(input);
    	TokenStream stream = new TokenStream(input);
    	Scanner scanner = new Scanner(stream);
    	scanner.scan();
    	System.out.println(stream);
    	Kind[] expectedKinds = { EOF };
    	String[] expectedTexts = { "" }; // need empty string for eof
    	assertArrayEquals(expectedKinds, makeKindArray(stream));
    	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	Token t = stream.nextToken();
	assertEquals(EOF, t.kind);
	assertEquals(19, t.beg);
	assertEquals(4,t.getLineNumber());

    }

    @Test
    public void commentWithCharacters() {
    	System.out.println("Test: comments with character");
    	String input = "/*123456789*/";
    	System.out.println(input);
    	TokenStream stream = new TokenStream(input);
    	Scanner scanner = new Scanner(stream);
    	scanner.scan();
    	System.out.println(stream);
    	Kind[] expectedKinds = { EOF };
    	String[] expectedTexts = { "" }; // need empty string for eof
    	assertArrayEquals(expectedKinds, makeKindArray(stream));
    	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
	Token t = stream.nextToken();
	assertEquals(EOF, t.kind);
	assertEquals(13, t.beg);
	assertEquals(1,t.getLineNumber());

    }
    @Test
    public void dotsAndRanges() {
	System.out.println("Test: dotsAndRanges");
	String input = ".\n..\n.. . . ..\n...\n";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(DOT, stream.nextToken().kind);
	assertEquals(RANGE, stream.nextToken().kind);
	assertEquals(RANGE, stream.nextToken().kind);
	assertEquals(DOT, stream.nextToken().kind);
	assertEquals(DOT, stream.nextToken().kind);
	assertEquals(RANGE, stream.nextToken().kind);
	assertEquals(RANGE, stream.nextToken().kind);
	assertEquals(DOT, stream.nextToken().kind);
	assertEquals(EOF, stream.nextToken().kind);
	assertEquals(3, stream.tokens.get(5).getLineNumber());// 5th token is on line 3
    }

    @Test
    public void separators() {
	System.out.println("Test: separators");
	String input = ":\n;\n([.. ] )  [  ] {\n},\n";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Token t = stream.nextToken();
	assertEquals(COLON, t.kind);
	assertEquals(0,t.beg);
	t = stream.nextToken();
	assertEquals(SEMICOLON, t.kind);
        assertEquals(2, t.beg);
	assertEquals(LPAREN, stream.nextToken().kind);
	assertEquals(LSQUARE, stream.nextToken().kind);
	assertEquals(RANGE, stream.nextToken().kind);
	assertEquals(RSQUARE, stream.nextToken().kind);
	assertEquals(RPAREN, stream.nextToken().kind);
	t = stream.nextToken();
	assertEquals(LSQUARE, t.kind);
	assertEquals(14, t.beg);
	assertEquals(RSQUARE, stream.nextToken().kind);
	assertEquals(LCURLY, stream.nextToken().kind);
	assertEquals(RCURLY, stream.nextToken().kind);
	assertEquals(COMMA, stream.nextToken().kind);
	assertEquals(3, stream.tokens.get(5).getLineNumber());// 5th token is on line 3
    }

  
    @Test
    public void skipWhiteSpace() {
	System.out.println("Test: skipWhiteSpace");
	String input = "   ;;;   %@%\n  \r   \r\n ;;;";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(SEMICOLON, stream.nextToken().kind);
	assertEquals(SEMICOLON, stream.nextToken().kind);
	assertEquals(SEMICOLON, stream.nextToken().kind);
	assertEquals(MOD, stream.nextToken().kind);
	assertEquals(AT, stream.nextToken().kind);
	assertEquals(MOD, stream.nextToken().kind);
	assertEquals(SEMICOLON, stream.nextToken().kind);
	assertEquals(SEMICOLON, stream.nextToken().kind);
	Token t = stream.nextToken();
	assertEquals(SEMICOLON, t.kind);
	assertEquals(4,t.getLineNumber());
    }
  
    @Test
    public void noWhiteSpace() {
	System.out.println("Test: noWhitespace");
	String input = "@%";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(3, stream.tokens.size()); // one each for @ and %, plus the eof token
	assertEquals(AT, stream.nextToken().kind);
	assertEquals(MOD, stream.nextToken().kind);
	assertEquals(EOF, stream.nextToken().kind);

    }

    @Test
    public void errorToken() {
	System.out.println("Test: illegal char");
	String input = "@#  *";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(4, stream.tokens.size()); // one each for @,#, and *, plus
	// the eof token
	assertEquals(AT, stream.nextToken().kind);
	assertEquals(ILLEGAL_CHAR, stream.nextToken().kind);
	assertEquals(TIMES, stream.nextToken().kind);
	assertEquals(EOF, stream.nextToken().kind);

    }


    @Test
    public void firstPartAtEndOfInput() {
	System.out.println("Test: firstPartATEndOfInput");
	String input = "!";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(NOT, stream.nextToken().kind);
	assertEquals(EOF, stream.nextToken().kind);
    }

    @Test
    public void twoStateTokens() {
	System.out.println("Test: twoStateTokens");
	String input = "= == =\n= ! != - -> -! =!!";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(ASSIGN, stream.nextToken().kind);
	assertEquals(EQUAL, stream.nextToken().kind);
	assertEquals(ASSIGN, stream.nextToken().kind);
	assertEquals(ASSIGN, stream.nextToken().kind);
	assertEquals(NOT, stream.nextToken().kind);
	assertEquals(NOTEQUAL, stream.nextToken().kind);
	assertEquals(MINUS, stream.nextToken().kind);
	assertEquals(ARROW, stream.nextToken().kind);
	assertEquals(MINUS, stream.nextToken().kind);
	assertEquals(NOT, stream.nextToken().kind);
	assertEquals(ASSIGN, stream.nextToken().kind);
	assertEquals(NOT, stream.nextToken().kind);
	assertEquals(NOT, stream.nextToken().kind);
	assertEquals(EOF, stream.nextToken().kind);
    }

    // This test constructs the exptected token list and compares to the one
    // created by the Scanner
    @Test
    public void compareTokenList() {
	System.out.println("Test: compareTokenList");
	String input = "= ==";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Token t0 = stream.new Token(ASSIGN, 0, 1, 1);
	Token t1 = stream.new Token(EQUAL, 2, 4, 1);
	Token t2 = stream.new Token(EOF, 4, 5, 1);
	ArrayList<Token> expected_tokens = new ArrayList<Token>();
	expected_tokens.add(t0);
	expected_tokens.add(t1);
	expected_tokens.add(t2);
	assertArrayEquals(expected_tokens.toArray(), stream.tokens.toArray());
    }

    @Test
    public void lessAndGreater() {
	System.out.println("Test: lessAndGreater");
	String input = " < << <= > >> >= -> <>";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(LT, stream.nextToken().kind);
	assertEquals(LSHIFT, stream.nextToken().kind);
	assertEquals(LE, stream.nextToken().kind);
	assertEquals(GT, stream.nextToken().kind);
	assertEquals(RSHIFT, stream.nextToken().kind);
	assertEquals(GE, stream.nextToken().kind);
	assertEquals(ARROW, stream.nextToken().kind);
	assertEquals(LT, stream.nextToken().kind);
	assertEquals(GT, stream.nextToken().kind);
	assertEquals(EOF, stream.nextToken().kind);
    }
  
    @Test
    public void intLiterals() {
	System.out.println("Test: lessAndGreater");
	String input = "0 1 23 45+ 67<=9";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, INT_LIT, PLUS,
				 INT_LIT, LE, INT_LIT, EOF };
	String[] expectedTexts = { "0", "1", "23", "45", "+", "67", "<=", "9",
				   "" }; // need empty string for eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void stringLiterals() {
	System.out.println("Test: stringLiterals");
	String input = " \"abc\" \"def\" \"ghijk\" \"123\" \"&^%$\" ";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { STRING_LIT, STRING_LIT, STRING_LIT,
				 STRING_LIT, STRING_LIT, EOF };
	String[] expectedTexts = { "abc", "def", "ghijk", "123", "&^%$", "" }; // need empty string for eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void identifiers() {
	System.out.println("Test: identifiers");
	String input = " abc ddef ghijk 123 a234 32a";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { IDENT, IDENT, IDENT, INT_LIT, IDENT, INT_LIT,
				 IDENT, EOF };
	String[] expectedTexts = { "abc", "ddef", "ghijk", "123", "a234", "32",
				   "a", "" }; // need empty string for eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void keywords() {
	System.out.println("Test: keywords");
	String input = " int  string  boolean import  class  def  while if  else  return  print aaa";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { KW_INT, KW_STRING, KW_BOOLEAN, KW_IMPORT,
				 KW_CLASS, KW_DEF, KW_WHILE, KW_IF, KW_ELSE, KW_RETURN,
				 KW_PRINT, IDENT, EOF };
	String[] expectedTexts = { "int", "string", "boolean", "import",
				   "class", "def", "while", "if", "else", "return", "print",
				   "aaa", "" }; // need empty string for eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void boolAndNullLiterals() {
	System.out.println("Test: boolAndNullLiterals");
	String input = " true false\n null";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { BL_TRUE, BL_FALSE, NL_NULL, EOF };
	String[] expectedTexts = { "true", "false", "null", "" }; // need empty
	// string
	// for eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }




    @Test
    public void multiLineString() {
	System.out.println("Test: multiLineString");
	String input = " \"true false\n null\" ";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { STRING_LIT, EOF };
	String[] expectedTexts = { "true false\n null", "" }; // need empty
	// string for
	// eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));

    }
    
    @Test
    public void comments() {
	System.out.println("comments");
	String input = "/**/ 0 1 45+ 67<=9";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
				 INT_LIT, EOF };
	String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "" }; // need
	// empty
	// string
	// for
	// eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void comments2() {
	System.out.println("comments2");
	String input = "/**/ 0 1 /** ***/ 45+ 67<=9";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
				 INT_LIT, EOF };
	String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "" }; // need
	// empty
	// string
	// for
	// eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void comments3() {
	System.out.println("comments3");
	String input = "/**/ 0 1 /** ***/ 45+ 67<=9/*";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
				 INT_LIT, UNTERMINATED_COMMENT, EOF };
	String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "/*",
				   "" }; // need empty string for eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void onlyComment2() {
	System.out.println("onlyComment");
	String input = "/**/";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	Kind[] expectedKinds = { EOF };
	String[] expectedTexts = { "" }; // need empty string for eof
	assertArrayEquals(expectedKinds, makeKindArray(stream));
	assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void singleSlash(){
	System.out.println("singleSlash");
	String input = "/";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
    }

    @Test
    public void unterminatedString(){
	System.out.println("Test: unterminated string");
	String input = "\"  ";
	System.out.println(input);
	TokenStream stream = new TokenStream(input);
	Scanner scanner = new Scanner(stream);
	scanner.scan();
	System.out.println(stream);
	assertEquals(UNTERMINATED_STRING, stream.nextToken().kind);
    }


    // Creates an array containing the kinds of the tokens in the token list
    Kind[] makeKindArray(TokenStream stream) {
	Kind[] kinds = new Kind[stream.tokens.size()];
	for (int i = 0; i < stream.tokens.size(); ++i) {
	    kinds[i] = stream.tokens.get(i).kind;
	}
	return kinds;
		
    }

    // Creates an array containing the texts of the tokens in the token list
    String[] makeTokenTextArray(TokenStream stream) {
	String[] kinds = new String[stream.tokens.size()];
	for (int i = 0; i < stream.tokens.size(); ++i) {
	    kinds[i] = stream.tokens.get(i).getText();
	}
	return kinds;
    }

	
}
