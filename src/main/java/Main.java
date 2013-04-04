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

	public static void main(final String[] args) throws IOException{
		if(args.length == 0){
			showGui();
		}else{
			CommandLineClient commandLineClient = createCommandLine();
			String token = args[0];
			if(token.equals("-p")){
				if(args.length != 2)
					throw new RuntimeException("Uso: -p <termo da procura>");
				String subtitleSearchTerm = args[1];
				commandLineClient.p(subtitleSearchTerm);
			}
			if(token.equals("-lt")){
				if(args.length < 2 || args.length > 6)
					throw new RuntimeException("Uso: -lt <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]");
				String subtitleSearchTerm = args[1];
				String regex = ".*";
				File destinyDirectory = FilmeUtilsFolder.getInstance().getSubtitlesDestination();
				if(args.length > 3){
					if(!args[2].equals("-r") && !args[2].equals("-d"))
						throw new RuntimeException("Uso: -lt <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]");
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
						throw new RuntimeException("Uso: -lt <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]");
					}
				}
				commandLineClient.lt(subtitleSearchTerm, regex, destinyDirectory);
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
