package filmeUtils;

import junit.framework.Assert;

import org.junit.Test;

public class ArgumentsParserTest {

	@Test
	public void testGuiArgument(){
		final ArgumentsParserImpl subject = new ArgumentsParserImpl();
		subject.parse(new String[]{"-a","v"});
		Assert.assertTrue(subject.shouldRefuseNonHD());
	}
	
}
