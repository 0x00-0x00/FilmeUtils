package filmeUtils;

public class SysOut implements OutputListener {

	private final boolean verbose;

	public SysOut(final ArgumentsParser cli) {
		verbose = cli.isVerbose(); 
	}
	
	public void out(final String string) {
		System.out.println(string);
	}

	public void outVerbose(final String string) {
		if(verbose){
			System.out.println(string);
		}
	}

}
