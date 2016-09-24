package filmeUtils.torrent.torrentSites;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import filmeUtils.commons.OutputListener;

public class TorrentSearcherImpl implements TorrentSearcher {

    private final List<TorrentSite> sites;

    public TorrentSearcherImpl() {
        this.sites = new ArrayList<>();
        this.sites.add(new PirateBaySe());
        this.sites.add(new BitSnoop());
    }

    @Override
    public Optional<String> getMagnetLinkForTerm(final String exactFileName, final OutputListener outputListener) {

        for (final TorrentSite site : this.sites) {
            final Optional<String> maybeMagnetLinkFirstResult = this.getMagnetLink(site, exactFileName, outputListener);
            if (maybeMagnetLinkFirstResult.isPresent()) {
                return maybeMagnetLinkFirstResult;
            }
        }
        return Optional.empty();
    }

    private Optional<String> getMagnetLink(final TorrentSite site, final String exactFileName,
            final OutputListener outputListener) {
        outputListener.outVerbose("Procurando magnet link para " + exactFileName + " em " + site.getSiteName());
        try {
            final Optional<String> magnetLinkFirstResult = site.getMagnetLinkFirstResult(exactFileName);
            if (magnetLinkFirstResult.isPresent()) {
                outputListener.outVerbose("Encontrado magnet link para " + exactFileName + " em " + site.getSiteName());
            } else {
                outputListener.outVerbose("Nenhum magnet link para" + exactFileName + " em " + site.getSiteName());
            }
            return magnetLinkFirstResult;
        } catch (final Exception e) {
            outputListener.out("Erro procurando torrent para " + exactFileName + " : " + e.getMessage());
            return Optional.empty();
        }
    }

}
