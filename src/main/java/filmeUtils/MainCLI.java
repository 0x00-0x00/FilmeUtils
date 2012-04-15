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
	private static final String SHOULD_EXTRACT_TOKEN = "e";
	private static final String SITE_LINKS_TOKEN = "s";
	private static final String SUBS_DESTINATION_TOKEN = "l";
	private static final String CREDENTIALS_TOKEN = "c";
	
	private static final String USER = "greasemonkey";
	private static final String PASSWORD = "greasemonkey";
	
	private final Options options;
	private boolean isDone;
	private CommandLine cmd;

	public MainCLI() {
		options = new Options();
    	options.addOption(SEARCH_TOKEN,"procura", true, "procura legendas");
    	
    	final Option newAdditionOption = new Option(NEW_ADDITIONS_TOKEN,"novos", true, "mostra novas legendas, o argumento é o número de legendas");
    	newAdditionOption.setOptionalArg(true);
    	
    	options.addOption(newAdditionOption);
    	options.addOption(SHOULD_EXTRACT_TOKEN,"extrair", false, "Extrai e os arquivos de legendas");
    	options.addOption(SUBS_DESTINATION_TOKEN,"local", true, "Caminho onde as legendas serão extraídas, se não for informado usará um diretório temporario");
    	options.addOption(SITE_LINKS_TOKEN,"site-links", false, "Imprime o link direto para os arquivos de legendas.");
    	options.addOption(CREDENTIALS_TOKEN,"credenciais", true, "Informa usuário e senha no legendas.tv ex: joao/senha123, se não for informado um usuário padrão é usado.");
    	options.addOption(HELP_TOKEN,"help", false, "Imprime essa ajuda");
    	
    	isDone = false;
	}
	
	private void printHelp() {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(APPLICATION_NAME, options );
		System.out.println(
				"Por exemplo, se você quiser um episódio de House, use primeiro:\n" +
				"filmeUtils -p House\n" +
				"Para procurar por house no legendas.tv,\n" +
				"copie o nome do episodio que você quer (digamos House.S01E05) e use o comando:\n" +
				"filmeUtils -p House.S01E05 -d -l CAMINHO_DAS_LEGENDAS\n" +
				"O token -d vai extrair as legendas no local passado no -l ou em um diretório temporário\n" +
				"O magnet link aparece do lado da legenda,  use-o em seu cliente de torrent\n" +
				"ou no próprio browser.\n" +
				"Se quiser ver as novas legenda adicionas no legendas.tv use\n" +
				"filmeUtils -n");
		isDone = true;
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

	public boolean extractContents() {
		return cmd.hasOption(SHOULD_EXTRACT_TOKEN);
	}

	public File getSubtitlesDestinationFolder() {
		File file;
		if(cmd.hasOption(SUBS_DESTINATION_TOKEN)){
			file = new File(cmd.getOptionValue(SUBS_DESTINATION_TOKEN));
		}else{
			try {
				file = File.createTempFile("FILMEUTILS", ""+System.currentTimeMillis());
				file.delete();
				file.mkdir();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		if(extractContents()){
			System.out.println("Diretório para legendas: "+file.getAbsolutePath());
		}
		return file;
	}

	public boolean showDirectLinks() {
		return cmd.hasOption(SITE_LINKS_TOKEN);
	}

	public String getUser() {
		if(userAndPasswordNotInformed()){
			return USER;
		}
		return cmd.getOptionValue(CREDENTIALS_TOKEN).split("/")[0];
	}

	private boolean userAndPasswordNotInformed() {
		return !cmd.hasOption(CREDENTIALS_TOKEN);
	}

	public String getPassword() {
		if(userAndPasswordNotInformed()){
			return PASSWORD;
		}
		return cmd.getOptionValue(CREDENTIALS_TOKEN).split("/")[1];
	}
}
