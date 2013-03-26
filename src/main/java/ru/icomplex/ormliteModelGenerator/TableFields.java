package ru.icomplex.ormliteModelGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * User: artem
 * Date: 26.03.13
 * Time: 10:47
 */
public class TableFields {
    String tableName;
    List<FieldModel> fieldModelList = new ArrayList<>();

    public TableFields(String tableName) {
        this.tableName = tableName;
    }

    public void addField(FieldModel model) {
        if (model != null) {
            fieldModelList.add(model);
        }
    }

    public String generate() {
        String result = "public class " + tableName + " extends Model { \r\n";


        //Названия полей таблицы
        for (FieldModel model : fieldModelList) {
            result += "\r\n\t" + getTableFieldName(model);
        }
        result += "\r\n";

        //аннотации и поля
        for (FieldModel model : fieldModelList) {
            result += "\r\n\t" + getAnnotation(model);
            result += "\r\n\t" + getField(model);
        }
        result += "\r\n\n}";

        return result;
    }

    private String getField(FieldModel model) {
        return getType(model, true) + " " + model.getName() + ";";
    }

    private String getTableFieldName(FieldModel model) throws NullPointerException {
        return "public final static String " + model.getName().toUpperCase() + " = \"" + model.getName() + "\";";
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

    private String getFieldDataType(FieldModel model, Boolean isObject) throws ClassCastException {
        String[] integerType = {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "UNSIGNED BIG INT", "INT2", "INT8"};
        String[] textType = {"CHARACTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NATIVE CHARACTER", "NVARCHAR", "TEXT", "CLOB"};
        String[] realType = {"REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT", "NUMERIC", "DECIMAL"};


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

    private String getType(FieldModel model, Boolean isObject) throws ClassCastException {
        String[] integerType = {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "UNSIGNED BIG INT", "INT2", "INT8"};
        String[] textType = {"CHARACTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NATIVE CHARACTER", "NVARCHAR", "TEXT", "CLOB"};
        String[] realType = {"REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT", "NUMERIC", "DECIMAL"};


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
                        type = "Decimal";
                    } else {
                        type = "decimal";
                    }
                    break;
                }
            }
        }

        if (type.isEmpty()) {
            if (model.getType().toUpperCase().equals("DATETIME")) {
                type = "DateTime";
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
}
