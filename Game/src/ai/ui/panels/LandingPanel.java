package ai.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import ai.ui.assets.ImageLoader;
import shared.AssetRegistry;
import team.control.HallOfFameController;

/**
 * דף הנחיתה של המשחק: הוראות המשחק בצד שמאל (כרגע תמונת רקע לבן - תוחלף בהמשך
 * בתמונת ההוראות הסופית), וטבלת ה-Hall of Fame מימין להן.
 * לחיצה על START GAME עוברת למסך המשחק עצמו (ה-callback מתקבל מה-Ui).
 */
public class LandingPanel extends JPanel {

    private final Image instructionsImage;
    private final DefaultTableModel hofModel;

    public LandingPanel(Runnable onStartClicked) {
        super(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        instructionsImage = ImageLoader.loadImage(AssetRegistry.instructions);

        JLabel title = new JLabel("Whack-a-Mole: Timon & Pumbaa", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // --- צד שמאל: תמונת הוראות (placeholder לבן כרגע) ---
        JPanel instructionsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (instructionsImage != null) {
                    g.drawImage(instructionsImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        instructionsPanel.setBackground(Color.WHITE);
        instructionsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // --- צד ימין: טבלת Hall of Fame (מתמלאת דרך setHallOfFameRows) ---
        hofModel = new DefaultTableModel(HallOfFameController.DISPLAY_HEADINGS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable hofTable = new JTable(hofModel);
        hofTable.setFont(new Font("Arial", Font.PLAIN, 14));
        hofTable.setRowHeight(24);
        hofTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JLabel hofTitle = new JLabel("Hall of Fame", SwingConstants.CENTER);
        hofTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel hofPanel = new JPanel(new BorderLayout(5, 5));
        hofPanel.setOpaque(false);
        hofPanel.add(hofTitle, BorderLayout.NORTH);
        hofPanel.add(new JScrollPane(hofTable), BorderLayout.CENTER);

        // --- הפיצול עצמו: הוראות משמאל, טבלה מימין ---
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(instructionsPanel);
        splitPanel.add(hofPanel);

        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(Color.GREEN);
        startButton.setFocusable(false);
        startButton.addActionListener(e -> onStartClicked.run());

        JPanel startPanel = new JPanel();
        startPanel.setOpaque(false);
        startPanel.add(startButton);

        add(title, BorderLayout.NORTH);
        add(splitPanel, BorderLayout.CENTER);
        add(startPanel, BorderLayout.SOUTH);
    }

    /** מרענן את טבלת ה-Hall of Fame עם השורות שמגיעות מה-Backend (ממוינות וחתוכות ל-n כבר שם). */
    public void setHallOfFameRows(String[][] rows) {
        hofModel.setRowCount(0);
        if (rows == null) return;
        for (String[] row : rows) {
            hofModel.addRow(row);
        }
    }
}
