/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of Graph.
 * * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph implements Graph<String> {
    
    private final Set<String> vertices = new HashSet<>();
    private final List<Edge> edges = new ArrayList<>();
    
    // Abstraction Function:
    //   AF(vertices, edges) = A directed graph where:
    //     - The set of vertices is the set of all strings in `vertices`.
    //     - The set of edges is the set of all (source, target, weight) triples
    //       represented by the Edge objects in the `edges` list.
    //
    // Representation Invariant:
    //   RI(vertices, edges) =
    //     - `vertices` is not null.
    //     - `edges` is not null.
    //     - `vertices` contains no null elements.
    //     - `edges` contains no null elements.
    //     - Every Edge in `edges` has a source vertex that is present in `vertices`.
    //     - Every Edge in `edges` has a target vertex that is present in `vertices`.
    //     - There are no duplicate edges: for any two distinct Edge objects e1, e2
    //       in `edges`, it is not the case that 
    //       e1.getSource().equals(e2.getSource()) && e1.getTarget().equals(e2.getTarget()).
    //     - All edge weights are positive (weight > 0), as Edge's constructor ensures this.
    //
    // Safety from Rep Exposure:
    //   - All fields (`vertices`, `edges`) are private and final.
    //   - The `vertices` set contains immutable Strings. The `vertices()` method
    //     returns a new, unmodifiable copy of the set, not a reference to the
    //     internal `vertices` set.
    //   - The `edges` list contains immutable `Edge` objects.
    //   - Methods `sources()` and `targets()` build and return new `Map` objects,
    //     so the client cannot access or modify the internal `edges` list.
    
    
    /**
     * Create an empty graph.
     */
    public ConcreteEdgesGraph() {
        // No setup needed, fields are initialized
        checkRep();
    }
    
    /**
     * Asserts that the representation invariant holds.
     */
    private void checkRep() {
        assert vertices != null;
        assert edges != null;
        
        // Use a set to check for duplicate edges
        Set<String> edgeUniquenessCheck = new HashSet<>();
        
        for (Edge e : edges) {
            assert e != null;
            assert vertices.contains(e.getSource()) : "Edge source not in vertices set";
            assert vertices.contains(e.getTarget()) : "Edge target not in vertices set";
            
            // Check for duplicates
            String edgePair = e.getSource() + "->" + e.getTarget();
            assert !edgeUniquenessCheck.contains(edgePair) : "Duplicate edge found";
            edgeUniquenessCheck.add(edgePair);
        }
    }
    
    @Override 
    public boolean add(String vertex) {
        if (vertices.contains(vertex)) {
            return false;
        }
        boolean added = vertices.add(vertex);
        checkRep();
        return added;
    }
    
    @Override 
    public int set(String source, String target, int weight) {
        // Add vertices if they don't already exist
        add(source);
        add(target);
        
        int previousWeight = 0;
        
        // We must use an iterator to safely remove items while looping
        for (Iterator<Edge> it = edges.iterator(); it.hasNext(); ) {
            Edge edge = it.next();
            if (edge.getSource().equals(source) && edge.getTarget().equals(target)) {
                previousWeight = edge.getWeight();
                it.remove(); // Remove the old edge
                break; // Found it, no need to keep searching
            }
        }
        
        // Add the new edge only if weight > 0
        if (weight > 0) {
            edges.add(new Edge(source, target, weight));
        }
        
        checkRep();
        return previousWeight;
    }
    
    @Override 
    public boolean remove(String vertex) {
        if (!vertices.contains(vertex)) {
            return false;
        }
        
        // Remove all edges connected to this vertex
        // We must use an iterator to safely remove items while looping
        for (Iterator<Edge> it = edges.iterator(); it.hasNext(); ) {
            Edge edge = it.next();
            if (edge.getSource().equals(vertex) || edge.getTarget().equals(vertex)) {
                it.remove();
            }
        }
        
        // Finally, remove the vertex itself
        boolean removed = vertices.remove(vertex);
        checkRep();
        return removed;
    }
    
    @Override 
    public Set<String> vertices() {
        // Return an unmodifiable *copy* to prevent rep exposure
        return Collections.unmodifiableSet(new HashSet<>(vertices));
    }
    
    @Override 
    public Map<String, Integer> sources(String target) {
        Map<String, Integer> sourcesMap = new HashMap<>();
        
        // Search the entire list of edges
        for (Edge edge : edges) {
            if (edge.getTarget().equals(target)) {
                sourcesMap.put(edge.getSource(), edge.getWeight());
            }
        }
        
        // This is a new map, so it's safe to return
        return sourcesMap;
    }
    
    @Override 
    public Map<String, Integer> targets(String source) {
        Map<String, Integer> targetsMap = new HashMap<>();
        
        // Search the entire list of edges
        for (Edge edge : edges) {
            if (edge.getSource().equals(source)) {
                targetsMap.put(edge.getTarget(), edge.getWeight());
            }
        }
        
        // This is a new map, so it's safe to return
        return targetsMap;
    }
    
    /**
     * @return a human-readable string representation of this graph,
     * listing all vertices and all edges.
     */
    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph:\n  Vertices: ").append(vertices.toString()).append("\n");
        sb.append("  Edges:\n");
        if (edges.isEmpty()) {
            sb.append("    (none)\n");
        }
        for (Edge e : edges) {
            sb.append("    ").append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}

/**
 * An immutable weighted directed edge.
 * * This class is a "static nested class" of ConcreteEdgesGraph. It is
 * static because it does not need to access any fields of its
 * enclosing ConcreteEdgesGraph instance.
 * * <p>PS2 instructions: this class is immutable.
 */
class Edge {
    
    private final String source;
    private final String target;
    private final int weight;
    
    // Abstraction Function:
    //   AF(source, target, weight) = A directed edge from `source` to `target`
    //                                with a positive integer weight of `weight`.
    //
    // Representation Invariant:
    //   RI(source, target, weight) =
    //     - `source` is not null.
    //     - `target` is not null.
    //     - `weight` is positive (weight > 0).
    //
    // Safety from Rep Exposure:
    //   - All fields are private and final.
    //   - All fields are immutable types (String, int).
    //   - Observer methods (`getSource`, `getTarget`, `getWeight`) return
    //     the values of these immutable fields directly.
    
    /**
     * Create a new edge.
     * * @param source source vertex label, must be non-null.
     * @param target target vertex label, must be non-null.
     * @param weight positive edge weight.
     * @throws IllegalArgumentException if source or target are null,
     * or if weight is not positive.
     */
    public Edge(String source, String target, int weight) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Vertex labels cannot be null");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Edge weight must be positive");
        }
        
        this.source = source;
        this.target = target;
        this.weight = weight;
        checkRep();
    }
    
    /**
     * Asserts that the representation invariant holds.
     */
    private void checkRep() {
        assert source != null;
        assert target != null;
        assert weight > 0;
    }
    
    /**
     * @return the source vertex of this edge.
     */
    public String getSource() {
        return source;
    }
    
    /**
     * @return the target vertex of this edge.
     */
    public String getTarget() {
        return target;
    }
    
    /**
     * @return the weight of this edge.
     */
    public int getWeight() {
        return weight;
    }
    
    /**
     * @return a string representation of this edge in the format:
     * "source -> target (weight)"
     */
    @Override 
    public String toString() {
        return source + " -> " + target + " (" + weight + ")";
    }
}