import java.io.IOException;

import filmeUtils.ArgumentsParserImpl;

public class Command {

	public static void main(String[] args) throws IOException {
		final ArgumentsParserImpl cli = Main.parseArgs(args);
    	boolean usingGui = false;
    	Main.runFilmeUtils(cli, usingGui);
	}
}
