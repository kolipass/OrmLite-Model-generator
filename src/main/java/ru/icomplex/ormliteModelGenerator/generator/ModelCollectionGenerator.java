package ru.icomplex.ormliteModelGenerator.generator;

import ru.icomplex.ormliteModelGenerator.dataDescription.TableFields;

import static ru.icomplex.ormliteModelGenerator.util.ClassNameUtil.getClassName;

/**
 * User: artem
 * Date: 27.03.13
 * Time: 17:28
 *
 * Генерирует Коллекцию моделей для anu-common
 */
public class ModelCollectionGenerator extends GeneratorAbstract {
    String classPath;
    TableFields tableFields;
    String modelClassName;
    private String classPackage;
    private static final String POSTFIX = "Collection";


    public ModelCollectionGenerator(String classPackage, String classPath, TableFields tableFields) {
        this.classPackage = classPackage;
        this.classPath = classPath;
        this.tableFields = tableFields;
        modelClassName = getClassName(tableFields.getTableName());
    }

    public String generateClassBody() {
        String result = "package " + classPackage + ";\n";

        result += "import ru.ifacesoft.anu.modelCollection.ModelCollection;\n";
        result += getImportModel(modelClassName, classPackage) + "\n\n";

        String extendsString = "<M extends "+modelClassName+"> extends ModelCollection<M>";
        result += "public class " + modelClassName + POSTFIX + " " + extendsString + "{ \r\n";


        result += getConstructor() + "\n\n";
        //аннотации и поля

        result += "\r\n\n}";

        return result;
    }

    private String getImportModel(String modelClassName, String classPackage) {
        return "import " + classPackage.substring(0, classPackage.lastIndexOf(".")) + ".model." + modelClassName + ";";
    }



    private String getConstructor() {
        String result = "";
//        ToDo Если вдруг когда-нибудь...
//        result += "    public " + modelClassName + POSTFIX) {\n" +
//                "        super();\n" +
//                "    }";
        return result;
    }

    @Override
    public boolean generate() throws Exception {
        return writeDao(classPath, modelClassName, generateClassBody());
    }

    private boolean writeDao(String classPath, String className, String generate) {
        return writeJava(classPath, className + POSTFIX, generate);
    }
}
