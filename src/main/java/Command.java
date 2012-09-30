import java.io.IOException;

import filmeUtils.ArgumentsParserImpl;

public class Command {

	public static void main(String[] args) throws IOException {
		final ArgumentsParserImpl cli = Main.parseArgs(args);
    	Main.runFilmeUtils(cli);
	}
}
