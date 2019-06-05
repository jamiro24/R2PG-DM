package com.java.r2pgdm;

import java.util.List;

import com.java.r2pgdm.graph.Edge;
import com.java.r2pgdm.graph.Node;
import com.java.r2pgdm.graph.Property;

import lombok.Getter;
import lombok.Setter;

public class Identifier {
    @Getter
    @Setter
    private static Integer GlobalID = 1;

    public static Integer id(Integer rId, String label) {
        return GlobalID++;
    }

    public static Integer id(Integer rIdR, String labelR, Integer rIdS, String labelS, List<String> fksR,
            List<String> fksS) {
        try {
            if (fksR.size() != fksS.size()) {
                throw new RuntimeException("The foreign keys size is different.");
            }

            return GlobalID++;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // private static IdDataModel IdDataModelNode(Node n, Integer id) {
    //     List<Property> props = App._PostgresGraph.GetPropertiesFromId(id);
    //     List<String> cols = App._Postgres.GetColumns(n.Label);

    // }

    // private static IdDataModel idDataModelEdge(Edge e, Integer id) {
    //     return null;
    // }

    // // Query here the db. Get all data from node, property based on 'id'. This void
    // // has to become a class with the params from above.
    // public static IdDataModel idRev(Integer id) {
    //     Node n = App._PostgresGraph.GetNodeFromId(id);
    //     Edge e = App._PostgresGraph.GetEdgeFromId(id);
    //     if (n != null) {
    //         return IdDataModelNode(n, id);
    //     } else if (e != null) {
    //         return idDataModelEdge(e, id);
    //     }
    //     return null;

    // }
}