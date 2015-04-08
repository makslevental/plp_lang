package cop5555sp15.ast;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
public class DummyDec extends Declaration {

	String ident;
	String dummyType;
	public DummyDec(String ident, String comment) {
		super(null);
		this.ident=ident;
		this.dummyType=comment;
		this.type = new UndeclaredType(new Token(NL_NULL, -1, -1, -1))
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return null;
	}
	
	@Override
	public String toString(){
		return  dummyType;
	}

}
