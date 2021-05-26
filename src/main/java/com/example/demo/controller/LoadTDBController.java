package com.example.demo.controller;

import com.example.demo.service.TDBPersistence;
import org.apache.jena.rdf.model.Statement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoadTDBController {
    @RequestMapping("/loadtdb")
    public String loadTDB(){
        //TDB的数据文件夹地址；
        String TDBPath = "./src/main/resources/files/tdb";
        //在Dataset中存放model的名字；
        String modelName = "tdb_after";
        //表示若有同名model，是否需要覆盖；
        Boolean flag = true;
        //rdf三元组文件的路径；
        String rdfPathName = "./src/main/resources/files/test_after.nt";
        //建立对象；
        TDBPersistence tdbPersistence = new TDBPersistence(TDBPath);
        //新建model；
        tdbPersistence.loadModel(modelName, rdfPathName, flag);
        //事务完成后必须关闭Dataset；
        tdbPersistence.closeTDB();

        //若主、谓、宾中有空缺，则表示该部分不参与匹配；
        //若主、谓、宾全部空缺，则输出所有三元组；
//        String subject = null;
//        String predicate =  "执掌";
//        String object = null;
//        TDBCrud tdbCrudDriver = new TDBCrudImpl();
//        List<Statement> list = tdbCrudDriver.getTriplet("tdb_test", subject, predicate, object);
//        for (Statement s : list) {
//            System.out.println(s);
//        }

        return "Hello Spring Boot";
    }
}
