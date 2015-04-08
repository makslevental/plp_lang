package cop5555sp15.ast;

import org.objectweb.asm.*;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TypeConstants;
//import static cop5555sp15.TokenStream.Kind.PLUS;
//import static cop5555sp15.TokenStream.Kind.MINUS;
//import static cop5555sp15.TokenStream.Kind.TIMES;
//import static cop5555sp15.TokenStream.Kind.DIV;
//import static cop5555sp15.TokenStream.Kind.PLUS;
//import static cop5555sp15.TokenStream.Kind.MINUS;
//import static cop5555sp15.TokenStream.Kind.TIMES;
//import static cop5555sp15.TokenStream.Kind.DIV;

public class CodeGenVisitor implements ASTVisitor, Opcodes, TypeConstants {

    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    // Because we used the COMPUTE_FRAMES flag, we do not need to
    // insert the mv.visitFrame calls that you will see in some of the
    // asmifier examples. ASM will insert those for us.
    // FYI, the purpose of those instructions is to provide information
    // about what is on the stack just before each branch target in order
    // to speed up class verification.
    FieldVisitor fv;
    String className;
    String classDescriptor;

    // This class holds all attributes that need to be passed downwards as the
    // AST is traversed. Initially, it only holds the current MethodVisitor.
    // Later, we may add more attributes.
    static class InheritedAttributes {
	public InheritedAttributes(MethodVisitor mv) {
	    super();
	    this.mv = mv;
	}

	MethodVisitor mv;
    }

    @Override
    public Object visitAssignmentStatement(
					   AssignmentStatement assignmentStatement, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression,
					Object arg) throws Exception {
	MethodVisitor mv = ((InheritedAttributes) arg).mv;
	
	binaryExpression.expression0.visit(this, arg);
	//mv.visitVarInsn(ASTORE, 1);
	binaryExpression.expression1.visit(this, arg);
	//mv.visitVarInsn(ASTORE, 2);
	Kind op = binaryExpression.op.kind;
	//top of the stack is the second expression, next is first expression

	switch(op){
	case TIMES:
	    mv.visitInsn(IMUL);
	    break;
	case MINUS:
	    mv.visitInsn(ISUB);
	    break;
	case DIV:
	    mv.visitInsn(IDIV);
	    break;
	case PLUS:
	    if(binaryExpression.getType().equals(intType)) mv.visitInsn(IADD);
	    else if(binaryExpression.getType().equals(stringType)){
		//top of the stack is the second expression, next is first expression right now. will
		//concat in reverse order if no swap
		mv.visitInsn(SWAP);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		//init consumes ref so hence dup
		mv.visitInsn(DUP);
		//mv.visitVarInsn(ALOAD, 1);
		//change stored var to string (even though it's already a string).
		//note in parens is the type of the argument and outside is the return type
		//mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
		/*init sb with first string
		  method invocation looks below the top of the stack. so right now
		  the top of the stack has a string and just below is sb ref. 
		  invokespecial will find the ref, call the 1 argument constructor and take the 
		  top of the stack is the argument
		*/
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
		//mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
		mv.visitInsn(SWAP);
		//push sb down and put second word
		//mv.visitVarInsn(ALOAD, 2);
		/*again method invocation will look below the top of the stack, find
		  find the sb ref and take the second word as the arg to append.
		*/
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		mv.visitInsn(SWAP);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
	    }
	    else throw new UnsupportedOperationException("unsupported operation");
	    break;
	case EQUAL:
	    if(binaryExpression.expression0.getType().equals(stringType)) mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
	    else branch_insn(mv, IF_ICMPEQ);
	    break;
	case NOTEQUAL:
	    if(binaryExpression.expression0.getType().equals(stringType)){
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		//if neq then equals returns zero and 0 xor 1 = 1;
		mv.visitInsn(ICONST_1);
		mv.visitInsn(IXOR);
	    }
	    else branch_insn(mv, IF_ICMPNE);
	    break;
	case LT:// expression0 < expression1
	    if(binaryExpression.expression0.getType().equals(intType)) branch_insn(mv, IF_ICMPLT);
	    else throw new UnsupportedOperationException("unsupported operation");
	    break;
	case LE:// expression0 < expression1
	    if(binaryExpression.expression0.getType().equals(intType)) branch_insn(mv, IF_ICMPLE);
	    else throw new UnsupportedOperationException("unsupported operation");
	    break;
	case GT:// expression0 < expression1
	    if(binaryExpression.expression0.getType().equals(intType)) branch_insn(mv, IF_ICMPGT);
	    else throw new UnsupportedOperationException("unsupported operation");
	    break;
	case GE:// expression0 < expression1
	    if(binaryExpression.expression0.getType().equals(intType)) branch_insn(mv, IF_ICMPGE);
	    else throw new UnsupportedOperationException("unsupported operation");
	    break;
	case BAR:
	    if(binaryExpression.getType().equals(booleanType)) mv.visitInsn(IOR);
	    else throw new UnsupportedOperationException("unsupported operation");
	    break;
	case AND:
	    if(binaryExpression.getType().equals(booleanType)) mv.visitInsn(IAND);
	    else throw new UnsupportedOperationException("unsupported operation");
	    break;
	default:    
	    throw new UnsupportedOperationException("code generation not yet implemented");
	} 
	return null;
    }

    private void branch_insn(MethodVisitor mv, int cmp){
	Label exit = new Label();
	Label testPass = new Label();

	mv.visitJumpInsn(cmp,testPass);

	mv.visitInsn(ICONST_0);
	mv.visitJumpInsn(GOTO,exit);
	
	mv.visitLabel(testPass);
	mv.visitInsn(ICONST_1);

	mv.visitLabel(exit);
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
	for (BlockElem elem : block.elems) {
	    elem.visit(this, arg);
	}
	return null;
    }

    @Override
    public Object visitBooleanLitExpression(
					    BooleanLitExpression booleanLitExpression, Object arg)
	throws Exception {
	MethodVisitor mv = ((InheritedAttributes) arg).mv;
	mv.visitLdcInsn(booleanLitExpression.value);
	return null;
    }

    @Override
    public Object visitClosure(Closure closure, Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitClosureDec(ClosureDec closureDeclaration, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitClosureEvalExpression(
					     ClosureEvalExpression closureExpression, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitClosureExpression(ClosureExpression closureExpression,
					 Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitExpressionLValue(ExpressionLValue expressionLValue,
					Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitExpressionStatement(
					   ExpressionStatement expressionStatement, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitIdentExpression(IdentExpression identExpression,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitIdentLValue(IdentLValue identLValue, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitIfElseStatement(IfElseStatement ifElseStatement,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression,
					Object arg) throws Exception {
	MethodVisitor mv = ((InheritedAttributes) arg).mv; 
	mv.visitLdcInsn(intLitExpression.value);
	return null;
    }

    @Override
    public Object visitKeyExpression(KeyExpression keyExpression, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitKeyValueExpression(
					  KeyValueExpression keyValueExpression, Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitKeyValueType(KeyValueType keyValueType, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitListExpression(ListExpression listExpression, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitListOrMapElemExpression(
					       ListOrMapElemExpression listOrMapElemExpression, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitListType(ListType listType, Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitMapListExpression(MapListExpression mapListExpression,
					 Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitPrintStatement(PrintStatement printStatement, Object arg)
	throws Exception {
	MethodVisitor mv = ((InheritedAttributes) arg).mv;
	Label l0 = new Label();
	mv.visitLabel(l0);
	mv.visitLineNumber(printStatement.firstToken.getLineNumber(), l0);
	mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
			  "Ljava/io/PrintStream;");
	printStatement.expression.visit(this, arg); // adds code to leave value
	// of expression on top of
	// stack.
	// Unless there is a good
	// reason to do otherwise,
	// pass arg down the tree
	String etype = printStatement.expression.getType();
	if (etype.equals("I") || etype.equals("Z")
	    || etype.equals("Ljava/lang/String;")) {
	    String desc = "(" + etype + ")V";
	    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
			       desc, false);
	} else
	    throw new UnsupportedOperationException(
						    "printing list or map not yet implemented");
	return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
	className = program.JVMName;
	classDescriptor = 'L' + className + ';';
	cw.visit(52, // version
		 ACC_PUBLIC + ACC_SUPER, // access codes
		 className, // fully qualified classname
		 null, // signature
		 "java/lang/Object", // superclass
		 new String[] { "cop5555sp15/Codelet" } // implemented interfaces
		 );
	cw.visitSource(null, null); // maybe replace first argument with source
	// file name

	// create init method
	{
	    MethodVisitor mv;
	    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
	    mv.visitCode();
	    Label l0 = new Label();
	    mv.visitLabel(l0);
	    mv.visitLineNumber(3, l0);
	    mv.visitVarInsn(ALOAD, 0);
	    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
			       "()V", false);
	    mv.visitInsn(RETURN);
	    Label l1 = new Label();
	    mv.visitLabel(l1);
	    mv.visitLocalVariable("this", classDescriptor, null, l0, l1, 0);
	    mv.visitMaxs(1, 1);
	    mv.visitEnd();
	}

	// generate the execute method
	MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "execute", // name of top
					  // level
					  // method
					  "()V", // descriptor: this method is parameterless with no
					  // return value
					  null, // signature.  This is null for us, it has to do with generic types
					  null // array of strings containing exceptions
					  );
	mv.visitCode();
	Label lbeg = new Label();
	mv.visitLabel(lbeg);
	mv.visitLineNumber(program.firstToken.lineNumber, lbeg);
	program.block.visit(this, new InheritedAttributes(mv));
	mv.visitInsn(RETURN);
	Label lend = new Label();
	mv.visitLabel(lend);
	mv.visitLocalVariable("this", classDescriptor, null, lbeg, lend, 0);
	mv.visitMaxs(0, 0);  //this is required just before the end of a method. 
	//It causes asm to calculate information about the
	//stack usage of this method.
	mv.visitEnd();

		
	cw.visitEnd();
	return cw.toByteArray();
    }

    @Override
    public Object visitQualifiedName(QualifiedName qualifiedName, Object arg) {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitRangeExpression(RangeExpression rangeExpression,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitSimpleType(SimpleType simpleType, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitSizeExpression(SizeExpression sizeExpression, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitStringLitExpression(
					   StringLitExpression stringLitExpression, Object arg)
	throws Exception {
	MethodVisitor mv = ((InheritedAttributes) arg).mv; 
	mv.visitLdcInsn(stringLitExpression.value);
	return null;
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression unaryExpression,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitValueExpression(ValueExpression valueExpression,
				       Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitWhileRangeStatement(
					   WhileRangeStatement whileRangeStatement, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitWhileStarStatement(WhileStarStatement whileStarStatment,
					  Object arg) throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

    @Override
    public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
	throws Exception {
	throw new UnsupportedOperationException(
						"code generation not yet implemented");
    }

}
