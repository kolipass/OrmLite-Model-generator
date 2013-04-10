package ru.icomplex.ormliteModelGenerator.dataDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * User: artem
 * Date: 26.03.13
 * Time: 10:47
 * <p/>
 * СУщьность описывает все поля таблицы и умеет генерировать java-код аннотированной модели подобной данной таблице
 */
public class TableFields {
    private static final String[] integerType = {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "UNSIGNED BIG INT", "INT2", "INT8"};
    private static final String[] textType = {"CHARACTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NATIVE CHARACTER", "NVARCHAR", "TEXT", "CLOB"};
    private static final String[] realType = {"REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT", "NUMERIC", "DECIMAL"};
    private static final String[] primarySimpleValue = {"id", "objectid"};
    private static final String[] secondarySimpleValue = {"name", "description", "url"};
    String tableName;
    String sql;
    List<FieldModel> fieldModelList = new ArrayList<>();


    public TableFields(String tableName, String sql) {
        this.tableName = tableName;
        this.sql = sql;
    }

    /**
     * Генератор модели, наследуемой от объекта
     *
     * @param model    собственно набор полей
     * @param isObject использовать примитив(например int) или его аналог объект(например Integer);
     * @return Строка соответствующая типу поля
     * @throws ClassCastException при всех непредвиденных обстоятельствах
     */


    public static String getFieldDataType(FieldModel model, Boolean isObject) throws ClassCastException {
        String type = "";

        for (String anIntegerType : integerType) {
            if (anIntegerType.equals(model.getType().toUpperCase())) {
                type = "INTEGER";

                if (isObject) {
                    type += "_OBJ";
                }

                break;
            }
        }

        if (type.isEmpty()) {
            for (String aTextType : textType) {
                if (model.getType().toUpperCase().contains(aTextType)) {
                    type = "STRING";
                    break;
                }
            }
        }

        if (type.isEmpty()) {
            for (String aRealType : realType) {
                if (model.getType().toUpperCase().contains(aRealType)) {
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
     * @param model    набор полей
     * @param isObject объект ли
     * @return Вернет строку, соответствующую типу
     */
    public static String getType(FieldModel model, Boolean isObject) throws ClassCastException {
        String type = "";

        for (String anIntegerType : integerType) {
            if (anIntegerType.equals(model.getType().toUpperCase())) {

                if (isObject) {
                    type = "Integer";
                } else {
                    type = "int";
                }

                break;
            }
        }

        if (type.isEmpty()) {
            for (String aTextType : textType) {
                if (model.getType().toUpperCase().contains(aTextType)) {
                    type = "String";
                    break;
                }
            }
        }

        if (type.isEmpty()) {
            for (String aRealType : realType) {
                if (model.getType().toUpperCase().contains(aRealType)) {
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

    public String getTableName() {
        return tableName;
    }

    public void addField(FieldModel model) {
        if (model != null) {
            fieldModelList.add(model);
        }
    }

    public FieldModel getPrimaryKeyFieldModel() throws RuntimeException {
        FieldModel primary = null;

        for (FieldModel model : fieldModelList) {

            for (String aPrimarySimpleValue : primarySimpleValue) {
                if (aPrimarySimpleValue.equals(model.getName())) {
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

            for (String aPrimarySimpleValue : primarySimpleValue) {
                if (aPrimarySimpleValue.equals(model.getName())) {
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

            for (String aSecondarySimpleValue : secondarySimpleValue) {
                if (aSecondarySimpleValue.equals(model.getName())) {
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

    public List<FieldModel> getFieldModelList() {
        return fieldModelList;
    }

    public String getSql() {
        return sql;
    }
}



