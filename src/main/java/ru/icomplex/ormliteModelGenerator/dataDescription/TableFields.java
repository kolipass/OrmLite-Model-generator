package ru.icomplex.ormliteModelGenerator.dataDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * User: artem
 * Date: 26.03.13
 * Time: 10:47
 */
public class TableFields {
    static String[] integerType = {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "UNSIGNED BIG INT", "INT2", "INT8"};
    static String[] textType = {"CHARACTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NATIVE CHARACTER", "NVARCHAR", "TEXT", "CLOB"};
    static String[] realType = {"REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT", "NUMERIC", "DECIMAL"};
    static String[] primarySimpleValue = {"id", "objectid"};
    static String[] secondarySimpleValue = {"name", "description", "url"};
    String tableName;
    List<FieldModel> fieldModelList = new ArrayList<>();

    public TableFields(String tableName) {
        this.tableName = tableName;
    }

    public static String upFirstLetter(String s) {
        char[] stringArray = s.toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        s = new String(stringArray);
        return s;
    }

    public String getTableName() {
        return tableName;
    }

    public void addField(FieldModel model) {
        if (model != null) {
            fieldModelList.add(model);
        }
    }

    public String generate(String classPath) {
        String result = "package " + classPath + ".ru.icomplex.gdeUslugi.dataDescription;\n";
        result += "public class " + upFirstLetter(tableName) + " { \r\n";

        //Названия полей таблицы
        result += tableFiledName();
        //аннотации и поля
        result += annotations();
        result += "\r\n\n}";

        return result;
    }

    /**
     * Генератор модели, наследуемой от объекта
     *
     * @param classPath            package для данной модели
     * @param parentModelClassName package+название класса-родителя
     * @return
     */
    public String generate(String classPath, String parentModelClassName) {
        String result = "package " + classPath + ";\n";
        result += "import " + parentModelClassName + "; \r\n";
        result += "import com.j256.ormlite.field.DataType;\r\n" +
                "import com.j256.ormlite.field.DatabaseField;\r\n" +
                "import com.j256.ormlite.table.DatabaseTable;" +
                "\r\n";
        String modelName = parentModelClassName.substring(parentModelClassName.lastIndexOf(".") + 1);
        result += "@DatabaseTable(tableName = \"" + tableName + "\") \r\n";
        result += "public class " + upFirstLetter(tableName) + " extends " + modelName + "{ \r\n";

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

    /**
     * аннотации и поля
     *
     * @return
     */
    private String annotations() {
        String result = "";
        for (FieldModel model : fieldModelList) {
            result += "\r\n\t" + getAnnotation(model);
            result += "\r\n\t" + getField(model);
        }
        return result;
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
    private String getField(FieldModel model) {
        return getType(model, true) + " " + model.getName() + ";";
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
