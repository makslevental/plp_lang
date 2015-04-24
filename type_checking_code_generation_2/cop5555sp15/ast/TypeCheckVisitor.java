package cop5555sp15.ast;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import cop5555sp15.TypeConstants;
import cop5555sp15.symbolTable.SymbolTable;

public class TypeCheckVisitor implements ASTVisitor, TypeConstants {

    @SuppressWarnings("serial")
    public static class TypeCheckException extends Exception {
	ASTNode node;

	public TypeCheckException(String message, ASTNode node) {
	    super(node.firstToken.lineNumber + ":" + message);
	    this.node = node;
	}
    }

    SymbolTable symbolTable;

    public TypeCheckVisitor(SymbolTable symbolTable) {
	this.symbolTable = symbolTable;
    }

    boolean check(boolean condition, String message, ASTNode node)
	throws TypeCheckException {
	if (condition)
	    return true;
	throw new TypeCheckException(message, node);
    }

    /**
     * Ensure that types on left and right hand side are compatible.
     */
    @Override
    public Object visitAssignmentStatement ( AssignmentStatement assignmentStatement, Object arg) throws Exception {
	
	//String lvaluetype = String.valueOf(assignmentStatement.lvalue.visit(this,arg));
	assignmentStatement.lvalue.visit(this,arg);
	//String rvaluetype = String.valueOf(assignmentStatement.expression.visit(this,arg));
	assignmentStatement.expression.visit(this,arg);
	//System.out.println("type check assign " + assignmentStatement.lvalue.getType() + " " + assignmentStatement.expression.getType());
	String lvaluetyp = assignmentStatement.lvalue.getType();
	String rvaluetyp = assignmentStatement.expression.getType();	
	String classType = assignmentStatement.lvalue.classtype; 
	// if(classType.equals("ExpressionLValue")){
	    
	//     if(!assignmentStatement.lvalue.visit(this,arg).equals(rvaluetyp)){
	// 	 throw new TypeCheckException("type mismatch in AssignmentStatement",assignmentStatement);
	//     }
	//     return null;
	// }
	if(!lvaluetyp.equals(rvaluetyp)){
	    if(lvaluetyp.startsWith("Ljava/util/List") && rvaluetyp.equals("Ljava/util/ArrayList;")) return null;
	    else throw new TypeCheckException("type mismatch in AssignmentStatement",assignmentStatement);
	}
	return null;
    }

    /**
     * Ensure that both types are the same, save and return the result type
     */
    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression,
					Object arg) throws Exception {

	binaryExpression.expression0.visit(this, arg);
	binaryExpression.expression1.visit(this, arg);
	
	if(!binaryExpression.expression0.getType().equals(binaryExpression.expression1.getType()))
	    throw new TypeCheckException("type mismatch in BinaryExpression",binaryExpression);
	
	Kind op = binaryExpression.op.kind;

	if(binaryExpression.expression0.getType().equals(intType) && (op == Kind.TIMES || op == Kind.DIV || op == Kind.PLUS || op == Kind.MINUS)){
	    binaryExpression.setType(intType);
	    return intType;
	}
	else if(binaryExpression.expression0.getType().equals(intType) && (op == Kind.LT || op == Kind.LE || op == Kind.GT || op == Kind.GE || op == Kind.EQUAL || op == Kind.NOTEQUAL)){
	    binaryExpression.setType(booleanType);
	    return booleanType;
	}
	else if(binaryExpression.expression0.getType().equals(booleanType) && (op == Kind.EQUAL || op == Kind.NOTEQUAL || op == Kind.AND || op == Kind.BAR)) {
	    binaryExpression.setType(booleanType);
	    return booleanType;
	} 
	else if(binaryExpression.expression0.getType().equals(stringType) && op == Kind.PLUS) {
	    
	    binaryExpression.setType(stringType);
	    return stringType;
	}
	else if(binaryExpression.expression0.getType().equals(stringType) && (op == Kind.EQUAL || op== Kind.NOTEQUAL)) {
	    binaryExpression.setType(booleanType);
	    return booleanType;
	}
	else throw new TypeCheckException("unsupported operation in BinaryExpression",binaryExpression);
	
    }

    /**
     * Blocks define scopes. Check that the scope nesting level is the same at
     * the end as at the beginning of block
     */
    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
	int numScopes = symbolTable.enterScope();
	// visit children
	for (BlockElem elem : block.elems) {
	    elem.visit(this, arg);
	}
	int numScopesExit = symbolTable.leaveScope();
	check(numScopesExit > 0 && numScopesExit == numScopes,
	      "unbalanced scopes", block);
	return null;
    }

    /**
     * Sets the expressionType to booleanType and returns it
     * 
     * @param booleanLitExpression
     * @param arg
     * @return
     * @throws Exception
     */
    @Override
    public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
	booleanLitExpression.setType(booleanType);
	//System.out.println("boolean lit expression:" + booleanLitExpression.value);
	return booleanType;
    }

    /**
     * A closure defines a new scope Visit all the declarations in the
     * formalArgList, and all the statements in the statementList construct and
     * set the JVMType, the argType array, and the result type
     * 
     * @param closure
     * @param arg
     * @return
     * @throws Exception
     */
    @Override
    public Object visitClosure(Closure closure, Object arg) throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Make sure that the name has not already been declared and insert in
     * symbol table. Visit the closure
     */
    @Override
    public Object visitClosureDec(ClosureDec closureDec, Object arg) {
	throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Check that the given name is declared as a closure Check the argument
     * types The type is the return type of the closure
     */
    @Override
    public Object visitClosureEvalExpression(
					     ClosureEvalExpression closureExpression, Object arg)
	throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Object visitClosureExpression(ClosureExpression closureExpression,
					 Object arg) throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Object visitExpressionLValue(ExpressionLValue expressionLValue,
					Object arg) throws Exception {
	if( !((ListType)symbolTable.lookup( expressionLValue.identToken.getText() ).getType()).prefix().equals( "Ljava/util/List"))
	    throw new TypeCheckException("type mismatch in expressionLValue", expressionLValue);
	
	if(!expressionLValue.expression.visit(this,arg).equals("I"))
	    throw new TypeCheckException("type mismatch in expressionLValue ", expressionLValue);
	expressionLValue.setType(((ListType)symbolTable.lookup( expressionLValue.identToken.getText() ).getType()).type.getJVMType());
	return expressionLValue.arraytype;
    }

    @Override
    public Object visitExpressionStatement(
					   ExpressionStatement expressionStatement, Object arg)
	throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Check that name has been declared in scope Get its type from the
     * declaration.
     * 
     */
    @Override
    public Object visitIdentExpression(IdentExpression identExpression,
				       Object arg) throws Exception {
	Declaration identDec = symbolTable.lookup(identExpression.identToken.getText());
	if(identDec == null)
	    throw new TypeCheckException("undeclared variable", identExpression);
	identExpression.setType(identDec.getType().getJVMType());
	//System.out.println("typecheck ident type "+identDec.getType().getJVMType());
	return identDec.getType().getJVMType();
    }

    @Override
    public Object visitIdentLValue(IdentLValue identLValue, Object arg) throws Exception {
	Declaration identDec = symbolTable.lookup(identLValue.identToken.getText());
	if(identDec == null)
	    throw new TypeCheckException("undeclared variable", identLValue);
	identLValue.setType(identDec.getType().getJVMType());
	return identDec.getType().getJVMType();
    }

    @Override
    public Object visitIfElseStatement(IfElseStatement ifElseStatement,
				       Object arg) throws Exception {
	ifElseStatement.expression.visit(this, arg);
	ifElseStatement.ifBlock.visit(this,arg);
	ifElseStatement.elseBlock.visit(this,arg);
	if(!ifElseStatement.expression.getType().equals("Z"))
	    throw new TypeCheckException("type mismatch in ifStatement", ifElseStatement);
	return "Z";
    }

    /**
     * expression type is boolean
     */
    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg)
	throws Exception {
	ifStatement.expression.visit(this, arg);
	ifStatement.block.visit(this,arg);
	if(!ifStatement.expression.getType().equals("Z"))
	    throw new TypeCheckException("type mismatch in ifStatement", ifStatement);
	return "Z";
    }

    /**
     * expression type is int
     */
    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression,
					Object arg) throws Exception {
	intLitExpression.setType(intType);
	//System.out.println("int lit expression:" + intLitExpression.value);

	return intType;
    }

    @Override
    public Object visitKeyExpression(KeyExpression keyExpression, Object arg)
	throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Object visitKeyValueExpression(
					  KeyValueExpression keyValueExpression, Object arg) throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Object visitKeyValueType(KeyValueType keyValueType, Object arg)
	throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    // visit the expressions (children) and ensure they are the same type
    // the return type is "Ljava/util/ArrayList<"+type0+">;" where type0 is the
    // type of elements in the list
    // this should handle lists of lists, and empty list. An empty list is
    // indicated by "Ljava/util/ArrayList;".
    @Override
    public Object visitListExpression(ListExpression listExpression, Object arg)
	throws Exception {
	
	if(listExpression.expressionList!=null){
	    if(listExpression.expressionList.isEmpty()){
		listExpression.setType("Ljava/util/ArrayList;");
		listExpression.setBoxType("empty");
		return "Ljava/util/ArrayList;";
	    }
	    else{
		String prevlistTyp = String.valueOf(listExpression.expressionList.get(0).visit(this,arg));
		String currlistTyp = "";
		for (Expression listExp : listExpression.expressionList){
		    currlistTyp = String.valueOf(listExp.visit(this,arg));
		    if(!prevlistTyp.equals(currlistTyp))
			throw new TypeCheckException("type mismatch in ListExpression", listExpression);
		    else
			prevlistTyp = currlistTyp; 
		}
		//System.out.println("type check visitlistexp "+listExpression.setBoxType(currlistTyp));
		listExpression.setType("Ljava/util/ArrayList;");
		return "Ljava/util/ArrayList<"+listExpression.setBoxType(currlistTyp)+">;";
	    }
	}
	else
	    return null;
    }

    /** gets the type from the enclosed expression */
    @Override
    public Object visitListOrMapElemExpression(
					       ListOrMapElemExpression listOrMapElemExpression, Object arg)
	throws Exception {
	
	if( !((ListType)symbolTable.lookup( listOrMapElemExpression.identToken.getText() ).getType()).prefix().equals( "Ljava/util/List"))
	    throw new TypeCheckException("type mismatch in ListOrMapElemExpression", listOrMapElemExpression);
	
	if(!listOrMapElemExpression.expression.visit(this,arg).equals("I"))
	    throw new TypeCheckException("type mismatch in ListOrMapElemExpression", listOrMapElemExpression);
	listOrMapElemExpression.expressionType = ((ListType)symbolTable.lookup( listOrMapElemExpression.identToken.getText() ).getType()).type.getJVMType();
	return listOrMapElemExpression.expressionType;
	// if(listExpression.expressionList!=null){
	//     if(listExpression.expressionList.isEmpty()){
	// 	listExpression.setType("Ljava/util/ArrayList;");
	// 	listExpression.setBoxType("empty");
	// 	return "Ljava/util/ArrayList;";
	//     }
	//     else{
	// 	String prevlistTyp = String.valueOf(listExpression.expressionList.get(0).visit(this,arg));
	// 	String currlistTyp = "";
	// 	for (Expression listExp : listExpression.expressionList){
	// 	    currlistTyp = String.valueOf(listExp.visit(this,arg));
	// 	    if(!prevlistTyp.equals(currlistTyp))
	// 		throw new TypeCheckException("type mismatch in ListExpression", listExpression);
	// 	    else
	// 		prevlistTyp = currlistTyp; 
	// 	}
	// 	System.out.println("type check visitlistexp "+listExpression.setBoxType(currlistTyp));
	// 	listExpression.setType("Ljava/util/ArrayList;");
	// 	return "Ljava/util/ArrayList<"+listExpression.setBoxType(currlistTyp)+">;";
	//     }
	// }
	// else
	//     return null;
    }

    @Override
    public Object visitListType(ListType listType, Object arg) throws Exception {
	listType.type.visit(this,arg);
	return null;
    }

    @Override
    public Object visitMapListExpression(MapListExpression mapListExpression,
					 Object arg) throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Object visitPrintStatement(PrintStatement printStatement, Object arg)
	throws Exception {
	printStatement.expression.visit(this, null);
	return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
	if (arg == null) {
	    program.JVMName = program.name;
	} else {
	    program.JVMName = arg + "/" + program.name;
	}
	// ignore the import statement
	if (!symbolTable.insert(program.name, null)) {
	    throw new TypeCheckException("name already in symbol table",
					 program);
	}
	program.block.visit(this, true);
	return null;
    }

    @Override
    public Object visitQualifiedName(QualifiedName qualifiedName, Object arg) {
	throw new UnsupportedOperationException();
    }

    /**
     * Checks that both expressions have type int.
     * 
     * Note that in spite of the name, this is not in the Expression type
     * hierarchy.
     */
    @Override
    public Object visitRangeExpression(RangeExpression rangeExpression,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    // nothing to do here
    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Object visitSimpleType(SimpleType simpleType, Object arg)
	throws Exception {
	return simpleType.getJVMType();
    }

    @Override
    public Object visitSizeExpression(SizeExpression sizeExpression, Object arg)
	throws Exception {
	sizeExpression.expression.visit(this,arg);
	if(!sizeExpression.expression.getType().startsWith("Ljava/util/List"))
	    throw new TypeCheckException("type mismatch in sizeExpression",sizeExpression);
	sizeExpression.setType("I");
	return "I";
    }

    @Override
    public Object visitStringLitExpression(
					   StringLitExpression stringLitExpression, Object arg)
	throws Exception {
	stringLitExpression.setType(stringType);
	return stringType;
    }

    /**
     * if ! and boolean, then boolean else if - and int, then int else error
     */
    @Override
    public Object visitUnaryExpression(UnaryExpression unaryExpression,
				       Object arg) throws Exception {

	unaryExpression.expression.visit(this, arg);
	Kind op = unaryExpression.op.kind;
	if(op == Kind.MINUS){
	    if(!unaryExpression.expression.getType().equals("I"))
		throw new TypeCheckException("type mismatch in unaryExpression",unaryExpression);
	    else{
		//System.out.println("unary before : " + (int)unaryExpression.expression.getValue());
		unaryExpression.value = new Integer(-(int)unaryExpression.expression.getValue());
		//System.out.println("unary after : " + (int)unaryExpression.expression.getValue());
		unaryExpression.setType("I");
		return "I";
	    }
	}
	else { //if ! type unary expression
	    if(!unaryExpression.expression.getType().equals("Z"))
		throw new TypeCheckException("type mismatch in unaryExpression",unaryExpression);
	    else{
		//System.out.println(unaryExpression.expression.getValue());
		unaryExpression.value = new Boolean(!(boolean)unaryExpression.expression.getValue());
		unaryExpression.setType("Z");
		return "Z";
	    }
	}
    }

    @Override
    public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"undeclared types not supported");
    }

    @Override
    public Object visitValueExpression(ValueExpression valueExpression,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * check that this variable has not already been declared in the same scope.
     */
    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
	
	varDec.type.visit(this,arg);
	if(symbolTable.lookup(varDec.identToken.getText())!= null)
	    throw new TypeCheckException("variable already declared", varDec);
	symbolTable.insert(varDec.identToken.getText(), varDec);
	return null;
    }

    /**
     * All checking will be done in the children since grammar ensures that the
     * rangeExpression is a rangeExpression.
     */
    @Override
    public Object visitWhileRangeStatement(
					   WhileRangeStatement whileRangeStatement, Object arg)
	throws Exception {
	throw new UnsupportedOperationException("not yet implemented");

    }

    @Override
    public Object visitWhileStarStatement(
					  WhileStarStatement whileStarStatement, Object arg) throws Exception {
	throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg)
	throws Exception {
	whileStatement.expression.visit(this, arg);
	whileStatement.block.visit(this,arg);
	if(!whileStatement.expression.getType().equals("Z"))
	    throw new TypeCheckException("type mismatch in whileStatement", whileStatement);
	return "Z";
    }

}
