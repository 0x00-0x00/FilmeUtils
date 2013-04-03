

import java.io.File;
import java.io.IOException;

import filmeUtils.commandLine.CommandLineClient;
import filmeUtils.commons.FilmeUtilsFolder;
import filmeUtils.commons.PrintSubtitlesZipNames;
import filmeUtils.commons.VerboseSysOut;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.utils.extraction.ExtractorImpl;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;

public class Main {

	public static void main(final String[] args) throws IOException{
    	runFilmeUtils();
    }

	static void runFilmeUtils() throws IOException {
		boolean search = true;
		boolean showNewAdditions = true;
		String searchTerm = "";
		
		final File cookieFile = FilmeUtilsFolder.getInstance().getCookiesFile();
    	final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    	final ExtractorImpl extractor = new ExtractorImpl();
    	final VerboseSysOut output = new VerboseSysOut();
    	final LegendasTv legendasTv = new LegendasTv(httpclient, output);
    	
    	PrintSubtitlesZipNames printSubtitlesZipNames = new PrintSubtitlesZipNames(output);
		final CommandLineClient commandLineClient = new CommandLineClient(httpclient,legendasTv,extractor,output,printSubtitlesZipNames);
    	if(search) {
			commandLineClient.search(searchTerm);
		}
    	if(showNewAdditions){
    		commandLineClient.showNewAdditions();
    	}
	}
}
