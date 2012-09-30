import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;

import filmeUtils.OutputListener;
import filmeUtils.http.OSMagnetLinkHandler;
import filmeUtils.http.SimpleHttpClient;
import filmeUtils.http.SimpleHttpClientImpl;
import filmeUtils.torrentSites.SiteOfflineException;
import filmeUtils.torrentSites.TorrentSearcherImpl;


public class Torrent {

	public static void main(String[] args) throws SiteOfflineException {
		OutputListener outputListener = new OutputListener() {
			
			@Override
			public void printHelp(String applicationName, Options options) {
				System.out.println("Nome do termo a ser procurado tem que ser informado");
			}
			
			@Override
			public void outVerbose(String string) {
				System.out.println(string);
			}
			
			@Override
			public void out(String string) {
				System.out.println(string);
			}
		};
		
		if(args.length < 1){
			outputListener.printHelp(null, null);
			System.exit(1);
		}
		
		final SimpleHttpClient httpclient = new SimpleHttpClientImpl();
		TorrentSearcherImpl torrentSearcherImpl = new TorrentSearcherImpl(httpclient);
		String magnetLinkForTermOrNull = torrentSearcherImpl.getMagnetLinkForTermOrNull(StringUtils.join(args,' '), outputListener);
		if(magnetLinkForTermOrNull == null)
			outputListener.out("No magnet link found");
		else
			new OSMagnetLinkHandler().openURL(magnetLinkForTermOrNull);
	}
}
