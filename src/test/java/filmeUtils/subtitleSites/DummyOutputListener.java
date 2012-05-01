package filmeUtils.subtitleSites;

import org.apache.commons.cli.Options;

import filmeUtils.OutputListener;

public class DummyOutputListener implements OutputListener {

	public void out(final String string) {
		//do nothing
	}

	public void outVerbose(final String string) {
		//do nothing
	}

	public void printHelp(final String applicationName, final Options options) {
		//do nothing
	}
}