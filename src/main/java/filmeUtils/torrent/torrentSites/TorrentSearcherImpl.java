package filmeUtils.torrent.torrentSites;

import java.util.ArrayList;
import java.util.List;

import filmeUtils.commons.OutputListener;
import filmeUtils.utils.http.SimpleHttpClient;

public class TorrentSearcherImpl implements TorrentSearcher {

	private final List<TorrentSite> sites;

	public TorrentSearcherImpl(final SimpleHttpClient httpclient) {
		sites = new ArrayList<TorrentSite>();
		sites.add(new PirateBaySe(httpclient));
		sites.add(new Rarbg(httpclient));
		sites.add(new BitSnoop(httpclient));
	}
	
	@Override
	public String getMagnetLinkForTermOrNull(final String exactFileName,final OutputListener outputListener){
		if(exactFileName.contains("tv")){
			System.out.println();
		}
		for (final TorrentSite site : sites) {
			outputListener.outVerbose("Procurando magnet link para "+exactFileName+" em "+site.getSiteName());
			String magnetLinkFirstResultOrNull = null;
			try {
				magnetLinkFirstResultOrNull = site.getMagnetLinkFirstResultOrNull(exactFileName);
			} catch (SiteOfflineException e) {
				outputListener.out("Erro procurando torrent para "+exactFileName+" : "+e.getMessage());
			}
			if(magnetLinkFirstResultOrNull != null){
				outputListener.outVerbose("Encontrado magnet link para "+exactFileName+" em "+site.getSiteName());
				return magnetLinkFirstResultOrNull;
			}
			outputListener.outVerbose("Nenhum magnet link para"+exactFileName+" em "+site.getSiteName());
		}
		return null;
	}
	
}
