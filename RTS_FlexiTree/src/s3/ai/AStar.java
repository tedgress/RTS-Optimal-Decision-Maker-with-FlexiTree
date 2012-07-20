package s3.ai;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import s3.base.S3;
import s3.entities.S3PhysicalEntity;
import s3.util.Pair;

/**
 * Implementation of A* Algorithm We follow the traditional approach by
 * including two lists of nodes - OPEN and CLOSED
 */

public class AStar {
	protected HashSet<Node> OPEN;

	protected HashSet<Node> CLOSED;

	protected Node startNode;

	protected Node goalNode;

	// protected List <Pair<Node,Node>> cameFromList;
	protected HashMap<Node, Node> cameFromHashMap;

	protected S3 s2;

	protected S3PhysicalEntity entity;
	
	
	public static int pathDistance(double start_x, double start_y, double goal_x, double goal_y,
			S3PhysicalEntity i_entity, S3 the_game) {
		AStar a = new AStar(start_x,start_y,goal_x,goal_y,i_entity,the_game);
		List<Pair<Double, Double>> path = a.computePath();
		if (path!=null) return path.size();
		return -1;
	}

	public AStar(double start_x, double start_y, double goal_x, double goal_y,
			S3PhysicalEntity i_entity, S3 the_game) {
		OPEN = new HashSet<Node>();
		CLOSED = new HashSet<Node>();
		startNode = new Node(start_x, start_y);
		goalNode = new Node(goal_x, goal_y);
		// Add the startNode to the OPEN set
		OPEN.add(startNode);
		// Make the returnList
		cameFromHashMap = new HashMap<Node, Node>();
		entity = (S3PhysicalEntity) (i_entity.clone());
		s2 = the_game;
	}

	public List<Pair<Double, Double>> computePath() {
		while (OPEN.size() > 0) {
			Node chosenNode = retrieveNodeWithSmallestDistanceFromOPEN();
			if (chosenNode.equals(goalNode)) {
				return reconstructPath(chosenNode);
			}

			OPEN.remove(chosenNode);
			CLOSED.add(chosenNode);
			for (Node neighborNode : chosenNode.getNeighbors(s2.getMap().getWidth(), s2.getMap()
					.getHeight())) {
				if (CLOSED.contains(neighborNode))
					continue;
				double tentative_g_score = chosenNode.g_score + 1;
				boolean tentative_is_better = false;
				if (OPEN.contains(neighborNode) == false) {
					OPEN.add(neighborNode);
					neighborNode.compute_heuristic_distance_to_goal();
					tentative_is_better = true;
				} else if (tentative_g_score < neighborNode.g_score)
					tentative_is_better = true;
				if (tentative_is_better == true) {
					cameFromHashMap.put(neighborNode, chosenNode);
					neighborNode.g_score = tentative_g_score;
				}
			}
		}
		return null;
	}
	
	private List<Pair<Double, Double>> reconstructPath(Node currentNode) {
		// System.out.println("\tNode (" + currentNode.x + "," + currentNode.y +
		// ")");
		if (cameFromHashMap.containsKey(currentNode)) {
			// System.out.println("\tNode (" + currentNode.x + "," +
			// currentNode.y + ")");
			List<Pair<Double, Double>> returnList = new ArrayList<Pair<Double, Double>>();
			returnList = reconstructPath(cameFromHashMap.get(currentNode));
			if (returnList != null)
				returnList.add(new Pair<Double, Double>(currentNode.x, currentNode.y));
			else {
				returnList = new ArrayList<Pair<Double, Double>>();
				returnList.add(new Pair<Double, Double>(currentNode.x, currentNode.y));
			}
			return returnList;
		}
		return null;
	}

	private Node retrieveNodeWithSmallestDistanceFromOPEN() {
		Node chosenNode = null;
		for (Node n : OPEN) {
			if (chosenNode==null || n.getTotalNodeScore() < chosenNode.getTotalNodeScore()) {
				chosenNode = n;
			}
		}
		return chosenNode;
	}

	protected class Node {
		private double x;

		private double y;

		// distance to this node
		private double g_score = 10000;

		// heuristic distance to goal node
		private double h_score = 10000;

		public Node(double node_x, double node_y) {
			x = node_x;
			y = node_y;
		}

		public void compute_heuristic_distance_to_goal() {
			h_score = computeDistance(this, goalNode);
		}

		public List<Node> getNeighbors(int width, int height) {
			// Add all the 9 neighbors of the node after checking to see if they
			// can be reached or not
			List<Node> neighborList = new ArrayList<Node>();
			for (int i = -1; i <= 1; i++)
				for (int j = -1; j <= 1; j++) {
					if (!((i == 0 && j == 0) || (x + i) < 0 || (y + j) < 0 || (x + i) >= width || (y + j) >= height)) {
						entity.setX((int) x + i);
						entity.setY((int) y + j);
						// perform check to see if the location is OK or not
						if (s2.anyLevelCollision(entity)==null) {
    						Node newNode = new Node(x + i, y + j);
							neighborList.add(newNode);
                        }
					}
				}

			return neighborList;
		}

		public void set_g_score(double i_g_score) {
			g_score = i_g_score;
		}

		public double getTotalNodeScore() {
			return h_score + g_score;
		}

		private double computeDistance(Node node1, Node node2) {
			return Math.sqrt((node1.x - node2.x) * (node1.x - node2.x) + (node1.y - node2.y)
					* (node1.y - node2.y));
		}

		public boolean equals(Object incoming) {
			Node n = (Node) incoming;
			if (this.x == n.x && this.y == n.y)
				return true;
			return false;
		}

        public int hashCode() {
            return (int)(x+(y*128));
        }

	}

}
