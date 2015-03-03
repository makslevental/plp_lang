package cop5555sp15;
import java.util.*;
import cop5555sp15.ast.*;
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



public class Parser {

    public static final Set<Kind> fctrPredSt = new HashSet<Kind>(Arrays.asList(IDENT,INT_LIT,BL_TRUE,BL_FALSE,STRING_LIT,LPAREN,NOT,MINUS,KW_SIZE,KW_KEY,KW_VALUE,LCURLY,AT));
    public static final Set<Kind> relOpPredSt = new HashSet<Kind>(Arrays.asList(BAR,AND,EQUAL,NOTEQUAL,LT,GT,LE,GE));
    public static final Set<Kind> stmtPredSt = new HashSet<Kind>(Arrays.asList(IDENT,KW_PRINT,KW_WHILE,KW_IF,MOD,KW_RETURN,SEMICOLON));
    public static final int DEBUGMAXPARSER = 0;
				
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

    Parser(TokenStream tokens) {
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

    List<SyntaxException> exceptionList = new ArrayList<SyntaxException>();

    public String getErrors() {
	StringBuilder sb = new StringBuilder();
	for(SyntaxException e: exceptionList){
	    sb.append(e.msg).append('\n');
	}
	return sb.toString();
    }

    public List<SyntaxException> getExceptionList() {
	return exceptionList;
    }

    public Program parse() throws SyntaxException {
	Program p = null;
	try {
	    p = Program();
	    if(p!=null)
		match(EOF);
	} catch (SyntaxException e) {
	    exceptionList.add(e);
	}
	if(exceptionList.isEmpty())
	    return p;
	else {
	    System.err.println("exception list not empty; " + exceptionList.toArray().length + " exceptions");
	    for(SyntaxException e : exceptionList)
	    	System.err.println(e.getMessage());
	    return null;
	}
    }


    private Program Program() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("programdown");

	Token ft = t;
	List<QualifiedName> impList = null;
	Block b = null;
	String className = null;

	try {
	    impList = ImportList();
	    match(KW_CLASS);
	    if(isKind(IDENT)) className = t.getText();
	    match(IDENT);
	    b = Block();
	} catch (SyntaxException e) {
	    exceptionList.add(e);
	}
	Program p = new Program(ft, impList, className, b); 
	
	if(DEBUGMAXPARSER==1) System.out.println("programup");
	return p;
    }

    private List<QualifiedName> ImportList() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("importlistdown");

	List<QualifiedName> impList = new ArrayList<QualifiedName>();
	
	Token ft;

	try {
	    while (isKind(KW_IMPORT)){
		try {
		    ft = t;
		    match(KW_IMPORT);
		    StringBuilder impStSb = new StringBuilder();
		    if(isKind(IDENT)) impStSb.append(t.getText());
		    match(IDENT);

		    while(isKind(DOT)){
			impStSb.append("\\");
			match(DOT);
			if(isKind(IDENT)) impStSb.append(t.getText());
			match(IDENT);
		    }

		    match(SEMICOLON);
		    impList.add(new QualifiedName(ft, impStSb.toString()));
		} catch (SyntaxException e) {
		    //if any match inside import statement throws an exception 
		    //(except semicolon of course because that terminates import)
		    //then eat everything until you hit the semicolon that terminates
		    //the import statement. but make sure not to eat part of forthcoming
		    //<Block>
		    while(!isKind(SEMICOLON) && !isKind(KW_CLASS) && !isKind(KW_IMPORT))
			match(t.kind);
		    if(isKind(SEMICOLON)) match(SEMICOLON);
		    exceptionList.add(e);
		}
	    }
	} catch (SyntaxException e) {
	    exceptionList.add(e);
	}

	if(DEBUGMAXPARSER==1) System.out.println("importlistup");
	return impList;
    }

    private Block Block() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("blockdown");
	
	Token ft = t;
	Block b = null;
	BlockElem bElem = null;
	List<BlockElem> bElemList = new ArrayList<BlockElem>();
	
	try {
	    match(LCURLY);
	    while(isKind(KW_DEF) || isInPredSt(stmtPredSt)){
		try {
		    if(isKind(KW_DEF)){
			bElem = Declaration();
			match(SEMICOLON);
		    }
		    else {
			bElem = Statement();
			match(SEMICOLON);
		    }
		    if(bElem!=null) bElemList.add(bElem);
		} catch (SyntaxException e) {
		    //if any match inside any blocl elem throws an exception 
		    //(except semicolon of course because that terminates blockelem)
		    //then eat everything until you hit the semicolon that terminates
		    //the import statement. but make sure not to eat end of the block
		    while(!isKind(SEMICOLON) && !isKind(RCURLY))
			match(t.kind);
		    if(isKind(SEMICOLON)) match(SEMICOLON);
		    exceptionList.add(e);
		}
	    }	   
	    match(RCURLY);
	} catch (SyntaxException e) {
	    exceptionList.add(e);
	}
	b = new Block(t, bElemList);
	
	if(DEBUGMAXPARSER==1) System.out.println("blockup");
	return b;
    }

    private Statement Statement() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("statementdown");
	
	Token ft = t;
	Statement st = null;
	Expression exp = null;
	LValue lval = null;
	
	if(isKind(IDENT)){
	    lval = LValue();
	    match(ASSIGN);
	    exp = Expression();
	    st = new AssignmentStatement(t,lval,exp);
	}
	else if(isKind(KW_PRINT)){
	    match(KW_PRINT);
	    exp = Expression();
	    st = new PrintStatement(t,exp);
	}
	else if(isKind(KW_WHILE))
	    st = While();
	else if(isKind(KW_IF))
	    st = If();
	else if(isKind(MOD)){
	    match(MOD);
	    exp = Expression();
	    st = new ExpressionStatement(t,exp);
	}
	else if(isKind(KW_RETURN)){
	    match(KW_RETURN);
	    exp = Expression();
	    st = new ReturnStatement(t,exp);
	}
	
	if(DEBUGMAXPARSER==1) System.out.println("statementup");
	return st;
    }

    class DecTailClass {
	
	String type;
	Type tp;
	Closure cl;

	DecTailClass(Closure c){
	    type = "ClosureDec";
	    cl = c;
	}
	//vardec with type
	DecTailClass(Type typ){
	    type = "VarDecWithType";
	    tp = typ;
	}

	//vardec no type
	DecTailClass(int dummy){
	    type = "VarDecNoType";
	}
    }


    private Declaration Declaration() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("declaration");
	
	Declaration de = null;
	Token id = null;
	Token ft = t;

	match(KW_DEF);
	id = t;
	match(IDENT);
	DecTailClass d = DecTail();

	if(d.type.equals("ClosureDec")) de = new ClosureDec(ft,id,d.cl);
	else if(d.type.equals("VarDecWithType")) de = new VarDec(ft,id,d.tp);
	else de = new VarDec(ft,id);

	if(DEBUGMAXPARSER==1) System.out.println("declaration");
	return de;
    }

    private DecTailClass DecTail() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("dectaildown");
	
	Closure cl = null;
	Type tp = null;
	DecTailClass d = null;

	//ClosureDec
	if(isKind(ASSIGN)){
	    match(ASSIGN);
	    cl = Closure();
	    d = new DecTailClass(cl);
	}//VarDec alternative
	else if(isKind(COLON)){
	    match(COLON);
	    //System.err.println(t);
	    tp = Type();
	    d = new DecTailClass(tp);
	}
	else
	    d = new DecTailClass(0);

	if(DEBUGMAXPARSER==1) System.out.println("dectailup");
	return d;	
    }

    private VarDec VarDec() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("vardecdown");

	VarDec v = null;
	Token ft = t;
	Token id = t;
	Type t = null;
	
	match(IDENT);
	
	if(isKind(COLON)){
	    match(COLON);
	    t = Type();
	    v = new VarDec(ft, id, t);
	}
	else v = new VarDec(ft, id);
	
	if(DEBUGMAXPARSER==1) System.out.println("vardecup");
	return v;//epsilon
	
    }

    class CompositeValueTypeClass {
	String type;
	SimpleType st;
	Type t;

	CompositeValueTypeClass(Type tt) {
	    type = "ListType";
	    t = tt;
	}

	CompositeValueTypeClass(SimpleType stt, Type tt) {
	    type = "KeyValueType";
	    st = stt;
	    t = tt;
	}
    }


    private Type Type() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("typedown");
	
	Token ft = t;
	CompositeValueTypeClass c = null;
	Type tp = null;

	if(isKind(KW_INT)||isKind(KW_BOOLEAN)||isKind(KW_STRING))
	    tp = SimpleType();
	else
	    c = CompositeValueType();

	if(c!=null){
	    switch(c.type) {
	    case "ListType":
		tp = new ListType(ft, c.t);
		break;
	    case "KeyValueType":
		tp = new KeyValueType(ft,c.st,c.t);
	    }
	}

	if(DEBUGMAXPARSER==1) System.out.println("typeup");
	return tp;
	
    }

    private SimpleType SimpleType() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("simpletypedown");
	
	SimpleType s = new SimpleType(t,t);

	if(isKind(KW_INT)){
	    match(KW_INT);
	}
	else if(isKind(KW_BOOLEAN)){
	    match(KW_BOOLEAN);
	}
	else
	    match(KW_STRING);

	if(DEBUGMAXPARSER==1) System.out.println("simpletypeup");
	return s;
    }

    private CompositeValueTypeClass CompositeValueType() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("compositevaluetypedown");
	
	Type ty = null;
	SimpleType st = null;
	CompositeValueTypeClass c = null;
	
	match(AT);
	if(isKind(LSQUARE)){
	    match(LSQUARE);
	    ty = Type();
	    match(RSQUARE);
	}
	else{
	    match(AT);
	    match(LSQUARE);
	    st = SimpleType();
	    match(COLON);
	    ty = Type();
	    match(RSQUARE);
	}
	
	if(st!=null) c = new CompositeValueTypeClass(st,ty);
	else c = new CompositeValueTypeClass(ty);

	if(DEBUGMAXPARSER==1) System.out.println("compositevaluetypeup");
	return c;
	    
    }

    private Closure Closure() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("closuredown");
	
	Token ft = t;
	Closure c = null;
	List<VarDec> l = null;
	List<Statement> s = new ArrayList<Statement>();
	
	match(LCURLY);
	l = FormalArgList();
	match(ARROW);
	while(isInPredSt(stmtPredSt)){
	    try {
		Statement ss = Statement();
		if(ss!=null) s.add(ss);
		match(SEMICOLON);
	    } catch (SyntaxException e) {
		while(!isKind(SEMICOLON) && !isKind(RCURLY))
		    match(t.kind);
		if(isKind(SEMICOLON)) match(SEMICOLON);
		exceptionList.add(e);
	    }
	}
	match(RCURLY);
	
	c = new Closure(ft,l,s);
	if(DEBUGMAXPARSER==1) System.out.println("closureup");
	return c;
    }

    private List<VarDec> FormalArgList() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("formalarglistdown");
	
	List<VarDec> l = new ArrayList<VarDec>();

	if(isKind(IDENT)){
	    l.add(VarDec());
	    while(isKind(COMMA)){
		match(COMMA);
		VarDec v = VarDec();
		if(v!=null) l.add(v);
	    }
	}
	
	if(DEBUGMAXPARSER==1) System.out.println("formalarglistup");
	return l;
    }

    private Statement If() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("ifdown");
	
	Statement st = null;
	Expression exp = null;
	Block ifBlock = null;
	Block elseBlock = null;
	Token ft = t;

	match(KW_IF);
	match(LPAREN);
	exp = Expression();
	match(RPAREN);
	ifBlock = Block();
	elseBlock = Else();
	
	if(elseBlock!=null) st = new IfElseStatement(t, exp, ifBlock, elseBlock);
	else st = new IfStatement(t,exp, ifBlock);

	if(DEBUGMAXPARSER==1) System.out.println("ifup");
	return st;
    }


    private Block Else() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("elsedown");
	
	Block b = null;

	if(isKind(KW_ELSE)){
	    match(KW_ELSE);
	    b = Block();
	}

	if(DEBUGMAXPARSER==1) System.out.println("elseup");
	return b;
    }

    class WhileStarClass {
	String type;
	Token t;
	Expression exp1;
	Expression exp2;

	WhileStarClass(Token tt, Expression exp){
	    type="WhileStar";
	    t = tt;
	    exp1 = exp;		
	}

	WhileStarClass(Token tt, Expression eexp1, Expression eexp2){
	    type="WhileRange";
	    t = tt;
	    exp1 = eexp1;
	    exp2 = eexp2;
	}
    }

    private Statement While() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("whiledown");
	
	Token ft = t;
	Statement st = null;
	Expression exp = null;
	Block b = null;
	WhileStarClass wstar = null;

	match(KW_WHILE);
	if(isKind(TIMES))
	    wstar = WhileStar();
	else{
	    match(LPAREN);
	    exp = Expression();
	    match(RPAREN);
	}
	b = Block();

	if(wstar!=null){
	    // RangeExpression has 3 fields and Expression has only 2
	    if(wstar.type.equals("WhileRange")){
		RangeExpression r = new RangeExpression(wstar.t,wstar.exp1,wstar.exp2);
		st = new WhileRangeStatement(t,r,b);
	    }
	    else
		st = new WhileStarStatement(t,wstar.exp1,b);
	} 
	else st = new WhileStatement(t,exp,b);
	    
	if(DEBUGMAXPARSER==1) System.out.println("whiledown");
	return st;
    }
    
    // *(<Expression>) or *(<Expression>..<Expression>)
    private WhileStarClass WhileStar() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("whilestardown");
	
	Token ft = t;
	WhileStarClass w = null;
	Expression exp1 = null;
	Expression exp2 = null;

	match(TIMES);
	match(LPAREN);
	exp1 = Expression();
	if(isKind(RANGE)){
	    match(RANGE);
	    exp2 = Expression();
	}
	match(RPAREN);
	
	if(exp2!=null) w = new WhileStarClass(t, exp1, exp2);
	else w = new WhileStarClass(t,exp1);
	
	if(DEBUGMAXPARSER==1) System.out.println("whilestarup");
	return w;
    }

    private LValue LValue() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("lvaluedown");

	Token ft = t;
	Expression e = null;
	LValue l = null;
	
	Token id = t;
	match(IDENT);

	if(isKind(LSQUARE)) e = LValueTail();	
	
	if(e!=null) l = new ExpressionLValue(ft,id,e);
	else l = new IdentLValue(t,id);

	if(DEBUGMAXPARSER==1) System.out.println("lvalueup");
	return l;
    }
    private Expression LValueTail() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("lrvaluedown");
	
	Expression e = null;

	match(LSQUARE);
	e = Expression();
	match(RSQUARE);
	
	if(DEBUGMAXPARSER==1) System.out.println("lrvalueup");
	return e;
    }

    private List<Expression> ExpressionList() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("expressionlistdown");
	
	List<Expression> l = new ArrayList<Expression>();
	Expression tempE1 = null;
	Expression tempE2 = null;

	if(isInPredSt(fctrPredSt)){
	    tempE1 = Expression();
	    l.add(tempE1);
	    while(isKind(COMMA)){
		match(COMMA);
		tempE2 = Expression();
		l.add(tempE2);
	    }
	}
	
	if(DEBUGMAXPARSER==1) System.out.println("expressionlistup");
	return l;
    }
    private List<KeyValueExpression> KeyValueList() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("keyvaluelistdown");

	List<KeyValueExpression> kvl = new ArrayList<KeyValueExpression>();

	if(isInPredSt(fctrPredSt)){
	    kvl.add(KeyValueExpression());
	    while(isKind(COMMA)){
		match(COMMA);
		kvl.add(KeyValueExpression());
	    }
	}
	
	if(DEBUGMAXPARSER==1) System.out.println("keyvaluelistup");
	return kvl;
    }
    private KeyValueExpression KeyValueExpression() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("keyvalueexpressiondown");
	
	Token ft = t;
	KeyValueExpression kv = null;
	Expression ky = null;
	Expression val = null;

	ky = Expression();
	match(COLON);
	val = Expression();
	
	kv = new KeyValueExpression(t, ky, val);
	
	if(DEBUGMAXPARSER==1) System.out.println("keyvalueexpressiondown");
	return kv;
    }
    //left to right associativity
    private BinaryExpression bins(List<Token> ops,List<Expression> exps) {
	
	BinaryExpression b = null;
	//System.err.println("**********************************" + exps.toArray().length);
	if(exps.toArray().length<=2)
	    b = new BinaryExpression(ops.get(0),exps.get(0),ops.get(0),exps.get(1));
	else b = new BinaryExpression(ops.get(0),
				      bins(ops.subList(0,ops.toArray().length-1),exps.subList(0,exps.toArray().length-1)),
				      ops.get(ops.toArray().length-1),
				      exps.get(exps.toArray().length-1) );

	return b;
    }

    private Expression Expression() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("expressiondown");
	
	Expression e = null;
	Expression t1 = null;
	List<Expression> tL = new ArrayList<Expression>();
	List<Token> oL = new ArrayList<Token>();

	t1 = Term();
	while(isInPredSt(relOpPredSt)){
	    oL.add(RelOp());
	    tL.add(Term());
	}
	
	if(!tL.isEmpty()){
	    tL.add(0,t1);
	    e = bins(oL,tL);
	}
	else e = t1;
 
	if(DEBUGMAXPARSER==1) System.out.println("expressionup");
	return e;
    }

    private Expression Term() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("termdown");
	
	Expression e = null;
	Expression e1 = null;
	List<Expression> eL = new ArrayList<Expression>();
	List<Token> oL = new ArrayList<Token>();

	e1 = Elem();
	while(isKind(PLUS)||isKind(MINUS)){
	    oL.add(WeakOp());
	    eL.add(Elem());
	}
	
	if(!eL.isEmpty()){
	    eL.add(0,e1);
	    e = bins(oL,eL);
	}
	else e = e1;
 
	if(DEBUGMAXPARSER==1) System.out.println("termup");
	return e;
    }

    private Expression Elem() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("elemdown");
	
	Expression e = null;
	Expression th1 = null;
	List<Expression> thL = new ArrayList<Expression>();
	List<Token> oL = new ArrayList<Token>();
	
	th1 = Thing();
	while(isKind(TIMES)||isKind(DIV)){
	    oL.add(StrongOp());
	    thL.add(Thing());
	}
		
	if(!thL.isEmpty()){
	    thL.add(0,th1);
	    e = bins(oL,thL);
	}
	else e = th1;
 
	if(DEBUGMAXPARSER==1) System.out.println("elemup");
	return e;
    }

    private Expression Thing() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("thingdown");

	Expression e = null;
	Expression f1 = null;
	List<Expression> fL = new ArrayList<Expression>();
	List<Token> oL = new ArrayList<Token>();

	f1 = Factor();
	while(isKind(LSHIFT)||isKind(RSHIFT)){
	    oL.add(VeryStrongOp());
	    fL.add(Factor());
	}

	if(!fL.isEmpty()){
	    fL.add(0,f1);
	    e = bins(oL,fL);
	}
	else e = f1;
	
	if(DEBUGMAXPARSER==1) System.out.println("thingup");
	return e;
    }

    //IdentExpression,ListOrMapElemExpression,ClosureEvalExpression
    class IdentInFactorClass {
	String type;
	Token id;
	Expression e;
	List<Expression> el;

	IdentInFactorClass(Token i){
	    type = "IdentExpression";
	    id = i;
	}

	IdentInFactorClass(Token i, Expression ex){
	    type = "ListOrMapElemExpression";
	    id = i;
	    e = ex;
	}

	IdentInFactorClass(Token i, List<Expression> l){
	    type = "ClosureEvalExpression";
	    id = i;
	    el = l;
	}
       
        void tostring() {
	    System.out.println(type + "\n");
	    System.out.println(id + "\n");
	    switch(type){
	    case "IdentExpression":
		break;
	    case "ListOrMapElemExpression":
		System.out.println(e + "\n");
		break;
	    case "ClosureEvalExpression":
		System.out.println(el.get(0) + "\n");
	    }
	}
    }

    private Expression Factor() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("factordown");

	Token ft = t;
	Expression f = null;
	Expression e = null;

	if(isKind(IDENT)) {
	    IdentInFactorClass id = IdentInFactor();
	    switch(id.type) {
	    case "IdentExpression":
		f = new IdentExpression(ft,id.id);
		break;
	    case "ListOrMapElemExpression":
		f = new ListOrMapElemExpression(ft,id.id,id.e);
		break;
	    case "ClosureEvalExpression":
		f = new ClosureEvalExpression(ft,id.id,id.el);
	    }
	}
	else if(isKind(INT_LIT)) {
	    f = new IntLitExpression(ft,t.getIntVal());
	    match(INT_LIT);
	}
	else if(isKind(BL_TRUE)){
	    f = new BooleanLitExpression(ft, t.getBooleanVal());
	    match(BL_TRUE);
	}
	else if(isKind(BL_FALSE)){
	    f = new BooleanLitExpression(ft, t.getBooleanVal());
	    match(BL_FALSE);
	}
	else if(isKind(STRING_LIT)){
	    f = new StringLitExpression(ft, t.getText());
	    match(STRING_LIT);
	}
	else if(isKind(NOT)){
	    Token op = t;
	    match(NOT);	 
	    e = Factor();
	    f = new UnaryExpression(ft, op, e);
	}
	else if(isKind(MINUS)){
	    Token op = t;
	    match(MINUS);
	    e = Factor();
	    f = new UnaryExpression(ft, op, e);
	}
	else if(isKind(KW_SIZE)){
	    match(KW_SIZE);
	    match(LPAREN);
	    e = Expression();
	    f = new SizeExpression(ft, e);
	    match(RPAREN);
	}
	else if(isKind(KW_KEY)){
	    match(KW_KEY);
	    match(LPAREN);
	    e = Expression();
	    f = new KeyExpression(ft, e);
	    match(RPAREN);
	}
	else if(isKind(KW_VALUE)){
	    match(KW_VALUE);
	    match(LPAREN);
	    e = Expression();
	    f = new ValueExpression(ft, e);
	    match(RPAREN);
	}
	else if(isKind(LCURLY)){
	    Closure cl = Closure();
	    f = new ClosureExpression(ft, cl);
	}
	else if(isKind(AT)) { //List is my list, not hers
	    ListClass l = List();
	    switch(l.type){
	    case "ListExpression":
		f = new ListExpression(ft, l.l);
		break;
	    case "MapListExpression":
		f = new MapListExpression(ft, l.ml);
	    }
	}
	else if(isKind(LPAREN)){
	    match(LPAREN);
	    f = Expression();
	    match(RPAREN);
	}
	else{
	    //not really the kind expected but since the exception class is stupid
	    //need to pass some sort of kind
	    throw new SyntaxException(t, KW_VALUE);		
	}

	if(DEBUGMAXPARSER==1) System.out.println("factorup");
	return f;
    }

    class ListClass {
	String type;
	List<Expression> l;
	List<KeyValueExpression> ml;
	//dummy is just for diff signatures
	ListClass(List<Expression> ll, int dummy){
	    type = "ListExpression";
	    l = ll;
	}

	ListClass(List<KeyValueExpression> mll){
	    type = "MapListExpression";
	    ml = mll;
	}
    }

    private ListClass List() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("listdown");

	List<KeyValueExpression> kvl = null;
	List<Expression> el = null;
	ListClass l = null;

	match(AT);
	// <MapList> i.e. @@[ <KeyValueList> ]
	if(isKind(AT)){
	    match(AT);
	    match(LSQUARE);
	    kvl = KeyValueList();
	    match(RSQUARE);
	}
	else{// <List> i.e. @[ <ExpressionList> ]
	    match(LSQUARE);
	    el = ExpressionList();
	    match(RSQUARE);
	}

	if(kvl!=null) l = new ListClass(kvl);
	else l = new ListClass(el,1);

	if(DEBUGMAXPARSER==1) System.out.println("listup");
	return l;
    }

    private IdentInFactorClass IdentInFactor() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("identinfactordown");

	IdentInFactorClass id = null;
	Expression e = null;
	List<Expression> l = null;

	Token idT = t;
	match(IDENT);

	if(isKind(LSQUARE)){
	    match(LSQUARE);
	    e = Expression();
	    match(RSQUARE);
	}
	else if(isKind(LPAREN)){
	    match(LPAREN);
	    l = ExpressionList();
	    match(RPAREN);
	}
	
	//System.err.println("%%%%%%%%%%%%%%" + idT);

	if(e!=null || l!=null){
	    //System.err.println("test1");
	    if(e!=null) id = new IdentInFactorClass(idT, e);
	    else id = new IdentInFactorClass(idT, l);
	}
	else{
	    //System.err.println("test2");
	    id = new IdentInFactorClass(idT);
	}
	//System.err.println("$$$$$$$$$$$$$$$$$$$ ");
	//id.tostring();
	if(DEBUGMAXPARSER==1) System.out.println("identinfactorup");
	return id;
    }

    private Token RelOp() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("relopdown");
	
	Token op = t;
	
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
	return op;
    }

    private Token WeakOp() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("weakopdown");
	
	Token op = t;
	
	if(isKind(PLUS))
	    match(PLUS);
	else if(isKind(MINUS))
	    match(MINUS);
	else{
	    throw new SyntaxException(t,"weakop exception");
	}
		
	if(DEBUGMAXPARSER==1) System.out.println("weakdown");
	return op;
    }

    private Token StrongOp() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("strongopdown");
	
	Token op = t;

	if(isKind(TIMES))
	    match(TIMES);
	else if(isKind(DIV))
	    match(DIV);
	else{
	    throw new SyntaxException(t,"strongop exception");	
	}

	if(DEBUGMAXPARSER==1) System.out.println("strongopup");
	return op;
    }

    private Token VeryStrongOp() throws SyntaxException {
	if(DEBUGMAXPARSER==1) System.out.println("verystrongopdown");
	
	Token op = t;
	
	if(isKind(LSHIFT))
	    match(LSHIFT);
	else if(isKind(RSHIFT))
	    match(RSHIFT);
	else{
	    throw new SyntaxException(t,"verystrongop exception");
	}

	if(DEBUGMAXPARSER==1) System.out.println("verystrongopup");
	return op;
    }


}
