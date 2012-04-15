package filmeUtils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class MainCLI {

	private static final int NEW_ADDS_DEFAUL_SHOW_VALUE = 23;
	private static final String APPLICATION_NAME = "filmeUtils";
	private static final String SEARCH_TOKEN = "p";
	private static final String HELP_TOKEN = "h";
	private static final String NEW_ADDITIONS_TOKEN = "n";
	private static final String UNCOMPRESS_TOKEN = "d";
	private static final String SUBS_DESTINATION_TOKEN = "l";
	
	private final Options options;
	private boolean isDone;
	private CommandLine cmd;

	public MainCLI() {
		options = new Options();
    	options.addOption(SEARCH_TOKEN,"procura", true, "procura legendas");
    	
    	final Option newAdditionOption = new Option(NEW_ADDITIONS_TOKEN,"novos", true, "mostra novas legendas, o argumento é o número de legendas");
    	newAdditionOption.setOptionalArg(true);
    	
    	options.addOption(newAdditionOption);
    	options.addOption(UNCOMPRESS_TOKEN,"descomprimir", false, "mostra o conteúdo dos arquivos");
    	options.addOption(SUBS_DESTINATION_TOKEN,"local", true, "lugar onde as legendas serão extraídas");
    	options.addOption(HELP_TOKEN,"help", false, "mostra essa ajuda");
    	
    	isDone = false;
	}

	public void parse(final String[] args) {
		final CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse( options, args);
		} catch (final ParseException e) {
			printHelp();
    		return;
		}
    	
    	if(cmd.hasOption(HELP_TOKEN) || args.length == 0){
    		printHelp();
    		return;
    	}
	}

	public boolean isDone() {
		return isDone;
	}

	public boolean search() {
		return cmd.hasOption(SEARCH_TOKEN);
	}

	public String searchTerm() {
		return cmd.getOptionValue(SEARCH_TOKEN);
	}

	public boolean showNewAdditions() {
		return cmd.hasOption(NEW_ADDITIONS_TOKEN);
	}

	public int newAdditionsPageCountToShow() {
		final String optionValue = cmd.getOptionValue(NEW_ADDITIONS_TOKEN);
		if(optionValue == null){
			return NEW_ADDS_DEFAUL_SHOW_VALUE;
		}
		try{
			final Integer valueOf = Integer.valueOf(optionValue);
			return valueOf;
		}catch (final Exception e) {
			throw new RuntimeException("Argumento deveria ser um número",e);
		}
	}

	public boolean showCompressedContents() {
		return cmd.hasOption(UNCOMPRESS_TOKEN);
	}

	private void printHelp() {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(APPLICATION_NAME, options );
		System.out.println("Por exemplo, se você quiser um episódio de House, use primeiro:\n" +
				"filmeUtils -p House\n" +
				"copie o nome do episodio que você quer (digamos House.S01E05) e rode\n" +
				"filmeUtils -p House.S01E05 -d -l CAMINHO_ONDE_O_SEU_CLIENTE_DE_TORRENT_SALVA_ARQUIVOS\n" +
				"O -d vai pegar o zip das legendas e deszipar no local passado no -l\n" +
				"Aí é só copiar o magnet link que aparece do lado da legenda e abrir no seu cliente de torren ou no\n" +
				"próprio browser.");
		isDone = true;
	}

	public File getSubtitlesDestinationFolder() {
		if(cmd.hasOption(SUBS_DESTINATION_TOKEN)){
			return new File(cmd.getOptionValue(SUBS_DESTINATION_TOKEN));
		}
		File createTempFile;
		try {
			createTempFile = File.createTempFile("FILMEUTILS", ""+System.currentTimeMillis());
			createTempFile.delete();
			createTempFile.mkdir();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return createTempFile;
	}
}
