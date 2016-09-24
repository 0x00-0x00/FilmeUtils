package filmeUtils.subtitleSites;

import org.apache.commons.cli.Options;

import filmeUtils.commons.OutputListener;

public class DummyOutputListener implements OutputListener {

    @Override
    public void out(final String string) {
        // do nothing
    }

    @Override
    public void outVerbose(final String string) {
        // do nothing
    }

    @Override
    public void printHelp(final String applicationName, final Options options) {
        // do nothing
    }
}