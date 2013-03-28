package ru.icomplex.ormliteModelGenerator.generator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.icomplex.ormliteModelGenerator.dataDescription.ModelGroup;
import ru.icomplex.ormliteModelGenerator.dataDescription.TableFields;

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
import java.util.List;

import static ru.icomplex.ormliteModelGenerator.dataDescription.TableFields.upFirstLetter;

/**
 * User: artem
 * Date: 28.03.13
 * Time: 11:15
 */
public class ModelDataSchemeGenerator extends GeneratorAbstract {
    private ModelGroup modelGroup;
    private String outPath;
    private String classPath;
    private List<TableFields> tableFieldsList;

    public ModelDataSchemeGenerator(String classPath, ModelGroup modelGroup, String outPath, List<TableFields> tableFieldsList) {
        this.classPath = classPath;
        this.modelGroup = modelGroup;
        this.outPath = outPath;
        this.tableFieldsList = tableFieldsList;
    }

    @Override
    public boolean generate() throws Exception {
        return generateModelDataScheme(tableFieldsList);
    }
    private boolean generateModelDataScheme(List<TableFields> tableFieldsList) throws TransformerException, ParserConfigurationException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElementDataScheme = doc.createElement("config");
        doc.appendChild(rootElementDataScheme);


        for (TableFields tableFields : tableFieldsList) {
            Element model = doc.createElement("model");
            model.appendChild(doc.createTextNode(modelGroup.getModelGroup() + ":" + upFirstLetter(tableFields.getTableName())));
            try {
                writeModelScheme(tableFields);
                rootElementDataScheme.appendChild(model);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Не удалось записать" + tableFields);
                return false;
            }
        }


        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);

        String directoryPath = outPath + baseFolder + "resources/" + classPath.replaceAll("\\.", "/") + "/dataScheme/";
        String filePath = directoryPath + modelGroup.getProjectName() + "DataScheme.xml";

        File dirFile = new File(directoryPath);
        dirFile.mkdirs();

        StreamResult result = new StreamResult(new File(filePath));

        System.out.println(modelGroup.getProjectName() + "DataScheme.xml" + " создана в папку " + directoryPath);

        transformer.transform(source, result);
        return true;
    }

    private boolean writeModelScheme(TableFields tableFields) throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("config");
        doc.appendChild(rootElement);

        // path elements
        Element dataSource = doc.createElement("dataSource");

        Element modelGroupElement = doc.createElement(classPath + ".dataScheme." + modelGroup.getProjectName() + "Scheme");
        modelGroupElement.appendChild(doc.createTextNode("dataSource"));
        dataSource.appendChild(modelGroupElement);

        rootElement.appendChild(dataSource);


        Element dataScheme = doc.createElement("dataScheme");
        dataScheme.appendChild(doc.createTextNode(modelGroup.getDataScheme()));

        rootElement.appendChild(dataSource);
        Element dataSchemeElement = doc.createElement("dataScheme");
        dataSchemeElement.appendChild(doc.createTextNode(modelGroup.getDataScheme()));
        rootElement.appendChild(dataSchemeElement);


        Element model = doc.createElement("model");
        Element primary = doc.createElement("primary");
        primary.appendChild(doc.createTextNode(tableFields.getPrimaryKeyName()));
        model.appendChild(primary);
        Element secondary = doc.createElement("secondary");
        secondary.appendChild(doc.createTextNode(tableFields.getSecondaryKeyName()));
        model.appendChild(secondary);


        rootElement.appendChild(model);


        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);

        String directoryPath = outPath + baseFolder + "resources/" + classPath.replaceAll("\\.", "/") + "/model/";
        String filePath = directoryPath + upFirstLetter(tableFields.getTableName()) + ".xml";

        File dirFile = new File(directoryPath);
        dirFile.mkdirs();

        StreamResult result = new StreamResult(new File(filePath));

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);

        System.out.println(upFirstLetter(tableFields.getTableName()) + ".xml" + " создана в папку " + directoryPath);
        return true;
    }
}
