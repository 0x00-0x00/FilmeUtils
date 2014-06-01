import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import filmeUtils.commandLine.CommandLineClient;
import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.VerboseSysOut;
import filmeUtils.gui.Gui;
import filmeUtils.subtitle.subtitleSites.legendasTV.LegendasTv;
import filmeUtils.utils.RegexForSubPackageAndSubFile;
import filmeUtils.utils.RegexUtils;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;

public class Main {

	private static LegendasTv legendasTv;

	public static void main(final String[] args) throws IOException{
		if(args.length == 0){
			showGui();
		}else{
			final CommandLineClient commandLineClient = createCommandLine();
			final String token = args[0];
			if(!token.equals("-t")  && 
			   !token.equals("-lt")	&& 
			   !token.equals("-l")  && 
			   !token.equals("-t")  && 
			   !token.equals("-n")	&& 
			   !token.equals("-f")  && 
			   !token.equals("-p")  && 
			   !token.equals("-auto")){
				h(commandLineClient);
				return;
			}
			if(token.equals("-t")){
				t(args, commandLineClient);
				return;
			}
			
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
			if(token.equals("-f")){
				f(args, commandLineClient);
				return;
			}
			if(token.equals("-auto")){
				auto(commandLineClient);
				return;
			}
			if(token.equals("-p")){
				p(args, commandLineClient);
				return;
			}
		}
    }

	private static void auto(final CommandLineClient commandLineClient) {
		commandLineClient.auto();
	}

	private static void f(final String[] args, final CommandLineClient commandLineClient) {
		final String errorMessage = "Uso: -f [arquivo de regex] [-d <diretório de destino>]";
		if(args.length > 4) throw new RuntimeException(errorMessage);
		final FileSystemUtils instance = FileSystemUtils.getInstance();
		List<String> subtitlesToDownloadPatterns = instance.getSubtitlesToDownloadPatterns();
		if(args.length >= 2){
			final File file = new File(args[1]);
			subtitlesToDownloadPatterns = instance.getSubtitlesToDownloadPatterns(file);
		}
		File subtitlesDestination = instance.getSubtitlesDestination();
		if(args.length > 2){
			if(!args[2].equals("-d")) throw new RuntimeException(errorMessage);
			subtitlesDestination = new File(args[3]);
		}
		
		final List<RegexForSubPackageAndSubFile> regexes = new ArrayList<RegexForSubPackageAndSubFile>();
		for (final String maybeComposedRegex : subtitlesToDownloadPatterns) {
			regexes.add(RegexUtils.getRegexForSubPackageAndSubFile(maybeComposedRegex));
		}
		
		commandLineClient.f(regexes, subtitlesDestination);
	}

	private static void t(final String[] args, final CommandLineClient commandLineClient) {
		if(args.length != 2) throw new RuntimeException("Uso: -t <termo da procura do torrent>");
		commandLineClient.t(args[1]);
	}

	private static void l(final String[] args, final CommandLineClient commandLineClient) {
		final String errorMessage = "Uso: -l <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]";
		final SubSearchTermRegexAndDestDir regexAndDestDir = getRegexAnDestDir(args, errorMessage);
		commandLineClient.l(regexAndDestDir.subtitleSearchTerm, regexAndDestDir.regex, regexAndDestDir.destinyDirectory);
	}

	private static void h(final CommandLineClient commandLineClient) {
		commandLineClient.h();
	}

	private static void n(final String[] args, final CommandLineClient commandLineClient) {
		if(args.length == 1){
			commandLineClient.n();
			return;
		}
		final String errorMessage = "Uso: -n  [-r <regex para pacote de legenda>[:regex para legenda]] [-d <diretório de destino>]";
		if(args.length > 5) throw new RuntimeException(errorMessage);
		String regex = ".*";
		File destinyDirectory = FileSystemUtils.getInstance().getSubtitlesDestination();
		if(!args[1].equals("-r") && !args[1].equals("-d"))
			throw new RuntimeException(errorMessage);
		if(args[1].equals("-r")){
			regex = args[2];
			if(!args[3].equals("-d")) throw new RuntimeException(errorMessage);
			destinyDirectory = new File(args[4]);
		}
		if(args[1].equals("-d")){
			destinyDirectory = new File(args[2]);
		}
		
		final RegexForSubPackageAndSubFile regexForSubPackageAndSubFile = RegexUtils.getRegexForSubPackageAndSubFile(regex);
		
		commandLineClient.n(regexForSubPackageAndSubFile,destinyDirectory);
	}

	private static class SubSearchTermRegexAndDestDir{
		public final String subtitleSearchTerm;
		public final String regex;
		public final File destinyDirectory;
		
		private SubSearchTermRegexAndDestDir(final String subtitleSearchTerm, final String regex, final File destinyDirectory) {
			this.subtitleSearchTerm = subtitleSearchTerm;
			this.regex = regex;
			this.destinyDirectory = destinyDirectory;
		}
	}
	
	private static void lt(final String[] args, final CommandLineClient commandLineClient) {
		final String errorMessage = "Uso: -lt <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]";
		final SubSearchTermRegexAndDestDir subSearchTermRegexDestDir = getRegexAnDestDir(args, errorMessage);
		commandLineClient.lt(subSearchTermRegexDestDir.subtitleSearchTerm, subSearchTermRegexDestDir.regex, subSearchTermRegexDestDir.destinyDirectory);
	}

	private static SubSearchTermRegexAndDestDir getRegexAnDestDir(final String[] args, final String errorMessage) {
		if(args.length < 2 || args.length > 6) throw new RuntimeException(errorMessage);
		final String subtitleSearchTerm = args[1];
		String regex = ".*";
		File destinyDirectory = FileSystemUtils.getInstance().getSubtitlesDestination();
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
		final SubSearchTermRegexAndDestDir regexAndDestDir = new SubSearchTermRegexAndDestDir(subtitleSearchTerm,regex,destinyDirectory);
		return regexAndDestDir;
	}

	private static void p(final String[] args,
			final CommandLineClient commandLineClient) {
		if(args.length != 2)
			throw new RuntimeException("Uso: -p <termo da procura>");
		final String subtitleSearchTerm = args[1];
		commandLineClient.p(subtitleSearchTerm);
	}

	private static CommandLineClient createCommandLine() {
		final SimpleHttpClient httpclient = new SimpleHttpClientImpl();
		final VerboseSysOut output = new VerboseSysOut();
		legendasTv = new LegendasTv( output);
		final ExtractorImpl extractor = new ExtractorImpl();
		final CommandLineClient commandLineClient = new CommandLineClient(httpclient, legendasTv, extractor, output);
		return commandLineClient;
	}

	private static void showGui() {
		final Gui gui = new Gui();
		gui.open();
	}
}
