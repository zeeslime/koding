package kodingProject;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.awt.GridBagLayout;
import javax.swing.JMenuBar;
import java.awt.FlowLayout;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

/**
 * This is a CrawlerProgram Class for Main
 * @author Zhan An, Wei Xiang, Jing Wei, Chang Hua
 */

public class CrawlerProgram {
	static ArrayList<MetaCritic> mc = new ArrayList<>();
	static ArrayList<SteamGames> sg = new ArrayList<>();
	static String selectGameTitle;
	static int selectGameindex;

	public static void main(String[] args) throws IOException {
		// Creates a connection to database "GamesReview"
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase("GamesReview"); // auto create if doesnt exist
		// Prevents duplication of data when program reruns
		db.drop();

		// Crawl a list of top selling game links from Steam
		ArrayList<String> listOfGameLinks = SteamCrawler.getLinks();
//		// Retrive individual game information based on the links
		SteamCrawler.getGameInfo(listOfGameLinks, db);
		sg = RetrieveData.retrieveSteam(db);

		// Using a list of games crawled from Steam to get game review information from
		// MetaCritic
		MetaCriticCrawler meta = new MetaCriticCrawler();
		int counter = 0;
		System.out.println("Start crawling for MetaCritic...");
		ArrayList<String> url = meta.getLinks(sg);
		for (SteamGames s : sg) {
			System.out.println("\nRetrieving review on MetaCritic for: " + s.getGameTitle());
			meta.getGameInfo(url.get(counter), s.getGameTitle(), db);
			counter++;
			System.out.println("Completed retrieving review on MetaCritic for: " + s.getGameTitle());
		}
		System.out.println("Crawling from MetaCritic completed.");
		mc = RetrieveData.retrieveMeta(db, sg); // store into ArrayList of metacritic

		// Jframe components
		JFrame HomeFrame = new JFrame("CRAWLER PROGRAM");

		CardLayout cardLayout;
		final ListPanel listPanel = new ListPanel();

		// Split view for the Home layout
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(3);
		splitPane.setPreferredSize(new Dimension(250, 25));
		splitPane.setBounds(0, 0, 1033, 674);
		HomeFrame.getContentPane().add(splitPane);

		// Side menu bar
		JPanel sideMenuBarPanel = new JPanel();
		splitPane.setLeftComponent(sideMenuBarPanel);
		sideMenuBarPanel.setLayout(new GridLayout(6, 6, 0, 0));

		// Interchangeable card layout for each tab in the side menu
		JPanel cardPanels = new JPanel();
		splitPane.setRightComponent(cardPanels);
		cardPanels.setLayout(new CardLayout(0, 0));

		// Start of individual panels in the cardLayout
		// Select games panel for components
		JPanel gameSelectCardPanel = new JPanel();
		gameSelectCardPanel.setName("gameSelectCardPanel");
		cardPanels.add(gameSelectCardPanel, "gameSelectCardPanel");
		gameSelectCardPanel.setLayout(new CardLayout(0, 0));
		
		// Steam games panel for components
		JPanel steamGamesCardPanel = new JPanel();
		steamGamesCardPanel.setName("steamGamesCardPanel");
		cardPanels.add(steamGamesCardPanel, "steamGamesCardPanel");
		steamGamesCardPanel.setLayout(new CardLayout(0, 0));
		
		JScrollPane gameScrollPanel = new JScrollPane();
		steamGamesCardPanel.add(gameScrollPanel, "gameScrollPanel");
		gameScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		JTextPane columnH = new JTextPane();
		columnH.setText("Top Selling Steam Games:");
		gameScrollPanel.setColumnHeaderView(columnH);
		gameScrollPanel.setViewportView(listPanel);

		// Steam review panel for components
		JPanel steamReviewCardPanel = new JPanel();
		steamReviewCardPanel.setName("steamReviewCardPanel");
		cardPanels.add(steamReviewCardPanel, "steamReviewCardPanel");
		steamReviewCardPanel.setLayout(new CardLayout(0, 0));
		
		JScrollPane gameReviewScrollPanel = new JScrollPane();
		gameReviewScrollPanel.setBackground(Color.PINK);
		steamReviewCardPanel.add(gameReviewScrollPanel, "gameReviewScrollPanel");
		gameReviewScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		JTextPane columnY = new JTextPane();
		columnY.setText("Steam review:");
		gameReviewScrollPanel.setColumnHeaderView(columnY);
		
		// Metacritic review panel for components
		JPanel metaReviewCardPanel = new JPanel();
		metaReviewCardPanel.setName("metaReviewCardPanel");
		cardPanels.add(metaReviewCardPanel, "metaReviewCardPanel");
		metaReviewCardPanel.setLayout(new CardLayout(0, 0));
		
		JPanel metaPanel = new JPanel();
		metaReviewCardPanel.add(metaPanel, "metaPanel");

		// Compare reviews panel for components
		JPanel compareCardPanel = new JPanel();
		compareCardPanel.setName("compareCardPanel");
		cardPanels.add(compareCardPanel, "compareCardPanel");
		compareCardPanel.setLayout(new CardLayout(0, 0));
		
		JSplitPane comparePanel = new JSplitPane();
		compareCardPanel.add(comparePanel, "comparePanel");
		// End of individual panels in the cardLayout

		// Buttons for the side menu bar
		// Select game button
		JButton gameListButton = new JButton("Select Game");
		sideMenuBarPanel.add(gameListButton);

		JLabel gameLabel = new JLabel("Game statistic: Game not selected yet");

		DefaultListModel<String> steamTitle = new DefaultListModel<String>();
		JList<String> jList = new JList<String>(steamTitle);
		for (SteamGames s : sg) {
			steamTitle.addElement(s.getGameTitle());
		}

		// Get selected game from MouseClick action to view selected game information
		jList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					// cardLayout.show(cardPanels, "cardPanel5");
					int index = jList.locationToIndex(evt.getPoint());
					selectGameindex = index;
					selectGameTitle = jList.getModel().getElementAt(index);

					MetaCritic game = new MetaCritic();
					for (MetaCritic m : mc) {
						if (m.getGameTitle().equals(selectGameTitle)) {
							game = m;
						}
					}
					gameLabel.setText("<html>" + game.getGameTitle() + "<br>" + "Overall Score: "
                            + game.getUserGameScore() + "<br>" + "Positive reviews:" + game.getPositiveReview() + "<br>"
                            + "Neutral reviews:" + game.getNeutralReview() + "<br>" + "Ngeative reviews:"
                            + game.getNegativeReview() + "</html>");
				}
			}
		});

		// Creates a scollPane for the game selection list
		JScrollPane scrollPane = new JScrollPane(jList);
		cardPanels.add(scrollPane, "gameSelectCardPanel");
		cardLayout = (CardLayout) (cardPanels.getLayout());
		cardLayout.show(cardPanels, "gameSelectCardPanel");
		

		// Displays game selection panel when button is clicked
		gameListButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanels, "gameSelectCardPanel");
				JList<String> jList = new JList<String>(steamTitle);
				JScrollPane scrollPane = new JScrollPane(jList);
				gameSelectCardPanel.add(scrollPane);
			}
		});

		// Adds individual steam games info to the Steam Games panel
		for (int i = 0; i < sg.size(); i++) {
			listPanel.addPanel(ListPanel.getJPanel(sg, i), new Random().nextInt(50) + 50);
		}

		// Display Steam games panel when clicked
		JButton steamButton = new JButton("Steam Games");
		steamButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanels, "steamGamesCardPanel");
			}
		});
		sideMenuBarPanel.add(steamButton);
		
		// Display Steam games review when clicked
		JButton steamReviewButton = new JButton("Steam Review");
		steamReviewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanels, "steamReviewCardPanel");
				steamReviewCardPanel.removeAll();
				
//				JScrollPane gameReviewScrollPanel = new JScrollPane();
//				steamReviewCardPanel.add(gameReviewScrollPanel);
				
//				JPanel revPanel = new JPanel();
				if (selectGameTitle == null) {
					JOptionPane.showMessageDialog(null, "Please Select a game");
				} else {
					try {
						steamReviewCardPanel.add(ListPanel.getReviewJPanel(sg, selectGameindex));
//						revPanel.add(ListPanel.getReviewJPanel(sg, selectGameindex));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		sideMenuBarPanel.add(steamReviewButton);
		

		// Metacritic review button
		JButton metaButton = new JButton("MetaCritic Review");
		sideMenuBarPanel.add(metaButton);
		metaButton.addActionListener(new ActionListener() {
			// Display list of metacritic reviews based on selected game title
			@Override
			public void actionPerformed(ActionEvent e) {
				metaPanel.removeAll();
				cardLayout.show(cardPanels, "metaReviewCardPanel");
				MetaCritic game = new MetaCritic();
				for (MetaCritic m : mc) {
					if (m.getGameTitle().equals(selectGameTitle)) {
						game = m;
					}
				}
				if (selectGameTitle == null) {
					JOptionPane.showMessageDialog(null, "Please Select a game");
				} else if (game.getUserGameScore() == 0) {
					JOptionPane.showMessageDialog(null, "There is no review for this game");
				} else {
					DisplayMetaFrame dmf = new DisplayMetaFrame(game);
					metaPanel.setLayout(new BoxLayout(metaPanel, BoxLayout.Y_AXIS));
					metaPanel.add(dmf.getPanel());
				}
			}
		});

		// Display both steam and metacritic reviews for selected games
		JButton compareButton = new JButton("Display both");
		compareButton.addActionListener(new ActionListener() {
			// when button clicked, retrieve games reviews based on selected games and display
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanels, "compareCardPanel");
				comparePanel.removeAll();
				
				MetaCritic game = new MetaCritic();
				SteamGames sgame = new SteamGames();
				for (MetaCritic m : mc) {
					if (m.getGameTitle().equals(selectGameTitle)) {
						game = m;
					}
				}
				for (SteamGames s : sg) {
					if (s.getGameTitle().equals(selectGameTitle)) {
						sgame = s;
					}
				}
				// Warning disalog promt 
				if (selectGameTitle == null) {
					JOptionPane.showMessageDialog(null, "Please Select a game");
				} else if (game.getUserGameScore() == 0) {
					JOptionPane.showMessageDialog(null, "There is no review for this game");
				} else {
					String reviewTitle = "Showing Metacritic review\n\n";
					String sreviewTitle = "Showing Steam review\n\n";
					String reviewBody = "";
					String sreviewBody = "";
					
					int i = 0;
					ArrayList<MetaCriticReview> mreviewList = game.getListOfReviews();
					for (MetaCriticReview mcr : mreviewList) {
						reviewBody = reviewBody + "MetaCritic Review #" + (i + 1) + "    User Score: "
								+ mcr.getReviewScore() + " (" + mcr.getReviewCategory() + ")" + "\n"
								+ mcr.getReviewText() + "\n\n";
						i++;
					}
					// set  metacritic review texts to the left side of the scrollPane
					JTextPane mtext = new JTextPane();
					mtext.setText(reviewTitle + reviewBody);
					JScrollPane mcrollPane = new JScrollPane(mtext);
					comparePanel.setLeftComponent(mcrollPane);
					
					int j = 0;
					ArrayList<SteamGameReviews> sreviewList = sgame.getListOfSteamReviews();
					for (SteamGameReviews s : sreviewList) {
						sreviewBody = sreviewBody + "Steam Review #" + (j + 1) + "    Found Helpful: "
								+ s.getFoundHelpful() + "\n" + s.getReview() + "\n\n";
						j++;
					}
					// set  steam review texts to the left side of the scrollPane
					JTextPane stext = new JTextPane();
					JTextPane sttext = new JTextPane();
					stext.setText(sreviewTitle + sreviewBody);
					sttext.setText("Showing steam reviews");
					JScrollPane steamscrollPane = new JScrollPane(stext);
					
					// Set the splitPane divider to center
					comparePanel.setDividerLocation(0.5);
					comparePanel.setRightComponent(steamscrollPane);
				}
			}
		});
		sideMenuBarPanel.add(compareButton);
		sideMenuBarPanel.add(gameLabel);
		
		// Settings for the Jframe
		HomeFrame.setResizable(false);
		HomeFrame.setSize(1057, 720);
		HomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		HomeFrame.getContentPane().setLayout(null);
		HomeFrame.setVisible(true);
	}
}