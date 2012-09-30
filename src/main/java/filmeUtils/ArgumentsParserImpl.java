package filmeUtils;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class ArgumentsParserImpl implements FilmeUtilsOptions{

	private static final String APPLICATION_NAME = "filmeUtils";
	
	private static final String HIGH_DEF_TOKEN =                      "a";
	private static final String CREDENTIALS_TOKEN =                   "c";
	private static final String SHOULD_EXTRACT_TOKEN =                "e";
	private static final String HELP_TOKEN =                          "h";
	private static final String NEW_ADDITIONS_TOKEN =                 "n";
	private static final String SEARCH_TOKEN =                        "p";
	private static final String GREED_TOKEN =                         "t";
	private static final String VERBOSE_TOKEN =                       "v";
	
	private static final int NEW_ADDS_DEFAUL_SHOW_VALUE = 23;
	private static final String USER = "filmeutils";
	private static final String PASSWORD = "filmeutilsfilme";
	
	private final Options options;
	private CommandLine cmd;

	private final OutputListener outputListener;

	public ArgumentsParserImpl(final OutputListener outputListener) {
		this.outputListener = outputListener;
		options = new Options();
    	options.addOption(SEARCH_TOKEN,"procura", true, "procura legendas");
    	
    	final Option newAdditionOption = new Option(NEW_ADDITIONS_TOKEN,"novos", true, "mostra novas legendas, o argumento é o número de legendas");
    	newAdditionOption.setOptionalArg(true);
    	
    	options.addOption(newAdditionOption);
    	options.addOption(SHOULD_EXTRACT_TOKEN,"extrair", true, 
    			"Extrai e os arquivos de legendas para o diretório informado");
    	options.addOption(HIGH_DEF_TOKEN,"alta-definicao", true, 
    			"Se argumento for v, pega vídeos 720/1080, se for f rejeita 720/1080. Se não for informado aceita todos.\n" +
    			"Na procura é aplicado o teste nos nomes dos arquivos e na extração é aplicado nos arquivos.");
    	options.addOption(CREDENTIALS_TOKEN,"credenciais", false, 
    			"Usuário e senha para logar no legendas.tv ex: joao/senha123, se não for informado, um usuário padrão é usado." +
    			"Se usado sem paramêtro força login");
    	options.addOption(GREED_TOKEN,"tudo", false, 
    			"Faz download de todos os resultados achados. O padrão é pegar o primeiro resultado com magnet link.");
    	options.addOption(VERBOSE_TOKEN,"verboso", false, 
    			"Imprime informações detalhadas.");
    	options.addOption(HELP_TOKEN,"help", false, 
    			"Imprime essa ajuda");
    	
	}
	
	private void printHelp() {
		outputListener.out("FilmeUtils é uma ferramenta de linha de commando para pegar legendas e torrents\n" +
				"atualmente usa o legendas.tv e o piratebaySe\n" +
				"Versão "+Version.VERSION);
		outputListener.printHelp(APPLICATION_NAME, options);
		outputListener.out(
				"Por exemplo, se você quiser um episódio de House, use primeiro:\n" +
				"filmeUtils -l -p House\n" +
				"Para procurar por house no legendas.tv,\n" +
				"copie o nome do episodio que você quer (digamos House.S01E05) e use o comando:\n" +
				"filmeUtils -l -p House.S01E05 -e CAMINHO_DAS_LEGENDAS\n" +
				"O token -e vai extrair as legendas no caminho passado e pegar os torrents no seu programa de torrent\n" +
				"Se quiser ver as novas legenda adicionadas no legendas.tv use\n" +
				"filmeUtils -n");
	}

	public void parse(final String[] args) {
		final CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse( options, args);
		} catch (final ParseException e) {
			outputListener.out(e.getMessage());
			printHelp();
    		return;
		}
    	
    	if(cmd.hasOption(HELP_TOKEN) || args.length == 0){
    		printHelp();
    		return;
    	}
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

	public File getSubtitlesDestinationFolderOrNull() {
		File file;
		if(cmd.hasOption(SHOULD_EXTRACT_TOKEN)){
			file = new File(cmd.getOptionValue(SHOULD_EXTRACT_TOKEN));
		}else{
			return null;
		}
		return file;
	}

	public String getUser() {
		if(userAndPasswordNotInformed()){
			return USER;
		}
		return cmd.getOptionValue(CREDENTIALS_TOKEN).split("/")[0];
	}

	private boolean userAndPasswordNotInformed() {
		return !cmd.hasOption(CREDENTIALS_TOKEN) || credentialsTokenUsedWithoutArguments();
	}

	private boolean credentialsTokenUsedWithoutArguments() {
		return cmd.getOptionValue(CREDENTIALS_TOKEN)==null;
	}

	public String getPassword() {
		if(userAndPasswordNotInformed()){
			return PASSWORD;
		}
		return cmd.getOptionValue(CREDENTIALS_TOKEN).split("/")[1];
	}

	public boolean forceLogin() {
		return cmd.hasOption(CREDENTIALS_TOKEN) && credentialsTokenUsedWithoutArguments();
	}

	public boolean isVerbose() {
		if(cmd == null)
			return true;
		return cmd.hasOption(VERBOSE_TOKEN);
	}

	public boolean shouldRefuseNonHD() {
		if(!cmd.hasOption(HIGH_DEF_TOKEN)){
			return false;
		}
		return cmd.getOptionValue(HIGH_DEF_TOKEN).toLowerCase().equals("v");
	}

	public boolean shouldRefuseHD() {
		if(!cmd.hasOption(HIGH_DEF_TOKEN)){
			return false;
		}
		return cmd.getOptionValue(HIGH_DEF_TOKEN).toLowerCase().equals("f");
	}

	public boolean isGeedy() {
		return cmd.hasOption(GREED_TOKEN);
	}

	public boolean isLazy() {
		return !isGeedy();
	}
}
