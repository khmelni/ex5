package ai.ui.panels;

import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** שורת הסטטוס התחתונה. */
public class StatusBarPanel extends JLabel {

    public StatusBarPanel() {
        super("", SwingConstants.CENTER);
        setFont(new Font("Arial", Font.ITALIC, 14));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void setStatus(String text) {
        setText(text);
    }
}
