import java.io.File;
import java.io.IOException;

import filmeUtils.commandLine.CommandLineClient;
import filmeUtils.commons.FilmeUtilsFolder;
import filmeUtils.commons.VerboseSysOut;
import filmeUtils.gui.Gui;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;

public class Main {

	private static LegendasTv legendasTv;

	public static void main(final String[] args) throws IOException{
		if(args.length == 0){
			showGui();
		}else{
			CommandLineClient commandLineClient = createCommandLine();
			String token = args[0];
			if(token.equals("-h")){
				h(commandLineClient);
				return;
			}
			if(token.equals("-t")){
				t(args, commandLineClient);
				return;
			}
			
			legendasTv.login();
			
			if(token.equals("-lt")){
				lt(args, commandLineClient);
				return;
			}
			if(token.equals("-l")){
				l(args, commandLineClient);
				return;
			}
			if(token.equals("-n")){
				n(args, commandLineClient);
				return;
			}
			if(token.equals("-p")){
				p(args, commandLineClient);
				return;
			}
		}
    }

	private static void t(String[] args, CommandLineClient commandLineClient) {
		if(args.length != 2) throw new RuntimeException("Uso: -t <termo da procura do torrent>");
		commandLineClient.t(args[1]);
	}

	private static void l(String[] args, CommandLineClient commandLineClient) {
		String errorMessage = "Uso: -l <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]";
		SubSearchTermRegexAndDestDir regexAndDestDir = getRegexAnDestDir(args, errorMessage);
		commandLineClient.l(regexAndDestDir.subtitleSearchTerm, regexAndDestDir.regex, regexAndDestDir.destinyDirectory);
	}

	private static void h(final CommandLineClient commandLineClient) {
		commandLineClient.h();
	}

	private static void n(String[] args, final CommandLineClient commandLineClient) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	private static class SubSearchTermRegexAndDestDir{
		public final String subtitleSearchTerm;
		public final String regex;
		public final File destinyDirectory;
		
		private SubSearchTermRegexAndDestDir(String subtitleSearchTerm, String regex, File destinyDirectory) {
			this.subtitleSearchTerm = subtitleSearchTerm;
			this.regex = regex;
			this.destinyDirectory = destinyDirectory;
		}
	}
	
	private static void lt(final String[] args, final CommandLineClient commandLineClient) {
		String errorMessage = "Uso: -lt <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]";
		SubSearchTermRegexAndDestDir subSearchTermRegexDestDir = getRegexAnDestDir(args, errorMessage);
		commandLineClient.lt(subSearchTermRegexDestDir.subtitleSearchTerm, subSearchTermRegexDestDir.regex, subSearchTermRegexDestDir.destinyDirectory);
	}

	private static SubSearchTermRegexAndDestDir getRegexAnDestDir(final String[] args, String errorMessage) {
		if(args.length < 2 || args.length > 6) throw new RuntimeException(errorMessage);
		String subtitleSearchTerm = args[1];
		String regex = ".*";
		File destinyDirectory = FilmeUtilsFolder.getInstance().getSubtitlesDestination();
		if(args.length > 3){
			if(!args[2].equals("-r") && !args[2].equals("-d"))
				throw new RuntimeException(errorMessage);
			if(args[2].equals("-r")){
				regex = args[3];
			}
			if(args[2].equals("-d")){
				destinyDirectory = new File(args[3]);
			}
		}
		if(args.length > 5){
			if(args[4].equals("-d")){
				destinyDirectory = new File(args[5]);
			}else{
				throw new RuntimeException(errorMessage);
			}
		}
		SubSearchTermRegexAndDestDir regexAndDestDir = new SubSearchTermRegexAndDestDir(subtitleSearchTerm,regex,destinyDirectory);
		return regexAndDestDir;
	}

	private static void p(final String[] args,
			CommandLineClient commandLineClient) {
		if(args.length != 2)
			throw new RuntimeException("Uso: -p <termo da procura>");
		String subtitleSearchTerm = args[1];
		commandLineClient.p(subtitleSearchTerm);
	}

	private static CommandLineClient createCommandLine() {
		final SimpleHttpClient httpclient = new SimpleHttpClientImpl();
		final VerboseSysOut output = new VerboseSysOut();
		legendasTv = new LegendasTv(httpclient, output, false);
		final ExtractorImpl extractor = new ExtractorImpl();
		final CommandLineClient commandLineClient = new CommandLineClient(httpclient, legendasTv, extractor, output);
		return commandLineClient;
	}

	private static void showGui() {
		Gui gui = new Gui();
		gui.open();
	}
}
