package filmeUtils.torrentSites;

import java.util.ArrayList;
import java.util.List;

import filmeUtils.SimpleHttpClient;

public class TorrentSearcher {

	private final List<TorrentSite> sites;

	public TorrentSearcher(final SimpleHttpClient httpclient) {
		sites = new ArrayList<TorrentSite>();
		sites.add(new BitSnoop(httpclient));
		sites.add(new PirateBaySe(httpclient));
	}
	
	public String getMagnetLinkForFileOrNull(final String exactFileName){
		for (final TorrentSite site : sites) {
			final String magnetLinkFirstResultOrNull = site.getMagnetLinkFirstResultOrNull(exactFileName);
			if(magnetLinkFirstResultOrNull != null){
				return magnetLinkFirstResultOrNull;
			}
		}
		return null;
	}
	
}
