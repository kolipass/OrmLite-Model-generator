package ru.icomplex.ormliteModelGenerator.generator;

import ru.icomplex.ormliteModelGenerator.dataDescription.TableFields;

import static ru.icomplex.ormliteModelGenerator.dataDescription.TableFields.upFirstLetter;

/**
 * User: artem
 * Date: 27.03.13
 * Time: 17:28
 */
public class DaoGenerator extends GeneratorAbstract {
    String classPath;
    TableFields tableFields;
    String modelClassName;
    private String classPackage;


    public DaoGenerator(String classPackage, String classPath, TableFields tableFields) {
        this.classPackage = classPackage;
        this.classPath = classPath;
        this.tableFields = tableFields;
        modelClassName = upFirstLetter(tableFields.getTableName());
    }

    public String generateClassBody() {
        String result = "package " + classPackage + ";\n";

        result += "import com.j256.ormlite.dao.BaseDaoImpl;\n" +
                "import com.j256.ormlite.support.ConnectionSource;\n" +
                "import java.sql.SQLException;\n" +
                "import java.util.List;\n";
        result += getImportModel(modelClassName, classPackage) + "\n\n";

        String extendsString = "extends BaseDaoImpl<" + modelClassName + "," + tableFields.getType(tableFields.getPrimaryKeyFieldModel(), true) + ">";
        result += "public class " + modelClassName + "DAO" + " " + extendsString + "{ \r\n";

        //Названия полей таблицы
        result += getConstructor() + "\n\n";
        //аннотации и поля
        result += getList();

        result += "\r\n\n}";

        return result;
    }

    private String getImportModel(String modelClassName, String classPackage) {
        return "import " + classPackage.substring(0, classPackage.lastIndexOf(".")) + ".model." + modelClassName + ";";
    }

    private String getList() {
        String result = "";
        result += "  public List<" + modelClassName + "> get" + modelClassName + "List() throws SQLException {\n" +
                "        return this.queryForAll();\n" +
                "    }";
        return result;

    }

    private String getConstructor() {
        String result = "";
        result += "    public " + modelClassName + "DAO(ConnectionSource connectionSource, Class<" + modelClassName + "> dataClass) throws SQLException {\n" +
                "        super(connectionSource, dataClass);\n" +
                "    }";
        return result;
    }

    @Override
    public boolean generate() throws Exception {
        return writeDao(classPath, modelClassName, generateClassBody());
    }

    private boolean writeDao(String classPath, String className, String generate) {
        return writeJava(classPath, className + "DAO", generate);
    }
}
