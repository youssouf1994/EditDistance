import java.util.*;

public class EditDistance implements EditDistanceInterface {

    int c_i, c_d, c_r;
    static int MAX = Integer.MAX_VALUE;
    static int UNDEF = -1;

    public EditDistance (int c_i, int c_d, int c_r) {
        c_i = c_i;
        c_d = c_d;
        c_r = c_r;
    }

    public int[][] getEditDistanceDP(String s1, String s2) {
        /* To be completed in Part 1. Remove line below. */
        assert(s1 != null && s2 != null);

        // Take the length of the string s1.
        int n = s1.length();

        // Take the length of the string s2.
        int m = s2.length();

        // Transform the string s1 to an array of chars.
        char[] c1 = s1.toCharArray();

        // Transform the string s1 to an array of chars.
        char[] c2 = s2.toCharArray();

        // Create an array for the optimal distances.
        int [][] d = new int[n+1][m+1];

        // Initialize d[0][j] with j*c_i.
        for(int j = 0; j <= m; j++) {
          d[0][j] = j*c_i;
        }

        // Initialize d[i][0] with i*c_d.
        for(int i = 0; i <= n; i++) {
          d[i][0] = i*c_d;
        }

        for(int i = 1; i <= n; i++) {
          for(int j = 1; j <= m; j++) {

            // Check if the i-th character of s1 is equal to the j-th character of s2.
            // If true put d[i][j] = d[i-1][j-1], otherwise d[i][j] = d[i-1][j-1] + c_r.

            if(c1[i-1] == c2[j-1]) {
              d[i][j] = d[i-1][j-1];
            }
            else {
              d[i][j] = d[i-1][j-1] + c_r;
            }

            // Check if it is better to delete
            if(d[i-1][j] + c_d < d[i][j]) {
              d[i][j] = d[i-1][j] + c_d;
            }

            // Check if it is better to insert.
            if(d[i][j-1] + c_i < d[i][j]) {
              d[i][j] = d[i][j-1] + c_i;
            }
          }
        }

        return d;
    }

    public List<String> getMinimalEditSequence(String s1, String s2) {
        /* To be completed in Part 2. Remove sample code block below. */
        assert(s1 != null && s2 != null);

        // Take the length of the string s1.
        int n = s1.length();

        // Take the length of the string s2.
        int m = s2.length();

        // |n-m|
        int p = (n > m) ? n-m : m-n;

        // Transform the string s1 to an array of chars.
        char[] c1 = s1.toCharArray();

        // Transform the string s1 to an array of chars.
        char[] c2 = s2.toCharArray();

        // for every i create an array to store the minimum index such that
        // |i-j| < |n-m| + X
        int[] jmin = new int[n+1];

        // for every i create an array to store the maximum index such that
        // |i-j| < |n-m| + X
        int[] jmax = new int[n+1];

        int[][] d = new int[n+1][];

        int X = 1;
        do {
          // Initialize the jmin and the jmax
          for(int i = 0; i <= n; i++) {
            jmin[i] = (i - p - X > 0) ? i-p-X : 0;
            jmax[i] = (i + p + X < m) ? i+p+X : m;
            d[i] = new int[jmax[i] - jmin[i] + 1];
          }

          for(int j = jmin[0]; j < jmax[0]; j++) {
            d[0][j-jmin[0]] = j*c_i;
          }

          for(int i = 1; i <= n; i++) {
            for(int j = jmin[i]; j <= jmax[i]; j++) {
              d[i][j-jmin[i]] = Integer.MAX_VALUE;
              if(j > jmin[i-1]) {
                if(c1[i-1] == c2[j-1]) {
                  if(d[i][j-jmin[i]] > d[i-1][j-jmin[i-1]-1]) {
                    d[i][j-jmin[i]] = d[i-1][j-jmin[i-1]-1];
                  }
                }
                else {
                  if(d[i][j-jmin[i]] > d[i-1][j-jmin[i-1]-1] + c_r) {
                    d[i][j-jmin[i]] = d[i-1][j-jmin[i-1]-1] + c_r;
                  }
                }
              }
              if(j <= jmax[i-1]) {
                if(d[i][j-jmin[i]] > d[i-1][j-jmin[i-1]] + c_d) {
                  d[i][j-jmin[i]] = d[i-1][j-jmin[i-1]] + c_d;
                }
              }
              if(j > jmin[i]) {
                if(d[i][j-jmin[i]] > d[i][j-jmin[i]-1] + c_i) {
                  d[i][j-jmin[i]] = d[i][j-jmin[i]-1] + c_i;
                }
              }
            }
          }
          X = 2*X;
        } while (X*(c_d + c_i) < d[n][jmax[n] - jmin[n]]);

        LinkedList<String> ls = new LinkedList<> ();
        int i = n;
        int j = jmax[n];
        while(i > 0) {
          if(j > jmin[i-1]) {
            if(d[i][j-jmin[i]] == d[i-1][j-1-jmin[i-1]] + c_r) {
              j--;
              i--;
              ls.addFirst("replace(" + j + "," + c2[j] + ")");
              continue;
            }
            if(d[i][j-jmin[i]] == d[i-1][j-1-jmin[i-1]]) {
              j--;
              i--;
              continue;
            }
          }
          if(j <= jmax[i-1]) {
            if(d[i][j-jmin[i]] == d[i-1][j-jmin[i-1]] + c_d) {
              i--;
              ls.addFirst("delete(" + j + ")");
              continue;
            }
          }
          if(j > jmin[i]) {
            if(d[i][j-jmin[i]] == d[i][j-jmin[i]-1] + c_i) {
              j--;
              ls.addFirst("insert("+ j + "," + c2[j] + ")");
              continue;
            }
          }
        }
        while(j > jmin[0]) {
          j--;
          ls.addFirst("insert("+ j + "," + c2[j] + ")");
        }
        return ls;
    }
};
