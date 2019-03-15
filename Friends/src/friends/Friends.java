package friends;
import java.io.*;
import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		if(g==null) {
			return null;
		}
		int person1Index = 0;
		int person2Index = 0;
		boolean containsP1 = false, containsP2 = false;
		for(int i=0;i<g.members.length;i++) {
			if(g.members[i].name.equals(p1)) {
				containsP1 = true;
				person1Index = i;
				break;
			}
		}
		if(containsP1==false) {
			return null;
		}
		for(int i=0;i<g.members.length;i++) {
			if(g.members[i].name.equals(p2)) {
				containsP2 = true;
				person2Index = i;
				break;
			}
		}
		if(containsP2==false) {
			return null;
		}
		ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
		findShortestPath(g.members,person1Index,person2Index,paths);
		ArrayList<Integer> shortestPathIndeces = paths.get(paths.size()-1);
		ArrayList<String> shortestPathNames = new ArrayList<String>();
		if(shortestPathIndeces.get(shortestPathIndeces.size()-1)==person2Index) {
			for(int i=0;i<shortestPathIndeces.size();i++) {
				shortestPathNames.add(g.members[shortestPathIndeces.get(i)].name);
			}
			return shortestPathNames;
		}
		return null;
	}
	
	private static void findShortestPath(Person[] people, int currentIndex, int targetIndex, ArrayList<ArrayList<Integer>> paths) {
		Queue<Integer> q = new Queue<Integer>();
		boolean[] visited = new boolean[people.length];
		int[] visitedFromWhere = new int[people.length];
		for(int i=0;i<people.length;i++) {
			visited[i]=false;
			visitedFromWhere[i]=-1;
		}
		q.enqueue(currentIndex);
		visited[currentIndex]=true;
		while(q.isEmpty()==false) {
			ArrayList<Integer> st = new ArrayList<Integer>();
			int thisOne = q.dequeue();
			if(paths.isEmpty()==false) {
				int fromWhere = visitedFromWhere[thisOne];
				for(int i=0;i<paths.size();i++) {
					ArrayList<Integer> thisPath = paths.get(i);
					if(thisPath.get(thisPath.size()-1)==fromWhere) {
						for(int j=0;j<paths.get(i).size();j++) {
							st.add(paths.get(i).get(j));
						}
						st.add(thisOne);
						paths.add(st);
						break;
					}
				}
			}
			else {
				st.add(thisOne);
				paths.add(st);
			}
			if(thisOne==targetIndex) {
				return;
			}
			for(Friend ptr = people[thisOne].first;ptr!=null;ptr=ptr.next) {
				if(visited[ptr.fnum]==false) {
					visited[ptr.fnum]=true;
					visitedFromWhere[ptr.fnum]=thisOne;
					q.enqueue(ptr.fnum);
				}
			}
		}
	}
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		if(g==null) {
			return null;
		}
		boolean exists = false;
		for(int i=0;i<g.members.length;i++) {
			if(g.members[i].student==true) {
				if(g.members[i].school.equals(school)) {
					exists=true;
					break;
				}
			}
		}
		if(exists==false) {
			return null;
		}
		ArrayList<ArrayList<Integer>> theCliques = new ArrayList<ArrayList<Integer>>();
		boolean[] visited = new boolean[g.members.length];
		for(int i=0;i<visited.length;i++) {
			visited[i]=false;
		}
		for(int i=0;i<g.members.length;i++) {
			if(g.members[i].student==true) {
				if(g.members[i].school.equals(school)&&visited[i]==false) {
					Queue<Integer> q = new Queue<Integer>();
					q.enqueue(i);
					visited[i]=true;
					ArrayList<Integer> cl = new ArrayList<Integer>();
					while(q.isEmpty()==false) {
						int thisOne = q.dequeue();
						cl.add(thisOne);
						for(Friend ptr = g.members[thisOne].first;ptr!=null;ptr=ptr.next) {
							if(g.members[ptr.fnum].student==true) {
								if(g.members[ptr.fnum].school.equals(school)&&visited[ptr.fnum]==false) {
									visited[ptr.fnum]=true;
									q.enqueue(ptr.fnum);
								}
							}
						}
					}
					theCliques.add(0,cl);
				}
			}
		}
		ArrayList<ArrayList<String>> cliqueNames = new ArrayList<ArrayList<String>>();
		for(int i=0;i<theCliques.size();i++) {
			ArrayList<String> aClique = new ArrayList<String>();
			for(int j=0;j<theCliques.get(i).size();j++) {
				aClique.add(g.members[theCliques.get(i).get(j)].name);
			}
			cliqueNames.add(aClique);
		}
		return cliqueNames;
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		if(g==null) {
			return null;
		}
		ArrayList<ArrayList<Integer>> groups = bfs(g.members);
		ArrayList<String> conns = new ArrayList<String>();
		for(int i=0;i<groups.size();i++) {
			Person[] gr = new Person[g.members.length];
			for(int j=0;j<gr.length;j++) {
				if(groups.get(i).contains(j)) {
					gr[j]=g.members[j];
				}
			}
			boolean[] visited = new boolean[g.members.length];
			for(int j=0;j<visited.length;j++) {
				visited[j]=false;
			}
			int[] dfsnum = new int[g.members.length];
			int[] back = new int[g.members.length];
			int start = 0;
			for(int j=0;j<gr.length;j++) {
				if(gr[j]!=null) {
					start=j;
					break;
				}
			}
			findConnectors(gr,start,1,start,dfsnum,back,visited,conns);
			int otherStart = g.members[start].first.fnum;
			visited = new boolean[g.members.length];
			for(int j=0;j<visited.length;j++) {
				visited[j]=false;
			}
			dfsnum = new int[g.members.length];
			back = new int[g.members.length];
			findConnectors(gr,otherStart,1,otherStart,dfsnum,back,visited,conns);
		}
		if(conns.size()==0) {
			return null;
		}
		return conns;
	}
	
	private static ArrayList<ArrayList<Integer>> bfs(Person[] members) {
		boolean[] visited = new boolean[members.length];
		ArrayList<ArrayList<Integer>> groups = new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<visited.length;i++) {
			visited[i]=false;
		}
		for(int i=0;i<members.length;i++) {
			if(visited[i]==false) {
				ArrayList<Integer> gr = new ArrayList<Integer>();
				Queue<Integer> q = new Queue<Integer>();
				q.enqueue(i);
				visited[i]=true;
				while(q.isEmpty()==false) {
					int thisOne = q.dequeue();
					gr.add(thisOne);
					for(Friend ptr = members[thisOne].first;ptr!=null;ptr=ptr.next) {
						if(visited[ptr.fnum]==false) {
							visited[ptr.fnum]=true;
							q.enqueue(ptr.fnum);
						}
					}
				}
				groups.add(gr);
			}
		}
		return groups;
	}
	
	private static void findConnectors(Person[] members, int current, int counter, int start, int[] dfsnum, int[] back, boolean[] visited, ArrayList<String> conns) {
		dfsnum[current]=counter;
		back[current]=counter;
		visited[current]=true;
		for(Friend ptr=members[current].first;ptr!=null;ptr=ptr.next) {
			if(visited[ptr.fnum]==false) {
				findConnectors(members,ptr.fnum,counter+1,start,dfsnum,back,visited,conns);
				
				if(dfsnum[current]<=back[ptr.fnum]) {
					if(current!=start&&!conns.contains(members[current].name)) {
						conns.add(members[current].name);
					}
				}
				else {
					back[current]=Math.min(back[ptr.fnum],back[current]);
				}
			}
			else {
				back[current]=Math.min(back[current],dfsnum[ptr.fnum]);
			}
			
		}
	}
}

