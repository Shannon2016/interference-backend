package com.example.demo.service;

import com.example.demo.service.interf.TDBCrud;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.List;

public class TDBCrudImpl implements TDBCrud {
    public static final String tdb_path = "./src/main/resources/files/tdb";

    @Override
    public List<Statement> getTriplet(String modelName, String subject, String predicate, String object) {

        List<Statement> list = new ArrayList<>();
        //如果不输入model name，则使用系统默认的TDB model；
        if(modelName == null) {
            modelName = "Default_Model";
            System.out.println("MODEL NAME为空，使用默认model： Default_Model");
        }

        TDBPersistence tdbPersistence = new TDBPersistence(tdb_path);
        //如果TDB中有当前model；
        if (tdbPersistence.findTDB(modelName)) {
            list = tdbPersistence.getTriplet(modelName, subject, predicate, object);
            tdbPersistence.closeTDB();
        }

        //如果TDB中没有当前model；
        else
            System.out.println(modelName + " 不存在，无法查询！");
        return list;
    }

    @Override
    public void addTriplet(String modelName, String subject, String predicate, String object) {

        //如果不输入model name，则使用系统默认的TDB model：TDB_agriculture；
        if(modelName == null) {
            modelName = "Default_Model";
            System.out.println("MODEL NAME为空，使用默认model： Default_Model");
        }

        TDBPersistence tdbPersistence = new TDBPersistence(tdb_path);
        //如果TDB中有当前model；
        if (tdbPersistence.findTDB(modelName)) {
            tdbPersistence.addTriplet(modelName, subject, predicate, object);
            tdbPersistence.closeTDB();
        }

        //如果TDB中没有当前model；
        else
            System.out.println(modelName + " 不存在，不执行添加操作！");
    }

    @Override
    public void removeTriplet(String modelName, String subject, String predicate, String object) {

        //如果不输入model name，则使用系统默认的TDB model：TDB_agriculture；
        if(modelName == null) {
            modelName = "Default_Model";
            System.out.println("MODEL NAME为空，使用默认model： Default_Model");
        }

        TDBPersistence tdbPersistence = new TDBPersistence(tdb_path);
        //如果TDB中有当前model；
        if (tdbPersistence.findTDB(modelName)) {
            tdbPersistence.removeTriplet(modelName, subject, predicate, object);
            tdbPersistence.closeTDB();
        }

        //如果TDB中没有当前model；
        else System.out.println(modelName + " 不存在，不执行删除操作！");
    }
}
