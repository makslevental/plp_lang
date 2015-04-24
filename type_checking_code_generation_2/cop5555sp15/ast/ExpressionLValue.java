package cop5555sp15.ast;

import cop5555sp15.TokenStream.Token;

public class ExpressionLValue extends LValue {
	Expression expression;
	String arraytype;
	public ExpressionLValue(Token firstToken, Token identToken,
			Expression expression) {
		super(firstToken);
		this.identToken = identToken;
		this.expression = expression;
		this.classtype = "ExpressionLValue";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionLValue(this,arg);
	}

}
