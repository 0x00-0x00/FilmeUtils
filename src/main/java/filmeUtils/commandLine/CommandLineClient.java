package filmeUtils.commandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import filmeUtils.Version;
import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.downloader.Downloader;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.torrent.Torrent;
import filmeUtils.utils.RegexForSubPackageAndSubFile;
import filmeUtils.utils.RegexUtils;
import filmeUtils.utils.extraction.Extractor;
import filmeUtils.utils.http.SimpleHttpClient;

public class CommandLineClient implements CommandLine {
	
	private final SimpleHttpClient httpclient;
	private final LegendasTv legendasTv;
	private final OutputListener output;
	private final Extractor extractor;

	public CommandLineClient(
			final SimpleHttpClient httpclient,
			final LegendasTv legendasTv,
			final Extractor extract,
			final OutputListener output) {
		this.httpclient = httpclient;
		this.legendasTv = legendasTv;
		extractor = extract;
		this.output = output;
	}

	@Override
	public void h() {
		output.out("FilmeUtils "+Version.getVersion()+"\n" +
				"ENQUANTO O LEGENDAS TV ESTIVER SÓ COM A VERSÃO DEV\n" +
				"SÓ A OPÇÃO -n ou -auto NA LINHA DE COMANDO VAI FUNCIONAR\n" +
				"Ajuda dos comandos:\n" +
				"Sem argumentos  \n" + 
				"	Abre a gui.  \n" + 
				"\n" + 
				"-h ou --help  \n" + 
				"	Mostra a ajuda  \n" + 
				"\n" + 
				"-lt <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]  \n" + 
				"	Procura e faz download do pacote de legendas,  \n" + 
				"	aplica a regex nas legendas do arquivo e tenta pegar o torrent das legendas  \n" + 
				"	que dão match. Copia as legendas que tem torrents para o diretório de  \n" + 
				"	destino. Se o destino não for especificado, usa-se o que estiver no   \n" + 
				"	HOME/.filmeUtils/subtitlefolder  \n" + 
				"	O termo de procura não é uma regex.    \n" + 
				"	Ex:  \n" + 
				"	java -jar filmeUtils.jar -lt \"game of\" -r \".*720.*\" -d \"/home/foo/Downloads\"  \n" + 
				"\n" + 
				"-l <termo da procura da legenda>  [-r <regex para arquivos de legenda>] [-d <diretório de destino>]  \n" + 
				"	Procura e faz download do pacote de legendas,   \n" + 
				"	aplica a regex nas legendas do pacote e copia as legendas que dão match  \n" + 
				"	para o destino. Se o destino não for especificado, usa-se o que estiver no  \n" + 
				"	HOME/.filmeUtils/subtitlefolder  \n" + 
				"	O termo de procura não é uma regex.\n" + 
				"	Ex:  \n" + 
				"	java -jar filmeUtils.jar -l \"game of\" -r \".*720.*\" -d \"/home/foo/Downloads\"     \n" + 
				"\n" + 
				"-t <termo da procura do torrent>  \n" + 
				"	Procura e faz download do torrent com mais seeds.  \n" + 
				"	O termo de procura não é uma regex.      \n" + 
				"	Ex:  \n" + 
				"	java -jar filmeUtils.jar -t \"game of S01E01\"  \n" + 
				"\n" + 
				"-n  [-r <regex para pacote de legenda>[:regex para legenda]] [-d <diretório de destino>]  \n" + 
				"	Se não for passado uma regex, mostra a lista legendas adicionadas  \n" + 
				"	recentemente. Se for passada a regex, faz download do pacote de legendas  \n" + 
				"	que dá match,  \n" + 
				"	e se for passada a segunda parte da regex com \":\" aplica regex nos arquivos  \n" + 
				"	de legendas. Tenta pegar o torrent das legendas que derão match e copia as  \n" + 
				"	legendas que tem torrents para o diretório de destino. Se o destino não for  \n" + 
				"	especificado, usa-se o que estiver no HOME/.filmeUtils/subtitlefolder    \n" + 
				"	Ex:  \n" + 
				"	java -jar filmeUtils.jar -n  \n" + 
				"		Lista os pacotes de legendas novos\n" + 
				"	java -jar filmeUtils.jar -n -r \".*game.*of.*:.*720.*\" -d \"/home/foo/Downloads\"  \n" + 
				"\n" + 
				"\n" + 
				"-f [arquivo de regex] [-d <diretório de destino>]  \n" + 
				"	Procura nas legendas adicionadas recentemente os pacotes de legenda que dão  \n" + 
				"	match com as regex no arquivo passado. Para os pacotes de legenda que dão  \n" + 
				"	match, aplica a segunda regex nos arquivos de legenda e faz download da    \n" + 
				"	legenda e do torrent. Copia as legendas para o caminho de destion. Se o  \n" + 
				"	destino não for	especificado, usa-se o que estiver  \n" + 
				"	no HOME/.filmeUtils/subtitlefolder   \n" + 
				"	Se não for passado o caminho do arquivo de regex,  \n" + 
				"	usa-se o arquivo padrão em HOME/.filmeUtils/downloadThis  \n" + 
				"	Formato do arquivo de regex  \n" + 
				"	<regex para pacote de legendas>[:regex para legenda]  \n" + 
				"	ex:  \n" + 
				"	.*meu.*seriado.*so.*em.*hd.*:720  \n" + 
				"	.*meu.*seriado.*qqer.*resolucao.*  \n" + 
				"\n" + 
				"-p <termo da procura>  \n" + 
				"	Somente lista os pacotes de legendas que batem com a procura e suas legendas.  \n" + 
				"	O termo de procura não é uma regex.   \n" + 
				"	\n" + 
				"-auto\n" + 
				"	Procura nas legendas adicionadas recentemente as legendas que dão match com as  \n" + 
				"	regex no arquivo HOME/.filmeUtils/downloadThis . Procura o torrent para essas  \n" + 
				"	legendas, se encontrar, baixa o torrent para o diretório configurado em  \n" + 
				"	HOME/.filmeUtils/subtitlefolder . Depois adiciona o nome do pacote de legendas  \n" + 
				"	no arquivo HOME/.filmeUtils/alreadyDownloaded . Se um torrent/legenda já foi  \n" + 
				"	pego, ele não faz o download.\n" + 
				"");
	}

	@Override
	public void lt(final String subtitleSearchTerm, final String regexToApplyOnSubtitlesFiles, final File destinantion) {
		final LegendasTv legendasTv = new LegendasTv(httpclient, output);
		final Downloader downloader = new Downloader(extractor,  httpclient, legendasTv, output);
		output.out("Procurando "+subtitleSearchTerm+" aplicando regex "+regexToApplyOnSubtitlesFiles+" salvando em "+destinantion.getAbsolutePath());
		downloader.download(subtitleSearchTerm, destinantion, regexToApplyOnSubtitlesFiles);
	}

	@Override
	public void l(final String subtitleSearchTerm, final String regexToApplyOnSubtitlesFiles, final File destinantion) {
		final Subtitle subtitle = new Subtitle(output,httpclient,legendasTv);
		output.out("Procurando "+subtitleSearchTerm);
		subtitle.download(subtitleSearchTerm,regexToApplyOnSubtitlesFiles,destinantion);
	}

	@Override
	public void t(final String torrentSearchTerm) {
		final Torrent torrent = new Torrent(output);
		torrent.download(torrentSearchTerm);
	}

	@Override
	public void n() {
		final Subtitle subtitle = new Subtitle(output,httpclient,legendasTv);
		output.out("Legendas adicionadas recentemente");
		subtitle.listNewSubtitles();
	}

	@Override
	public void n(final RegexForSubPackageAndSubFile regex, final File destinantion) {
		output.out("Procurando "+regex.packageRegex+" nas legendas adicionadas recentemente");
		output.out("Aplicando "+regex.fileRegex+" nos arquivos de legendas");
		output.out("Salvando em "+destinantion.getAbsolutePath());
		
		final LegendasTv legendasTv = new LegendasTv(httpclient, output);
		final Downloader downloader = new Downloader(extractor, httpclient, legendasTv, output);
		downloader.downloadFromNewest(regex, destinantion);
	}

	@Override
	public void f(final List<RegexForSubPackageAndSubFile> regexes, final File destinantion) {
		final LegendasTv legendasTv = new LegendasTv(httpclient, output);
		final Downloader downloader = new Downloader(extractor, httpclient,legendasTv, output);
		downloader.downloadFromNewest(regexes, destinantion);
	}

	@Override
	public void p(final String subtitleSearchTerm) {
		final Subtitle subtitle = new Subtitle(output,httpclient,legendasTv);
		output.out("Procurando "+subtitleSearchTerm);
		subtitle.search(subtitleSearchTerm);
	}

	@Override
	public void auto() {
		final FileSystemUtils instance = FileSystemUtils.getInstance();
		final List<String> subtitlesToDownloadPatterns = instance.getSubtitlesToDownloadPatterns();
		final List<RegexForSubPackageAndSubFile> regexes = new ArrayList<RegexForSubPackageAndSubFile>();
		for (final String maybeComposedRegex : subtitlesToDownloadPatterns) {
			regexes.add(RegexUtils.getRegexForSubPackageAndSubFile(maybeComposedRegex));
		}
		final LegendasTv legendasTv = new LegendasTv(httpclient, output);
		final Downloader downloader = new Downloader(extractor, httpclient,legendasTv, output);
		downloader.downloadFromNewest(regexes, instance.getSubtitlesDestination(),instance.getAlreadyDownloaded());
	}

}
