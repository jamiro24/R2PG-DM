package com.java.r2pgdm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.java.r2pgdm.graph.Edge;
import com.java.r2pgdm.graph.Node;
import com.java.r2pgdm.graph.Property;

public class PsqlGraph {

    private static final int FIVE = 5;
    private static Connection _con;

    public PsqlGraph(String url, String user, String pass) {
        Connect(url, user, pass);
        CreateGraphSQL();
    }

    private void Connect(String url, String user, String pass) {
        try {
            _con = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection PsqlGraph established.");
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
            stmt.executeUpdate("CREATE TABLE node(id INTEGER NOT NULL, label TEXT, PRIMARY KEY (id));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateEdgeTable() {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate(
                    "CREATE TABLE edge(id INTEGER NOT NULL, srcId INTEGER, tgtId INTEGER, label TEXT, PRIMARY KEY (id));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreatePropertyTable() {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate(
                    "CREATE TABLE property(id INTEGER NOT NULL, pkey VARCHAR(256), pvalue TEXT, PRIMARY KEY (id, pkey));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void InsertPropertyRow(Property prop) {

        try {
            String sql = "INSERT INTO property VALUES(?,?,?);";
            PreparedStatement st = _con.prepareStatement(sql);
            st.setInt(1, Integer.parseInt(prop.Id));
            st.setString(2, prop.Key);
            st.setString(3, prop.Value);

            Integer result = st.executeUpdate();
            System.out.println(result.toString().concat(" property added."));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void InsertEdgeRow(Edge edge) {
        try {
            String sql = "INSERT INTO edge VALUES(?,?,?,?);";
            PreparedStatement st = _con.prepareStatement(sql);
            st.setInt(1, Integer.parseInt(edge.Id));
            st.setInt(2, Integer.parseInt(edge.SrcId));
            st.setInt(3, Integer.parseInt(edge.TgtId));
            st.setString(4, edge.Label);

            Integer result = st.executeUpdate();
            System.out.println(result.toString().concat(" edge added."));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void InsertNodeRow(Node n) {
        try {
            String sql = "INSERT INTO node VALUES(?,?);";
            PreparedStatement st = _con.prepareStatement(sql);
            st.setInt(1, Integer.parseInt(n.Id));
            st.setString(2, n.Label);

            Integer result = st.executeUpdate();
            System.out.println(result.toString().concat(" node added."));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> JoinNodeAndProperty(String relName, String key, String val) {
        String sql = "SELECT n.id, n.label, p.pkey, p.pvalue FROM node n INNER JOIN property p ON n.id = p.id AND p.pvalue='"
                .concat(val).concat("' AND p.pkey='").concat(key).concat("' AND n.label='").concat(relName).concat("';");
        List<String> results = new ArrayList<>();

        try {
            Statement stmt = _con.createStatement();
            ResultSet values = stmt.executeQuery(sql);
            while (values.next()) {
                results.add(values.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            return results;
        }

    }

    public static void Statistics() {
        String sql = "SELECT COUNT(*) as stats FROM node UNION SELECT COUNT(*) FROM property UNION SELECT COUNT(*) FROM edge;";
        List<String> results = new ArrayList<>();

        try {
            Statement stmt = _con.createStatement();
            ResultSet values = stmt.executeQuery(sql);
            while (values.next()) {
                results.add(values.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("# Nodes: ".concat(results.get(2)));
            System.out.println("# Properties: ".concat(results.get(1)));
            System.out.println("# Edges: ".concat(results.get(0)));
        }
    }

    public static ResultSet GetNodeData() {
        String sql = "select * from node;";

        try {
            PreparedStatement stmt = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(FIVE);
            ResultSet values = stmt.executeQuery();
            return values;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet GetPropertyData() {
        String sql = "select * from property;";

        try {
            PreparedStatement stmt = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(FIVE);
            ResultSet values = stmt.executeQuery();
            return values;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet GetEdgeData() {
        String sql = "select * from edge;";

        try {
            PreparedStatement stmt = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(FIVE);
            ResultSet values = stmt.executeQuery();
            return values;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
