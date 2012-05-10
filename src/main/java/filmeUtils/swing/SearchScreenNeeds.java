package filmeUtils.swing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import filmeUtils.Downloader;
import filmeUtils.SearchListener;
import filmeUtils.subtitleSites.LegendasTv;


public class SearchScreenNeeds {

	private static final String LOW = "Resolução normal";
	private static final String HIGH = "Alta definição";
	private static final String ALL = "Todas as resoluções";
	private File subtitleFolder;
	private final LegendasTv legendasTv;
	private final Downloader downloader;
	private final MutableFilmeUtilsOptions filmeUtilsOptions;

	public SearchScreenNeeds(final LegendasTv legendasTv, final Downloader downloader) {
		this.legendasTv = legendasTv;
		this.downloader = downloader;
		filmeUtilsOptions = new MutableFilmeUtilsOptions();
		setSubtitleFolder(new File(System.getProperty("user.home")));
	}
	
	public void download(final String item) {
		legendasTv.search(item, new SearchListener() {
			public boolean foundReturnIfShouldStopLooking(final String name, final String link) {
				return downloader.download(name, link, filmeUtilsOptions);
			}
		});
	}

	public void setSubtitleFolder(final File folder) {
		subtitleFolder = folder;
		filmeUtilsOptions.setSubtitlesDestinationFolder(folder);
	}

	public String getSubtitleFolder() {
		return subtitleFolder.getAbsolutePath();
	}

	public String[] getDefaultList() {
		final List<String> subtitles = new ArrayList<String>();
		
		legendasTv.getNewer(50, new SearchListener(){
			public boolean foundReturnIfShouldStopLooking(final String name,final String link) {
				subtitles.add(name);
				return false;
			}
			
		});
		final String[] array = subtitles.toArray(new String[subtitles.size()]);
		return array;
	}

	public String[] getResultsFor(final String text) {
		final List<String> subtitles = new ArrayList<String>();
		
		legendasTv.search(text, new SearchListener(){
			public boolean foundReturnIfShouldStopLooking(final String name,final String link) {
				subtitles.add(name);
				return false;
			}
			
		});
		final String[] array = subtitles.toArray(new String[subtitles.size()]);
		return array;
	}

	public void setResolution(final String resolution) {
		if(resolution.equals(ALL)){
			filmeUtilsOptions.setShouldRefuseHD(false);
			filmeUtilsOptions.setShouldRefuseNonHD(false);
		}
		if(resolution.equals(HIGH)){
			filmeUtilsOptions.setShouldRefuseHD(false);
			filmeUtilsOptions.setShouldRefuseNonHD(true);
		}
		if(resolution.equals(LOW)){
			filmeUtilsOptions.setShouldRefuseHD(true);
			filmeUtilsOptions.setShouldRefuseNonHD(false);
		}
	}

	public String allResolutionsString() {
		return ALL;
	}

	public String highResolutionString() {
		return HIGH;
	}

	public String lowResolutionString() {
		return LOW;
	}

}
