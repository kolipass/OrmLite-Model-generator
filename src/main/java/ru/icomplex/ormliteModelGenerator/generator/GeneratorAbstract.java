package ru.icomplex.ormliteModelGenerator.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * User: artem
 * Date: 28.03.13
 * Time: 10:44
 */
public abstract class GeneratorAbstract implements Generator {
    protected static final String baseFolder = "src/main/";

    protected boolean writeJava(String path, String className, String text) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path + "/" + className + ".java"));
            out.write(text);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected String generateClassPath(String outPath, String classPackage) {
        String classDirectoryPath = classPackage.replaceAll("\\.", "/");

        String path = outPath + baseFolder + "java/" + classDirectoryPath;
        File classDirectory = new File(path);

        return classDirectory.mkdirs() || (classDirectory.exists() && classDirectory.isDirectory()) ? path : "";
    }

    protected String stringListToString(List<String> list) {
        String result = "";

        for (String aList : list) {
            result += aList;
            result += "\r\n";

        }
        return result;
    }
}
