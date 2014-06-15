package filmeUtils.torrent.torrentSites;

import java.util.ArrayList;
import java.util.List;

import filmeUtils.commons.OutputListener;

public class TorrentSearcherImpl implements TorrentSearcher {

	private final List<TorrentSite> sites;

	public TorrentSearcherImpl() {
		sites = new ArrayList<TorrentSite>();
		sites.add(new PirateBaySe());
		sites.add(new BitSnoop());
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
			} catch (Exception e) {
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
