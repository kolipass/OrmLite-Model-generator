package ru.icomplex.ormliteModelGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * User: artem
 * Date: 26.03.13
 * Time: 10:37
 */
public class Generator {
    String dbfile;
    String outPath;

    public Generator(String dbfile, String outPath) {
        this.dbfile = dbfile;
        this.outPath = outPath;
    }

    public void generate() throws Exception {
        Class.forName("org.sqlite.JDBC");

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbfile);

        PreparedStatement statementTables = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type = \"table\"");
        ResultSet tablesResult = statementTables.executeQuery();
        while (tablesResult.next()) {
            String tableName = tablesResult.getString(1);

            PreparedStatement statementTableInfo = connection.prepareStatement("PRAGMA table_info(" + tableName + ");");
            ResultSet tableInfoResult = statementTableInfo.executeQuery();

            TableFields tableFields = new TableFields(tableName);

            while (tableInfoResult.next()) {
                FieldModel model = new FieldModel(tableInfoResult);

                tableFields.addField(model);
            }
            System.out.println(" --------- " + tableName + " --------- ");
            System.out.println(tableFields.generate());

            tableInfoResult.close();
            statementTableInfo.close();
        }
    }
}
