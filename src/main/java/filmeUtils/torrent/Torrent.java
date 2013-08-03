package filmeUtils.torrent;

import filmeUtils.commons.OutputListener;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.http.URISchemeLinkHandlerImpl;
import filmeUtils.utils.http.SimpleHttpClient;
import filmeUtils.utils.http.SimpleHttpClientImpl;

public class Torrent {
	
	private OutputListener output;
	private TorrentSearcherImpl torrentSearcherImpl;

	public Torrent(final OutputListener output) {
		this.output = output;
		final SimpleHttpClient httpclient = new SimpleHttpClientImpl();
		torrentSearcherImpl = new TorrentSearcherImpl(httpclient);
	}
	
	public void download(final String searchTerm){
		String magnetLinkForTermOrNull = torrentSearcherImpl.getMagnetLinkForTermOrNull(searchTerm, output);
		if(magnetLinkForTermOrNull == null){
			output.out("Nenhum magnet link encontrado!!!");
			return;
		}
		output.out("Encontrado "+magnetLinkForTermOrNull);
		new URISchemeLinkHandlerImpl().openURL(magnetLinkForTermOrNull);
	}

}
