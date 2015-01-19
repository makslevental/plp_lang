package cop5555sp15;
import java.util.*;
import java.io.IOException;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import static cop5555sp15.TokenStream.Kind.*;


public class Scanner {

    private enum State {
	START, GOT_EQUALS, IDENT_PART, GOT_ZERO, DIGITS, EOF, GOTCR, SLASH, COMMENT, WHITESPACE, OPERATOR, SEPARATOR
    }
    private State state;
    public static final Set<Character> sepBegs = new HashSet<Character>(Arrays.asList('.' , ';' , ',' , '(' , ')' , '[' , ']', '{' , '}', ':', '?'));
    public static final Set<Character> opBegs = new HashSet<Character>(Arrays.asList('=' , '|' , '&' , '!' , '<' , '>' , '+' , '-' , '*' , '/' , '%' , '@'));
    public static final Set<Character> whiteSp = new HashSet<Character>(Arrays.asList('\n','\t','\r',' '));
    //local references to TokenStream objects for convenience
    final TokenStream tStream;  //set in constructor
    private int index; // points to the next char to process during scanning, or if none, past the end of the array
    char ch;
    int lineNum;
    public Scanner(TokenStream tStrm){
	tStream = tStrm;
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
		if(ch =='\0'){
		    state = State.EOF;
		    break;
		}
		else if(whiteSp.contains(ch)){
		    state = State.WHITESPACE;
		    break;
		}
		else if(opBegs.contains(ch)){
		    state = State.OPERATOR; 
		    break;
		}
		else if(sepBegs.contains(ch)){
		    state = State.SEPARATOR;
		    break;
		}
		else{
		    switch(ch){
		    }
		    getch();
		}
		//System.out.println(index);
		break; // end of state START
	    case EOF:
		t = makeDefaTok(EOF,begOffset);
		break;
	    case WHITESPACE:
		if(ch==' ' || ch=='\t'){ //blank space, getch, then go back to start
		    getch();
		}
		else if(ch=='\r'){ // CR, getch next char, if not \n then incr lineNum and leave next otherwise, increment and getch again
		    getch();
		    if(ch == '\n')
			getch();
		    lineNum++;	    
		}
		else {// if(ch== '\n'):
		    getch();
		    lineNum++;
		}
	        state = State.START;
	        break;
	    case OPERATOR:
		if (ch=='/'){
		    getch();
		    switch(ch){
		    case '*':
			state = State.COMMENT;
			break;
		    default:
			t = tStream.new Token(DIV, begOffset, index-1, lineNum);
			//don't eat the character
			state = State.START;
		    }
		    break;
		}
	    case SEPARATOR:
		if(ch=='.'){
		    //tok.makeDefaTok(DOT);//
		    t = makeDefaTok(DOT, begOffset);
		}

		///////////////////////////////////////////////
		//implement rest of separators
		//
		///////////////////////////////////////////////





	    case COMMENT:
		boolean inComment = true;
		getch(); // have eaten /*, char right after '/*' in ch
		//System.out.println(ch+" "+index);
		do {
		    switch(ch){
		    case '\n':
			lineNum++;
			break;
		    case '\r':
			getch();
			if(ch=='\n'){
			    lineNum++;
			    break;
			}
			else{
			    lineNum++;
			    continue;
			}
		    case '*':
			getch();
			if(ch=='/'){
			    state = State.START;
			    inComment = false;
			    break;
			}
		    default:
			//do nada
		    }
		    getch();
		} while(inComment);
		break;
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

    private Token makeDefaTok(Kind typ,int begOffset){
	return tStream.new Token(typ,begOffset,index,lineNum);
    }

   
}

