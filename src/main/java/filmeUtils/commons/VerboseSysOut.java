package filmeUtils.commons;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class VerboseSysOut implements OutputListener {

    @Override
    public void out(final String string) {
        System.out.println(string);
    }

    @Override
    public void outVerbose(final String string) {
        System.out.println(string);
    }

    @Override
    public void printHelp(final String applicationName, final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(applicationName, options);
    }

}
