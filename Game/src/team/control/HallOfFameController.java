package team.control;

import db.ExcelDB;
import db.ExcelTable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import shared.HallOfFameEntry;

public class HallOfFameController {
    private static final String TABLE_NAME = "HallOfFame";
    private static final String[] HEADINGS = {"ID", "PlayerName", "Score", "Date"};
    private static final int SCORE_COLUMN = 2;
    private static final String FILE_PATH = "db_tables\\" + TABLE_NAME + ".xlsx";

    private ExcelTable table;

    public HallOfFameController() {
        ExcelDB db = ExcelDB.getInstance();
        if (new File(FILE_PATH).exists()) {
            this.table = db.createTableFromExcel(TABLE_NAME);
        } else {
            try {
                this.table = db.createNewTable(TABLE_NAME, HEADINGS);
            } catch (Exception e) {
                e.printStackTrace();
                this.table = null;
            }
        }
    }

    public void addScore(String playerName, int score) {
        if (table == null) return;
        try {
            String id = playerName + "_" + System.currentTimeMillis();
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
            table.insertRow(new String[]{id, playerName, String.valueOf(score), date});
            ExcelDB.getInstance().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** n הציונים הגבוהים ביותר, מהגבוה לנמוך. */
    public HallOfFameEntry[] getTopScores(int n) {
        // טבלה ריקה (הפעלה ראשונה): אין מה למיין, ו-sortTable קורס על טבלה בלי שורות
        if (table == null || table.getTableAsMatrix().length == 0) return new HallOfFameEntry[0];
        table.sortTable(SCORE_COLUMN, true);
        String[][] allRows = table.getTableAsMatrix();
        int size = Math.min(n, allRows.length);
        HallOfFameEntry[] entries = new HallOfFameEntry[size];
        for (int i = 0; i < size; i++) {
            // עמודות הטבלה השמורה: ID, PlayerName, Score, Date
            entries[i] = new HallOfFameEntry(allRows[i][1], parseScore(allRows[i][2]), allRows[i][3]);
        }
        return entries;
    }

    private static int parseScore(String text) {
        try {
            return (int) Double.parseDouble(text.trim());
        } catch (Exception e) {
            return 0; // ערך פגום בטבלה - לא מפילים את התצוגה
        }
    }
}