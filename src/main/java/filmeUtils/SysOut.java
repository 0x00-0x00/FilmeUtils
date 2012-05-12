package filmeUtils;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class SysOut implements OutputListener {

	private final boolean verbose;

	public SysOut(final FilmeUtilsOptions cli) {
		if(cli == null){
			verbose = true;
			return;
		}
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

	public void printHelp(final String applicationName, final Options options) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(applicationName, options );
	}

}
