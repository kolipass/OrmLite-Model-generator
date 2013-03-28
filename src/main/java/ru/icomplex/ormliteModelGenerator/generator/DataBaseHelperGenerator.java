package ru.icomplex.ormliteModelGenerator.generator;

import java.util.ArrayList;
import java.util.List;

import static ru.icomplex.ormliteModelGenerator.dataDescription.TableFields.lowerFirstLetter;

/**
 * User: artem
 * Date: 28.03.13
 * Time: 13:33
 */
public class DataBaseHelperGenerator extends GeneratorAbstract {
    public static final String HELPER_NAME = "DatabaseHelper";
    public static final String DATABASE_VERSION = "1";
    List<String> modelList = new ArrayList<>();
    List<String> daoList = new ArrayList<>();
    private String classPath;
    private String classPackage;
    private String dbName;

    public DataBaseHelperGenerator(String classPath, String dbName, String classPackage) {
        this.classPath = classPath;
        this.dbName = dbName;
        this.classPackage = classPackage;
    }

    public void addDao(String modelName, String daoName) {
        modelList.add(modelName);
        daoList.add(daoName);
    }

    private boolean writeHelper(String classPath, String generate) {
        return writeJava(classPath, HELPER_NAME, generate);
    }

    private String generateClassBody() {
        String result = "package " + classPackage + ";\n\n";

        result += generateImports();

        result += "public class " + HELPER_NAME + " extends OrmLiteSqliteOpenHelper {";

        result += generateField();

        result += generateConstructor();
        result += generateOnCreate();
        result += generateOnUpdate();

        result += generateSingletons();
        result += generateClose();

        result += "\r\n\n}";

        return result;
    }

    private String generateImports() {
        String result =
                "import android.content.Context;\n" +
                        "import android.database.sqlite.SQLiteDatabase;\n" +
                        "import android.util.Log;\n" +
                        "import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;\n" +
                        "import com.j256.ormlite.support.ConnectionSource;\n" +
                        "import java.sql.SQLException;\n" +
                        "import com.j256.ormlite.table.TableUtils;\n\n";

        result += getDaoImport();
        result += getModelImport();

        return result;
    }

    private String getDaoImport() {
        String result = "";
        for (String daoName : daoList) {
            result += "import " + classPackage + ".DAO." + daoName + ";" + "\n";
        }
        return result;
    }

    private String getModelImport() {
        String result = "";
        for (String modelName : modelList) {
            result += "import " + classPackage + ".model." + modelName + ";" + "\n";
        }
        return result;
    }

    private String generateField() {
        String result = "";

        result += "private static final String TAG = " + HELPER_NAME + ".class.getSimpleName();\n" +
                "private static final String DATABASE_NAME = \"" + dbName + "\";\n" +
                "private static final int DATABASE_VERSION = " + DATABASE_VERSION + ";\n";

        for (String daoName : daoList) {
            result += "private " + daoName + " " + lowerFirstLetter(daoName) + " = null;\n";
        }
        return result;
    }

    private String generateConstructor() {
        return "    public " + HELPER_NAME + "(Context context) {\n" +
                "        super(context, DATABASE_NAME, null, DATABASE_VERSION);\n" +
                "    }";
    }

    private String generateSingletons() {
        String result = "";
        int i = -1;
        for (String daoName : daoList) {
            i++;
            result += "public " + daoName + " get" + daoName + "() throws SQLException {\n" +
                    "        if (" + lowerFirstLetter(daoName) + " == null) {\n" +
                    "            " + lowerFirstLetter(daoName) + " = new " + daoName + "(getConnectionSource(), " + modelList.get(i) + ".class);\n" +
                    "        }\n" +
                    "        return " + lowerFirstLetter(daoName) + ";\n" +
                    "    }\n";
        }
        return result;
    }

    private String generateOnCreate() {
        String result = "@Override\n" +
                "    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {\n" +
                "        try {";

        for (String modelName : modelList) {
            result += "TableUtils.createTable(connectionSource, " + modelName + ".class);\n";
        }

        result += "        } catch (SQLException e) {\n" +
                "            Log.e(TAG, \"error creating DB \" + DATABASE_NAME);\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "    }";
        return result;

    }

    private String generateOnUpdate() {
        String result = "@Override\n" +
                "    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,\n" +
                "                          int newVer) {\n" +
                "        try {";

        for (String modelName : modelList) {
            result += "TableUtils.dropTable(connectionSource, " + modelName + ".class, true);\n";
        }

        result += "  onCreate(db, connectionSource);\n" + "        } catch (SQLException e) {\n" +
                "            Log.e(TAG, \"error upgrading db \" + DATABASE_NAME + \"from ver \" + oldVer);\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "    }";
        return result;
    }

    private String generateClose() {
        String result = "@Override\n" +
                "    public void close() {\n" +
                "        super.close();";

        for (String daoName : daoList) {
            result += lowerFirstLetter(daoName) + "= null;\n";
        }

        result += "    }";
        return result;
    }

    @Override
    public boolean generate() throws Exception {
        return writeHelper(classPath, generateClassBody());
    }
}
