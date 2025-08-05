package fazebook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
 * This class is used to implement a graph using an adjacency map. It has two
 * fields, a map with the Vertex as the key and the adjacency map of neighbors
 * as the value and a comparator used in the consolidateVertices() method. Every
 * method that takes an object as a parameter checks if it is null and will
 * throw an exception if so. The constructor initializes the graph to be an
 * empty map and initializes the comparator to the parameter value. Then the
 * newEWDGraphVertex(V vertexData) method takes a value V and adds it to the 
 * current graph without any edges. If the value is already in the graph,
 * nothing is changed and false is returned. If the value is successfully added
 * true is returned. Method isEWDGraphVertex(V vertexData) returns true if the
 * value is in the graph and false if not. Method getEWDGraphVertices() returns
 * a collection of vertices in the current graph. Method 
 * newEWDGraphEdge(V srcVert, V destVert, int weight) creates a new edge from
 * srcVert to destVert with weight weight. If weight is 0 or negative or
 * srcVert and destVert are the same the method will return false and do
 * nothing. Otherwise the edge is added and true is returned. If either of the
 * parameter vertices are not in the graph, they will be added and then the
 * edge is created. Method getEWDGraphEdge(V srcVert, V destVert) returns the
 * weight of the edge from srcVert to destVert. If either of the vertices aren't
 * in the graph, or if they are both in the graph but no edge exists between 
 * srcVert to destVert -1 is returned. Method 
 * removeEWDGraphEdge(V srcVert, V destVert) removes the edge from srcVert to
 * destVert and returns true only if both vertices are in the graph, and an
 * edge exists from srcVert to destVert. Otherwise false is returned and 
 * nothing changes. Method removeEWDGraphVertex(V vertexData) removes Vertex
 * with value vertexData if it is in the graph and returns true. It also removes
 * every outgoing and incoming edge from the vertex. If there is no vertex with
 * value vertexData then false is returned and nothing changes. Method 
 * getNeighborsOfVertex(V vertexData) returns a Collection of every neighbor
 * of vertex with value vertexData if it is in the graph, otherwise it returns
 * null. Method consolidateVertices(V vertex1, V vertex2) combines vertices
 * with values vertex1 and vertex2 into one and returns true. It only does this
 * if both vertices exist in the graph and an edge exists between them (either
 * from 1 to 2 or vice versa). First it removes the edge between them and then
 * it sets the value of the new combined vertex to be the smaller of the two
 * values. It then uses private helper methods copyOutgoing and copyIncoming
 * to create two maps that store all incoming and outgoing edges of the
 * two parameter vertices. These helper methods will be explained later. It
 * then calls the removeEWDGraphVertex() method to remove both parameter
 * vertices and calls newEWDGraphVertex to create a new vertex with the smaller
 * value of the two that will be the new consolidated vertex. It then uses
 * an enhanced for loop to add every one of the outgoing edges (from the 
 * parameter vertices) to the new vertex's adjacency map of neighbors. Finally
 * it uses another enhanced for loop to add all the incoming edges to the new
 * vertex and returns true. If either vertex is not in the graph, or no edge
 * exists between them the method returns false and nothing changes. The
 * copyOutgoing(V vertex1, V vertex2) helper method returns a map with every 
 * outgoing edge from both parameter vertices. The map has vertices as keys and
 * weights as values. First it adds every outgoing edge of vertex1 and then of
 * vertex2, but when adding vertex2's edges it checks if vertex1 also had an 
 * outgoing edge to that vertex. If so it adds the outgoing edge with the
 * smaller of the two weights. The copyIncoming(V vertex1, V vertex2) method
 * pretty much works the same way but instead of returning a map of outgoing
 * edges, it returns the incoming edges of both vertices in a map. It also
 * has the same behavior of taking the smaller of the 2 weights if one vertex
 * has edges going to both vertex1 and vertex2
 */

public class EWDGraph<V> {

    private HashMap<V, HashMap<V, Integer>> graph;
    private Comparator<V> compare;

    // constructor initializes the comparator to the parameter comparator and
    // creates the map to be used for the graph itself
    public EWDGraph(Comparator<V> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException();
        }

        graph = new HashMap<>();
        compare = comparator;
    }

    // This method uses the parameter to add a vertex with the parameter value.
    // It affects the current object by adding a vertex to it. Returns true if
    // added successfully and false otherwise.
    public boolean newEWDGraphVertex(V vertexData) {
        if (vertexData == null) {
            throw new IllegalArgumentException();
        }

        boolean result = false;

        if (!graph.containsKey(vertexData)) {
            HashMap<V, Integer> adjMap = new HashMap<>();

            // adds the vertex to the graph with an empty adjacency map.
            graph.put(vertexData, adjMap);

            result = true;
        }

        return result;

    }

    // This method returns true if the vertex with the parameter value is in the
    // graph and false otherwise.
    public boolean isEWDGraphVertex(V vertexData) {
        if (vertexData == null) {
            throw new IllegalArgumentException();
        }

        return graph.containsKey(vertexData);
    }

    // This method creates and returns a Collection which stores every vertex
    // currently stored in the graph.
    public Collection<V> getEWDGraphVertices() {
        ArrayList<V> vertices = new ArrayList<>();

        for (V val : graph.keySet()) {
            vertices.add(val);
        }

        return vertices;
    }

    // This method creates a new edge from vertex srcVert to vertex destVert
    // with weight weight. It returns true if added successfully and false
    // otherwise. Affects current object by adding an edge. If either vertex
    // is not in the graph it adds them to the graph, and won't add an edge if
    // srcVert is the same as destVert or if weight is 0 or negative.
    public boolean newEWDGraphEdge(V srcVert, V destVert, int weight) {
        if (srcVert == null || destVert == null) {
            throw new IllegalArgumentException();
        }

        boolean result = false;

        // ensures valid weight and parameter vertices
        if (weight > 0 && !srcVert.equals(destVert)) {
            // if the vertices aren't already in the graph it adds them.
            if (!graph.containsKey(srcVert)) {
                newEWDGraphVertex(srcVert);
            }

            if (!graph.containsKey(destVert)) {
                newEWDGraphVertex(destVert);
            }

            if(!graph.get(srcVert).containsKey(destVert)) {
                // adds the edge from srcVert to destVert with weight weight to
                // adjacency map.
                graph.get(srcVert).put(destVert, weight);

                result = true;
            }
        }

        return result;
    }

    // This method returns the weight of the edge from vertex srcVert to vertex
    // destVert. If either vertex is not in the graph, or there is no edge from
    // srcVert to desVert -1 is returned.
    public int getEWDGraphEdge(V srcVert, V destVert) {
        if (srcVert == null || destVert == null) {
            throw new IllegalArgumentException();
        }

        int weight;

        // ensures valid parameters
        if (!graph.containsKey(srcVert) || !graph.containsKey(destVert)
                || !graph.get(srcVert).containsKey(destVert)) {
            weight = -1;
        }

        else {
            weight = graph.get(srcVert).get(destVert);
        }

        return weight;
    }

    // This method removes the edge from vertex srcVert to vertex destVert and
    // returns true if successfully removed and false otherwise. If either of
    // the parameter vertices are not in the graph or if there is no edge from
    // srcVert to destVert false is returned and nothing happens.
    public boolean removeEWDGraphEdge(V srcVert, V destVert) {
        if (srcVert == null || destVert == null) {
            throw new IllegalArgumentException();
        }

        boolean result = false;

        // ensures both parameter vertices are in the graph and have a valid
        // edge
        if (graph.containsKey(srcVert) && graph.containsKey(destVert)
                && graph.get(srcVert).containsKey(destVert)) {

            graph.get(srcVert).remove(destVert);

            result = true;
        }

        return result;
    }

    // This method removes the vertex with the parameter value from the graph
    // and returns true if successful and false otherwise. In deleting the
    // vertex it also deletes all incoming and outgoing edges from the vertex.
    // If the vertex with the parameter value isn't in the graph false is
    // returned and nothing happens.
    public boolean removeEWDGraphVertex(V vertexData) {
        if (vertexData == null) {
            throw new IllegalArgumentException();
        }

        boolean result = false;

        if (graph.containsKey(vertexData)) {
            // clears the vertex's adjacency map
            graph.get(vertexData).clear();

            // removes the vertex from the graph
            graph.remove(vertexData);

            // removes any incoming edges from vertex
            for (V val : graph.keySet()) {
                if (graph.get(val).containsKey(vertexData)) {
                    graph.get(val).remove(vertexData);
                }
            }

            result = true;
        }

        return result;
    }

    // This method returns a collection of all neighbors of the vertex with
    // the parameter value in the graph. If this vertex is not in the graph,
    // null is returned. By neighbors this means any outgoing edges.
    public Collection<V> getNeighborsOfVertex(V vertexData) {
        if (vertexData == null) {
            throw new IllegalArgumentException();
        }
        
        ArrayList<V> Neighbors = new ArrayList<>();

        if (graph.containsKey(vertexData)) {
            for (V val : graph.get(vertexData).keySet()) {
                Neighbors.add(val);
            }
        }

        return Neighbors;
    }

    // This method consolidates/combines both the parameter vertices to become
    // one. This returns true if successful and false otherwise. It only works
    // if both vertices are in the graph and there is an edge between them
    // (could be from vertex1 to vertex2 or vice versa). It removes both the old
    // vertices and creates a new one with the lower value, and then it adds
    // all the outgoing and incoming edges both parameter vertices had. If both
    // had an edge with the same vertex it picks the edge with the lower weight
    // to transfer. This transfer is handled by two helper methods.
    public boolean consolidateVertices(V vertex1, V vertex2) {
        if (vertex1 == null || vertex2 == null) {
            throw new IllegalArgumentException();
        }

        boolean result = false;

        // ensures parameters are in the graph
        if (graph.containsKey(vertex1) && graph.containsKey(vertex2)) {
            // ensures a valid edge between the parameter vertices
            if (graph.get(vertex1).containsKey(vertex2)) {
                // removes one edge
                removeEWDGraphEdge(vertex1, vertex2);

                // if another edge exists in opposite direction, removes it too
                if (graph.get(vertex2).containsKey(vertex1)) {
                    removeEWDGraphEdge(vertex2, vertex1);
                }

                V newVal;

                // lower vertex value is stored for the new vertex
                if (compare.compare(vertex1, vertex2) <= 0) {
                    newVal = vertex1;
                }

                else {
                    newVal = vertex2;
                }

                // create two maps of all outgoing and incoming edges of both
                // vertices
                Map<V, Integer> outgoing = copyOutgoing(vertex1, vertex2);
                Map<V, Integer> incoming = copyIncoming(vertex1, vertex2);

                // remove both vertices
                removeEWDGraphVertex(vertex1);
                removeEWDGraphVertex(vertex2);

                // add the new vertex
                newEWDGraphVertex(newVal);

                // add all outgoing edges
                for (V val : outgoing.keySet()) {
                    graph.get(newVal).put(val, outgoing.get(val));
                }

                // add all incoming edges
                for (V val : incoming.keySet()) {
                    graph.get(val).put(newVal, incoming.get(val));
                }

                result = true;
            }

            // same as if statement above but if the edge went the other way
            else if (graph.get(vertex2).containsKey(vertex1)) {
                removeEWDGraphEdge(vertex2, vertex1);

                if (graph.get(vertex1).containsKey(vertex2)) {
                    removeEWDGraphEdge(vertex1, vertex2);
                }

                V newVal;

                if (compare.compare(vertex1, vertex2) <= 0) {
                    newVal = vertex1;
                }

                else {
                    newVal = vertex2;
                }

                Map<V, Integer> outgoing = copyOutgoing(vertex1, vertex2);
                Map<V, Integer> incoming = copyIncoming(vertex1, vertex2);

                removeEWDGraphVertex(vertex1);
                removeEWDGraphVertex(vertex2);

                newEWDGraphVertex(newVal);

                for (V val : outgoing.keySet()) {
                    graph.get(newVal).put(val, outgoing.get(val));
                }

                for (V val : incoming.keySet()) {
                    graph.get(val).put(newVal, incoming.get(val));
                }

                result = true;
            }
        }

        return result;
    }

    // This private helper method creates and returns a map that stores all
    // outgoing edges of both parameter vertices. If both have an outgoing edge
    // with the same vertex it stores the edge with the lower weight.
    private Map<V, Integer> copyOutgoing(V vertex1, V vertex2) {
        HashMap<V, Integer> neighbors = new HashMap<>();

        // adds all of vertex1's outgoing edges
        for (V val : graph.get(vertex1).keySet()) {
            neighbors.put(val, graph.get(vertex1).get(val));
        }

        for (V val : graph.get(vertex2).keySet()) {
            // adds all of vertex2's outgoing edges
            neighbors.put(val, graph.get(vertex2).get(val));

            // if both vertices have an outgoing edge to the same vertex this
            // ensures the edge with the smallest weight is added
            if (graph.get(vertex1).containsKey(val)) {
                int min;
                int weight1;
                int weight2;

                // stores both weights and uses the smaller one
                weight1 = graph.get(vertex1).get(val);
                weight2 = graph.get(vertex2).get(val);

                if (weight1 <= weight2) {
                    min = weight1;
                }

                else {
                    min = weight2;
                }

                neighbors.put(val, min);
            }
        }

        return neighbors;
    }

    // This private helper method creates and returns a map that stores all
    // incoming edges of both parameter vertices. If both have an incoming edge
    // with the same vertex it stores the edge with the lower weight.
    private Map<V, Integer> copyIncoming(V vertex1, V vertex2) {
        HashMap<V, Integer> neighbors = new HashMap<>();

        // adds all of vertex1's incoming edges to the map
        for (V val : graph.keySet()) {
            if (graph.get(val).containsKey(vertex1)) {
                neighbors.put(val, graph.get(val).get(vertex1));
            }
        }

        // adds vertex2's incoming edges and checks for overlap
        for (V val : graph.keySet()) {
            if (graph.get(val).containsKey(vertex2)) {
                // if one vertex has an outgoing edge with both vertex1 and 2
                if (graph.get(val).containsKey(vertex1)) {
                    int min;
                    int weight1;
                    int weight2;

                    // stores both their weights and adds the edge with the
                    // smaller one
                    weight1 = graph.get(val).get(vertex1);
                    weight2 = graph.get(val).get(vertex2);

                    if (weight1 <= weight2) {
                        min = weight1;
                    }

                    else {
                        min = weight2;
                    }

                    neighbors.put(val, min);
                }

                // if this vertex only has an edge with vertex2 add it to map
                else {
                    neighbors.put(val, graph.get(val).get(vertex2));
                }
            }
        }

        return neighbors;
    }

}
