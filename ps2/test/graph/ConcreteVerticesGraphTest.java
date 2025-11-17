/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Collections;

import org.junit.Test;

/**
 * Tests for ConcreteVerticesGraph.
 * * This class runs the GraphInstanceTest tests against ConcreteVerticesGraph, as
 * well as tests for that particular implementation.
 * * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteVerticesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteVerticesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteVerticesGraph();
    }
    
    /*
     * Testing ConcreteVerticesGraph...
     */
    
    // Testing strategy for ConcreteVerticesGraph.toString()
    //
    // Partition on graph state:
    //   - Empty graph
    //   - Graph with vertices, but no edges
    //   - Graph with vertices and edges
    
    @Test
    public void testToStringEmptyGraph() {
        Graph<String> graph = emptyInstance();
        String str = graph.toString();
        assertTrue("toString() should mention 'Graph'", str.contains("Graph:"));
        assertTrue("toString() should mention 'empty'", str.contains("(empty)"));
    }

    @Test
    public void testToStringVerticesNoEdges() {
        Graph<String> graph = emptyInstance();
        graph.add("A");
        graph.add("B");
        String str = graph.toString();
        
        // Check that vertices are listed, but no edges
        assertTrue("toString() should contain A", str.contains("\n  A\n") || str.contains("\n  A"));
        assertTrue("toString() should contain B", str.contains("\n  B\n") || str.contains("\n  B"));
        assertFalse("toString() should not contain '->'", str.contains("->"));
    }

    @Test
    public void testToStringWithEdges() {
        Graph<String> graph = emptyInstance();
        graph.set("A", "B", 10);
        String str = graph.toString();
        
        // Check that the vertex's toString() is used
        assertTrue("toString() should contain A -> {B(10)}", str.contains("A -> {B(10)}"));
    }
    
    /*
     * Testing Vertex...
     */
    
    // Testing strategy for Vertex (mutable)
    //
    // Partition on Vertex constructor:
    //   - Input: (valid label) -> OK
    //   - Exception: (null label) -> IllegalArgumentException
    //
    // Partition on getLabel():
    //   - Check it returns the constructor-provided label.
    //
    // Partition on setEdgeTo():
    //   - Add new edge (weight > 0), check return (should be 0)
    //   - Update existing edge (weight > 0), check return (should be prevWeight)
    //   - Remove existing edge (weight == 0), check return (should be prevWeight)
    //   - "Remove" non-existent edge (weight == 0), check return (should be 0)
    //
    // Partition on getWeightTo():
    //   - Edge exists, check weight
    //   - Edge does not exist, check for 0
    //
    // Partition on getOutgoingEdges():
    //   - State: 0 edges, 1 edge, multiple edges
    //   - Check for rep exposure:
    //     - Get the map, try to modify it, expect UnsupportedOperationException
    //
    // Partition on removeEdgeTo():
    //   - Edge exists, remove it, check getWeightTo == 0
    //   - Edge does not exist, call remove, check no errors
    //
    // Partition on toString():
    //   - Vertex with 0 outgoing edges
    //   - Vertex with 1+ outgoing edges
    
    @Test
    public void testVertexConstructorAndGetLabel() {
        Vertex v = new Vertex("A");
        assertEquals("Label should be A", "A", v.getLabel());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testVertexConstructorNullLabel() {
        // This should throw IllegalArgumentException
        new Vertex(null);
    }
    
    @Test
    public void testVertexSetEdgeAddAndUpdate() {
        Vertex v = new Vertex("A");
        
        // Add new edge
        int prevWeight = v.setEdgeTo("B", 10);
        assertEquals("Adding new edge should return 0", 0, prevWeight);
        assertEquals("getWeightTo should return 10", 10, v.getWeightTo("B"));
        
        // Update existing edge
        prevWeight = v.setEdgeTo("B", 20);
        assertEquals("Updating edge should return 10", 10, prevWeight);
        assertEquals("getWeightTo should return 20", 20, v.getWeightTo("B"));
    }
    
    @Test
    public void testVertexSetEdgeRemove() {
        Vertex v = new Vertex("A");
        v.setEdgeTo("B", 10); // Add
        
        // Remove existing edge
        int prevWeight = v.setEdgeTo("B", 0);
        assertEquals("Removing edge should return 10", 10, prevWeight);
        assertEquals("getWeightTo should be 0", 0, v.getWeightTo("B"));
        
        // "Remove" non-existent edge
        prevWeight = v.setEdgeTo("C", 0);
        assertEquals("Removing non-existent edge should return 0", 0, prevWeight);
    }
    
    @Test
    public void testVertexRemoveEdgeTo() {
        Vertex v = new Vertex("A");
        v.setEdgeTo("B", 10);
        
        // Remove existing edge
        v.removeEdgeTo("B");
        assertEquals("Edge should be gone", 0, v.getWeightTo("B"));
        
        // Remove non-existent edge (should not crash)
        v.removeEdgeTo("C");
        assertEquals("No change expected", 0, v.getWeightTo("C"));
    }
    
    @Test
    public void testVertexGetOutgoingEdges() {
        Vertex v = new Vertex("A");
        v.setEdgeTo("B", 10);
        v.setEdgeTo("C", 20);
        
        Map<String, Integer> targets = v.getOutgoingEdges();
        Map<String, Integer> expected = Map.of("B", 10, "C", 20);
        
        assertEquals("Map contents should be correct", expected, targets);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testVertexGetOutgoingEdgesRepExposure() {
        Vertex v = new Vertex("A");
        v.setEdgeTo("B", 10);
        Map<String, Integer> targets = v.getOutgoingEdges();
        
        // This should fail if the map is properly unmodifiable
        targets.put("D", 50); 
    }
    
    @Test
    public void testVertexToString() {
        Vertex v = new Vertex("A");
        assertEquals("toString for vertex with no edges", "A", v.toString());
        
        v.setEdgeTo("B", 10);
        v.setEdgeTo("C", 20);
        String str = v.toString();
        
        // Order in map isn't guaranteed, so check for contains
        assertTrue("toString should contain A", str.contains("A"));
        assertTrue("toString should contain B(10)", str.contains("B(10)"));
        assertTrue("toString should contain C(20)", str.contains("C(20)"));
    }
}