package com.java.r2pgdm;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.java.r2pgdm.graph.Edge;
import com.java.r2pgdm.graph.Node;
import com.java.r2pgdm.graph.Property;

public class PsqlGraph {
    private static Connection _con;

    public PsqlGraph(String url) {
        Connect(url);
        CreateGraphSQL();
    }

    private void Connect(String url) {
        try {
            _con = DriverManager.getConnection(url);
            System.out.println("Connection established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void DropTablesIfExists() {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS node;");
            stmt.executeUpdate("DROP TABLE IF EXISTS edge;");
            stmt.executeUpdate("DROP TABLE IF EXISTS property;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateNodeTable() {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate("CREATE TABLE node(id INTEGER NOT NULL, label VARCHAR(100), PRIMARY KEY (id));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateEdgeTable() {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate(
                    "CREATE TABLE edge(id INTEGER NOT NULL, srcId VARCHAR(100), tgtId VARCHAR(100), label VARCHAR(100), PRIMARY KEY (id));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreatePropertyTable() {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate(
                    "CREATE TABLE property(id INTEGER NOT NULL, key VARCHAR(100), value VARCHAR(100), PRIMARY KEY (id, key));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void InsertPropertyRow(Property prop) {

        try {
            Statement st = _con.createStatement();
            Object[] args = { prop.Id, prop.Key, prop.Value };
            String query = MessageFormat.format("INSERT INTO property VALUES ({0}, ''{1}'', ''{2}'');", args);
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void InsertEdgeRow(Edge edge) {
        try {
            Statement st = _con.createStatement();
            Object[] args = { edge.Id, edge.SrcId, edge.TgtId, edge.Label };
            String query = MessageFormat.format("INSERT INTO edge VALUES ({0}, ''{1}'', ''{2}'', ''{3}'');", args);
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void InsertNodeRow(Node n) {
        try {
            Statement st = _con.createStatement();
            String query = "INSERT INTO node VALUES(".concat(n.Id).concat(",'").concat(n.Label).concat("');");
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> JoinNodeAndProperty(String relName, String val) {
        String sql = "SELECT n.id, n.label, p.key, p.value FROM node n INNER JOIN property p ON n.id = p.id AND p.value='"
                .concat(val).concat("' AND n.label='").concat(relName).concat("';");
        List<String> results = new ArrayList<>();

        try {
            Statement stmt = _con.createStatement();
            ResultSet values = stmt.executeQuery(sql);
            while (values.next()) {
                results.add(values.getString(1));
            }
            throw new NullPointerException("JoinNodeAndProperty is null.");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            return results;
        }

    }

    public void CreateGraphSQL() {
        DropTablesIfExists();
        CreateNodeTable();
        CreateEdgeTable();
        CreatePropertyTable();
        System.out.println("Mapping - Created tables.");
    }
}
