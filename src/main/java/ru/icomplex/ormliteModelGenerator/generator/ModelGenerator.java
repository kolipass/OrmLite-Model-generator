package ru.icomplex.ormliteModelGenerator.generator;

import ru.icomplex.ormliteModelGenerator.dataDescription.FieldModel;
import ru.icomplex.ormliteModelGenerator.dataDescription.TableFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.icomplex.ormliteModelGenerator.dataDescription.TableFields.getFieldDataType;
import static ru.icomplex.ormliteModelGenerator.dataDescription.TableFields.getType;
import static ru.icomplex.ormliteModelGenerator.util.ClassNameUtil.getClassName;
import static ru.icomplex.ormliteModelGenerator.util.ClassNameUtil.getVariableName;

/**
 * User: artem
 * Date: 27.03.13
 * Time: 17:28
 * <p/>
 * Примитивный генератор DAO
 */
public class ModelGenerator extends GeneratorAbstract {
    public static final String POSTFIX = "model";
    private static final boolean foreignAutoRefresh = true;
    private static final String datePackage = "java.util.Date";
    TableFields tableFields;
    String modelClassName;
    List<String> imports = new ArrayList<>();
    String classPath;
    String parentModelClassName;
    String classPackage;

    public ModelGenerator(String classPath, String classPackage, TableFields tableFields, String parentModelClassName) {
        this.classPath = classPath;
        this.classPackage = classPackage;
        this.tableFields = tableFields;
        this.parentModelClassName = parentModelClassName;
        modelClassName = getClassName(tableFields.getTableName());
    }

    public String generateClassBody() {
        String result;

        String packageString = "package " + classPackage + ";\n";

        generateBasicImports();
        imports.add("import " + parentModelClassName + "; \r\n");

        String modelName = parentModelClassName.substring(parentModelClassName.lastIndexOf(".") + 1);

        String classAnnotation = "@DatabaseTable(tableName = \"" + tableFields.getTableName() + "\") \r\n";
        String className = "public class " + getClassName(tableFields.getTableName()) + " extends " + modelName + "{ \r\n";

        String _constructor = getConstructor();
        String _tableFiledName = tableFiledName();
        String _annotations = annotations();
        String _imports = stringListToString(this.imports);

        result = packageString;
        result += _imports;
        result += classAnnotation;
        result += className;
        result += _constructor;
        //Названия полей таблицы
        result += _tableFiledName;
        //аннотации и поля
        result += _annotations;

        result += "\r\n\n}";

        return result;
    }

    private void generateBasicImports() {
        imports.clear();
        imports.add("import com.j256.ormlite.field.DataType;");
        imports.add("import com.j256.ormlite.field.DatabaseField;");
        imports.add("import com.j256.ormlite.table.DatabaseTable;");
    }

    private String getConstructor() {
        String result = "";
        //ToDo Если вдруг понадобится
//        result += "    public " + modelClassName + POSTFIX + "(ConnectionSource connectionSource, Class<" + modelClassName + "> dataClass) throws SQLException {\n" +
//                "        super(connectionSource, dataClass);\n" +
//                "    }";
        return result;
    }

    /**
     * Названия полей таблицы
     */
    private String tableFiledName() {
        String result = "";
        for (FieldModel model : tableFields.getFieldModelList()) {
            result += "\r\n\t" + getTableFieldName(model);
        }
        result += "\r\n";
        return result;
    }

    private String getTableFieldName(FieldModel model) throws NullPointerException {
        return "public final static String " + model.getName().toUpperCase() + " = \"" + model.getName() + "\";";
    }

    /**
     * аннотации и поля
     */
    private String annotations() {
        Map<String, Filed> filedMap = new HashMap<>();

        Pattern pattern = Pattern.compile("(\\w+).*?REFERENCES \"(\\w+)\" \\(\"(\\w+)\"\\)");
        Matcher matcher = pattern.matcher(tableFields.getSql());
        while (matcher.find()) {
            Filed filed = new Filed();

            String filedName = matcher.group(1);
            String referentObjName = matcher.group(2);
//            String referentObjFile = matcher.group(3);

            filed.declaration = getFieldDeclaration(getClassName(referentObjName), getVariableName(referentObjName));
            filed.annotation = getForeignAnnotation(filedName, foreignAutoRefresh);
            filedMap.put(filedName, filed);
        }


        String result = "";
        for (FieldModel model : tableFields.getFieldModelList()) {
            Filed filed = filedMap.get(model.getName());
            if (filed != null) {
                result += "\r\n\t" + filed.annotation;
                result += "\r\n\t" + filed.declaration;
            } else {
                result += "\r\n\t" + getAnnotation(model);
                result += "\r\n\t" + getFieldDeclaration(model);
            }
        }
        return result;
    }

    private String getForeignAnnotation(String fieldName, boolean foreignAutoRefresh) throws NullPointerException {
        String annotation = "@DatabaseField(";
        annotation += "foreign = true, foreignAutoRefresh = " + foreignAutoRefresh + ",";
        annotation += " columnName =" + fieldName.toUpperCase();
        annotation += ")";
        return annotation;

    }

    /**
     * Возвращает тип значения для аннотации
     */

    private String getAnnotation(FieldModel model) throws NullPointerException {
        String annotation = "@DatabaseField(";
        annotation += getFieldDataType(model, true);
        annotation += ", columnName =" + model.getName().toUpperCase();
        if (model.getPk().equals("1")) {
            annotation += ", id = true";
        }
        annotation += ")";
        return annotation;
    }

    /**
     * Возвращает описание модели тип - наименование.
     */
    private String getFieldDeclaration(FieldModel model) {
        String type = getType(model, true);

        if (type.equals("Date")) {
//            TODO Переделать под нормальный вид
            imports.add("import " + datePackage + ";");
        }

        return getFieldDeclaration(type, model.getName());
    }

    private String getFieldDeclaration(String type, String name) {
        return "\t" + type + " " + name + ";";
    }

    @Override
    public boolean generate() throws Exception {
        return writeModel(classPath, modelClassName, generateClassBody());
    }

    private boolean writeModel(String classPath, String className, String generate) {
        return writeJava(classPath, className, generate);
    }
}

/**
 * Сущьность описывает поле класса будующей модели.
 */
class Filed {
    String declaration;
    String annotation;

    @Override
    public String toString() {
        return annotation + '\n' + declaration;
    }
}