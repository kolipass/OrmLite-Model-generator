OrmLite-Model-generator
=======================

Генератор моделей, аннотированных для ормлайта.
    Поддерживаемые типы: {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "UNSIGNED BIG INT", "INT2", "INT8"} - преобразуются в Integer или int в зависимости от настроек;
    {"CHARACTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NATIVE CHARACTER", "NVARCHAR", "TEXT", "CLOB"};  - преобразуются в  String;
    {"REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT", "NUMERIC", "DECIMAL"}; - преобразуются в Double или double в зависимости от настроек;
    {"DATETIME","DATE"} -   java.util.Date (Захардкожено. при надобности изменить в TableFields.java);
    {"BOOL","BOOLEAN"} - преобразуются в Boolean или boolean в зависимости от настроек;
    REFERENCES поддерживаются. foreignAutoRefresh по дефолту ставится = true; (Захардкожено. при надобности изменить в TableFields.java);



Генератор DAO каждой модели для ормлайта.

Генератор хэлпера для DAO.

Генератор датасхем моделей для anu-common.
Генератор коллекция моделей для          anu-common.