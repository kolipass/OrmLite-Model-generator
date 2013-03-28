package ru.icomplex.ormliteModelGenerator.generator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.icomplex.ormliteModelGenerator.dataDescription.ModelGroup;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * User: artem
 * Date: 28.03.13
 * Time: 10:38
 */
public class MainDataSchemeGenerator extends GeneratorAbstract {
    private ModelGroup modelGroup;
    private String classPath;
    private String outPath;

    public MainDataSchemeGenerator( String classPath, ModelGroup modelGroup, String outPath) {
        this.classPath = classPath;
        this.modelGroup = modelGroup;
        this.outPath = outPath;
    }

    private boolean generateMainDataScheme() throws TransformerException, ParserConfigurationException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("config");
        doc.appendChild(rootElement);

        // path elements
        Element path = doc.createElement("path");
        rootElement.appendChild(path);

        // shorten way
        // path.setAttribute("id", "1");

        // firstname elements
        Element modelGroupElement = doc.createElement(modelGroup.getModelGroup());
        modelGroupElement.appendChild(doc.createTextNode(classPath + ".dataScheme"));
        path.appendChild(modelGroupElement);

        Element dataSchemeElement = doc.createElement("dataScheme");
        dataSchemeElement.appendChild(doc.createTextNode(modelGroup.getDataScheme()));
        rootElement.appendChild(dataSchemeElement);

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);

        String directoryPath = outPath + baseFolder + "resources.ru.ifacesoft.anu.dataScheme".replaceAll("\\.", "/") + "/";
        String filePath = directoryPath + "DataScheme.xml";

        File dirFile = new File(directoryPath);
        dirFile.mkdirs();

        StreamResult result = new StreamResult(new File(filePath));

        transformer.transform(source, result);
        System.out.println("DataScheme.xml создана в папку " + directoryPath);
        return true;
    }

    @Override
    public boolean generate() throws Exception {
        return generateMainDataScheme();
    }
}
