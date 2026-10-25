package ai.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ai.ui.assets.ImageLoader;
import shared.AssetRegistry;
import team.control.HallOfFameController;

/**
 * דף הנחיתה של המשחק: הוראות המשחק בצד שמאל (כרגע תמונת רקע לבן - תוחלף בהמשך
 * בתמונת ההוראות הסופית), ומימין להן ה-Hall of Fame: תמונת רקע (עם הכותרת והמסגרת)
 * והטבלה מוצגת מעליה, בתוך המסגרת.
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

        JLabel title = new JLabel("Whack-a-Meerkat", SwingConstants.CENTER);
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
        // גודל שמתאים למסגרת שבתמונה: כותרת + 10 שורות נכנסים בלי פס גלילה
        hofTable.setFont(new Font("Arial", Font.PLAIN, 12));
        hofTable.setRowHeight(21);
        hofTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // הטבלה שקופה, כדי שצבע המסגרת של תמונת הרקע יופיע מאחוריה
        hofTable.setOpaque(false);
        ((javax.swing.table.DefaultTableCellRenderer) hofTable.getDefaultRenderer(Object.class)).setOpaque(false);
        hofTable.setFillsViewportHeight(true);

        // יחסי רוחב שמתאימים לתוכן: שם / ניקוד / תאריך (dd/MM/yyyy HH:mm)
        TableColumnModel columns = hofTable.getColumnModel();
        columns.getColumn(0).setPreferredWidth(70);
        columns.getColumn(1).setPreferredWidth(50);
        columns.getColumn(2).setPreferredWidth(150);

        JScrollPane tablePane = new JScrollPane(hofTable);
        tablePane.setOpaque(false);
        tablePane.getViewport().setOpaque(false);
        tablePane.setBorder(BorderFactory.createEmptyBorder());

        // --- צד ימין: תמונת הרקע של ה-Hall of Fame, והטבלה מעליה ---
        JPanel hofPanel = new HallOfFamePanel(ImageLoader.loadImage(AssetRegistry.hallOfFameBackground), tablePane);

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

    /**
     * פאנל ה-Hall of Fame: מצייר את תמונת הרקע (יחס גובה-רוחב נשמר, ממורכזת),
     * וממקם את הטבלה בתוך המסגרת הפנימית של התמונה - גם כשהחלון משנה גודל.
     */
    private static final class HallOfFamePanel extends JPanel {

        // המסגרת הפנימית של התמונה (בפיקסלים של התמונה המקורית), עם שוליים קטנים בגלל הפינות המעוגלות
        private static final double tableLeft = 176;
        private static final double tableTop = 515;
        private static final double tableRight = 1017;
        private static final double tableBottom = 1431;

        // צבע שוליי התמונה - כדי שהרווח שמסביב לתמונה (כשהיחס שונה) ייראה כהמשך שלה
        private static final Color edgeColor = new Color(247, 238, 208);

        private final Image backgroundImage;
        private final JScrollPane tablePane;

        // תמונה מוקטנת במטמון - איכות טובה יותר מציור ישיר של תמונה גדולה
        private Image scaledImage;
        private int scaledWidth = -1;
        private int scaledHeight = -1;

        HallOfFamePanel(Image backgroundImage, JScrollPane tablePane) {
            super(null); // מיקום ידני של הטבלה לפי התמונה
            this.backgroundImage = backgroundImage;
            this.tablePane = tablePane;
            setBackground(edgeColor);
            add(tablePane);
        }

        /** המלבן שבו התמונה מצוירת: כל הפאנל, או מרכוז לפי יחס התמונה. */
        private Rectangle imageBounds() {
            if (backgroundImage == null) {
                return new Rectangle(0, 0, getWidth(), getHeight());
            }
            double scale = Math.min(
                    getWidth() / (double) backgroundImage.getWidth(null),
                    getHeight() / (double) backgroundImage.getHeight(null));
            int width = (int) Math.round(backgroundImage.getWidth(null) * scale);
            int height = (int) Math.round(backgroundImage.getHeight(null) * scale);
            return new Rectangle((getWidth() - width) / 2, (getHeight() - height) / 2, width, height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage == null || getWidth() == 0 || getHeight() == 0) return;

            Rectangle bounds = imageBounds();
            if (bounds.width != scaledWidth || bounds.height != scaledHeight) {
                scaledImage = backgroundImage.getScaledInstance(bounds.width, bounds.height, Image.SCALE_SMOOTH);
                scaledWidth = bounds.width;
                scaledHeight = bounds.height;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(scaledImage, bounds.x, bounds.y, this);
        }

        @Override
        public void doLayout() {
            if (backgroundImage == null) {
                tablePane.setBounds(0, 0, getWidth(), getHeight()); // אין תמונה - הטבלה ממלאת את הפאנל
                return;
            }
            Rectangle bounds = imageBounds();
            double scale = bounds.width / (double) backgroundImage.getWidth(null);
            int x = bounds.x + (int) Math.round(tableLeft * scale);
            int y = bounds.y + (int) Math.round(tableTop * scale);
            int width = (int) Math.round((tableRight - tableLeft) * scale);
            int height = (int) Math.round((tableBottom - tableTop) * scale);
            tablePane.setBounds(x, y, width, height);
        }
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
