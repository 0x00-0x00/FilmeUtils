package filmeUtils;

import junit.framework.Assert;

import org.junit.Test;

public class ArgumentsParserTest {

	@Test
	public void testGuiArgument(){
		final ArgumentsParser subject = new ArgumentsParser();
		subject.parse(new String[]{"-g"});
		Assert.assertTrue(subject.usingGuiMome());
	}
	
}
