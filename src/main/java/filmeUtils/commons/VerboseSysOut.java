package filmeUtils.commons;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class VerboseSysOut implements OutputListener {
	
	public void out(final String string) {
		System.out.println(string);
	}

	public void outVerbose(final String string) {
		System.out.println(string);
	}

	public void printHelp(final String applicationName, final Options options) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(applicationName, options );
	}

}
