package kodingProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

/**
 * This is the ListPanel class for UI Listings
 * 
 * @author Chang Hua, Zhan An, Wei Xiang, Jing Wei
 */

public class ListPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JPanel fillerPanel;
    private ArrayList<JPanel> panels;

    public ListPanel(List<JPanel> panels, int height)
    {
        this(panels, height, new Insets(2, 0, 2, 0));
    }

    public ListPanel(List<JPanel> panels, int height, Insets insets)
    {
        this();
        for (JPanel panel : panels)
            addPanel(panel, height, insets);
    }
    
    public ListPanel()
    {
        super();
        this.fillerPanel = new JPanel();
        this.fillerPanel.setMinimumSize(new Dimension(0, 0));
        this.panels = new ArrayList<JPanel>();
        setLayout(new GridBagLayout());
    }

    public void addPanel(JPanel p, int height)
    {
        addPanel(p, height, new Insets(2, 0, 2, 0));
    }
    
    // add a panel to a listpanel
    public void addPanel(JPanel p, int height, Insets insets)
    {
        super.remove(fillerPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = getComponentCount();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.ipady = height;
        gbc.insets = insets;
        gbc.weightx = 1.0;
        panels.add(p);
        add(p, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = getComponentCount();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        add(fillerPanel, gbc);
        revalidate();
        invalidate();
        repaint();
    }

    public ArrayList<JPanel> getPanels()
    {
        return this.panels;
    }
    
    public static void main(String[] args)
    {
    	//testing if adding panels work
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setMinimumSize(new Dimension(500, 500));
        f.setLocationRelativeTo(null);
        f.getContentPane().setLayout(new BorderLayout());
        final ListPanel listPanel = new ListPanel();
        for (int i = 1; i <= 10; i++)
            listPanel.addPanel(getRandomJPanel(), new Random().nextInt(50) + 50);
    }

    public static JPanel getRandomJPanel()
    {
        JPanel panel = new JPanel();
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel.add(new JLabel("This is a randomly sized JPanel"));
        panel.setBackground(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        return panel;
    }
    
    // get steam agem information to be deplayed in the Jpanel
    public static JPanel getJPanel(ArrayList<SteamGames> sg, int i) throws IOException {
		JPanel panel = new JPanel();
		JLabel gameLabel = new JLabel();
		JLabel descLabel = new JLabel();
		JLabel sumReviewLabel = new JLabel();
		JLabel priceLabel = new JLabel();
		SteamGames game = new SteamGames();

		game = sg.get(i);
		URL url = new URL(game.getImg());
		BufferedImage image = ImageIO.read(url);
		
		gameLabel.setText(game.getGameTitle());
		descLabel.setText(game.getDesc());
		sumReviewLabel.setText(game.getSumReview());
		priceLabel.setText(game.getPrice());
		
		panel.add(Box.createVerticalGlue());
		panel.add(new JLabel(new ImageIcon(image)));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(gameLabel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(descLabel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(sumReviewLabel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(priceLabel);
		panel.add(Box.createVerticalGlue());

		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		return panel;
	}
}