package ru.icomplex.ormliteModelGenerator.dataDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.icomplex.ormliteModelGenerator.util.ClassNameUtil.getClassName;
import static ru.icomplex.ormliteModelGenerator.util.ClassNameUtil.getVariableName;

/**
 * User: artem
 * Date: 26.03.13
 * Time: 10:47
 */
public class TableFields {
    private static final String[] integerType = {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "UNSIGNED BIG INT", "INT2", "INT8"};
    private static final String[] textType = {"CHARACTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NATIVE CHARACTER", "NVARCHAR", "TEXT", "CLOB"};
    private static final String[] realType = {"REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT", "NUMERIC", "DECIMAL"};
    private static final String[] primarySimpleValue = {"id", "objectid"};
    private static final String[] secondarySimpleValue = {"name", "description", "url"};
    private static final boolean foreignAutoRefresh = true;
    private static final String datePackage = "java.util.Date";
    String tableName;
    String sql;
    List<FieldModel> fieldModelList = new ArrayList<>();
    List<String> imports = new ArrayList<String>();

    public TableFields(String tableName, String sql) {
        this.tableName = tableName;
        this.sql = sql;
    }

    public String getTableName() {
        return tableName;
    }

    public void addField(FieldModel model) {
        if (model != null) {
            fieldModelList.add(model);
        }
    }

//    public String generate(String classPath) {
//        String result = "package " + classPath + ".ru.icomplex.gdeUslugi.dataDescription;\n";
//        result += "public class " + getClassName(tableName) + " { \r\n";
//
//        //Названия полей таблицы
//        result += tableFiledName();
//        //аннотации и поля
//        result += annotations();
//        result += "\r\n\n}";
//
//        return result;
//    }

    /**
     * Генератор модели, наследуемой от объекта
     *
     * @param classPath            package для данной модели
     * @param parentModelClassName package+название класса-родителя
     * @return
     */
    public String generate(String classPath, String parentModelClassName) {
        String result;

        String packageString = "package " + classPath + ";\n";

        generateBasicImports();
        imports.add("import " + parentModelClassName + "; \r\n");

        String modelName = parentModelClassName.substring(parentModelClassName.lastIndexOf(".") + 1);

        String classAnnotation = "@DatabaseTable(tableName = \"" + tableName + "\") \r\n";
        String className = "public class " + getClassName(tableName) + " extends " + modelName + "{ \r\n";


        result = packageString;
        result += stringListToString(imports);
        result += classAnnotation;
        result += className;
        //Названия полей таблицы
        result += tableFiledName();
        //аннотации и поля
        result += annotations();

        result += "\r\n\n}";

        return result;
    }

    /**
     * Названия полей таблицы
     *
     * @return
     */
    private String tableFiledName() {
        String result = "";
        for (FieldModel model : fieldModelList) {
            result += "\r\n\t" + getTableFieldName(model);
        }
        result += "\r\n";
        return result;
    }

    private String getTableFieldName(FieldModel model) throws NullPointerException {
        return "public final static String " + model.getName().toUpperCase() + " = \"" + model.getName() + "\";";
    }

    private void generateBasicImports() {
        imports.clear();
        imports.add("import com.j256.ormlite.field.DataType;");
        imports.add("import com.j256.ormlite.field.DatabaseField;");
        imports.add("import com.j256.ormlite.table.DatabaseTable;");
    }

    private String stringListToString(List<String> list) {
        String result = "";

        for (int i = 0; i < list.size(); i++) {
            result += list.get(i);
            result += "\r\n";

        }
        return result;
    }

    /**
     * аннотации и поля
     *
     * @return
     */
    private String annotations() {
        Map<String, Filed> filedMap = new HashMap<>();

        Pattern pattern = Pattern.compile("(\\w+).*?REFERENCES \"(\\w+)\" \\(\"(\\w+)\"\\)");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            Filed filed = new Filed();

            String filedName = matcher.group(1);
            String referentObjName = matcher.group(2);
            String referentObjFile = matcher.group(3);

            filed.declaration = getFieldDeclaration(getClassName(referentObjName), getVariableName(referentObjName));
            filed.annotation = getForeignAnnotation(filedName, foreignAutoRefresh);
            filedMap.put(filedName, filed);
        }


        String result = "";
        for (FieldModel model : fieldModelList) {
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
     *
     * @param model
     * @return
     */
    private String getFieldDeclaration(FieldModel model) {
        String type = getType(model, true);

        if (type.equals("DATE")) {
            imports.add("\nimport " + datePackage + ";\n");
        }

        return getFieldDeclaration(type, model.getName());
    }

    private String getFieldDeclaration(String type, String name) {
        return "\t" + type + " " + name + ";";
    }

    /**
     * Возвращает тип значения для аннотации
     *
     * @param model
     * @return
     */

    private String getFieldDataType(FieldModel model, Boolean isObject) throws ClassCastException {
        String type = "";

        for (int i = 0; i < integerType.length; i++) {
            if (integerType[i].equals(model.getType().toUpperCase())) {
                type = "INTEGER";

                if (isObject) {
                    type += "_OBJ";
                }

                break;
            }
        }

        if (type.isEmpty()) {
            for (int i = 0; i < textType.length; i++) {
                if (model.getType().toUpperCase().contains(textType[i])) {
                    type = "STRING";
                    break;
                }
            }
        }

        if (type.isEmpty()) {
            for (int i = 0; i < realType.length; i++) {
                if (model.getType().toUpperCase().contains(realType[i])) {
                    type = "DOUBLE";

                    if (isObject) {
                        type += "_OBJ";
                    }
                    break;
                }
            }
        }

        if (type.isEmpty()) {
            if (model.getType().toUpperCase().equals("BOOL") || model.getType().toUpperCase().equals("BOOLEAN")) {
                type = "BOOLEAN";

                if (isObject) {
                    type += "_OBJ";

                }
            }
        }

        if (type.isEmpty()) {
            if (model.getType().toUpperCase().equals("DATETIME")) {
                type = "DATE_TIME";
            }
        }
        if (type.isEmpty()) {
            if (model.getType().toUpperCase().equals("DATE")) {
                type = "DATE";
            }
        }

        String result;
        if (!type.isEmpty()) {
            result = "dataType = DataType.";

            result += type;
        } else {
            throw (new ClassCastException("Не найдено аннотации для типа: " + model.getType().toUpperCase()));
        }

        return result;
    }

    /**
     * Возвращает тип значения для поля
     * Поддерживаемые типы: {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "UNSIGNED BIG INT", "INT2", "INT8"} - преобразуются в Integer или int в зависимости от настроек
     * {"CHARACTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NATIVE CHARACTER", "NVARCHAR", "TEXT", "CLOB"};  - преобразуются в  String
     * {"REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT", "NUMERIC", "DECIMAL"}; - преобразуются в Double или double в зависимости от настроек
     * {"DATETIME","DATE"} -   Date
     * {"BOOL","BOOLEAN"} - преобразуются в Boolean или boolean в зависимости от настроек
     *
     * @param model
     * @return
     */
    public String getType(FieldModel model, Boolean isObject) throws ClassCastException {
        String type = "";

        for (int i = 0; i < integerType.length; i++) {
            if (integerType[i].equals(model.getType().toUpperCase())) {

                if (isObject) {
                    type = "Integer";
                } else {
                    type = "int";
                }

                break;
            }
        }

        if (type.isEmpty()) {
            for (int i = 0; i < textType.length; i++) {
                if (model.getType().toUpperCase().contains(textType[i])) {
                    type = "String";
                    break;
                }
            }
        }

        if (type.isEmpty()) {
            for (int i = 0; i < realType.length; i++) {
                if (model.getType().toUpperCase().contains(realType[i])) {
                    if (isObject) {
                        type = "Double";
                    } else {
                        type = "double";
                    }
                    break;
                }
            }
        }

        if (type.isEmpty()) {
            if (model.getType().toUpperCase().equals("DATETIME")) {
                type = "Date";
            }
        }
        if (type.isEmpty()) {
            if (model.getType().toUpperCase().equals("DATE")) {
                type = "Date";
            }
        }

        if (type.isEmpty()) {
            if (model.getType().toUpperCase().equals("BOOL") || model.getType().toUpperCase().equals("BOOLEAN")) {
                if (isObject) {
                    type = "Boolean";
                } else {
                    type = "boolean";
                }
            }
        }

        String result;
        if (!type.isEmpty()) {
            result = type;
        } else {
            throw (new ClassCastException("Не найдено аннотации для типа: " + model.getType().toUpperCase()));
        }


        return result;
    }

    public FieldModel getPrimaryKeyFieldModel() throws RuntimeException {
        FieldModel primary = null;

        for (FieldModel model : fieldModelList) {

            for (int i = 0; i < primarySimpleValue.length; i++) {
                if (primarySimpleValue[i].equals(model.getName())) {
                    primary = model;
                    break;

                }
            }
            if (!(primary == null)) {
                break;
            }
        }
        if (primary == null) {
            throw new RuntimeException("Первичный ключ для таблицы " + tableName + " не найден");
        }
        return primary;
    }

    /**
     * Получить строку-наименование поля первичного ключа. Получение по словарю primarySimpleValue
     *
     * @return строка-наименование поля
     * @throws RuntimeException
     */

    public String getPrimaryKeyName() throws RuntimeException {
        String primary = "";

        for (FieldModel model : fieldModelList) {

            for (int i = 0; i < primarySimpleValue.length; i++) {
                if (primarySimpleValue[i].equals(model.getName())) {
                    primary = model.getName();
                    break;

                }
            }
            if (!primary.isEmpty()) {
                break;
            }
        }
        if (primary.isEmpty()) {
            throw new RuntimeException("Первичный ключ для таблицы " + tableName + " не найден");
        }
        return primary;
    }

    /**
     * Получить строку-наименование поля вторичного ключа. Получение по словарю secondarySimpleValue. Если в словаре нет, то вернется первичный ключ
     *
     * @return строка-наименование поля
     * @throws RuntimeException
     */
    public String getSecondaryKeyName() {
        String secondary = "";

        for (FieldModel model : fieldModelList) {

            for (int i = 0; i < secondarySimpleValue.length; i++) {
                if (secondarySimpleValue[i].equals(model.getName())) {
                    secondary = model.getName();
                    break;

                }
            }
            if (!secondary.isEmpty()) {
                break;
            }
        }
        if (secondary.isEmpty()) {
            secondary = getPrimaryKeyName();
            System.out.println("Вторичный ключ для таблицы " + tableName + " не найден, записан аналогичный первичному: " + secondary);
        }
        return secondary;
    }

    public String getFieldType(String fieldName) {
        FieldModel model = null;
        for (FieldModel fieldModel : fieldModelList) {
            if (fieldModel.getName().equals(fieldName)) {
                model = fieldModel;
                break;
            }
        }

        if (model != null) {
            return getFieldDataType(model, true);
        }

        return null;
    }

}

class Filed {
    String declaration;
    String annotation;

    @Override
    public String toString() {
        return annotation + '\n' + declaration;
    }
}


