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
	
	private final JTextField searchString;
	private final DefaultListModel defaultListModel;
	
	public SearchScreen(final SearchScreenNeeds searchScreenNeeds) {
		
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel upperPanel = new JPanel();
		final JPanel searchPanel = new JPanel();
		final GridBagLayout gbl_searchPanel = new GridBagLayout();
		searchString = new JTextField();
		final GridBagConstraints gbc_searchString = new GridBagConstraints();
		final JButton searchButton = new JButton("Procurar");
		searchButton.setFocusable(false);
		final GridBagConstraints gbc_searchButton = new GridBagConstraints();
		gbc_searchButton.fill = GridBagConstraints.HORIZONTAL;
		final JPanel optionsPane = new JPanel();
		final JButton btnNovasLegendas = new JButton("Novas legendas");
		btnNovasLegendas.setFocusable(false);
		final GridBagConstraints gbc_btnNovasLegendas = new GridBagConstraints();
		final JComboBox resolution = new JComboBox();
		resolution.setFocusable(false);
		final GridBagConstraints gbc_resolution = new GridBagConstraints();
		final JLabel subtitlesFolder = new JLabel("...");
		final GridBagConstraints gbc_subtitlesFolder = new GridBagConstraints();
		final JLabel lblPastaDeLegendas = new JLabel("Pasta de legendas: ");
		final GridBagConstraints gbc_lblPastaDeLegendas = new GridBagConstraints();
		final JButton subtitlesDest = new JButton("...");
		subtitlesDest.setFocusable(false);
		final GridBagConstraints gbc_subtitlesDest = new GridBagConstraints();
		final JPanel searchResultsPane = new JPanel();
		final JList result = new JList();
		result.setFocusable(false);
		final JScrollPane jScrollPane = new JScrollPane();
		final JScrollPane jScrollPane2 = new JScrollPane();
		final JTextArea warnings = new JTextArea();
		warnings.setFocusable(false);
		warnings.setRows(3);
		jScrollPane2.setViewportView(warnings);
		
		final String defaultSubtitlesFolder = searchScreenNeeds.getSubtitleFolder();
		final String defaultSearchString = "Digite sua procura aqui...";
		
		final SearchCallback endSearch = new SearchCallback() {
			public void done() {
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
		final ActionListener searchForSearchTerm = new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				final String searchTerm = searchString.getText();
				warnings.append("\nProcurando "+searchTerm);
				warnings.setCaretPosition(warnings.getDocument().getLength());
				clearList();
				searchScreenNeeds.getResultsFor(searchTerm, endSearch);
			}

			
		};
		defaultListModel = new DefaultListModel();
		
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		upperPanel.setLayout(new BorderLayout(0, 0));
		
		upperPanel.add(searchPanel, BorderLayout.CENTER);
		gbl_searchPanel.columnWidths = new int[]{122, 100, 0};
		gbl_searchPanel.rowHeights = new int[]{27, 0};
		gbl_searchPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_searchPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		searchPanel.setLayout(gbl_searchPanel);
		
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
		gbc_searchString.fill = GridBagConstraints.BOTH;
		gbc_searchString.insets = new Insets(0, 0, 0, 5);
		gbc_searchString.gridx = 0;
		gbc_searchString.gridy = 0;
		searchPanel.add(searchString, gbc_searchString);
		searchString.setColumns(10);
		
		gbc_searchButton.anchor = GridBagConstraints.NORTH;
		gbc_searchButton.gridx = 1;
		gbc_searchButton.gridy = 0;
		searchButton.addActionListener(searchForSearchTerm);
		searchPanel.add(searchButton, gbc_searchButton);
		
		upperPanel.add(optionsPane, BorderLayout.NORTH);
		optionsPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		final GridBagLayout gbl_optionsPane = new GridBagLayout();
		gbl_optionsPane.columnWidths = new int[]{0, 0, 126, 0, 0, 0};
		gbl_optionsPane.rowHeights = new int[]{15, 0};
		gbl_optionsPane.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_optionsPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		optionsPane.setLayout(gbl_optionsPane);
		
		final ActionListener searchNewSubtitles = new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				if(!warnings.getText().equals("")){
					warnings.append("\n");
				}
				warnings.append("Procurando novas legendas...");
				warnings.setCaretPosition(warnings.getDocument().getLength());
				clearList();
				searchScreenNeeds.getNewAddsList(endSearch);
			}
		};
		searchNewSubtitles.actionPerformed(null);
		btnNovasLegendas.addActionListener(searchNewSubtitles);
		gbc_btnNovasLegendas.insets = new Insets(0, 0, 0, 5);
		gbc_btnNovasLegendas.gridx = 0;
		gbc_btnNovasLegendas.gridy = 0;
		optionsPane.add(btnNovasLegendas, gbc_btnNovasLegendas);
		
		gbc_resolution.insets = new Insets(0, 0, 0, 5);
		gbc_resolution.gridx = 1;
		gbc_resolution.gridy = 0;
		optionsPane.add(resolution, gbc_resolution);
		resolution.setModel(new DefaultComboBoxModel(new String[] { searchScreenNeeds.allResolutionsString() , searchScreenNeeds.highResolutionString(), searchScreenNeeds.lowResolutionString()}));
		resolution.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent arg0) {
				searchScreenNeeds.setResolution(resolution.getSelectedItem().toString());
			}
		});
		
		gbc_subtitlesFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_subtitlesFolder.insets = new Insets(0, 0, 0, 5);
		gbc_subtitlesFolder.gridx = 3;
		gbc_subtitlesFolder.gridy = 0;
		subtitlesFolder.setText(defaultSubtitlesFolder);
		optionsPane.add(subtitlesFolder, gbc_subtitlesFolder);
		
		
		
		gbc_lblPastaDeLegendas.insets = new Insets(0, 0, 0, 5);
		gbc_lblPastaDeLegendas.anchor = GridBagConstraints.WEST;
		gbc_lblPastaDeLegendas.gridx = 2;
		gbc_lblPastaDeLegendas.gridy = 0;
		optionsPane.add(lblPastaDeLegendas, gbc_lblPastaDeLegendas);
		
		gbc_subtitlesDest.gridx = 4;
		gbc_subtitlesDest.gridy = 0;
		optionsPane.add(subtitlesDest, gbc_subtitlesDest);
		
		getContentPane().add(searchResultsPane, BorderLayout.CENTER);
		searchResultsPane.setLayout(new BorderLayout(0, 0));
		
		result.setModel(defaultListModel);
		
		result.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				final int index = result.locationToIndex(e.getPoint());
				final ListModel dlm = result.getModel();
				final Object item = dlm.getElementAt(index);
				result.ensureIndexIsVisible(index);
				warnings.append("\nFazendo o download de '"+item+"'.");
				warnings.setCaretPosition(warnings.getDocument().getLength());
				searchScreenNeeds.download((String) item, new DownloadCallback() {
					public void done() {
						warnings.append("\nDowload de '"+item+"' terminado.");
						warnings.setCaretPosition(warnings.getDocument().getLength());
					}
				});
			}
		}});
		
		
		searchString.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||e.getKeyCode() == KeyEvent.VK_PAGE_UP||e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
					result.dispatchEvent(e);
			}
		});
		
		
		searchResultsPane.add(jScrollPane, BorderLayout.CENTER);
		jScrollPane.setViewportView(result);
		
		final JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		searchResultsPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(jScrollPane2);
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
		
		setSize(800,600);
		setVisible(true);
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
