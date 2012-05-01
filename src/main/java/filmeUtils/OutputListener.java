package filmeUtils;

import org.apache.commons.cli.Options;

public interface OutputListener {

	void out(String string);
	void outVerbose(String string); 
	void printHelp(String applicationName, Options options);

}
