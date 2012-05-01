package filmeUtils;

import junit.framework.Assert;

import org.junit.Test;

import filmeUtils.subtitleSites.DummyOutputListener;

public class ArgumentsParserTest {

	@Test
	public void testSimpleSearch(){
		final ArgumentsParserImpl subject = new ArgumentsParserImpl(new DummyOutputListener());
		subject.parse(new String[]{"-p","foo"});
		Assert.assertTrue(subject.search());
		Assert.assertTrue(subject.isLazy());
		Assert.assertEquals("foo", subject.searchTerm());
	}
	
	@Test
	public void testSimpleSearchWithExtractionFolder(){
		final ArgumentsParserImpl subject = new ArgumentsParserImpl(new DummyOutputListener());
		subject.parse(new String[]{"-p","foo","-e","/foo/bar"});
		Assert.assertTrue(subject.search());
		Assert.assertTrue(subject.isLazy());
		Assert.assertEquals("foo", subject.searchTerm());
		Assert.assertEquals("/foo/bar", subject.getSubtitlesDestinationFolderOrNull().getAbsolutePath());
	}
	
	@Test
	public void testShowNewAdditions(){
		final ArgumentsParserImpl subject = new ArgumentsParserImpl(new DummyOutputListener());
		subject.parse(new String[]{"-n","42"});
		Assert.assertFalse(subject.search());
		Assert.assertTrue(subject.showNewAdditions());
		Assert.assertEquals(42, subject.newAdditionsPageCountToShow());
	}
}
