package filmeUtils.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class SearchScreen extends JFrame {
	
	private JTextField searchString;
	private final DefaultListModel defaultListModel;
	private JPanel upperPanel;
	private JPanel searchPanel;
	private GridBagLayout gbl_searchPanel;
	private GridBagConstraints gbc_searchString;
	private JButton searchButton;
	private GridBagConstraints gbc_searchButton;
	private JPanel optionsPanel;
	private JButton newSubtitlesFolder;
	private GridBagConstraints gbc_btnNovasLegendas;
	private JComboBox resolution;
	private GridBagConstraints gbc_resolution;
	private JLabel subtitlesFolder;
	private GridBagConstraints gbc_subtitlesFolder;
	private JLabel subtitleFolderLabel;
	private GridBagConstraints gbc_lblPastaDeLegendas;
	private JButton subtitlesDest;
	private GridBagConstraints gbc_subtitlesDest;
	private JPanel searchResultsPanel;
	private JList result;
	private JScrollPane resultJScrollPane;
	private JScrollPane warningsJScrollPane;
	private final JTextArea warnings;
	private JPanel warningsPanel;
	private final SearchCallback endSearch;
	private final ActionListener searchForSearchTerm;
	private final SearchScreenNeeds searchScreenNeeds;
	private final JProgressBar progressBar;
	
	public SearchScreen(final SearchScreenNeeds searchScreenNeeds) {
		
		this.searchScreenNeeds = searchScreenNeeds;
		
		defaultListModel = new DefaultListModel();
		endSearch = new SearchCallback() {
			public void done() {
				progressBar.setIndeterminate(false);
				warnings.append("\nProcura completa.");
				warnings.setCaretPosition(warnings.getDocument().getLength());
			}
			
			public void found(final String name) {
				addSubtitleToList(name);
			}
			
			private void addSubtitleToList(final String name) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						defaultListModel.addElement(name);
					}
				});
			}
		};
		
		searchForSearchTerm = new ActionListener() {
			
			String lastSearch = "";
			
			public void actionPerformed(final ActionEvent event) {
				final String searchTerm = searchString.getText();
				if(lastSearch.equals(searchTerm)){
					result.dispatchEvent(event);
				}
				lastSearch = searchTerm;
				warnings.append("\nProcurando "+searchTerm);
				progressBar.setIndeterminate(true);
				warnings.setCaretPosition(warnings.getDocument().getLength());
				clearList();
				searchScreenNeeds.getResultsFor(searchTerm, endSearch);
			}			
		};
		
		setupJFrame();
		
		warnings = new JTextArea();
		progressBar = new JProgressBar();
		
		setupUpperPanel();
		setupSearchResultPanel();
		
		setVisible(true);
	}

	private void setupSearchResultPanel() {
		searchResultsPanel = new JPanel();
		searchResultsPanel.setLayout(new BorderLayout(0, 0));		
		resultJScrollPane = new JScrollPane();	
		
		result = new JList();
		result.setFocusable(false);
		result.setModel(defaultListModel);
		result.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				final int index = result.locationToIndex(e.getPoint());
				final ListModel dlm = result.getModel();
				final Object item = dlm.getElementAt(index);
				result.ensureIndexIsVisible(index);
				progressBar.setIndeterminate(true);
				warnings.append("\nFazendo o download de '"+item+"'.");
				warnings.setCaretPosition(warnings.getDocument().getLength());
				searchScreenNeeds.download((String) item, new DownloadCallback() {
					public void done() {
						progressBar.setIndeterminate(false);
						warnings.append("\nDowload de '"+item+"' terminado.");
						warnings.setCaretPosition(warnings.getDocument().getLength());
					}
				});
			}
		}});
		resultJScrollPane.setViewportView(result);
		searchResultsPanel.add(resultJScrollPane, BorderLayout.CENTER);
		
		searchResultsPanel.add(progressBar, BorderLayout.SOUTH);
		
		getContentPane().add(searchResultsPanel, BorderLayout.CENTER);
		
		setupWarningsPanel();
	}

	private void setupWarningsPanel() {
		warningsPanel = new JPanel();
		
		warningsJScrollPane = new JScrollPane();
		
		warnings.setFocusable(false);
		warnings.setRows(3);
		warningsJScrollPane.setViewportView(warnings);
		warningsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		searchResultsPanel.add(warningsPanel, BorderLayout.NORTH);
		warningsPanel.setLayout(new BorderLayout(0, 0));
		warningsPanel.add(warningsJScrollPane);
	}

	private void setupUpperPanel() {
		upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout(0, 0));
		searchPanel = new JPanel();
		gbl_searchPanel = new GridBagLayout();
		gbl_searchPanel.columnWidths = new int[]{122, 100, 0};
		gbl_searchPanel.rowHeights = new int[]{27, 0};
		gbl_searchPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_searchPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		searchPanel.setLayout(gbl_searchPanel);
		upperPanel.add(searchPanel, BorderLayout.CENTER);
		
		setupOptionsPanel();
		setupSearchPanel();
		
		getContentPane().add(upperPanel, BorderLayout.NORTH);
	}

	private void setupSearchPanel() {
		
		searchString = new JTextField();
		gbc_searchString = new GridBagConstraints();
		gbc_searchString.fill = GridBagConstraints.BOTH;
		gbc_searchString.insets = new Insets(0, 0, 0, 5);
		gbc_searchString.gridx = 0;
		gbc_searchString.gridy = 0;
		searchString.setColumns(10);
		final String defaultSearchString = "Digite sua procura aqui...";
		searchString.setText(defaultSearchString);
		searchString.addFocusListener(new FocusListener() {
			
			public void focusLost(final FocusEvent e) {
				if(searchString.getText().equals("")){
					searchString.setText(defaultSearchString);
				}
			}
			
			public void focusGained(final FocusEvent e) {
				if(searchString.getText().equals(defaultSearchString)){
					searchString.setText("");
				}
			}
		});
		searchString.addActionListener(searchForSearchTerm);
		searchString.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP || 
					e.getKeyCode() == KeyEvent.VK_DOWN ||
					e.getKeyCode() == KeyEvent.VK_PAGE_UP||
					e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
					result.dispatchEvent(e);
			}
		});
		searchPanel.add(searchString, gbc_searchString);
		
		searchButton = new JButton("Procurar");
		gbc_searchButton = new GridBagConstraints();
		gbc_searchButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchButton.anchor = GridBagConstraints.NORTH;
		gbc_searchButton.gridx = 1;
		gbc_searchButton.gridy = 0;
		searchButton.addActionListener(searchForSearchTerm);
		searchButton.setFocusable(false);
		searchPanel.add(searchButton, gbc_searchButton);
	}

	private void setupOptionsPanel() {
		optionsPanel = new JPanel();
		final GridBagLayout gbl_optionsPanel = new GridBagLayout();
		gbl_optionsPanel.columnWidths = new int[]{0, 0, 126, 0, 0, 0};
		gbl_optionsPanel.rowHeights = new int[]{15, 0};
		gbl_optionsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_optionsPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		optionsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		optionsPanel.setLayout(gbl_optionsPanel);
		upperPanel.add(optionsPanel, BorderLayout.NORTH);
		
		newSubtitlesFolder = new JButton("Novas legendas");
		gbc_btnNovasLegendas = new GridBagConstraints();
		gbc_btnNovasLegendas.insets = new Insets(0, 0, 0, 5);
		gbc_btnNovasLegendas.gridx = 0;
		gbc_btnNovasLegendas.gridy = 0;
		newSubtitlesFolder.setFocusable(false);
		final ActionListener searchNewSubtitles = new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				if(!warnings.getText().equals("")){
					warnings.append("\n");
				}
				warnings.append("Procurando novas legendas...");
				progressBar.setIndeterminate(true);
				warnings.setCaretPosition(warnings.getDocument().getLength());
				clearList();
				searchScreenNeeds.getNewAddsList(endSearch);
			}
		};
		searchNewSubtitles.actionPerformed(null);
		newSubtitlesFolder.addActionListener(searchNewSubtitles);
		optionsPanel.add(newSubtitlesFolder, gbc_btnNovasLegendas);
		
		resolution = new JComboBox();
		gbc_resolution = new GridBagConstraints();
		gbc_resolution.insets = new Insets(0, 0, 0, 5);
		gbc_resolution.gridx = 1;
		gbc_resolution.gridy = 0;
		optionsPanel.add(resolution, gbc_resolution);
		resolution.setFocusable(false);
		resolution.setModel(new DefaultComboBoxModel(new String[] { searchScreenNeeds.allResolutionsString() , searchScreenNeeds.highResolutionString(), searchScreenNeeds.lowResolutionString()}));
		resolution.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent arg0) {
				searchScreenNeeds.setResolution(resolution.getSelectedItem().toString());
			}
		});
		
		
		subtitleFolderLabel = new JLabel("Pasta de legendas: ");
		gbc_lblPastaDeLegendas = new GridBagConstraints();
		gbc_lblPastaDeLegendas.insets = new Insets(0, 0, 0, 5);
		gbc_lblPastaDeLegendas.anchor = GridBagConstraints.WEST;
		gbc_lblPastaDeLegendas.gridx = 2;
		gbc_lblPastaDeLegendas.gridy = 0;
		optionsPanel.add(subtitleFolderLabel, gbc_lblPastaDeLegendas);		
		
		subtitlesFolder = new JLabel("...");
		gbc_subtitlesFolder = new GridBagConstraints();
		gbc_subtitlesFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_subtitlesFolder.insets = new Insets(0, 0, 0, 5);
		gbc_subtitlesFolder.gridx = 3;
		gbc_subtitlesFolder.gridy = 0;
		final String defaultSubtitlesFolder = searchScreenNeeds.getSubtitleFolder();
		subtitlesFolder.setText(defaultSubtitlesFolder);
		optionsPanel.add(subtitlesFolder, gbc_subtitlesFolder);		
		
		subtitlesDest = new JButton("...");
		gbc_subtitlesDest = new GridBagConstraints();
		gbc_subtitlesDest.gridx = 4;
		gbc_subtitlesDest.gridy = 0;
		optionsPanel.add(subtitlesDest, gbc_subtitlesDest);
		subtitlesDest.setFocusable(false);
		subtitlesDest.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
				jFileChooser.showDialog(null, "OK");
				final File selectedFile = jFileChooser.getSelectedFile();
				if(selectedFile == null) return;
				searchScreenNeeds.setSubtitleFolder(selectedFile);
				subtitlesFolder.setText(selectedFile.getAbsolutePath());
			}
		});
		
	}

	private void setupJFrame() {
		final URL resource = SearchScreen.class.getResource("filmeUtils.png");
		final ImageIcon imageIcon = new ImageIcon(resource);
		setIconImage(imageIcon.getImage());
		
		try {
			setSystemLookAndFeel();
		} catch (final Exception e) {
			try {
				setNimbusLookAndFeel();
			} catch (final Exception e1) {
			}
		}
		setTitle("FilmeUtils");
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(800,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setSystemLookAndFeel() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	
	private void clearList() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				defaultListModel.clear();
			}
		});
	}

	private void setNimbusLookAndFeel() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		final LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		for (final LookAndFeelInfo info : installedLookAndFeels) {
			if ("Nimbus".equals(info.getName())) {
				UIManager.setLookAndFeel(info.getClassName());
				break;
			}
		}
	}
	
}
