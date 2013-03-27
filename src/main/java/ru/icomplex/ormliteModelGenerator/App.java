package ru.icomplex.ormliteModelGenerator;

public class App {

    public static void main(String[] args) {
        String dbfile = "export_mobileapp.sqlite3";

        try {
            Generator generator = new Generator(dbfile, "ru.icomplex.gdeUslugi", new ModelGroup("Gu", "GdeUslugi"), "./ignore/");
            generator.generate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
