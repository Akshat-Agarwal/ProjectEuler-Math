package randomproblems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class QuoraNearby {
   // Topic data structure
   private static class Topic {
      double x;
      double y;
      int[] questions;
      int pointer = 0;

      public Topic(double x, double y, int nQ) {
         this.x = x;
         this.y = y;
         questions = new int[nQ];
      }

      public void append(int i) {
         questions[pointer] = i;
         pointer++;
      }
   }


   // data structure to hold topic/question vs distance
   private static class DistancePair {
      public final int id;
      public final double distance;

      public DistancePair(int id, double distance) {
         this.id = id;
         this.distance = distance;
      }
   }

   // comparator for topic/distance pair
   private static class DistanceComparator implements Comparator<DistancePair> {
      public int compare(DistancePair p1, DistancePair p2) {
         double d1 = p1.distance;
         double d2 = p2.distance;
         if (Math.abs(d1 - d2) < 0.001d)
            return p1.id < p2.id ? 1 : -1;
         else if (d1 < d2)
            return -1;
         else
            return 1;
      }
   }

   // compute distance between query position and topic
   private static double computeDistance(int topicNum, double x, double y) {
      double tX = topics.get(topicNum).x;
      double tY = topics.get(topicNum).y;
      double xDiff = tX - x;
      double yDiff = tY - y;
      return yDiff * yDiff + xDiff * xDiff;
   }

   private static String findTResults(int numResults, ArrayList<DistancePair> distances) {
      Collections.sort(distances, new DistanceComparator());
      StringBuffer returnString = new StringBuffer();
      int n = Math.min(numResults, distances.size());
      for (int i = 0; i < n; i++)
         returnString.append(distances.get(i).id).append(' ');

      return returnString.toString().trim();
   }

   private static String findQResults(int numResults, ArrayList<DistancePair> distances) {
      DistanceComparator comparator = new DistanceComparator();
      Collections.sort(distances, comparator);
      StringBuffer returnString = new StringBuffer();
      HashSet<Integer> questionSet = new HashSet<Integer>();
      ArrayList<DistancePair> questionArray = new ArrayList<DistancePair>(nT);

      for (DistancePair tpPair : distances) {
         int topicId = tpPair.id;
         double distance = tpPair.distance;
         int[] questions = topics.get(topicId).questions;
         int count = topics.get(topicId).pointer;
         for (int i = 0; i < count; i++) {
            int currQuestion = questions[i];
            if (questionSet.contains(currQuestion))
               continue;
            DistancePair qdPair = new DistancePair(currQuestion, distance);
            questionSet.add(currQuestion);
            questionArray.add(qdPair);
         }
      }

      Collections.sort(questionArray, comparator);

      int n = Math.min(numResults, questionArray.size());
      for (int i = 0; i < n; i++)
         returnString.append(questionArray.get(i).id).append(' ');

      return returnString.toString().trim();
   }

   private static HashMap<Integer, Topic> topics;
   private static int nT;
   private static int nQ;
   private static int nR;

   public static void run(Scanner in) {
      nT = in.nextInt();
      nQ = in.nextInt();
      nR = in.nextInt();
      topics = new HashMap<Integer, Topic>(nT, 1.0f);

      // Construct topics
      for (int i = 0; i < nT; i++) {
         int topicNum = in.nextInt();
         Topic tempTopic = new Topic(in.nextDouble(), in.nextDouble(), nQ);
         topics.put(topicNum, tempTopic);
      }

      // Add questions to topics
      for (int i = 0; i < nQ; i++) {
         int questionNum = in.nextInt();
         int numCategories = in.nextInt();
         for (int j = 0; j < numCategories; j++) {
            topics.get(in.nextInt()).append(questionNum);
         }
      }

      // Run queries
      in.nextLine();
      for (int i = 0; i < nR; i++) {
         char queryType = in.next().charAt(0);
         int numResults = in.nextInt();
         double x = in.nextDouble();
         double y = in.nextDouble();
         // result searching
         ArrayList<DistancePair> distances = new ArrayList<DistancePair>(nT);
         Set<Integer> keys = topics.keySet();
         for (Integer j : keys) {
            DistancePair pair = new DistancePair(j, computeDistance(j, x, y));
            distances.add(pair);
         }
         if (queryType == 't') {
            System.out.println(findTResults(numResults, distances));
         } else {
            System.out.println(findQResults(numResults, distances));
         }
      }
   }

   public static final boolean TEST = true;

   public static void main(String[] args) {
      if (!TEST) {
         Scanner in = new Scanner(System.in);
         run(in);
         in.close();
      } else {
         run(new Scanner("0 0 0\n"));

         run(new Scanner("0 0 1\nq 100 0.0 0.0\n"));
         run(new Scanner("0 0 1\nt 100 0.0 0.0\n"));

         run(new Scanner("0 1 0\n0 0\n"));
         run(new Scanner("1 0 0\n0 0.0 0.0\n"));
         run(new Scanner("1 1 0\n0 0.0 0.0\n 0 1 0\n"));

         run(new Scanner("0 1 1\n0 0\nq 10 0.0 0.0\n"));
         run(new Scanner("1 0 1\n0 0.0 0.0\nq 10 0.0 0.0\n"));
         run(new Scanner("1 1 1\n0 0.0 0.0\n0 1 0\nq 10 0.0 0.0\n"));

         run(new Scanner("2 2 1\n0 0.0 0.0\n1 0.0 0.0\n1 1 1\n0 1 0\nq 10 0.0 0.0\n")); // 1 0

         Random rand = new Random(0x32423);
         StringBuilder sb = new StringBuilder();
         sb.append("10000 1000 100\n");
         for (int i = 0; i < 10000; i++) {
            sb.append(i).append(' ').append(1000 * rand.nextDouble()).append(' ').append(1000 * rand.nextDouble())
                  .append('\n');
         }
         for (int i = 0; i < 1000; i++) {
            sb.append(i).append(" 1 ").append(rand.nextInt(1000)).append('\n');
         }
         for (int i = 0; i < 50; i++) {
            sb.append("q 10 ").append(1000 * rand.nextDouble()).append(' ').append(1000 * rand.nextDouble()).append('\n');
            sb.append("t 10 ").append(1000 * rand.nextDouble()).append(' ').append(1000 * rand.nextDouble()).append('\n');
         }
         long t0 = System.currentTimeMillis();
         run(new Scanner(sb.toString()));
         System.out.println(System.currentTimeMillis() - t0);
      }
   }
}
