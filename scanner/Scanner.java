package cop5555sp15;

import java.io.IOException;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import static cop5555sp15.TokenStream.Kind.*;

public class Scanner {

    private enum State {
	START, GOT_EQUALS, IDENT_PART, GOT_ZERO, DIGITS, EOF, GOTCR, SLASH, COMMENT
    }
    private State state;
    //local references to TokenStream objects for convenience
    final TokenStream tStream;  //set in constructor
    private int index; // points to the next char to process during scanning, or if none, past the end of the array
    char ch;
    int lineNum;
    public Scanner(TokenStream tStrm){
	tStream = tStrm;
	state = State.START;
	index = 0;
	lineNum = 1;
	
    }
   
    private char getch() throws IOException {
	if(tStream.inputChars.length>=index+1)
	    ch = tStream.inputChars[index++];
	else
	    ch = '\0';
	//System.out.println("getch:"+(int)ch);
	return ch;
    }

    //returns the next token in the input
    public Token next() throws IOException, NumberFormatException {
	state = State.START;
	Token t = null;
	int begOffset=0;
	getch();
	do { // loop terminates when a token is created 
	    switch (state) { 
		/*in each state, check the next character.
		  either create a token or change state
		*/
	    case START:
		begOffset = index;
		//System.out.println("index:"+index);
		//System.out.println((int)ch);
		switch (ch) {
		case '\0':  //empty file
		    //System.out.println((int)ch);
		    //System.out.println("eof");
		    state = State.EOF;
		    break;
		case ' ': //blank space, stay in start
		    //		    System.out.println("blank space");
		    break;
		case '\r':
		    state = State.GOTCR;
		    break;
		case '\n':
		    lineNum++;
		    break;
		case '\t':
		    break;
		case '/':
		    state = State.SLASH;
		    // case '=':
		    //     state = State.GOT_EQUALS;
		    //     break;
		    // case '*':
		    //     t = new Token(TIMES, begOffset, index);
		    //     break;
		    // case '+':
		    //     t = new Token(PLUS, begOffset, index);
		    //     break;
		    // case '0':
		    //     state = State.GOT_ZERO;
		    //     break;
		    // default:
		    //     if (Character.isDigit(ch)) {
		    // 	state = State.DIGITS;
		    //     } else if (Character.isJavaIdentifierStart(ch)) {
		    // 	state = State.IDENT_PART;
		    //     } else {
		    // 	handle error
		    // 	    }
		}
		if(state != State.EOF){
		    getch();
		    //System.out.println(index);
		}
		break; // end of state START
	    case GOTCR:
		switch(ch){
		case '\n':
		    //ms newline, eat \n
		    getch();
		    break;
		default:
		    //carriage return for new line so don't eat current char
		}
		lineNum++;
		state = State.START;
		break;
		// case IDENT:
		// case KEYWORD: ....
		// case INT_LITERAL: ....
		// case BOOLEAN_LITERAL: ....
		// case NULL_LITERAL: ....
		// case SEPARATOR: ....
		// case OPERATOR:
		// case STRING_LITERAL: 
		// 	switch (ch) {
		// 	case '':
		// 	    t = new Token(ASSIGN, begOffset, index);
		// 	    break;
		// 	case '=':
		// 	    t = new Token(EQUAL, begOffset, index);
		// 	    break;
		// 	default:
		// 	    if (Character.isDigit(ch)) {
		// 		state = State.DIGITS;
		// 	    } else if (Character.isJavaIdentifierStart(ch)) {
		// 		state = State.IDENT_PART;
		// 	    } else {
		// 		handle error
		// 		    }
		// 	}
		// 	getch();
		// 	break; // end of state START
	    case EOF:
		t = tStream.new Token(EOF, begOffset, index, lineNum);
		break;
	    case SLASH:
		switch(ch){
		case '*':
		    state = State.COMMENT;
		    break;
		default:
		    t = tStream.new Token(DIV, begOffset, index-1, lineNum);
		    //don't eat the character
		    state = State.START;
		}
	    case COMMENT:
		boolean inComment = true;
		getch(); // have eaten /*, char right after '/*' in ch
		do {
		    switch(ch){
		    case '*':
			getch();
			if(ch=='/'){
			    inComment = false;
			    break;
			}
		    default:
			//do nada
		    }
		    getch();
		} while(inComment);
	    default:
		assert false : "should not reach here";
	    }// end of switch(state)
	} while(t==null);
	return t;
    }    
 

    public void scan() {
	Token t;
	try{
	do {
	    t = next();
	    tStream.tokens.add(t);
	} while (!t.kind.equals(EOF));
	} catch (Exception e){
	    e.printStackTrace();
	}
    }
}

