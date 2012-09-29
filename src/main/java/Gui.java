import java.io.IOException;

import filmeUtils.ArgumentsParserImpl;


public class Gui {

	public static void main(String[] args) throws IOException {
		final ArgumentsParserImpl cli = Main.parseArgs(args);
    	boolean usingGui = true;
    	Main.runFilmeUtils(cli, usingGui);
	}
	
}
