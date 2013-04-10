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

import static ru.icomplex.ormliteModelGenerator.util.ClassNameUtil.getClassName;

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

        PreparedStatement statementTables = connection.prepareStatement("SELECT name,sql FROM sqlite_master WHERE type = \"table\"");
        ResultSet tablesResult = statementTables.executeQuery();
        List<TableFields> tableFieldsList = new ArrayList<>();

        DataBaseHelperGenerator baseHelperGenerator = new DataBaseHelperGenerator(generateClassPath(outPath, classPackage), dbfile, classPackage);


        while (tablesResult.next()) {
            String tableName = tablesResult.getString(1);
            String sql = tablesResult.getString(2);

            PreparedStatement statementTableInfo = connection.prepareStatement("PRAGMA table_info(\"" + tableName + "\");");
            ResultSet tableInfoResult = statementTableInfo.executeQuery();

            TableFields tableFields = new TableFields(tableName, sql);

            while (tableInfoResult.next()) {
                FieldModel model = new FieldModel(tableInfoResult);

                tableFields.addField(model);
            }


            //Генерируем модель
            String modelClassPackage = classPackage + "." + ModelGenerator.POSTFIX;
            String modelPath = generateClassPath(outPath, modelClassPackage);
            if (!modelPath.isEmpty()) {
                if (new ModelGenerator(modelPath, modelClassPackage, tableFields, "ru.ifacesoft.anu.model.Model").generate()) {
                    System.out.println(tableName + " модель создана в папку " + modelPath);
                } else {
                    System.out.println("Не удалось записать" + tableName + "в папку " + modelPath);
                }
                String daoClassPackage = classPackage + "." + DaoGenerator.POSTFIX;
                String daoPath = generateClassPath(outPath, daoClassPackage);
//                            Генерируем дао
                if (new DaoGenerator(daoClassPackage, daoPath, tableFields).generate()) {
                    String modelName = getClassName(tableName);
                    String daoName = modelName + DaoGenerator.POSTFIX;

                    baseHelperGenerator.addDao(modelName, daoName);

                    System.out.println(tableName + " " + DaoGenerator.POSTFIX + " создана в папку " + daoPath);
                } else {
                    System.out.println("Не удалось записать " + tableName + DaoGenerator.POSTFIX + " в папку " + daoPath);
                }

                String modelCollectionClassPackage = classPackage + ".modelCollection";
                String modelCollectionPath = generateClassPath(outPath, modelCollectionClassPackage);

                if (new ModelCollectionGenerator(modelCollectionClassPackage, modelCollectionPath, tableFields).generate()) {
                    System.out.println(tableName + " " + ModelCollectionGenerator.POSTFIX + " создана в папку " + daoPath);
                } else {
                    System.out.println("Не удалось записать " + tableName + ModelCollectionGenerator.POSTFIX + " в папку " + daoPath);
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