package com.example.demo.service.interf;

import org.apache.jena.rdf.model.Statement;

import java.util.List;

public interface TDBCrud {
    /**
     * 查询Model中三元组；
     */
    List<Statement> getTriplet(String modelName, String subject, String predicate, String object);

    /**
     * 增加Model中三元组；
     */
    void addTriplet(String modelName, String subject, String predicate, String object);

    /**
     * 删除Model中的三元组；
     */
    void removeTriplet(String modelName, String subject, String predicate, String object);
}
