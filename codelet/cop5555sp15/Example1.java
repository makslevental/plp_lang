package cop5555sp15;
import java.util.List;
/** This class prints all of the Fibonacci numbers between 2 and 1597

output:
2
3
5
8
13
21
34
55
89
144
233
377
610
987
1597

*/
public class Example1 {
    public static void main(String[] args) throws Exception{
	String source = "class Example1{\n"
	    + "def i1: int;\n"
	    + "def i2: int;\n"
	    + "def i3: int;\n"
	    + "i1 = 1;\n"
	    + "i2 = 1;\n"
	    + "i3 = 0;\n"
	    + "while(i3<1000) { i3=i1+i2; print i3; i1 = i2; i2=i3; };"
	    + "}";
	Codelet codelet = CodeletBuilder.newInstance(source);
	codelet.execute();
    }
}
