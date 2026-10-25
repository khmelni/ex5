package ai.ui.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/** חלון "Game Over" עם הניקוד הסופי. מחזיר את בחירת השחקן דרך callbacks. */
public final class GameOverDialog {


    private GameOverDialog() {}

    

    public static void show(JFrame parent, int finalScore, Runnable onStartAgain, Runnable onExit) {
        JPanel content = new JPanel(new GridLayout(2, 1, 10, 10));

        JLabel title = new JLabel("Game Over", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.RED);

        JLabel scoreLabel = new JLabel("Final Score: " + finalScore, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

        content.add(title);
        content.add(scoreLabel);

        Object[] options = {"Start Again", "Exit"};
        int choice = JOptionPane.showOptionDialog(
                parent, content, "Game Over",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            onStartAgain.run();
        } else {
            onExit.run(); // Exit או סגירת החלון
        }
    }
}
