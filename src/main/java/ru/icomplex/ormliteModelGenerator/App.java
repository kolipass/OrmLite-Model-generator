package ru.icomplex.ormliteModelGenerator;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String dbfile = "export_mobileapp.sqlite3";

        try {
            Generator generator = new Generator(dbfile, "");
            generator.generate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
