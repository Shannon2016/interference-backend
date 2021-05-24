package com.example.demo.controller;

import com.example.demo.myclass.MyTriple;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.InfGraph;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@RestController
public class HelloWorldController {
    @RequestMapping("/hello")
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("/test")
    public HashMap<String, ArrayList<MyTriple>> result() throws FileNotFoundException {
        HashMap<String, ArrayList<MyTriple>> ans = new HashMap<>();

        String filepath = "./src/main/resources/files/";

        Model myMod = ModelFactory.createDefaultModel();
        String finance = "http://www.example.org/kse/finance#";
        Resource 孙宏斌 = myMod.createResource(finance + "孙宏斌");
        Resource 融创中国 = myMod.createResource(finance + "融创中国");
        Resource 乐视网 = myMod.createResource(finance + "乐视网");
        Property 执掌 = myMod.createProperty(finance + "执掌");
        Resource 贾跃亭 = myMod.createResource(finance + "贾跃亭");
        Resource 地产公司 = myMod.createResource(finance + "地产公司");
        Resource 公司 = myMod.createResource(finance + "公司");
        Resource 法人实体 = myMod.createResource(finance + "法人实体");
        Resource 人 = myMod.createResource(finance + "人");
        Property 主要收入 = myMod.createProperty(finance + "主要收入");
        Resource 地产事业 = myMod.createResource(finance + "地产事业");
        Resource 王健林 = myMod.createResource(finance + "王健林");
        Resource 万达集团 = myMod.createResource(finance + "万达集团");
        Property 主要资产 = myMod.createProperty(finance + "主要资产");


        Property 股东 = myMod.createProperty(finance + "股东");
        Property 关联交易 = myMod.createProperty(finance + "关联交易");
        Property 收购 = myMod.createProperty(finance + "收购");

        // 加入三元组
        ArrayList<MyTriple> before = new ArrayList<>();
        myMod.add(孙宏斌, 执掌, 融创中国);
        before.add(new MyTriple("孙宏斌", "执掌", "融创中国"));
        myMod.add(贾跃亭, 执掌, 乐视网);
        before.add(new MyTriple("贾跃亭", "执掌", "乐视网"));
        myMod.add(王健林, 执掌, 万达集团);
        before.add(new MyTriple("王健林", "执掌", "万达集团"));
        myMod.add(乐视网, RDF.type, 公司);
        before.add(new MyTriple("乐视网", "type", "公司"));
        myMod.add(万达集团, RDF.type, 公司);
        before.add(new MyTriple("万达集团", "type", "公司"));
        myMod.add(融创中国, RDF.type, 地产公司);
        before.add(new MyTriple("融创中国", "type", "地产公司"));
        myMod.add(地产公司, RDFS.subClassOf, 公司);
        before.add(new MyTriple("地产公司", "subClassOf", "公司"));
        myMod.add(公司, RDFS.subClassOf, 法人实体);
        before.add(new MyTriple("公司", "subClassOf", "法人实体"));
        myMod.add(孙宏斌, RDF.type, 人);
        before.add(new MyTriple("孙宏斌", "type", "人"));
        myMod.add(贾跃亭, RDF.type, 人);
        before.add(new MyTriple("贾跃亭", "type", "人"));
        myMod.add(王健林, RDF.type, 人);
        before.add(new MyTriple("王健林", "type", "人"));
        myMod.add(万达集团,主要资产,地产事业);
        before.add(new MyTriple("万达集团", "主要资产", "地产事业"));
        myMod.add(融创中国,主要收入,地产事业);
        before.add(new MyTriple("融创中国", "主要收入", "地产事业"));
        myMod.add(孙宏斌, 股东, 乐视网);
        before.add(new MyTriple("孙宏斌", "股东", "乐视网"));
        myMod.add(孙宏斌, 收购, 万达集团);
        before.add(new MyTriple("孙宏斌", "收购", "万达集团"));
        ans.put("before", before);

        // myMod.write(System.out);
        // 保存成三元组文件
        OutputStream out = new FileOutputStream(new File(filepath+"/test_before.nt"));
        RDFDataMgr.write(out, myMod, Lang.NTRIPLES);

        PrintUtil.registerPrefix("", finance);


        // 输出当前模型
        StmtIterator i = myMod.listStatements(null,null,(RDFNode)null);
        while (i.hasNext()) {
            System.out.println(" !- " + PrintUtil.print(i.nextStatement()));
        }

        GenericRuleReasoner reasoner = (GenericRuleReasoner) GenericRuleReasonerFactory.theInstance().create(null);
        reasoner.setRules(Rule.parseRules(
                "[ruleHoldShare: (?p :执掌 ?c) -> (?p :股东 ?c)] \n"
                        + "[ruleConnTrans: (?p :收购 ?c) -> (?p :股东 ?c)] \n"
                        + "[ruleConnTrans: (?p :股东 ?c) (?p :股东 ?c2) -> (?c :关联交易 ?c2)] \n"
                        + "-> tableAll()."));
        reasoner.setMode(GenericRuleReasoner.HYBRID);

        InfGraph infgraph = reasoner.bind(myMod.getGraph());
        infgraph.setDerivationLogging(true);

        // 保存成三元组文件
        OutputStream out2 = new FileOutputStream(new File(filepath+"/test_after.nt"));
        RDFDataMgr.write(out2, infgraph, Lang.NT);

//        String res = "";
        ArrayList<MyTriple> after = new ArrayList<>();
        ArrayList<MyTriple> newNodes = new ArrayList<>();
        System.out.println("推理后...\n");
        Iterator<Triple> tripleIterator = infgraph.find(null, null, null);
        while (tripleIterator.hasNext()) {
            Triple cur = tripleIterator.next();
            MyTriple newNode = new MyTriple(cur.getSubject().toString().substring(finance.length()),
                    cur.getPredicate().toString().split("#")[1],
                    cur.getObject().toString().substring(finance.length()));
            after.add(newNode);
            if(!before.contains(newNode)){
                newNodes.add(newNode);
            }
//            System.out.println(cur.getSubject().toString().substring(finance.length()));
//            System.out.println(cur.getPredicate().toString().split("#")[1]);
//            System.out.println(cur.getObject().toString().substring(finance.length()));
//            res = res + " - " + PrintUtil.print(cur);
//            System.out.println(" - " + PrintUtil.print(cur));
        }
        ans.put("after", after);
        ans.put("new", newNodes);
        Model model_before = FileManager.get().loadModel(filepath+"/test_before.nt");
        Model model_after = FileManager.get().loadModel(filepath+"/test_after.nt");

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("执掌");
        arrayList.add("收购");
        arrayList.add("股东");


        for (int j = 0; j < arrayList.size(); j++) {
            String str = arrayList.get(j);
            String query_str = "SELECT * { ?s <http://www.example.org/kse/finance#" + str + "> ?o }";
            Query query = QueryFactory.create(query_str);
            QueryExecution qexec_before = QueryExecutionFactory.create(query, model_before);

            try {
                ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec_before.execSelect());

//                System.out.println("\n---- CSV ----");
//                System.out.println(query_str);
                OutputStream result_out = new FileOutputStream(new File(filepath+"result/"+str));
                ResultSetFormatter.outputAsCSV(result_out, results);
                results.reset();

            } finally {
                qexec_before.close();
            }
        }


        String query_after_str = "SELECT * { ?s <http://www.example.org/kse/finance#关联交易> ?o }";
        Query query_after = QueryFactory.create(query_after_str);
        QueryExecution qexec_after = QueryExecutionFactory.create(query_after, model_after);
        try {
            ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec_after.execSelect());

            System.out.println("\n---- CSV ----");
            System.out.println(query_after_str);
            OutputStream result_out = new FileOutputStream(new File(filepath+"/result/关联交易"));
            ResultSetFormatter.outputAsCSV(result_out, results);
            results.reset();

        } finally {
            qexec_after.close();
        }


        String query_path_str = "SELECT * " +
                "where { " +
                "?s1 <http://www.example.org/kse/finance#股东> ?o1. " +
                "?s1 <http://www.example.org/kse/finance#股东> ?o2." +
                "?o1 <http://www.example.org/kse/finance#关联交易> ?o2" +
                "OPTIONAL {?s1 <http://www.example.org/kse/finance#执掌> ?o1. }" +
                "OPTIONAL {?s1 <http://www.example.org/kse/finance#执掌> ?o2. }" +
                "OPTIONAL {?s1 <http://www.example.org/kse/finance#收购> ?o1. }" +
                "OPTIONAL {?s1 <http://www.example.org/kse/finance#收购> ?o2. }" +
                "}";


        Query query_path = QueryFactory.create(query_path_str);
        QueryExecution qexec_path = QueryExecutionFactory.create(query_path, model_after);
        try {
            ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec_path.execSelect());

            OutputStream result_out = new FileOutputStream(new File(filepath+"result/path"));
            ResultSetFormatter.outputAsCSV(result_out, results);
            System.out.println(result_out);
            results.reset();

        } finally {
            qexec_after.close();
        }



        return ans;
    }
}
