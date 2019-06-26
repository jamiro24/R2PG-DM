package com.java.r2pgdm;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

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

            Instant starts = Instant.now();
            // Create node + props
            List<String> tables = inputConn.GetTableName();
            tables.forEach(t -> {
                inputConn.CreateNodesAndProperties(t);
            });

            // Create edges
            tables.forEach(t -> {

                List<CompositeForeignKey> fks = inputConn.GetForeignKeys(t);
                fks.forEach(fk -> {
                    String sql = "INSERT INTO edge VALUES(?,?,?,?);";
                    try {
                        _statementEdges = OutputConnection._con.prepareStatement(sql);
                        inputConn.CreateEdges(fk);
                        int[] result = _statementEdges.executeBatch();
                        System.out.println(result.length + " edge(s) added.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

            });

            Instant ends = Instant.now();
            System.out.println(Duration.between(starts, ends).toMillis());
            System.out.println("Mapping - Done.");
            OutputConnection.Statistics();

            Export.GenerateCSVs();

        } catch (InvalidFileFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Config GetConfiguration(Section section) {
        if (section.getName().equals("input")) {
            return new Config(section.get("connectionString"), section.get("driver"), section.get("database"));
        }
        return new Config(section.get("connectionString"));
    }
}
