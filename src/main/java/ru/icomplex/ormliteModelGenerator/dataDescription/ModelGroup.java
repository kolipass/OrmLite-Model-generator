package ru.icomplex.ormliteModelGenerator.dataDescription;

/**
 * User: artem
 * Date: 26.03.13
 * Time: 10:37
 *
 * Служебная сущьность для описания датасхем вида Gu:GdeUslugi
 */

public class ModelGroup {
    String modelGroup;
    String projectName;

    public ModelGroup(String modelGroup, String projectName) {
        this.modelGroup = modelGroup;
        this.projectName = projectName;
    }

    ModelGroup(String projectName) {
        this.projectName = projectName;
    }

    public String getModelGroup() {
        return modelGroup;
    }

    void setModelGroup(String modelGroup) {
        this.modelGroup = modelGroup;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDataScheme() {
        return modelGroup + ":" + projectName;
    }
}
