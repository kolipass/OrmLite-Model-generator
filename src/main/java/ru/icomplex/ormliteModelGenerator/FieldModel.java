package ru.icomplex.ormliteModelGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: artem
 * Date: 26.03.13
 * Time: 10:24
 * <p/>
 * Модель, описывающая поле таблицы, которое отдает бд при запросе вида:  PRAGMA table_info (tableName)
 */
public class FieldModel {
    /**
     * Номер поля по порядку
     */
    String cid;
    String name;
    String type;
    String notnull;
    String dflt_value;
    /**
     * Примари кей, 1 если так, 0 если нет.
     */
    String pk;

    /**
     * Конструктор модели. Соберет модель из
     * @param tableInfoResult результат выполнения запроса
     * @throws SQLException ошибка доступа к результату запроса
     */

    public FieldModel(ResultSet tableInfoResult) throws SQLException {
        this.setCid(tableInfoResult.getString("cid"));
        this.setName(tableInfoResult.getString("name"));
        this.setType(tableInfoResult.getString("type"));
        this.setNotnull(tableInfoResult.getString("notnull"));
        this.setDflt_value(tableInfoResult.getString("dflt_value"));
        this.setPk(tableInfoResult.getString("pk"));
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getDflt_value() {
        return dflt_value;
    }

    public void setDflt_value(String dflt_value) {
        this.dflt_value = dflt_value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotnull() {
        return notnull;
    }

    public void setNotnull(String notnull) {
        this.notnull = notnull;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FieldModel{" +
                "cid='" + cid + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", notnull='" + notnull + '\'' +
                ", dflt_value='" + dflt_value + '\'' +
                ", pk='" + pk + '\'' +
                '}';
    }
}
