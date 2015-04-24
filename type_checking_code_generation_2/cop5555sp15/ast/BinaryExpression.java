package cop5555sp15.ast;

import cop5555sp15.TokenStream.Token;

public class BinaryExpression extends Expression {
	Expression expression0;
	Token op;
	Expression expression1;
	
	String binaryExpressionType;

	public String getType() {
		return binaryExpressionType;
	}

	public void setType(String type) {
		this.binaryExpressionType = type;
	}

	public BinaryExpression(Token firstToken, Expression expression0,
			Token op, Expression expression1) {
		super(firstToken);
		this.expression0 = expression0;
		this.op = op;
		this.expression1 = expression1;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitBinaryExpression(this,arg);
	}

}
