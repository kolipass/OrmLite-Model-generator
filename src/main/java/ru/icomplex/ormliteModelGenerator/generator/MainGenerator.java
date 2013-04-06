package ru.icomplex.ormliteModelGenerator.generator;

import ru.icomplex.ormliteModelGenerator.dataDescription.FieldModel;
import ru.icomplex.ormliteModelGenerator.dataDescription.ModelGroup;
import ru.icomplex.ormliteModelGenerator.dataDescription.TableFields;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static ru.icomplex.ormliteModelGenerator.util.ClassNameUtil.*;

public class MainGenerator extends GeneratorAbstract {
    String dbfile;
    String outPath;
    String classPackage;
    ModelGroup modelGroup;

    public MainGenerator(String dbfile, String classPath, ModelGroup modelGroup, String outPath) {
        this.classPackage = classPath;
        this.modelGroup = modelGroup;
        this.dbfile = dbfile;
        this.modelGroup = modelGroup;
        this.outPath = outPath;
    }

    /**
     * Мультикомбайн - сгенирирует классики,  дата схемы ну и dao
     * о проблемах расскажет в лог
     *
     * @throws Exception
     */

    public boolean generate() throws Exception {
        Class.forName("org.sqlite.JDBC");

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbfile);

        PreparedStatement statementTables = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type = \"table\"");
        ResultSet tablesResult = statementTables.executeQuery();
        List<TableFields> tableFieldsList = new ArrayList<>();

        DataBaseHelperGenerator baseHelperGenerator = new DataBaseHelperGenerator(generateClassPath(outPath, classPackage), dbfile, classPackage);


        while (tablesResult.next()) {
            String tableName = tablesResult.getString(1);

            PreparedStatement statementTableInfo = connection.prepareStatement("PRAGMA table_info(" + tableName + ");");
            ResultSet tableInfoResult = statementTableInfo.executeQuery();

            TableFields tableFields = new TableFields(tableName);

            while (tableInfoResult.next()) {
                FieldModel model = new FieldModel(tableInfoResult);

                tableFields.addField(model);
            }

            String modelClassPackage = classPackage + ".model";
            String modelPath = generateClassPath(outPath, modelClassPackage);
            if (!modelPath.isEmpty()) {
                if (writeJava(modelPath, getClassName(tableName), tableFields.generate(modelClassPackage, " ru.ifacesoft.anu.model.Model"))) {
                    System.out.println(tableName + " модель создана в папку " + modelPath);
                } else {
                    System.out.println("Не удалось записать" + tableName + "в папку " + modelPath);
                }
                String daoClassPackage = classPackage + ".DAO";
                String daoPath = generateClassPath(outPath, daoClassPackage);

                if (new DaoGenerator(daoClassPackage, daoPath, tableFields).generate()) {
                    String modelName = getClassName(tableName);
                    String daoName = modelName + "DAO";

                    baseHelperGenerator.addDao(modelName, daoName);

                    System.out.println(tableName + " DAO создана в папку " + daoPath);
                } else {
                    System.out.println("Не удалось записать " + tableName + "DAO в папку " + daoPath);
                }

            } else {
                System.out.println(" modelClassPath пуст, модель " + tableName + " не записана");
            }

            tableFieldsList.add(tableFields);

            tableInfoResult.close();
            statementTableInfo.close();
        }

        if (baseHelperGenerator.generate()) {
            System.out.println(" baseHelperGenerator создан в " + classPackage);
        } else {
            System.out.println(" baseHelperGenerator не создан");
        }

        return (new MainDataSchemeGenerator(classPackage, modelGroup, outPath).generate() &&
                new ModelDataSchemeGenerator(classPackage, modelGroup, outPath, tableFieldsList).generate());
    }
}