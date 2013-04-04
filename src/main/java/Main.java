import java.io.IOException;

import filmeUtils.commandLine.CommandLineClient;
import filmeUtils.commons.VerboseSysOut;
import filmeUtils.gui.Gui;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;

public class Main {

	public static void main(final String[] args) throws IOException{
		if(args.length == 0){
			showGui();
		}else{
			CommandLineClient commandLineClient = createCommandLine();
			if(args[0].equals("-p")){
				commandLineClient.p(args[1]);
			}
		}
    }

	private static CommandLineClient createCommandLine() {
		final SimpleHttpClient httpclient = new SimpleHttpClientImpl();
		final VerboseSysOut output = new VerboseSysOut();
		final LegendasTv legendasTv = new LegendasTv(httpclient, output);
		final ExtractorImpl extractor = new ExtractorImpl();
		CommandLineClient commandLineClient = new CommandLineClient(httpclient, legendasTv, extractor, output);
		return commandLineClient;
	}

	private static void showGui() {
		Gui gui = new Gui();
		gui.open();
	}
}
