package cop5555sp15;
import java.util.*;
import java.io.IOException;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import static cop5555sp15.TokenStream.Kind.*;


public class Scanner {

    private enum State {
	START, IDENT_PART, DIGITS, EOF,  COMMENT, WHITESPACE, OPERATOR, SEPARATOR, STRING_LIT
    }
    private State state;
    public static final Set<Character> sepBegs = new HashSet<Character>(Arrays.asList('.' , ';' , ',' , '(' , ')' , '[' , ']', '{' , '}', ':', '?'));
    public static final Set<Character> opBegs = new HashSet<Character>(Arrays.asList('=' , '|' , '&' , '!' , '<' , '>' , '+' , '-' , '*' , '/' , '%' , '@'));
    public static final Set<Character> whiteSp = new HashSet<Character>(Arrays.asList('\n','\t','\r',' '));

    public static final String[] keywordStrs = {"int","string","boolean","import","class","def","while","if","else","return","print","true","false","null"};
    public static final Kind[] keywordKnds = {KW_INT, KW_STRING, KW_BOOLEAN, KW_IMPORT, KW_CLASS, KW_DEF, KW_WHILE, KW_IF, KW_ELSE, KW_RETURN, KW_PRINT,BL_TRUE, BL_FALSE,NL_NULL,};
    public static final Map<String,Kind> keywords;
    static{
	keywords = new HashMap<String,Kind>();
	for(int i=0; i<keywordStrs.length;++i)
	    keywords.put(keywordStrs[i],keywordKnds[i]);
    }

    //local references to TokenStream objects for convenience
    final TokenStream tStream;  //set in constructor
    private int index; // points to the next char to process during scanning, or if none, past the end of the array
    char ch;
    int lineNum;
    int begOffset;
    public Scanner(TokenStream tStrm){
	tStream = tStrm;
	index = 0;
	begOffset = 0;
	lineNum = 1;
    }
   
    
    private char getch() throws IOException {
	if(tStream.inputChars.length>index) ch = tStream.inputChars[index];
	else ch = '\0';
	index++;
	//System.out.println("getch:"+(int)ch);
	return ch;
    }

    //returns the next token in the input
    public Token next() throws IOException, NumberFormatException {

	state = State.START;
	Token t = null;
	getch();
	do { // loop terminates when a token is created 
	    switch (state) { 
		/*in each state, check the next character.
		  either create a token or change state
		*/
	    case START:
		begOffset = index-1;
		//System.out.println("index:"+index);
		//System.out.println((int)ch);
		if(ch=='\0') t = makeDefaTok(EOF);
		else if(whiteSp.contains(ch)) state = State.WHITESPACE;
		else if(opBegs.contains(ch)) state = State.OPERATOR; 
		else if(sepBegs.contains(ch)) state = State.SEPARATOR;
		else if(Character.isDigit(ch)) state = State.DIGITS;
		else if(Character.isJavaIdentifierStart(ch)) state = State.IDENT_PART;
		else if(ch=='"') state = State.STRING_LIT;
		else t = makeDefaTok(ILLEGAL_CHAR);
		//System.out.println(index);
		break; // end of state START
	    case WHITESPACE:
		if(ch==' ' || ch=='\t') getch(); //blank space, getch, then go back to start
		else if(ch=='\r'){ // CR, getch next char, if not \n then incr lineNum and leave next otherwise, increment and getch again
		    getch();
		    if(ch == '\n') getch();
		    lineNum++;	    
		}
		else {// if(ch== '\n'):
		    getch();
		    lineNum++;
		}
	        state = State.START;
	        break;
	    case OPERATOR:
		//System.out.println(ch);
		if(ch=='='){
		    getch();
		    if(ch=='=') t = makeDefaTok(EQUAL);
		    else t = makePeekTok(ASSIGN);
		} 
		else if(ch=='|') t = makeDefaTok(BAR);
		else if(ch=='!'){
		    getch();
		    if(ch=='=') t = makeDefaTok(NOTEQUAL);
		    else t = makePeekTok(NOT);
		}
		else if(ch=='<'){
		    getch();
		    if(ch=='=') t = makeDefaTok(LE);
		    else if(ch=='<') t = makeDefaTok(LSHIFT);
		    else t = makePeekTok(LT);
		}
		else if(ch=='>'){
		    getch();
		    if(ch=='=') t = makeDefaTok(GE);
		    else if(ch=='>') t = makeDefaTok(RSHIFT);
		    else t = makePeekTok(GT);
		}
		else if(ch=='+') t = makeDefaTok(PLUS);
		else if(ch=='-'){
		    getch();
		    if(ch=='>') t = makeDefaTok(ARROW);
		    else t = makePeekTok(MINUS);
		}
		else if(ch=='*') t = makeDefaTok(TIMES);
		else if(ch=='/'){
		    getch();
		    if(ch== '*')
			state = State.COMMENT;
		    else
			t = makePeekTok(DIV);//tStream.new Token(DIV, begOffset, --index, lineNum);
		}
		else if(ch=='%') t = makeDefaTok(MOD);
		else t = makeDefaTok(AT);
		break;
	    case SEPARATOR:
		if(ch=='.'){ 
		    getch();
		    if(ch=='.')	t = makeDefaTok(RANGE);
		    else t = makePeekTok(DOT);
		}
		else if(ch==';') t = makeDefaTok(SEMICOLON);
		else if(ch==',') t = makeDefaTok(COMMA);
		else if(ch=='(') t = makeDefaTok(LPAREN);
		else if(ch==')') t = makeDefaTok(RPAREN);
		else if(ch=='[') t = makeDefaTok(LSQUARE);
		else if(ch==']') t = makeDefaTok(RSQUARE);
		else if(ch=='{') t = makeDefaTok(LCURLY);
		else if(ch=='}') t = makeDefaTok(RCURLY);
		else if(ch==':') t = makeDefaTok(COLON);
		else t = makeDefaTok(QUESTION);
		break;
	    case COMMENT:
		boolean inComment = true;
		getch(); // have eaten /*, char right after '/*' in ch
		
		while(true){    
		    //System.out.println((int)ch);
		    if(ch=='\0'){ //unterminated comment
			t = makePeekTok(UNTERMINATED_COMMENT);
			break;
		    }
		    else if(ch=='\n'){
			lineNum++;
			getch();
		    }
		    else if(ch=='\r'){
			getch();
			if(ch=='\n'){
			    lineNum++;
			    getch();
			}
			else{
			    lineNum++;
			    continue;
			}
		    }
		    else if(ch=='*'){
			getch();
			if(ch=='/'){
			    state = State.START;
			    getch();
			    break;
			}
		    }
		    else getch();
		    
		}
		break;
	    case DIGITS:
		if(ch=='0') t = makeDefaTok(INT_LIT);
		else{
		    while(true){
			getch();
			if(ch<48 || 57<ch) break;
		    }
		    t = makePeekTok(INT_LIT);
		}
		break;
	    case IDENT_PART:
		while(true){
		    getch();
		    if(!Character.isJavaIdentifierStart(ch) && !Character.isDigit(ch)) break;
		}
		t = makePeekTok(IDENT);
		if(keywords.containsKey(t.getText()))
		    t = tStream.new Token(keywords.get(t.getText()), t.beg, t.end, t.lineNumber);
		break;
	    case STRING_LIT:
		//System.out.println("stringlit"+ch);
		while(getch() != '"')
		    if(ch=='\0'){
			t = makeDefaTok(UNTERMINATED_STRING);
			return t;
		    }
		t = makeDefaTok(STRING_LIT);
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

    private Token makeDefaTok(Kind typ){
	return tStream.new Token(typ,begOffset,index,lineNum);
    }

    //for 1 char look ahead have to reset index if diff token
    private Token makePeekTok(Kind typ) throws IOException {
	return tStream.new Token(typ,begOffset,--index,lineNum);
    }
   
}

