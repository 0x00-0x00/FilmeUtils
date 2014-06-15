package filmeUtils.torrent;

import filmeUtils.commons.OutputListener;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.http.URISchemeLinkHandlerImpl;

public class Torrent {
	
	private OutputListener output;
	private TorrentSearcherImpl torrentSearcherImpl;

	public Torrent(final OutputListener output) {
		this.output = output;
		torrentSearcherImpl = new TorrentSearcherImpl();
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
