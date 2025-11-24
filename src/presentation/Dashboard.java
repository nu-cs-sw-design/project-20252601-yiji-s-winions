package presentation;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JPanel {

    private static final int PANEL_HEIGHT = 120;
    private final JPanel contentPanel;

    public Dashboard() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Banking System Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JButton createBtn = new JButton("Create");
        JButton logoutBtn = new JButton("Logout");
        JButton closeUserBtn = new JButton("Close User Account");

        Dimension smallBtn = new Dimension(110, 28);
        createBtn.setPreferredSize(smallBtn);
        logoutBtn.setPreferredSize(smallBtn);
        closeUserBtn.setPreferredSize(new Dimension(160, 28));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonBar.add(createBtn);
        buttonBar.add(logoutBtn);
        buttonBar.add(closeUserBtn);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(buttonBar, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        createBtn.addActionListener(e -> addSectionPanel(createSampleAccountPanel("Checking Account", "$" + 0)));
        logoutBtn.addActionListener(e -> moveToLogin());
    }

    private void moveToLogin() {
        JFrame oldFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        oldFrame.dispose();

        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new Login());
        frame.setVisible(true);
    }

    public void addSectionPanel(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Height remains fixed
        int fixedHeight = PANEL_HEIGHT;

        // Let BoxLayout stretch width to fit the scroll area
        Dimension maxSize = new Dimension(Integer.MAX_VALUE, fixedHeight);
        Dimension prefSize = new Dimension(1, fixedHeight);  // width 1 = flexible

        panel.setPreferredSize(prefSize);
        panel.setMinimumSize(prefSize);
        panel.setMaximumSize(maxSize);

        contentPanel.add(panel);
        contentPanel.add(Box.createVerticalStrut(10));

        contentPanel.revalidate();
        contentPanel.repaint();
    }


    // Example template panel (you can delete or customize)
    private JPanel createSampleAccountPanel(String name, String balance) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel balanceLbl = new JLabel(balance);
        balanceLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        p.add(nameLbl, BorderLayout.WEST);
        p.add(balanceLbl, BorderLayout.EAST);

        return p;
    }
}
