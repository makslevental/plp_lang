package cop5555sp15.ast;

import cop5555sp15.TokenStream.Token;

public class IntLitExpression extends Expression {
	int value;
	String type;
	

	public IntLitExpression(Token firstToken, int value) {
		super(firstToken);
		this.value = value;
		this.type = "int";
		this.expressionType = "int";
	}
	@Override
	public Integer getValue(){
		return new Integer(this.value);
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIntLitExpression(this,arg);
	}

}
