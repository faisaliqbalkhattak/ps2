/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for ConcreteEdgesGraph.
 * * This class runs the GraphInstanceTest tests against ConcreteEdgesGraph, as
 * well as tests for that particular implementation.
 * * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteEdgesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteEdgesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteEdgesGraph();
    }
    
    /*
     * Testing ConcreteEdgesGraph...
     */
    
    // Testing strategy for ConcreteEdgesGraph.toString()
    //
    // Partition on graph state:
    //   - Empty graph (no vertices, no edges)
    //   - Graph with vertices, but no edges
    //   - Graph with vertices and one edge
    //   - Graph with vertices and multiple edges
    
    @Test
    public void testToStringEmptyGraph() {
        Graph<String> graph = emptyInstance();
        String str = graph.toString();
        
        // We check for 'contains' to be flexible about formatting (e.g., whitespace)
        // and order (e.g., Set order isn't guaranteed)
        assertTrue("toString() should mention 'Vertices'", str.contains("Vertices: []"));
        assertTrue("toString() should mention 'Edges'", str.contains("Edges:"));
        assertTrue("toString() should mention 'none' for empty edges", str.contains("(none)"));
    }

    @Test
    public void testToStringVerticesNoEdges() {
        Graph<String> graph = emptyInstance();
        graph.add("A");
        graph.add("B");
        String str = graph.toString();
        
        // Use regex to check for A and B inside the vertices list
        assertTrue("toString() should contain 'A'", str.matches("(?s).*Vertices:.*[AB].*[AB].*"));
        assertTrue("toString() should contain 'B'", str.matches("(?s).*Vertices:.*[AB].*[AB].*"));
        assertTrue("toString() should mention 'none' for empty edges", str.contains("(none)"));
    }

    @Test
    public void testToStringWithEdges() {
        Graph<String> graph = emptyInstance();
        graph.set("A", "B", 10);
        graph.set("B", "C", 20);
        String str = graph.toString();
        
        // Check that the edge representations are present
        assertTrue("toString() should contain A -> B (10)", str.contains("A -> B (10)"));
        assertTrue("toString() should contain B -> C (20)", str.contains("B -> C (20)"));
    }
    
    /*
     * Testing Edge...
     */
    
    // Testing strategy for Edge
    //
    // Partition on Edge constructor:
    //   - Inputs: (valid source, valid target, valid weight > 0) -> OK
    //   - Exception: (null source) -> IllegalArgumentException
    //   - Exception: (null target) -> IllegalArgumentException
    //   - Exception: (weight == 0) -> IllegalArgumentException
    //   - Exception: (weight < 0) -> IllegalArgumentException
    //
    // Partition on observers (getSource, getTarget, getWeight):
    //   - Check that they return the same values passed to the constructor.
    //
    // Partition on toString():
    //   - Check for the exact string format "source -> target (weight)".
    
    @Test
    public void testEdgeConstructorAndObservers() {
        Edge edge = new Edge("A", "B", 5);
        assertEquals("Source should be A", "A", edge.getSource());
        assertEquals("Target should be B", "B", edge.getTarget());
        assertEquals("Weight should be 5", 5, edge.getWeight());
    }
    
    @Test
    public void testEdgeToString() {
        Edge edge = new Edge("Src", "Tgt", 10);
        assertEquals("toString() format is incorrect", "Src -> Tgt (10)", edge.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEdgeConstructorNullSource() {
        // This should throw IllegalArgumentException
        new Edge(null, "B", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEdgeConstructorNullTarget() {
        // This should throw IllegalArgumentException
        new Edge("A", null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEdgeConstructorZeroWeight() {
        // This should throw IllegalArgumentException
        new Edge("A", "B", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEdgeConstructorNegativeWeight() {
        // This should throw IllegalArgumentException
        new Edge("A", "B", -1);
    }
}