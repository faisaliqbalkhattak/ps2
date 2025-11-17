/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;


import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class GraphInstanceTest {
    
    /**
     * Overridden by implementation-specific test classes.
     * * @return a new empty graph of the particular implementation being tested
     */
    public abstract Graph<String> emptyInstance();
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Tests for vertices() (related to initial state)
    
    // Testing strategy for vertices()
    //
    // We partition the state of the graph:
    //   - new, empty graph
    //   - graph after adding vertices
    //   - graph after removing vertices
    //
    // (Tests for add/remove cases are covered in their respective sections)
    
    @Test
    public void testInitialVerticesEmpty() {
        // This test is provided for you
        assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
    }
    
        // --- Tests for add()
    
    // Testing strategy for add(vertex)
    //
    // We partition the inputs based on:
    //   - vertex:
    //     - new to the graph
    //     - already in the graph (duplicate)
    //   - graph state:
    //     - empty
    //     - non-empty
    //
    // We'll test these combinations:
    //   - add new vertex to empty graph
    //   - add duplicate vertex to non-empty graph
    //   - add multiple new vertices to non-empty graph
    
    @Test
    public void testAddSingleVertex() {
        Graph<String> graph = emptyInstance();
        String vertex = "A";
        
        assertTrue("add() should return true for a new vertex", graph.add(vertex));
        assertEquals("graph should contain 1 vertex", 1, graph.vertices().size());
        assertTrue("graph should contain the added vertex", graph.vertices().contains(vertex));
    }
    
    @Test
    public void testAddDuplicateVertex() {
        Graph<String> graph = emptyInstance();
        String vertex = "A";
        
        graph.add(vertex); // Add it the first time
        
        assertFalse("add() should return false for a duplicate vertex", graph.add(vertex));
        assertEquals("graph should still contain only 1 vertex", 1, graph.vertices().size());
    }

    @Test
    public void testAddMultipleVertices() {
        Graph<String> graph = emptyInstance();
        
        assertTrue("add() 'A' should return true", graph.add("A"));
        assertTrue("add() 'B' should return true", graph.add("B"));
        
        Set<String> expectedVertices = Set.of("A", "B");
        assertEquals("graph should contain 2 vertices", 2, graph.vertices().size());
        assertEquals("graph should contain correct vertices", expectedVertices, graph.vertices());
    }

    
    // Tests for set()
    
    // Testing strategy for set(source, target, weight)
    //
    // We partition based on:
    //   - weight:
    //     - weight > 0 (add or update an edge)
    //     - weight == 0 (remove an edge)
    //
    //   - edge state (when weight > 0):
    //     - edge is new
    //     - edge already exists (update weight)
    //
    //   - edge state (when weight == 0):
    //     - edge exists
    //     - edge does not exist
    //
    //   - vertices (source, target):
    //     - both already in graph
    //     - one or both are new to the graph
    //
    //   - edge type:
    //     - regular edge (A -> B)
    //     - self-loop (A -> A)
    
    @Test
    public void testSetAddNewEdge() {
        Graph<String> graph = emptyInstance();
        
        // This test covers: weight > 0, edge is new, vertices are new
        int prevWeight = graph.set("A", "B", 5);
        
        assertEquals("expected previous weight to be 0", 0, prevWeight);
        assertEquals("graph should now have 2 vertices", 2, graph.vertices().size());
        assertTrue("vertices should include 'A'", graph.vertices().contains("A"));
        assertTrue("vertices should include 'B'", graph.vertices().contains("B"));
    }

    @Test
    public void testSetUpdateEdgeWeight() {
        Graph<String> graph = emptyInstance();
        graph.set("A", "B", 5); // Add edge
        
        // This test covers: weight > 0, edge already exists
        int prevWeight = graph.set("A", "B", 10); // Update edge
        
        assertEquals("expected previous weight to be 5", 5, prevWeight);
        assertEquals("targets() map should show new weight", 
                     Map.of("B", 10), graph.targets("A"));
    }
    
    @Test
    public void testSetRemoveEdge() {
        Graph<String> graph = emptyInstance();
        graph.set("A", "B", 5); // Add edge
        
        // This test covers: weight == 0, edge exists
        int prevWeight = graph.set("A", "B", 0); // Remove edge
        
        assertEquals("expected previous weight to be 5", 5, prevWeight);
        assertTrue("targets() map should be empty after edge removal",
                   graph.targets("A").isEmpty());
    }

    @Test
    public void testSetAddSelfLoop() {
        Graph<String> graph = emptyInstance();
        
        // This test covers: weight > 0, edge is new, self-loop
        int prevWeight = graph.set("A", "A", 7);
        
        assertEquals("expected previous weight to be 0", 0, prevWeight);
        assertEquals("targets() map should show self-loop",
                     Map.of("A", 7), graph.targets("A"));
        assertEquals("sources() map should show self-loop",
                     Map.of("A", 7), graph.sources("A"));
    }

    @Test
    public void testSetRemoveNonExistentEdge() {
        Graph<String> graph = emptyInstance();
        graph.add("A");
        graph.add("B");
        
        // This test covers: weight == 0, edge does not exist
        int prevWeight = graph.set("A", "B", 0); // Try to remove edge that isn't there
        
        assertEquals("expected previous weight to be 0", 0, prevWeight);
        assertTrue("targets() map should remain empty", graph.targets("A").isEmpty());
    }

   
    // --- Tests for remove()
    // Testing strategy for remove(vertex)
    //
    // We partition based on:
    //   - vertex:
    //     - exists in the graph
    //     - does not exist in the graph
    //
    //   - vertex's edge state (if it exists):
    //     - no incoming or outgoing edges
    //     - has only outgoing edges (these should be removed)
    //     - has only incoming edges (these should be removed)
    //     - has both incoming and outgoing edges (all should be removed)
    
    @Test
    public void testRemoveNonExistentVertex() {
        Graph<String> graph = emptyInstance();
        graph.add("A");
        
        // This test covers: vertex does not exist
        assertFalse("remove() should return false for non-existent vertex",
                    graph.remove("B"));
        assertEquals("graph size should not change", 1, graph.vertices().size());
    }

    @Test
    public void testRemoveVertexNoEdges() {
        Graph<String> graph = emptyInstance();
        graph.add("A");
        
        // This test covers: vertex exists, no edges
        assertTrue("remove() should return true for existing vertex",
                   graph.remove("A"));
        assertTrue("graph should be empty after removal",
                   graph.vertices().isEmpty());
    }
    
    @Test
    public void testRemoveVertexWithEdges() {
        Graph<String> graph = emptyInstance();
        // A -> B -> C
        // D -> B
        graph.set("A", "B", 1);
        graph.set("B", "C", 2);
        graph.set("D", "B", 3);
        
        // This test covers: vertex exists, has both incoming and outgoing edges
        assertTrue("remove() 'B' should return true", graph.remove("B"));
        
        // Check vertices
        Set<String> expectedVertices = Set.of("A", "C", "D");
        assertEquals("graph should have 3 vertices left", 3, graph.vertices().size());
        assertEquals("graph should contain correct remaining vertices",
                     expectedVertices, graph.vertices());
                     
        // Check edges are gone
        assertTrue("targets('A') should be empty (A -> B removed)",
                   graph.targets("A").isEmpty());
        assertTrue("sources('C') should be empty (B -> C removed)",
                   graph.sources("C").isEmpty());
        assertTrue("targets('D') should be empty (D -> B removed)",
                   graph.targets("D").isEmpty());
    }
    
    // --- Tests for sources() 
    
    // Testing strategy for sources(target)
    //
    // We partition based on:
    //   - target vertex:
    //     - does not exist in graph
    //     - exists in graph
    //
    //   - target's incoming edges (if it exists):
    //     - 0 incoming edges
    //     - 1 incoming edge
    //     - multiple incoming edges
    
    @Test
    public void testSourcesTargetDoesNotExist() {
        Graph<String> graph = emptyInstance();
        graph.add("A");
        
        // This test covers: target does not exist
        assertTrue("sources() for non-existent vertex should be empty",
                   graph.sources("B").isEmpty());
    }
    
    @Test
    public void testSourcesTargetHasNoSources() {
        Graph<String> graph = emptyInstance();
        graph.add("A"); // 'A' exists but has no incoming edges
        
        // This test covers: target exists, 0 incoming edges
        assertTrue("sources('A') should be empty", graph.sources("A").isEmpty());
    }
    
    @Test
    public void testSourcesTargetHasOneSource() {
        Graph<String> graph = emptyInstance();
        graph.set("A", "B", 5);
        
        // This test covers: target exists, 1 incoming edge
        Map<String, Integer> expectedSources = Map.of("A", 5);
        assertEquals("sources('B') should return map with 'A'",
                     expectedSources, graph.sources("B"));
    }
    
    @Test
    public void testSourcesTargetHasMultipleSources() {
        Graph<String> graph = emptyInstance();
        graph.set("A", "C", 1);
        graph.set("B", "C", 2);
        
        // This test covers: target exists, multiple incoming edges
        Map<String, Integer> expectedSources = Map.of("A", 1, "B", 2);
        assertEquals("sources('C') should return all sources",
                     expectedSources, graph.sources("C"));
    }
    
    // --- Tests for targets()
    
    // Testing strategy for targets(source)
    //
    // We partition based on:
    //   - source vertex:
    //     - does not exist in graph
    //     - exists in graph
    //
    //   - source's outgoing edges (if it exists):
    //     - 0 outgoing edges
    //     - 1 outgoing edge
    //     - multiple outgoing edges
    
    @Test
    public void testTargetsSourceDoesNotExist() {
        Graph<String> graph = emptyInstance();
        graph.add("A");
        
        // This test covers: source does not exist
        assertTrue("targets() for non-existent vertex should be empty",
                   graph.targets("B").isEmpty());
    }
    
    @Test
    public void testTargetsSourceHasNoTargets() {
        Graph<String> graph = emptyInstance();
        graph.add("A"); // 'A' exists but has no outgoing edges
        
        // This test covers: source exists, 0 outgoing edges
        assertTrue("targets('A') should be empty", graph.targets("A").isEmpty());
    }
    
    @Test
    public void testTargetsSourceHasOneTarget() {
        Graph<String> graph = emptyInstance();
        graph.set("A", "B", 5);
        
        // This test covers: source exists, 1 outgoing edge
        Map<String, Integer> expectedTargets = Map.of("B", 5);
        assertEquals("targets('A') should return map with 'B'",
                     expectedTargets, graph.targets("A"));
    }
    
    @Test
    public void testTargetsSourceHasMultipleTargets() {
        Graph<String> graph = emptyInstance();
        graph.set("A", "B", 1);
        graph.set("A", "C", 2);
        
        // This test covers: source exists, multiple outgoing edges
        Map<String, Integer> expectedTargets = Map.of("B", 1, "C", 2);
        assertEquals("targets('A') should return all targets",
                     expectedTargets, graph.targets("A"));
    }
    
   

    // --- Comprehensive "Story" Test 
    
    // This test checks the interaction and combination of multiple
    // methods in a sequence, simulating a real "story" of using the graph.
    
    @Test
    public void testGraphMutationStory() {
        Graph<String> graph = emptyInstance();
        
        // Initial state: empty
        assertTrue(graph.vertices().isEmpty());
        
        // Add A -> B (10) and A -> C (20)
        graph.set("A", "B", 10);
        graph.set("A", "C", 20);
        
        assertEquals(Set.of("A", "B", "C"), graph.vertices());
        assertEquals(Map.of("B", 10, "C", 20), graph.targets("A"));
        
        // Add B -> C (30)
        graph.set("B", "C", 30);
        assertEquals(Map.of("A", 20, "B", 30), graph.sources("C"));
        
        // Update A -> B (5)
        int prevWeight = graph.set("A", "B", 5);
        assertEquals("Previous A->B weight should be 10", 10, prevWeight);
        assertEquals(Map.of("B", 5, "C", 20), graph.targets("A"));
        
        // Remove vertex B
        assertTrue(graph.remove("B"));
        
        // Check state after removing B
        assertEquals(Set.of("A", "C"), graph.vertices());
        assertEquals("A -> B edge should be gone",
                     Map.of("C", 20), graph.targets("A"));
        assertEquals("B -> C edge should be gone",
                     Map.of("A", 20), graph.sources("C"));
                     
        // Remove edge A -> C
        prevWeight = graph.set("A", "C", 0);
        assertEquals("Previous A->C weight should be 20", 20, prevWeight);
        assertTrue(graph.targets("A").isEmpty());
        assertTrue(graph.sources("C").isEmpty());
    }
}