package cop5555sp15;
import java.util.List;
import java.io.File;
/** Computes remainder on division of the larger of two integers by the smalle of two integers.

output:
1

*/
public class Example2 {
    public static void main(String[] args) throws Exception{
	File f = new File("/home/maksim/Desktop/plp_lang/codelet/test_src.pl");
	// String source = "class Example2{\n"
//     + "def i1: int;\n"
	//     + "def i2: int;\n"
	//     + "def i3: int;\n"
	//     + "i1 = 131; i2=5; i3=1;"
	//     + "if(i1==i2){ print i1; }"
	//     + "else {"
	//     + "  if(i1<i2) {"
	//     + "     while(i3*i1<i2 & (i3*i1!=i2) ) { i3 = i3+1; };"
	//     + "     print i2-((i3-1)*i1);"
	//     + "  }"
	//     + "  else { "
	//     + "     while(i3*i2<i1 & (i3*i2!=i1) ) { i3 = i3+1;}; "
	//     + "     print i1-((i3-1)*i2);"
	//     + "  };"
	//     + "};"
	//     + "}";
	Codelet codelet = CodeletBuilder.newInstance(f);
	codelet.execute();
    }
}
