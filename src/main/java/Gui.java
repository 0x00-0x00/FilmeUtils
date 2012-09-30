import java.io.File;
import java.io.IOException;

import filmeUtils.Downloader;
import filmeUtils.FilmeUtilsFolder;
import filmeUtils.VerboseSysOut;
import filmeUtils.extraction.ExtractorImpl;
import filmeUtils.fileSystem.FileSystem;
import filmeUtils.fileSystem.FileSystemImpl;
import filmeUtils.http.MagnetLinkHandler;
import filmeUtils.http.OSMagnetLinkHandler;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.subtitleSites.LegendasTv;
import filmeUtils.swing.SearchScreen;
import filmeUtils.swing.SearchScreenNeeds;
import filmeUtils.torrentSites.TorrentSearcher;
import filmeUtils.torrentSites.TorrentSearcherImpl;


public class Gui {

	public static void main(String[] args) throws IOException {
		System.out.println("FilmeUtils é uma ferramenta para pegar filmes e séries com legendas tendo o mínimo de trabalho possível.\n" + 
				"Como usar  \n" + 
				"Para abrir a GUI\n" + 
				"java -cp filmeUtils.jar Gui\n" +
				"ou\n" +
				"java -jar filmeUtils.jar" + 
				"\n" + 
				"Para usar a linha de comando\n" + 
				"java -cp filmeUtils.jar Command\n" + 
				"\n" + 
				"Para so procurar a legenda\n" + 
				"java -cp filmeUtils.jar Subtitle\n" + 
				"\n" + 
				"Para so procurar um link de torrent\n" + 
				"java -cp filmeUtils.jar Torrent\n" + 
				"\n" + 
				"Para executar como Deamon \n" + 
				"java -cp filmeUtils.jar Daemon\n" + 
				"\n" + 
				"\n" + 
				"Sites onde a procura é feita:\n" + 
				"\n" + 
				"Legendas:  \n" + 
				"legendas.tv  \n" + 
				"\n" + 
				"Torrents:  \n" + 
				"piratebaySe\n" + 
				"rarbg  \n" + 
				"bitsnoop");
		
		final ExtractorImpl extract = new ExtractorImpl();
		final FileSystem fileSystem = new FileSystemImpl();
		final File cookieFile = new File(FilmeUtilsFolder.get(),"cookies.serialized");
    	final SimpleHttpClient httpclient = new SimpleHttpClientImpl(cookieFile);
    	final TorrentSearcher torrentSearcher = new TorrentSearcherImpl(httpclient);
    	final MagnetLinkHandler magnetLinkHandler = new OSMagnetLinkHandler();
    	final VerboseSysOut output = new VerboseSysOut();
    	final LegendasTv legendasTv = new LegendasTv(httpclient, output);
		final Downloader downloader = new Downloader(extract, fileSystem, httpclient, torrentSearcher, magnetLinkHandler, legendasTv, output);
		final SearchScreenNeeds searchScreenNeeds = new SearchScreenNeeds(legendasTv, downloader);
		new SearchScreen(searchScreenNeeds);
	}
	
}
