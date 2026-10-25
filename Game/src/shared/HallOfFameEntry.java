package shared;

/**
 * שורה אחת בטבלת ה-Hall of Fame: שם השחקן, הניקוד הסופי ותאריך המשחק.
 *
 * ה-Backend יוצר אותה (מהנתונים השמורים) ושולח ל-UI דרך ה-Router, וה-UI רק מציג אותה.
 * היא ב-shared כדי שה-UI לא יצטרך להכיר מחלקות של ה-Backend (כמו HallOfFameController),
 * וכך החוזה בין שני הצדדים מוגדר במקום אחד. אובייקט בלתי-משתנה.
 */
public final class HallOfFameEntry {

    private final String playerName;
    private final int score;
    private final String date;

    public HallOfFameEntry(String playerName, int score, String date) {
        this.playerName = playerName;
        this.score = score;
        this.date = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    /** תאריך המשחק כפי ששמור בטבלה (dd/MM/yyyy HH:mm). */
    public String getDate() {
        return date;
    }
}
