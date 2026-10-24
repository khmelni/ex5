package team.control;

import db.ExcelDB;
import db.ExcelTable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HallOfFameController {
    private static final String TABLE_NAME = "HallOfFame";
    private static final String[] HEADINGS = {"ID", "PlayerName", "Score", "Date"};
    private static final int SCORE_COLUMN = 2;
    public static final String[] DISPLAY_HEADINGS = {"Player", "Score", "Date"};
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

    public String[][] getTopScores(int n) {
        if (table == null) return new String[0][];
        table.sortTable(SCORE_COLUMN, true);
        String[][] allRows = table.getTableAsMatrix();
        int size = Math.min(n, allRows.length);
        String[][] displayRows = new String[size][3];
        for (int i = 0; i < size; i++) {
            displayRows[i][0] = allRows[i][1];
            displayRows[i][1] = allRows[i][2];
            displayRows[i][2] = allRows[i][3];
        }
        return displayRows;
    }
}