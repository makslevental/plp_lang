package cop5555sp15.ast;

import cop5555sp15.TokenStream.Token;


public abstract class Declaration extends BlockElem {
	
	public boolean globalScope;
	public Type type;
	Declaration(Token firstToken) {
		super(firstToken);
	}
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
