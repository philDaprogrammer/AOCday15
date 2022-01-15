import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


public class Solution {
    private final ArrayList<ArrayList<Integer>> graph;

    public Solution(String filename) { this.graph = parse(filename); }

    private ArrayList<ArrayList<Integer>> parse(String filename) {
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>();

        try {
            File f            = new File(filename);
            FileReader fr     = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            ArrayList<Integer> row;
            String line;

            while ((line = br.readLine()) != null) {
                row = new ArrayList<>();

                for (Character c : line.toCharArray()) {
                    row.add(Integer.parseInt(c.toString()));
                }

                graph.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    private ArrayList<ArrayList<Integer>> growGraph() {
        ArrayList<ArrayList<Integer>> newGraph = new ArrayList<>();

        for (int i=0; i < this.graph.size() * 5; ++i) { // initialize all graph entries to zero
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < this.graph.get(0).size() * 5; ++j) { row.add(0); }
            newGraph.add(row);
        }

        for (int r=0; r < 5; ++r){
            for (int c=0; c < 5; ++c) {
                int row = r * this.graph.size();
                int col = c * this.graph.get(0).size();

                for (int i=0; i < this.graph.size(); ++i) {
                    for (int j=0; j < this.graph.get(i).size(); ++j) {
                        int newValue = (this.graph.get(i).get(j) + r + c) % 9;

                        if (newValue == 0) {
                            newGraph.get(row + i).set(j + col, 9);
                        } else {
                            newGraph.get(row + i).set(j + col, newValue);
                        }
                    }
                }
            }
        }

        return newGraph;
    }

    private void computeOptPaths(ArrayList<ArrayList<Integer>> graph) {
        int[][] optPaths = new int[graph.size()][graph.get(0).size()];
        optPaths[0][0]   = 0;

        for (int j=1; j < graph.get(0).size(); ++j) // initialize all optimal traversal costs for first row
            optPaths[0][j] = graph.get(0).get(j) + optPaths[0][j-1];

        for (int i=1; i < graph.size(); ++i) {
            optPaths[i][0] = graph.get(i).get(0) + optPaths[i - 1][0]; // update the first entry in a row to its optimal cost

            // compute the optimal cost for every other entry within a row (once a row is complete, every entry contains its optimal traversal cost)
            for (int j=1; j < graph.get(i).size(); ++j) {
               int above =  optPaths[i - 1][j] + graph.get(i).get(j);
               int left   = optPaths[i][j - 1] + graph.get(i).get(j);

               if (above < left) { // maybe we missed some optimal paths
                   optPaths[i][j] = above;

                   for (int x=j-1; x >= 0; --x) { // recompute optimal paths if any
                       optPaths[i][x] = Math.min(optPaths[i][x+1] + graph.get(i).get(x), optPaths[i][x]);
                   }
               } else {
                   optPaths[i][j] = left;
               }
            }
        }

        System.out.println("Optimal path cost: " + optPaths[optPaths.length-1][optPaths[0].length-1]);
    }

    public void solveP1() {
        computeOptPaths(this.graph);
    }

    public void solveP2() {
        ArrayList<ArrayList<Integer>> newGraph = growGraph();
        computeOptPaths(newGraph);
    }
}