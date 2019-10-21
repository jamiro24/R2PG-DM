package com.java.r2pgdm;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.ini4j.Profile.Section;

public class App {
    public static PreparedStatement _statementEdges;

    public static void main(String[] args) {
        try {
            Wini ini = new Wini(new File("config.ini"));
            Config input = GetConfiguration(ini.get("input"));
            Config output = GetConfiguration(ini.get("output"));

            InputConnection inputConn = new InputConnection(input.ConnectionString, input.Database, input.Driver);
            OutputConnection outputConn = new OutputConnection(output.ConnectionString);

            List<String> tables = inputConn.GetTableName();
            // Transform tables in parallel
            int tCount = Runtime.getRuntime().availableProcessors();
            tCount = 1; //REMOVE LATER
            ExecutorService executorService = Executors.newFixedThreadPool(tCount);

            ArrayList<Future<?>> tFinished = new ArrayList<>();

            // Create node + props
//            tables.forEach(t -> tFinished.add(executorService.submit(() -> inputConn.CreateNodesAndProperties(t))));
//            awaitTableCompletion(tFinished); // Wait for nodes and properties to finish creating
//            System.out.println("Nodes with properties created");

            createEdges(inputConn, tables.get(3));

            // Create edges
//            tables.forEach(t -> tFinished.add(executorService.submit(() -> {
//                createEdges(inputConn, t);
//
//            }
            //)));
            awaitTableCompletion(tFinished); // Wait for edges to finish creating
            System.out.println("Edges created");

            System.out.println("Mapping - Done.");
//            OutputConnection.Statistics();
//
//            Export.GenerateCSVs();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void awaitTableCompletion(ArrayList<Future<?>> tFinished) {
        tFinished.forEach((future) ->{
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        tFinished.clear();
    }

    private static void createEdges(InputConnection inputConn, String t) {
        List<CompositeForeignKey> fks = inputConn.GetForeignKeys(t);
        System.out.println(fks.size() + " fks where found in table " + t);
        fks.forEach(fk -> inputConn.CreateEdges(fk, t));
    }

    private static Config GetConfiguration(Section section) {
        if (section.getName().equals("input")) {
            return new Config(section.get("connectionString"), section.get("driver"), section.get("database"));
        }
        return new Config(section.get("connectionString"));
    }
}
