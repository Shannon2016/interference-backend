package com.example.demo.service;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TDBPersistence {

    public Dataset dataset = null;

    /**
     * 建立TDB数据文件夹；
     */
    public TDBPersistence(String tdbName) {
        dataset = TDBFactory.createDataset(tdbName);
    }

    /**
     * 将rdf文件加载到model中；
     */
    public void loadModel(String modelName, String rdfFilePath, Boolean isOverride) {

        int result;
        Model model = null;
        dataset.begin(ReadWrite.WRITE);
        try {
            //已有同名model，且不需要使用新的三元组覆盖旧TDB文件；
            if (dataset.containsNamedModel(modelName) && (!isOverride)) {
                result = 1;
            }
            //没有同名model，或者有同名文件需要覆盖；
            else {
                if (dataset.containsNamedModel(modelName))
                    result = 2;
                else
                    result = 3;
                //移除已有的model；
                dataset.removeNamedModel(modelName);
                //建立一个新的TDB Model，一个TDB可以有多个model，类似数据库的多个表；
                model = dataset.getNamedModel(modelName);
                //事务开始；
                model.begin();
                //读取RDF文件到model中；
                FileManager.get().readModel(model, rdfFilePath);
                //将事务提交；
                model.commit();
                //务必记得将dataset的事务提交，否则无法完成增删改查操作；
                dataset.commit();
            }
        } catch (Exception e) {
            result = 0;
        } finally {
            if (model != null && !model.isEmpty())
                model.close();
            dataset.end();
        }
        switch (result) {
            case 0:
                System.out.println(modelName + "：读取model错误！");
                break;
            case 1:
                System.out.println(modelName + "：已有该model，不需要覆盖！");
                break;
            case 2:
                System.out.println(modelName + "：已有该model，覆盖原TDB文件，并建立新的model！");
                break;
            case 3:
                System.out.println(modelName + "：建立新的TDB model！");
                break;
        }
    }

    /**
     * 删除Dataset中的某个model；
     */
    public void removeModel(String modelName) {
        if (!dataset.isInTransaction())
            dataset.begin(ReadWrite.WRITE);
        try {
            dataset.removeNamedModel(modelName);
            dataset.commit();
            System.out.println(modelName + "：已被移除!");
        } finally {
            dataset.end();
        }
    }

    /**
     * 关闭TDB连接；
     */
    public void closeTDB() {
        dataset.close();
    }

    /**
     * 判断Dataset中是否存在model；
     */
    public boolean findTDB(String modelName) {
        boolean result;
        dataset.begin(ReadWrite.READ);
        try {
            if (dataset.containsNamedModel(modelName))
                result = true;
            else
                result = false;
        } finally {
            dataset.end();
        }
        return result;
    }

    /**
     * 列出Dataset中所有model；
     */
    public List<String> listModels() {
        dataset.begin(ReadWrite.READ);
        List<String> uriList = new ArrayList<>();
        try {
            Iterator<String> names = dataset.listNames();
            String name;
            while (names.hasNext()) {
                name = names.next();
                uriList.add(name);
            }
        } finally {
            dataset.end();
        }
        return uriList;
    }

    /**
     * 获得Dataset中某个model；
     */
    public Model getModel(String modelName) {

        Model model;
        dataset.begin(ReadWrite.READ);
        try {
            model = dataset.getNamedModel(modelName);
        } finally {
            dataset.end();
        }
        return model;
    }

    /**
     * 获取默认模型；
     */
    public Model getDefaultModel() {
        dataset.begin(ReadWrite.READ);
        Model model;
        try {
            model = dataset.getDefaultModel();
            dataset.commit();
        } finally {
            dataset.end();
        }
        return model;
    }

    /**
     * 查询Model中三元组；
     */
    public List<Statement> getTriplet(String modelName, String subject, String predicate, String object) {
        List<Statement> results = new ArrayList<>();

        Model model = null;

        dataset.begin(ReadWrite.READ);
        try {
            model = dataset.getNamedModel(modelName);

            Selector selector = new SimpleSelector(
                    (subject != null) ? model.createResource(subject) : null,
                    (predicate != null) ? model.createProperty(predicate) : null,
                    (object != null) ? model.createResource(object) : null
            );

            StmtIterator it = model.listStatements(selector);
            while (it.hasNext()) {
                Statement stmt = it.next();
                results.add(stmt);
            }
            dataset.commit();
        } finally {
            if (model != null) model.close();
            dataset.end();
        }
        return results;
    }

    /**
     * 增加Model中三元组；
     */
    public void addTriplet(String modelName, String subject, String predicate, String object) {
        Model model = null;

        dataset.begin(ReadWrite.WRITE);
        try {
            model = dataset.getNamedModel(modelName);
            //三元组信息不全则不添加；
            if (subject == null || predicate == null || object == null) {
                System.out.println("三元组信息有缺失，不执行添加操作！");
            } else {
                //存在当前三元组则不添加；
                Selector selector = new SimpleSelector(
                        (subject != null) ? model.createResource(subject) : null,
                        (predicate != null) ? model.createProperty(predicate) : null,
                        (object != null) ? model.createResource(object) : null
                );
                StmtIterator it = model.listStatements(selector);
                if (it.hasNext()) {
                    System.out.println("已有该三元组，不执行添加操作！");
                } else {
                    Statement stmt = model.createStatement
                            (
                                    model.createResource(subject),
                                    model.createProperty(predicate),
                                    model.createResource(object)
                            );
                    model.add(stmt);
                    model.commit();
                    dataset.commit();
                    System.out.println("添加三元组： " + subject + " " + predicate + " " + object);
                }
            }
        } finally {
            if (model != null)
                model.close();
            dataset.end();
        }
    }

    /**
     * 删除Model中的三元组；
     */
    public void removeTriplet(String modelName, String subject, String predicate, String object) {
        Model model = null;
        dataset.begin(ReadWrite.WRITE);
        try {
            model = dataset.getNamedModel(modelName);

            //三元组信息不全则不添加；
            if (subject == null || predicate == null || object == null) {
                System.out.println("三元组信息有缺失，不执行删除操作！");
            }

            else {
                //不存在当前三元组则不执行删除添加；
                Selector selector = new SimpleSelector(
                        (subject != null) ? model.createResource(subject) : null,
                        (predicate != null) ? model.createProperty(predicate) : null,
                        (object != null) ? model.createResource(object) : null
                );
                StmtIterator it = model.listStatements(selector);
                if (!it.hasNext()) {
                    System.out.println("没有该三元组，不执行删除操作！");
                } else {
                    Statement stmt = model.createStatement
                            (
                                    model.createResource(subject),
                                    model.createProperty(predicate),
                                    model.createResource(object)
                            );
                    model.remove(stmt);
                    dataset.commit();
                    System.out.println("删除三元组： " + subject + " " + predicate + " " + object);
                }
            }
        } finally {
            if (model != null) model.close();
            dataset.end();
        }
    }
}
