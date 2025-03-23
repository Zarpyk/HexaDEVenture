package com.hexadeventure.adapter.out.pathfinder;

import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.model.map.Vector2;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AStarAdapter implements AStarPathfinder {
    
    @Override
    public Queue<Vector2> generatePath(Vector2 start, Vector2 end, Map<Vector2, Integer> costMap) {
        if(start == null || end == null || costMap.isEmpty()) return null;
        
        PriorityQueue<Node> checklist = new PriorityQueue<>();
        HashSet<Vector2> checkedPosition = new HashSet<>();
        HashMap<Vector2, Node> nodeCache = new HashMap<>();
        
        Node startNode = new Node(start, null, 0, manhattanDistance(start, end));
        checklist.add(startNode);
        nodeCache.put(start, startNode);
        
        while (!checklist.isEmpty()) {
            Node current = checklist.poll();
            
            if(current.position.equals(end)) {
                return getFinalPath(current);
            }
            
            checkedPosition.add(current.position);
            
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                Vector2 positionToCheck = new Vector2(current.position.x + dir[0],
                                                      current.position.y + dir[1]);
                
                // If the position has already been checked or is out of bounds, skip it
                if(checkedPosition.contains(positionToCheck) || !isValidPosition(positionToCheck, costMap)) continue;
                
                // Check if the position can't be accessed
                Integer cost = costMap.get(positionToCheck);
                if(cost < 0) continue;
                
                int nodeCost = current.actualCost + cost;
                
                Node checkNode = nodeCache.get(positionToCheck);
                if(checkNode == null) {
                    // If the node doesn't exist, create it
                    checkNode = new Node(positionToCheck, current, nodeCost, manhattanDistance(positionToCheck, end));
                    nodeCache.put(positionToCheck, checkNode);
                    checklist.add(checkNode);
                } else if(nodeCost < checkNode.actualCost) {
                    // If the existing node has a higher cost, update it
                    checkNode.parent = current;
                    checkNode.actualCost = nodeCost;
                    checkNode.totalCost = nodeCost + checkNode.heuristic;
                    if(!checklist.contains(checkNode)) {
                        checklist.add(checkNode);
                    }
                }
            }
        }
        
        return null;
    }
    
    private static class Node implements Comparable<Node> {
        Vector2 position;
        Node parent;
        int actualCost;
        int heuristic;
        int totalCost;
        
        Node(Vector2 pos, Node parent, int actualCost, int heuristic) {
            this.position = pos;
            this.parent = parent;
            this.actualCost = actualCost;
            this.heuristic = heuristic;
            this.totalCost = actualCost + heuristic;
        }
        
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.totalCost, other.totalCost);
        }
    }
    
    private int manhattanDistance(Vector2 start, Vector2 end) {
        return Math.abs(end.x - start.x) + Math.abs(end.y - start.y);
    }
    
    private boolean isValidPosition(Vector2 pos, Map<Vector2, Integer> map) {
        return map.containsKey(pos);
    }
    
    private Queue<Vector2> getFinalPath(Node end) {
        LinkedList<Vector2> path = new LinkedList<>();
        Node current = end;
        while (current != null) {
            path.addFirst(current.position);
            current = current.parent;
        }
        return path;
    }
}
