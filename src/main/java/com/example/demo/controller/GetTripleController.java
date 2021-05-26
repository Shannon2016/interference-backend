package com.example.demo.controller;

import com.example.demo.myclass.MyTriple;
import com.example.demo.service.TDBCrudImpl;
import com.example.demo.service.TDBPersistence;
import com.example.demo.service.interf.TDBCrud;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GetTripleController {
    @RequestMapping("/get_triple")
    public HashMap<String, ArrayList<MyTriple>> getTriple(HttpServletRequest request){
        String finance = "http://www.example.org/kse/finance#";

        String subject = request.getParameter("subject");
        String predicate = request.getParameter("predicate");
        String object = request.getParameter("object");
        String model = request.getParameter("model");
        if (model == null) {
            model = "tdb_after";
        }
        TDBCrud tdbCrudDriver = new TDBCrudImpl();
        List<Statement> list1 = tdbCrudDriver.getTriplet(model, subject, predicate, object);
        List<MyTriple> list2 = new ArrayList<>();
        for (Statement s : list1) {
            Resource subject2 = s.getSubject();
            Property predicate2 = s.getPredicate();
            RDFNode object2 = s.getObject();
            list2.add(new MyTriple(subject2.toString().substring(finance.length()),
                    predicate2.toString().split("#")[1], object2.toString().substring(finance.length())));
        }
        HashMap<String, ArrayList<MyTriple>> ans = new HashMap<>();
        ans.put("data", new ArrayList<>(list2));
        return ans;
    }


    @RequestMapping("/get_triple_step")
    public HashMap<String, ArrayList<MyTriple>> getStepTriple(HttpServletRequest request){
//        String tdb_path = "./src/main/resources/files/tdb";
        String finance = "http://www.example.org/kse/finance#";
        String filepath = "./src/main/resources/files/";
        String subject = request.getParameter("subject");
//        String step = request.getParameter("step");
//        String model = request.getParameter("model");
//        if (model == null) {
//            model = "tdb_after";
//        }
        String instance = "<" + finance + subject + ">";

        HashMap<String, ArrayList<MyTriple>> ans = new HashMap<>();

        String query_str = "SELECT * where { " + instance + " ?r1 ?o1. ?o1 ?r2 ?o2.}";

        Model model_step = FileManager.get().loadModel(filepath+"/test_after.nt");
        System.out.println(query_str);
//        TDBPersistence tdbPersistence = new TDBPersistence(tdb_path);
//        Model model_step = tdbPersistence.getModel(model);

        Query query = QueryFactory.create(query_str);
        QueryExecution qexec = QueryExecutionFactory.create(query, model_step);
        try {
            ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec.execSelect());
            OutputStream result_out = new FileOutputStream(new File(filepath+"result/path"));
            ResultSetFormatter.outputAsCSV(result_out, results);
            results.reset();
            ArrayList<MyTriple> subres = new ArrayList<>();
            while(results.hasNext()) {
                QuerySolution result = results.next();
                MyTriple newNode = new MyTriple(subject,
                        result.get("r1").toString().split("#")[1], result.get("o1").toString().substring(finance.length()));
                subres.add(newNode);
                MyTriple newNode2 = new MyTriple(result.get("o1").toString().substring(finance.length()),
                        result.get("r2").toString().split("#")[1], result.get("o2").toString().substring(finance.length()));
                subres.add(newNode2);
            }
            // 去个重
            List<MyTriple> unique = subres.stream().distinct().collect(Collectors.toList());
            ans.put("data", new ArrayList<>(unique));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            qexec.close();
//            tdbPersistence.closeTDB();
        }

//        String predicate = request.getParameter("predicate");
//        String object = request.getParameter("object");
//        String model = request.getParameter("model");
//        if (model == null) {
//            model = "tdb_after";
//        }
//        TDBCrud tdbCrudDriver = new TDBCrudImpl();
//        List<Statement> list1 = tdbCrudDriver.getTriplet(model, subject, predicate, object);
//        List<MyTriple> list2 = new ArrayList<>();
//        for (Statement s : list1) {
//            Resource subject2 = s.getSubject();
//            Property predicate2 = s.getPredicate();
//            RDFNode object2 = s.getObject();
//            list2.add(new MyTriple(subject2.toString().substring(finance.length()),
//                    predicate2.toString().split("#")[1], object2.toString().substring(finance.length())));
//        }
//        HashMap<String, ArrayList<MyTriple>> ans = new HashMap<>();
//        ans.put("data", new ArrayList<>(list2));
        return ans;
    }
}
