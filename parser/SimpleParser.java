package cop5555sp15;

import java.util.*;

import static cop5555sp15.TokenStream.Kind.AND;
import static cop5555sp15.TokenStream.Kind.ARROW;
import static cop5555sp15.TokenStream.Kind.ASSIGN;
import static cop5555sp15.TokenStream.Kind.AT;
import static cop5555sp15.TokenStream.Kind.BAR;
import static cop5555sp15.TokenStream.Kind.BL_FALSE;
import static cop5555sp15.TokenStream.Kind.BL_TRUE;
import static cop5555sp15.TokenStream.Kind.COLON;
import static cop5555sp15.TokenStream.Kind.COMMA;
import static cop5555sp15.TokenStream.Kind.DIV;
import static cop5555sp15.TokenStream.Kind.DOT;
import static cop5555sp15.TokenStream.Kind.EOF;
import static cop5555sp15.TokenStream.Kind.EQUAL;
import static cop5555sp15.TokenStream.Kind.GE;
import static cop5555sp15.TokenStream.Kind.GT;
import static cop5555sp15.TokenStream.Kind.IDENT;
import static cop5555sp15.TokenStream.Kind.INT_LIT;
import static cop5555sp15.TokenStream.Kind.KW_BOOLEAN;
import static cop5555sp15.TokenStream.Kind.KW_CLASS;
import static cop5555sp15.TokenStream.Kind.KW_DEF;
import static cop5555sp15.TokenStream.Kind.KW_ELSE;
import static cop5555sp15.TokenStream.Kind.KW_IF;
import static cop5555sp15.TokenStream.Kind.KW_IMPORT;
import static cop5555sp15.TokenStream.Kind.KW_INT;
import static cop5555sp15.TokenStream.Kind.KW_PRINT;
import static cop5555sp15.TokenStream.Kind.KW_RETURN;
import static cop5555sp15.TokenStream.Kind.KW_STRING;
import static cop5555sp15.TokenStream.Kind.KW_WHILE;
import static cop5555sp15.TokenStream.Kind.KW_SIZE;
import static cop5555sp15.TokenStream.Kind.KW_KEY;
import static cop5555sp15.TokenStream.Kind.KW_VALUE;
import static cop5555sp15.TokenStream.Kind.LCURLY;
import static cop5555sp15.TokenStream.Kind.LE;
import static cop5555sp15.TokenStream.Kind.LPAREN;
import static cop5555sp15.TokenStream.Kind.LSHIFT;
import static cop5555sp15.TokenStream.Kind.LSQUARE;
import static cop5555sp15.TokenStream.Kind.LT;
import static cop5555sp15.TokenStream.Kind.MINUS;
import static cop5555sp15.TokenStream.Kind.MOD;
import static cop5555sp15.TokenStream.Kind.NOT;
import static cop5555sp15.TokenStream.Kind.NOTEQUAL;
import static cop5555sp15.TokenStream.Kind.PLUS;
import static cop5555sp15.TokenStream.Kind.RANGE;
import static cop5555sp15.TokenStream.Kind.RCURLY;
import static cop5555sp15.TokenStream.Kind.RPAREN;
import static cop5555sp15.TokenStream.Kind.RSHIFT;
import static cop5555sp15.TokenStream.Kind.RSQUARE;
import static cop5555sp15.TokenStream.Kind.SEMICOLON;
import static cop5555sp15.TokenStream.Kind.STRING_LIT;
import static cop5555sp15.TokenStream.Kind.TIMES;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;

public class SimpleParser {

    public static final Set<Kind> fctrPredSt = new HashSet<Kind>(Arrays.asList(IDENT,INT_LIT,BL_TRUE,BL_FALSE,STRING_LIT,LPAREN,NOT,MINUS,KW_SIZE,KW_KEY,KW_VALUE,LCURLY,AT));
    public static final Set<Kind> relOpPredSt = new HashSet<Kind>(Arrays.asList(BAR,AND,EQUAL,NOTEQUAL,LT,GT,LE,GE));
    public static final Set<Kind> stmtPredSt = new HashSet<Kind>(Arrays.asList(IDENT,KW_PRINT,KW_WHILE,KW_IF,MOD,KW_RETURN));
								 
				
    @SuppressWarnings("serial")
    public class SyntaxException extends Exception {
	Token t;
	Kind[] expected;
	String msg;

	SyntaxException(Token t, Kind expected) {
	    this.t = t;
	    msg = "";
	    this.expected = new Kind[1];
	    this.expected[0] = expected;

	}

	public SyntaxException(Token t, String msg) {
	    this.t = t;
	    this.msg = msg;
	}

	public SyntaxException(Token t, Kind[] expected) {
	    this.t = t;
	    msg = "";
	    this.expected = expected;
	}

	public String getMessage() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(" error at token ").append(t.toString()).append(" ")
		.append(msg);
	    sb.append(". Expected: ");
	    for (Kind kind : expected) {
		sb.append(kind).append(" ");
	    }
	    return sb.toString();
	}
    }

    TokenStream tokens;
    Token t;

    SimpleParser(TokenStream tokens) {
	this.tokens = tokens;
	t = tokens.nextToken();
    }

    private Kind match(Kind kind) throws SyntaxException {
	if (isKind(kind)) {
	    consume();
	    return kind;
	}
	throw new SyntaxException(t, kind);
    }

    private Kind match(Kind... kinds) throws SyntaxException {
	Kind kind = t.kind;
	if (isKind(kinds)) {
	    consume();
	    return kind;
	}
	StringBuilder sb = new StringBuilder();
	for (Kind kind1 : kinds) {
	    sb.append(kind1).append(kind1).append(" ");
	}
	throw new SyntaxException(t, "expected one of " + sb.toString());
    }

    private boolean isKind(Kind kind) {
	return (t.kind == kind);
    }

    private boolean isInPredSt(Set predSt) {
	return (predSt.contains(t.kind));
    }

    private void consume() {
	if (t.kind != EOF)
	    t = tokens.nextToken();
    }

    private boolean isKind(Kind... kinds) {
	for (Kind kind : kinds) {
	    if (t.kind == kind)
		return true;
	}
	return false;
    }

    //This is a convenient way to represent fixed sets of
    //token kinds.  You can pass these to isKind.
    static final Kind[] REL_OPS = { BAR, AND, EQUAL, NOTEQUAL, LT, GT, LE, GE };
    static final Kind[] WEAK_OPS = { PLUS, MINUS };
    static final Kind[] STRONG_OPS = { TIMES, DIV };
    static final Kind[] VERY_STRONG_OPS = { LSHIFT, RSHIFT };


    public void parse() throws SyntaxException {
	Program();
	match(EOF);
    }

    private void Program() throws SyntaxException {
	ImportList();
	match(KW_CLASS);
	match(IDENT);
	Block();
    }

    private void ImportList() throws SyntaxException {
	while (isKind(KW_IMPORT))
	    {
		match(KW_IMPORT);
		match(IDENT);
		while(isKind(DOT)){
		    match(DOT);
		    match(IDENT);
		}
		match(SEMICOLON);
	    }
    }

    private void Block() throws SyntaxException {
	match(LCURLY);
	while(isKind(KW_DEF) || isInPredSt(stmtPredSt)){
	    if(isKind(KW_DEF)){
		Decalaration();
		match(SEMICOLON);
	    }
	    else {
		Statement();
		match(SEMICOLON);
	    }
	}
		   
	match(RCURLY);
    }

    private void Statement() throws SyntaxException {
	if(isKind(IDENT)){
	    LValue();
	    match(EQUAL);
	    Expression();
	}
	else if(isKind(KW_PRINT)){
	    match(KW_PRINT);
	    Expression();
	}
	else if(isKind(KW_WHILE))
	    While();
	else if(isKind(KW_IF))
	    If();
	else if(isKind(MOD)){
	    match(MOD);
	    Expression();
	}
	else if(isKind(KW_RETURN)){
	    match(KW_RETURN);
	    Expression();
	}
	else
	    return;//epsilon
	    
    }


    private void Decalaration() throws SyntaxException {
	
    }
    private void VarDec() throws SyntaxException {
	
    }


    private void Type() throws SyntaxException {
	
    }

    private void SimpleType() throws SyntaxException {
	
    }

    private void ValueType() throws SyntaxException {
	
    }
    private void ClosureDec() throws SyntaxException {
	
    }
    private void Closure() throws SyntaxException {
	
    }
    private void FormalArgList() throws SyntaxException {
	
    }
    private void If() throws SyntaxException {
	match(KW_IF);
	match(LPAREN);
	Expression();
	match(RPAREN);
	Block();
	Else();
    }
    private void Else() throws SyntaxException {
	if(isKind(KW_ELSE)){
	    match(KW_ELSE);
	    Block();
	}
    }
    private void While() throws SyntaxException {
	match(KW_WHILE);
	if(isKind(TIMES))
	    WhileStar();
	else{
	    match(LPAREN);
	    Expression();
	    match(RPAREN);
	    Block();
	}
    }
    private void WhileStar() throws SyntaxException {
	match(TIMES);
	match(LPAREN);
	Expression();
	WhileStarExprTail();
    }

    private void WhileStarExprTail() throws SyntaxException {
	if(isKind(RANGE))
	    RangeExprTail();
	match(RPAREN);
	Block();
    }

    private void RangeExprTail() throws SyntaxException {
	match(RANGE);
	Expression();
    }

    private void LValue() throws SyntaxException {
	match(IDENT);
	if(isKind(LSQUARE));
    }
    private void LRValue() throws SyntaxException {
	match(LSQUARE);
	Expression();
	match(RSQUARE);
    }

    private void ExpressionList() throws SyntaxException {
	
    }
    private void KeyValueList() throws SyntaxException {
	
    }
    private void KeyValueExpression() throws SyntaxException {
	
    }
    private void RangeExpr() throws SyntaxException {
	
    }
    private void Expression() throws SyntaxException {
	//fctrPredSt == trmPredSt
	if(isInPredSt(fctrPredSt))
	    Term();
	while(isInPredSt(relOpPredSt)){
	    RelOp();
	    Term();
	}
    }
    private void Term() throws SyntaxException {
	
    }
    private void Elem() throws SyntaxException {
	
    }
    private void Thing() throws SyntaxException {
	
    }
    private void Factor() throws SyntaxException {
	
    }
    private void IdentInFactor() throws SyntaxException {
	
    }
    private void RelOp() throws SyntaxException {
	
    }
    private void WeakOp() throws SyntaxException {
	
    }
    private void StrongOp() throws SyntaxException {
	
    }
    private void VeryStrongOp() throws SyntaxException {
	
    }


}
