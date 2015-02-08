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
    public static final Set<Kind> stmtPredSt = new HashSet<Kind>(Arrays.asList(IDENT,KW_PRINT,KW_WHILE,KW_IF,MOD,KW_RETURN,SEMICOLON));
    public static final int DEBUGMAXPARSER = 1;
				
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
	    if(DEBUGMAXPARSER==1) System.out.println("***"+t.toString()+"***");
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
	if(DEBUGMAXPARSER==1) System.out.println("programdown");
	ImportList();
	match(KW_CLASS);
	match(IDENT);
	Block();
	if(DEBUGMAXPARSER==1) System.out.println("programup");
    }

    private void ImportList() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("importlistdown");
	while (isKind(KW_IMPORT)){
	    match(KW_IMPORT);
	    match(IDENT);
	    while(isKind(DOT)){
		match(DOT);
		match(IDENT);
	    }
	    match(SEMICOLON);
	}
	if(DEBUGMAXPARSER==1) System.out.println("importlistup");
    }

    private void Block() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("blockdown");
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
	if(DEBUGMAXPARSER==1) System.out.println("blockup");
    }

    private void Statement() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("statementdown");
	if(isKind(IDENT)){
	    LValue();
	    match(ASSIGN);
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
	else{
	    if(DEBUGMAXPARSER==1) System.out.println("statementup");
	    return;//epsilon
	}
    }


    private void Decalaration() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("declaration");
	match(KW_DEF);
	DecHead();	
    }

    private void DecHead() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("decheaddown");
	match(IDENT);
	DecTail();
	if(DEBUGMAXPARSER==1) System.out.println("decheadup");
    }

    private void DecTail() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("dectaildown");
	if(isKind(ASSIGN)){
	    match(ASSIGN);
	    Closure();
	}
	else if(isKind(COLON)){
	    match(COLON);
	    if(isKind(AT))
		CompositeValueType();
	    else
		SimpleType();
	}
	else if(isKind(KW_INT)||isKind(KW_BOOLEAN)||isKind(KW_STRING)){
	    SimpleType();
	}
	else{
	    if(DEBUGMAXPARSER==1) System.out.println("dectailup");
	    return;//epsilon
	}
    }


    private void VarDec() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("vardecdown");
	match(IDENT);
	if(isKind(SEMICOLON)){
	    match(SEMICOLON);
	    Type();
	}
	else{
	    if(DEBUGMAXPARSER==1) System.out.println("vardecup");
	    return;//epsilon
	}
    }


    private void Type() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("typedown");
	if(isKind(KW_INT)||isKind(KW_BOOLEAN)||isKind(KW_STRING))
	   SimpleType();
	else
	    CompositeValueType();	   
	if(DEBUGMAXPARSER==1) System.out.println("typeup");
    }

    private void SimpleType() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("simpletypedown");
	if(isKind(KW_INT))
	    match(KW_INT);
	else if(isKind(KW_BOOLEAN))
	    match(KW_BOOLEAN);
	else
	    match(KW_STRING);
	if(DEBUGMAXPARSER==1) System.out.println("simpletypeup");
    }

    private void CompositeValueType() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("compositevaluetypedown");
	match(AT);
	if(isKind(LSQUARE)){
	    match(LSQUARE);
	    Type();
	    match(RSQUARE);
	}
	else{
	    match(AT);
	    match(LSQUARE);
	    SimpleType();
	    match(COLON);
	    Type();
	    match(RSQUARE);
	}
	if(DEBUGMAXPARSER==1) System.out.println("compositevaluetypeup");
	    
    }
    // private void ClosureDec() throws SyntaxException {
    // 	if(DEBUGMAXPARSER==1) System.out.println("closuredecdown");
    // 	match(IDENT);
    // 	match(ASSIGN);
    // 	Closure();
    // 	if(DEBUGMAXPARSER==1) System.out.println("closuredecup");
    // }
    private void Closure() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("closuredown");
	match(LCURLY);
	FormalArgList();
	match(ARROW);
	while(isInPredSt(stmtPredSt)){
	    Statement();
	    match(SEMICOLON);
	}
	match(RCURLY);
	if(DEBUGMAXPARSER==1) System.out.println("closureup");
    }
    private void FormalArgList() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("formalarglistdown");
	if(isKind(IDENT)){
	    VarDec();
	    while(isKind(COMMA)){
		match(COMMA);
		VarDec();
	    }
	}
	if(DEBUGMAXPARSER==1) System.out.println("formalarglistup");
    }
    private void If() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("ifdown");
	match(KW_IF);
	match(LPAREN);
	Expression();
	match(RPAREN);
	Block();
	Else();
	if(DEBUGMAXPARSER==1) System.out.println("ifup");
    }
    private void Else() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("elsedown");
	if(isKind(KW_ELSE)){
	    match(KW_ELSE);
	    Block();
	}
	if(DEBUGMAXPARSER==1) System.out.println("elseup");
    }
    private void While() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("whiledown");
	match(KW_WHILE);
	if(isKind(TIMES))
	    WhileStar();
	else{
	    match(LPAREN);
	    Expression();
	    match(RPAREN);
	    Block();
	}
	if(DEBUGMAXPARSER==1) System.out.println("whiledown");
    }
    private void WhileStar() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("whilestardown");
	match(TIMES);
	match(LPAREN);
	Expression();
	WhileStarExprTail();
	if(DEBUGMAXPARSER==1) System.out.println("whilestarup");
    }

    private void WhileStarExprTail() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("whilestarexprtaildown");
	if(isKind(RANGE))
	    RangeExprTail();
	match(RPAREN);
	Block();
	if(DEBUGMAXPARSER==1) System.out.println("whilestarexprtailup");
    }

    private void RangeExprTail() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("rangeexprtaildown");
	match(RANGE);
	Expression();
	if(DEBUGMAXPARSER==1) System.out.println("rangeexprtailup");
    }

    private void LValue() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("lvaluedown");
	match(IDENT);
	if(isKind(LSQUARE))
	    LRValue();	
	if(DEBUGMAXPARSER==1) System.out.println("lvalueup");
    }
    private void LRValue() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("lrvaluedown");
	match(LSQUARE);
	ExpressionList();
	match(RSQUARE);
	if(DEBUGMAXPARSER==1) System.out.println("lrvalueup");
    }

    private void ExpressionList() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("expressionlistdown");
	if(isInPredSt(fctrPredSt)){
	    Expression();
	    while(isKind(COMMA)){
		match(COMMA);
		Expression();
	    }
	}
	if(DEBUGMAXPARSER==1) System.out.println("expressionlistup");
	    
    }
    private void KeyValueList() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("keyvaluelistdown");
	if(isInPredSt(fctrPredSt)){
	    KeyValueExpression();
	    while(isKind(COMMA)){
		match(COMMA);
		KeyValueExpression();
	    }
	}
	if(DEBUGMAXPARSER==1) System.out.println("keyvaluelistup");
    }
    private void KeyValueExpression() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("keyvalueexpressionup");
	Expression();
	match(COLON);
	Expression();
	if(DEBUGMAXPARSER==1) System.out.println("keyvalueexpressiondown");
    }
    private void RangeExpr() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("rangeexprdown");
	Expression();
	match(RANGE);
	Expression();
	if(DEBUGMAXPARSER==1) System.out.println("rangeexprup");
    }
    private void Expression() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("expressiondown");
	Term();
	while(isInPredSt(relOpPredSt)){
	    RelOp();
	    Term();
	}
	if(DEBUGMAXPARSER==1) System.out.println("expressionup");
    }
    private void Term() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("termdown");
	Elem();
	while(isKind(PLUS)||isKind(MINUS)){
	    WeakOp();
	    Elem();
	}
	if(DEBUGMAXPARSER==1) System.out.println("termup");
    }
    private void Elem() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("elemdown");
	Thing();
	while(isKind(TIMES)||isKind(DIV)){
	    StrongOp();
	    Thing();
	}
	if(DEBUGMAXPARSER==1) System.out.println("elemup");
    }
    private void Thing() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("thingdown");
	Factor();
	while(isKind(LSHIFT)||isKind(RSHIFT)){
	    VeryStrongOp();
	    Factor();
	}
	if(DEBUGMAXPARSER==1) System.out.println("thingup");
    }
    private void Factor() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("factordown");
	if(isKind(IDENT))
	    IdentInFactor();
	else if(isKind(INT_LIT))
	    match(INT_LIT);
	else if(isKind(BL_TRUE))
	    match(BL_TRUE);
	else if(isKind(BL_FALSE))
	    match(BL_FALSE);
	else if(isKind(STRING_LIT))
	    match(STRING_LIT);
	else if(isKind(NOT)){
	    match(NOT);
	    Factor();
	}
	else if(isKind(MINUS)){
	    match(MINUS);
	    Factor();
	}
	else if(isKind(KW_SIZE)){
	    match(KW_SIZE);
	    match(LPAREN);
	    Expression();
	    match(RPAREN);
	}
	else if(isKind(KW_KEY)){
	    match(KW_KEY);
	    match(LPAREN);
	    Expression();
	    match(RPAREN);
	}
	else if(isKind(KW_VALUE)){
	    match(KW_KEY);
	    match(LPAREN);
	    Expression();
	    match(RPAREN);
	}
	else if(isKind(LCURLY))
	    Closure();
	else if(isKind(AT))
	    List();
	else if(isKind(LPAREN)){
	    match(LPAREN);
	    Expression();
	    match(RPAREN);
	}
	else{
	    throw new SyntaxException(t, "factor exception");		
	}
	if(DEBUGMAXPARSER==1) System.out.println("factorup");
    }

    private void List() throws SyntaxException {
	match(AT);
	if(isKind(AT)){
	    match(AT);
	    match(LSQUARE);
	    KeyValueList();
	    match(RSQUARE);
	}
	else{
	    match(LSQUARE);
	    ExpressionList();
	    match(RSQUARE);
	}
    }

    private void IdentInFactor() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("indentinfactordown");
	match(IDENT);
	if(isKind(LSQUARE)){
	    match(LSQUARE);
	    Expression();
	    match(RSQUARE);
	}
	else if(isKind(LPAREN)){
	    match(LPAREN);
	    ExpressionList();
	    match(RPAREN);
	}
	else{
	    if(DEBUGMAXPARSER==1) System.out.println("indentinfactorup");
	    return;//epsilon
	}
    }

    private void RelOp() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("relopdown");
	if(isKind(BAR))
	    match(BAR);
	else if(isKind(AND))
	    match(AND);
	else if(isKind(EQUAL))
	    match(EQUAL);
 	else if(isKind(NOTEQUAL))
	    match(NOTEQUAL);
 	else if(isKind(LT))
	    match(LT);
 	else if(isKind(GT))
	    match(GT);
 	else if(isKind(LE))
	    match(LE);
 	else if(isKind(GE))
	    match(GE);
 	else{
	    throw new SyntaxException(t,"relop exception"); 
	}
	if(DEBUGMAXPARSER==1) System.out.println("relopup");
    }

    private void WeakOp() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("weakopdown");
	if(isKind(PLUS))
	    match(PLUS);
	else if(isKind(MINUS))
	    match(MINUS);
	else{
	    throw new SyntaxException(t,"weakop exception");
	}
	if(DEBUGMAXPARSER==1) System.out.println("weakdown");
    }
    private void StrongOp() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("strongopdown");
	if(isKind(TIMES))
	    match(TIMES);
	else if(isKind(DIV))
	    match(DIV);
	else{
	    throw new SyntaxException(t,"strongop exception");	
	}
	if(DEBUGMAXPARSER==1) System.out.println("strongopup");
    }
    private void VeryStrongOp() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("verystrongopdown");
	if(isKind(LSHIFT))
	    match(LSHIFT);
	else if(isKind(RSHIFT))
	    match(RSHIFT);
	else{
	    throw new SyntaxException(t,"verystrongop exception");
	}
	if(DEBUGMAXPARSER==1) System.out.println("verystrongopup");

    }


}
