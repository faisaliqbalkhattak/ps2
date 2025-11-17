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
public class ConcreteVerticesGraph implements Graph<String> {
    
    private final List<Vertex> vertices = new ArrayList<>();
    
    // Abstraction Function:
    //   AF(vertices) = A directed graph where:
    //     - The set of vertices is the set of all labels from the Vertex objects
    //       in the `vertices` list.
    //     - The set of edges is the union of all outgoing edges stored within
    //       each Vertex object in the `vertices` list.
    //
    // Representation Invariant:
    //   RI(vertices) =
    //     - `vertices` is not null.
    //     - `vertices` contains no null elements.
    //     - No two Vertex objects in the `vertices` list have the same label
    //       (i.e., vertex labels are unique).
    //     - Every Vertex in `vertices` satisfies its own rep invariant.
    //
    // Safety from Rep Exposure:
    //   - The `vertices` field is private and final.
    //   - The `Vertex` class is a private static nested class, so clients
    //     cannot see or interact with `Vertex` objects directly.
    //   - All methods that return collections (`vertices()`, `sources()`, `targets()`)
    //     build and return *new, unmodifiable copies* of the data. They never
    //     return references to the internal `vertices` list or the internal
    //     maps within the `Vertex` objects.
    
    /**
     * Create an empty graph.
     */
    public ConcreteVerticesGraph() {
        // No setup needed, field is initialized
        checkRep();
    }
    
    /**
     * Asserts that the representation invariant holds.
     */
    private void checkRep() {
        assert vertices != null;
        
        Set<String> vertexLabels = new HashSet<>();
        for (Vertex v : vertices) {
            assert v != null;
            assert !vertexLabels.contains(v.getLabel()) : "Duplicate vertex label found";
            vertexLabels.add(v.getLabel());
            
            // Also check the RI of the vertex itself
            v.checkRep();
        }
    }

    /**
     * Finds and returns the Vertex object with the given label.
     * * @param label the label of the vertex to find, must be non-null.
     * @return the Vertex object, or null if not found.
     */
    private Vertex findVertex(String label) {
        for (Vertex v : vertices) {
            if (v.getLabel().equals(label)) {
                return v;
            }
        }
        return null;
    }

    @Override 
    public boolean add(String vertex) {
        if (findVertex(vertex) != null) {
            return false;
        }
        
        vertices.add(new Vertex(vertex));
        checkRep();
        return true;
    }
    
    @Override 
    public int set(String source, String target, int weight) {
        // Find the source vertex, or create it if it doesn't exist.
        Vertex sourceVertex = findVertex(source);
        if (sourceVertex == null) {
            sourceVertex = new Vertex(source);
            vertices.add(sourceVertex);
        }
        
        // Ensure the target vertex exists *if* we are adding/updating an edge.
        // We don't need to create it if weight is 0 (removing an edge).
        if (weight > 0 && findVertex(target) == null) {
            vertices.add(new Vertex(target));
        }
        
        // Let the Vertex object handle setting the edge
        int previousWeight = sourceVertex.setEdgeTo(target, weight);
        
        checkRep();
        return previousWeight;
    }
    
    @Override 
    public boolean remove(String vertex) {
        Vertex vertexToRemove = findVertex(vertex);
        if (vertexToRemove == null) {
            return false; // Vertex not in graph
        }
        
        // Remove the vertex from the main list
        vertices.remove(vertexToRemove);
        
        // Now, we must iterate over all *other* vertices and remove any
        // edges they had that pointed to the removed vertex.
        for (Vertex v : vertices) {
            v.removeEdgeTo(vertex);
        }
        
        checkRep();
        return true;
    }
    
    @Override 
    public Set<String> vertices() {
        Set<String> labels = new HashSet<>();
        for (Vertex v : vertices) {
            labels.add(v.getLabel());
        }
        // Return an unmodifiable copy
        return Collections.unmodifiableSet(labels);
    }
    
    @Override 
    public Map<String, Integer> sources(String target) {
        Map<String, Integer> sourcesMap = new HashMap<>();
        
        // Check every vertex to see if it has an edge to the target
        for (Vertex v : vertices) {
            int weight = v.getWeightTo(target);
            if (weight > 0) {
                sourcesMap.put(v.getLabel(), weight);
            }
        }
        
        // This is a new map, so it's safe to return
        return sourcesMap;
    }
    
    @Override 
    public Map<String, Integer> targets(String source) {
        Vertex sourceVertex = findVertex(source);
        
        if (sourceVertex == null) {
            // If the source vertex doesn't exist, it has no targets
            return Collections.emptyMap();
        }
        
        // Ask the vertex for its targets and return a *copy*
        return sourceVertex.getOutgoingEdges();
    }
    
    /**
     * @return a human-readable string representation of this graph,
     * listing all vertices and their outgoing edges.
     */
    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph:\n");
        if (vertices.isEmpty()) {
            sb.append("  (empty)\n");
        }
        for (Vertex v : vertices) {
            sb.append("  ").append(v.toString()).append("\n");
        }
        return sb.toString();
    }
}


/**
 * A mutable vertex in a directed graph.
 * * This class is a "static nested class" of ConcreteVerticesGraph. It is
 * static because it does not need to access any fields of its
 * enclosing ConcreteVerticesGraph instance.
 * * <p>PS2 instructions: this class is mutable.
 */
class Vertex {

    private final String label;
    private final Map<String, Integer> outgoingEdges;
    
    // Abstraction Function:
    //   AF(label, outgoingEdges) = A vertex named `label`. It has a directed,
    //     weighted edge to every vertex `v` that is a key in `outgoingEdges`,
    //     with the weight given by `outgoingEdges.get(v)`.
    //
    // Representation Invariant:
    //   RI(label, outgoingEdges) =
    //     - `label` is not null.
    //     - `outgoingEdges` is not null.
    //     - `outgoingEdges` contains no null keys.
    //     - All values (weights) in `outgoingEdges` are positive (weight > 0).
    //
    // Safety from Rep Exposure:
    //   - `label` is private, final, and immutable (String).
    //   - `outgoingEdges` is private and final.
    //   - The mutator methods (`setEdgeTo`, `removeEdgeTo`) carefully control
    //     changes to the `outgoingEdges` map.
    //   - The observer method `getOutgoingEdges()` returns a *new, unmodifiable copy*
    //     of the map, not a reference to the internal map, preventing clients
    //     from changing the vertex's state unexpectedly.
    
    /**
     * Create a new vertex with no outgoing edges.
     * * @param label non-null vertex label.
     */
    public Vertex(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Vertex label cannot be null");
        }
        this.label = label;
        this.outgoingEdges = new HashMap<>();
        checkRep();
    }
    
    /**
     * Asserts that the representation invariant holds.
     */
    public void checkRep() {
        assert label != null;
        assert outgoingEdges != null;
        for (Map.Entry<String, Integer> entry : outgoingEdges.entrySet()) {
            assert entry.getKey() != null;
            assert entry.getValue() != null;
            assert entry.getValue() > 0;
        }
    }
    
    /**
     * @return the label of this vertex.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Add, update, or remove an outgoing edge from this vertex.
     * * @param target the label of the target vertex.
     * @param weight the new weight. If 0, the edge is removed. If > 0,
     * the edge is added or its weight is updated.
     * @return the previous weight of the edge, or 0 if there was no edge.
     */
    public int setEdgeTo(String target, int weight) {
        int previousWeight = 0;
        
        if (outgoingEdges.containsKey(target)) {
            previousWeight = outgoingEdges.get(target);
        }
        
        if (weight > 0) {
            outgoingEdges.put(target, weight);
        } else {
            // Weight is 0, so remove the edge if it exists
            outgoingEdges.remove(target);
        }
        
        checkRep();
        return previousWeight;
    }
    
    /**
     * Remove an outgoing edge from this vertex, if it exists.
     * * @param target the label of the target vertex.
     */
    public void removeEdgeTo(String target) {
        outgoingEdges.remove(target);
        checkRep();
    }
    
    /**
     * Get the weight of the outgoing edge to a target vertex.
     * * @param target the label of the target vertex.
     * @return the weight of the edge, or 0 if no edge exists.
     */
    public int getWeightTo(String target) {
        // .getOrDefault is perfect here. If no edge, returns 0.
        return outgoingEdges.getOrDefault(target, 0);
    }

    /**
     * Get all outgoing edges from this vertex.
     * * @return an unmodifiable map from target vertex labels to
     * positive edge weights.
     */
    public Map<String, Integer> getOutgoingEdges() {
        // Return an unmodifiable *copy* to prevent rep exposure
        return Collections.unmodifiableMap(new HashMap<>(outgoingEdges));
    }
    
    /**
     * @return a string representation of this vertex and its outgoing edges.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(label);
        if (!outgoingEdges.isEmpty()) {
            sb.append(" -> {");
            for (Map.Entry<String, Integer> entry : outgoingEdges.entrySet()) {
                sb.append(entry.getKey()).append("(").append(entry.getValue()).append("), ");
            }
            // Remove last ", "
            sb.setLength(sb.length() - 2);
            sb.append("}");
        }
        return sb.toString();
    }
}