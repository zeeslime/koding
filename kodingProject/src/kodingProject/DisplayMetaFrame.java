package kodingProject;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * This is the DisplayMetaFrame class for UI Display 
 * 
 * @author Chang Hua, Zhan An, Wei Xiang, Jing Wei
 */
public class DisplayMetaFrame {

	JPanel panel = new JPanel();
	JRadioButton helpful = new JRadioButton("Most Helpful");
	JRadioButton positive = new JRadioButton("Positive");
	JRadioButton neutral = new JRadioButton("Neutral");
	JRadioButton negative = new JRadioButton("Negative");
	JLabel label = new JLabel("Keyword:");
	JTextField textfield = new JTextField();
	JButton searchButton = new JButton("search");
	JTextPane textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(textPane);
	MetaCritic mc = new MetaCritic();
	String category = "";

	public DisplayMetaFrame(MetaCritic mc) {
		this.mc = mc;
	
		panel.setLayout(new FlowLayout());

		ButtonGroup group = new ButtonGroup();
		group.add(helpful);
		group.add(positive);
		group.add(neutral);
		group.add(negative);

		textfield.setPreferredSize(new Dimension(100, 20));
		
		textPane.setText("Please select option or search");
		textPane.setEditable(false);
		textPane.setPreferredSize(new Dimension(600, 500));
		// JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		helpful.setSelected(true);
		sortReview("most helpful", (int) mc.getUserGameScore());
		helpful.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				category = "most helpful";
				sortReview("most helpful", (int) mc.getUserGameScore());
			}
		});
		positive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sortReview("positive", mc.getPositiveReview());
				category = "positive";
			}
		});
		neutral.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sortReview("neutral", mc.getNeutralReview());
				category = "neutral";
			}
		});
		negative.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sortReview("negative", mc.getNegativeReview());
				category = "negative";
			}
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchReview();
			}
		});
		panel.add(label);
		panel.add(textfield);
		panel.add(searchButton);
		panel.add(helpful);
		panel.add(positive);
		panel.add(neutral);
		panel.add(negative);
		panel.add(scrollPane);
		panel.setVisible(true);
	}
	
	// Sorts the Metacritic reviews based on Categories
	public void sortReview(String category, int categorySize) {
		String reviewTitle = "Showing " + category + " review\n\n";
		String reviewBody = "";
		int i = 0;
		if (categorySize != 0) {
			ArrayList<MetaCriticReview> reviewList = mc.getListOfReviews();
			for (MetaCriticReview mcr : reviewList) {
				if (mcr.getReviewCategory().equals(category)) {
					reviewBody = reviewBody + "MetaCritic Review #" + (i + 1) + "    User Score: "
							+ mcr.getReviewScore() + "\n" + mcr.getReviewText() + "\n\n";
					i++;
				}
			}
			if (reviewBody.equals("")) {
				reviewBody = reviewBody + "There is no review for this category";
			}
		}
		textPane.setText(reviewTitle + reviewBody);
		textPane.setCaretPosition(0);
	}
	
	// Search for reviews based on keywords
	public void searchReview() {
		String keyword = textfield.getText();
		String reviewBody = "";
		String reviewTitle = "Showing results for all reviews related to " + keyword + "\n\n";
		int i = 0;

		if (keyword.trim().isBlank()) {
			textPane.setText("Please enter keyword that is not empty");
		} else {
			ArrayList<MetaCriticReview> reviewList = mc.getListOfReviews();
			for (MetaCriticReview mcr : reviewList) {
				if (mcr.getReviewText().contains(keyword)) {
					reviewBody = reviewBody + "MetaCritic Review #" + (i + 1) + "    User Score: "
							+ mcr.getReviewScore() + " (" + mcr.getReviewCategory() + ")" + "\n" + mcr.getReviewText()
							+ "\n\n";
					i++;
				}
			}
			textPane.setText(reviewTitle + reviewBody);
		}
		textPane.setCaretPosition(i);
	}
	
	public JPanel getPanel() {
		return panel;
	}
}
