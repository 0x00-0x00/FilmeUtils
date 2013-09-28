package filmeUtils.subtitleSites;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import filmeUtils.utils.http.SimpleHttpClientImpl;

public class LegendasTvTest {
	
	private final OutputListener dummyOutputListener;
	final SimpleHttpClientMock mock = new SimpleHttpClientMock();

	public LegendasTvTest() {
		dummyOutputListener = new DummyOutputListener();
	}
	
	@Test
	public void simpleSearchWithMoreThanOneResultTwoPagesGreedyTest(){
		mock.setResponseForUrl("http://legendas.tv/util/carrega_legendas_busca/termo:house%20of%20the%20dead/page:1", "LegendasTvLotsOfResultsTwoPages.html");
		mock.setResponseForUrl("http://legendas.tv/util/carrega_legendas_busca/termo:house%20of%20the%20dead/page:2", "LegendasTvOneResult.html");
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		final Map<String, String> expectedResults = new LinkedHashMap<String, String>();
		expectedResults.put("House [2008]DVDRip[Xvid AC3[5 1]-RoCK&BlueLadyRG", "http://legendas.tv/pages/downloadarquivo/c214ba8d3a08aa5ed2e6d0c228cb7c08");
		expectedResults.put("House (1986) DVDRip XviD-DitriS", "http://legendas.tv/pages/downloadarquivo/2a385576ec54161d101d59aeae496bbf");
		expectedResults.put("House (1986) Xvid [ENG] Dvdrip", "http://legendas.tv/pages/downloadarquivo/b9348478cdbca402907acfb6ab509874");
		expectedResults.put("Breaking Bad S05E13 HDTV x264-EVOLVE-AFG-mSD-ChameE-IMMERSE-BS", "http://legendas.tv/pages/downloadarquivo/137620a9b17d9065bbcad03a9e3feaf7");
		subject.search("house of the dead", new SubtitleLinkSearchCallback(){@Override public void process(final SubtitlePackageAndLink subAndLink) {
			final String name = subAndLink.name;
			final String link = subAndLink.link;
			final String linkExpected = expectedResults.get(name);
			Assert.assertEquals("For "+name,linkExpected, link);
			expectedResults.remove(name);
		}});
		Assert.assertEquals(0, expectedResults.size());
	}
	
	@Test
	public void getNewAdds(){
		mock.setResponseForUrl("http://legendas.tv/util/carrega_destaques/page:1", "LegendasTvNewSeriesFirstPage.html");
		mock.setResponseForUrl("http://legendas.tv/util/carrega_destaques/page:2", "LegendasTvNewSeriesSecondPage.html");
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		final Map<String, String> expectedResults = new LinkedHashMap<String, String>();
		expectedResults.put("Sons of Anarchy S06E01 PROPER HDTV x264 ASAP REPACK AFG REPACK FUM REPACK EVOLVE", "http://legendas.tv/pages/downloadarquivo/d5179f666b2d04c77b0c4de496050860");
		expectedResults.put("Suits S03E09 HDTV x264 EVOLVE AFG EVOLVE mSD HWD", "http://legendas.tv/pages/downloadarquivo/7b7a85a10c14185754a0c599a6e8163b");
		expectedResults.put("Paranormal Witness S03E11 HDTV x264 INNOCENCE AFG INNOCENCE", "http://legendas.tv/pages/downloadarquivo/1d22e8cb551426fde59db248607b64fd");
		expectedResults.put("Hell on Wheels S03E06 HDTV x264 EVOLVE AFG EVOLVE", "http://legendas.tv/pages/downloadarquivo/3be6ce89d4aa35652875358cf441badc");
		subject.getNewer(2, new SubtitleLinkSearchCallback(){@Override public void process(final SubtitlePackageAndLink subAndLink) {
			final String name = subAndLink.name;
			final String link = subAndLink.link;
			final String linkExpected = expectedResults.get(name);
			Assert.assertEquals("For "+name,linkExpected, link);
			expectedResults.remove(name);
		}});
		Assert.assertEquals(0, expectedResults.size());
	}
	
	@Test
	public void getNewAddsForReal(){
		final LegendasTv subject = new LegendasTv(new SimpleHttpClientImpl(), dummyOutputListener);
		final AtomicBoolean atLeastOne = new AtomicBoolean(false);
		subject.getNewer(1, new SubtitleLinkSearchCallback(){@Override public void process(final SubtitlePackageAndLink subAndLink) {
			atLeastOne.set(true);
		}});
		Assert.assertTrue(atLeastOne.get());
	}
}