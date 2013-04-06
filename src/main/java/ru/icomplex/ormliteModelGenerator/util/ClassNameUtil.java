package ru.icomplex.ormliteModelGenerator.util;

/**
 * User: artem
 * Date: 06.04.13
 * Time: 13:01
 * <p/>
 * <p/>
 * Классик умеет из строк делать всевозможные названия классов
 */
public class ClassNameUtil {
    static final String OLD_PREFIX = "export_mobileapp_";
    static final String NEW_PREFIX = "Gu";
    static final int TYPE = 0;

    public static String getClassName(String name) {
        switch (TYPE) {
            case 0: {
                name = replacesUnderline(replayPrefix(name, OLD_PREFIX, NEW_PREFIX));
                break;
            }
            case 1: {
                name = replacesUnderline(name);
                break;
            }
        }
        return name;
    }

    public static String getObjectName(String className) {
        switch (TYPE) {
            case 0: {
                className = new LowerFirstLetter(null, className).exec();
            }
        }
        return className;
    }

    public static String replacesUnderline(String s) {
        return new ReplacesUnderline(null, s).exec();
    }

    public static String replayPrefix(String s, String oldPrefix, String newPrefix) {
        return new AddPrefix(new UpFirstLetter(new CutPrefix(null, s, oldPrefix)), newPrefix).exec();
    }


}

abstract class StringDecoratorAbstract {
    protected StringDecoratorAbstract decorator;
    protected String originalString;

    protected StringDecoratorAbstract(StringDecoratorAbstract decorator, String originalString) {
        this.decorator = decorator;
        this.originalString = originalString;
    }

    abstract String decorate(String name);

    public final String exec() {
        if (decorator != null) {
            originalString = decorator.exec();
        }
        return decorate(originalString);
    }
}

/**
 * Вырезает префикс, если он есть
 */
class CutPrefix extends StringDecoratorAbstract {
    private String prefix;

    CutPrefix(StringDecoratorAbstract decorator, String originalString, String prefix) {
        super(decorator, originalString);
        this.prefix = prefix;
    }

    @Override
    String decorate(String name) {
        if (originalString != null && !originalString.isEmpty() && prefix != null && !prefix.isEmpty() && originalString.contains(prefix)) {
            originalString = originalString.substring(originalString.indexOf(prefix) + prefix.length());
        }
        return originalString;
    }
}

/**
 * Добавляет префикс к названию
 */
class AddPrefix extends StringDecoratorAbstract {
    private String prefix;

    AddPrefix(StringDecoratorAbstract decorator, String originalString, String prefix) {
        super(decorator, originalString);
        this.prefix = prefix;
    }

    AddPrefix(StringDecoratorAbstract decorator, String prefix) {
        super(decorator, null);
        this.prefix = prefix;
    }

    @Override
    String decorate(String name) {
        if (originalString != null && !originalString.isEmpty() && prefix != null && !prefix.isEmpty()) {
            originalString = prefix + originalString;
        }
        return originalString;
    }
}

/**
 * Преобразует строку вида Export_mobileapp_offer_points    к виду:   ExportMobileappOfferPoints
 */
class ReplacesUnderline extends StringDecoratorAbstract {

    ReplacesUnderline(StringDecoratorAbstract decorator, String originalString) {
        super(decorator, originalString);
    }

    @Override
    String decorate(String name) {
        if (originalString != null && !originalString.isEmpty()) {
            int underLinePos = originalString.indexOf("_");
            while (underLinePos > -1) {
                originalString = originalString.substring(0, underLinePos) + new UpFirstLetter(null, originalString.substring(underLinePos + 1)).exec();
                underLinePos = originalString.indexOf("_");
            }
        }
        return originalString;
    }
}

class UpFirstLetter extends StringDecoratorAbstract {
    protected UpFirstLetter(StringDecoratorAbstract decorator) {
        super(decorator, null);
    }

    protected UpFirstLetter(StringDecoratorAbstract decorator, String originalString) {
        super(decorator, originalString);
    }

    @Override
    String decorate(String name) {
        char[] stringArray = name.toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        name = new String(stringArray);
        return name;
    }

}

class LowerFirstLetter extends StringDecoratorAbstract {
    protected LowerFirstLetter(StringDecoratorAbstract decorator) {
        super(decorator, null);
    }

    protected LowerFirstLetter(StringDecoratorAbstract decorator, String originalString) {
        super(decorator, originalString);
    }

    @Override
    String decorate(String name) {
        char[] stringArray = name.toCharArray();
        stringArray[0] = Character.toLowerCase(stringArray[0]);
        name = new String(stringArray);
        return name;
    }
}