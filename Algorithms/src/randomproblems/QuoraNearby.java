package randomproblems;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.TreeSet;

public class QuoraNearby {
   // Topic data structure
   static public class Topic {
      double x;
      double y;
      TreeSet<Integer> questions = new TreeSet<Integer>(Collections.reverseOrder());

      public Topic(double x, double y) {
         this.x = x;
         this.y = y;
      }

      public void addQuestion(int n) {
         questions.add(n);
      }

      public TreeSet<Integer> getQuestions() {
         return questions;
      }

      public double getX() {
         return x;
      }

      public double getY() {
         return y;
      }
   }

   // data structure to hold topic vs distance
   static public class TopicDistancePair {
      public final int topic;
      public final double distance;

      public TopicDistancePair(int topic, double distance) {
         this.topic = topic;
         this.distance = distance;
      }
   }

   // comparator for topic/distance pair
   static class DistanceComparator implements Comparator<TopicDistancePair> {
      public int compare(TopicDistancePair p1, TopicDistancePair p2) {
         double d1 = p1.distance;
         double d2 = p2.distance;

         if (d1 == d2)
            return p1.topic > p2.topic ? 1 : -1;
         else if (d1 < d2)
            return 1;
         else
            return -1;
      }
   }

   // compute distance between query position and topic
   static double computeDistance(int topicNum, double x, double y) {
      double tX = topics[topicNum].x;
      double tY = topics[topicNum].y;
      double xDiff = tX - x;
      double yDiff = tY - y;
      return Math.sqrt(yDiff * yDiff + xDiff * xDiff);
   }

   // print results given query type
   static String printResults(String queryType, int numResults, TreeSet<TopicDistancePair> distances) {
      String returnString = "";
      char type = queryType.charAt(0);
      if (type == 't') {
         for (int i = 0; i < numResults; i++) {
            returnString += distances.pollLast().topic + " ";
         }
      } else if (type == 'q') {
         LinkedHashSet<Integer> questionSet = new LinkedHashSet<Integer>();
         int setSize = distances.size();
         loop: for (int i = 0; i < setSize; i++) {
            int topicId = distances.pollLast().topic;
            TreeSet<Integer> questions = topics[topicId].getQuestions();
            for (int q : questions) {
               questionSet.add(q);
               if (questionSet.size() == numResults)
                  break loop;
            }
         }
         for (int i : questionSet)
            returnString += i + " ";
      }
      return returnString;
   }

   static Topic[] topics;

   public static void main(String[] args) {
      @SuppressWarnings("resource")
      Scanner in = new Scanner(System.in);
      int nT = in.nextInt();
      int nQ = in.nextInt();
      int nR = in.nextInt();

      // Construct topics
      topics = new Topic[nT];
      for (int i = 0; i < nT; i++) {
         in.nextInt();
         topics[i] = new Topic(in.nextDouble(), in.nextDouble());
      }
      // Add questions to topics
      for (int i = 0; i < nQ; i++) {
         in.nextInt();
         int numCategories = in.nextInt();
         for (int j = 0; j < numCategories; j++) {
            topics[in.nextInt()].addQuestion(i);
         }
      }
      // Run queries
      in.nextLine();
      for (int i = 0; i < nR; i++) {
         String query = in.nextLine();
         String[] params = query.split(" ");
         int numResults = Integer.valueOf(params[1]);
         double x = Double.valueOf(params[2]);
         double y = Double.valueOf(params[3]);
         TreeSet<TopicDistancePair> distances = new TreeSet<TopicDistancePair>(new DistanceComparator());
         for (int j = 0; j < nT; j++) {
            TopicDistancePair pair = new TopicDistancePair(j, computeDistance(j, x, y));
            distances.add(pair);
         }
         System.out.println(printResults(params[0], numResults, distances));
      }
   }
}