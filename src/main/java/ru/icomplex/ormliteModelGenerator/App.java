package ru.icomplex.ormliteModelGenerator;

import ru.icomplex.ormliteModelGenerator.dataDescription.ModelGroup;
import ru.icomplex.ormliteModelGenerator.generator.MainGenerator;

public class App {

    public static void main(String[] args) {
        String dbfile = "export_mobileapp.sqlite3";

        try {
            MainGenerator generator = new MainGenerator(dbfile, "ru.icomplex.gdeUslugi", new ModelGroup("Gu", "GdeUslugi"), "./target/");
            generator.generate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
