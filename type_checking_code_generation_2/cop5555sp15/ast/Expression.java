package cop5555sp15.ast;

import cop5555sp15.TokenStream.Token;

public abstract class Expression extends ASTNode {
	
	String expressionType;
	Object value;
	public String getType() {
		return this.expressionType;
	}

	public Object getValue(){
		return this.value;
	}
	
	public void setType(String type) {
		this.expressionType = type;
	}
	
	Expression(Token firstToken) {
		super(firstToken);
	}
	
	public String boxtype;
	public String setBoxType(String prim){
		if(prim.equals("I")){ // box int to Integer
		    boxtype = "Integer";
		}
		else if(prim.equals("Z")){
		    boxtype = "Boolean";
		}
		else if(prim.equals("Ljava/lang/String;")){
		    boxtype = "Integer";
		}
		else if(prim.equals("list of lists")){
		    boxtype = "listoflists";
		}
		else if(prim.equals("empty"))
		    boxtype = "empty";
		return boxtype;
	}

}
