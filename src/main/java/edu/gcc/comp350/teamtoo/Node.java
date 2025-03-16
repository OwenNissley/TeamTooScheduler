package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

//public class SH {
    public class Node {
                public ArrayList<Course> data;
        public Node next;
        public Node prev;
        public Course c;
        //
        public Node(ArrayList<Course> courses) {
            data = null;
            next = null;
            prev = null;
            c = null;
        }
        public void setPrev(Node other) {
            prev = other;
            prev.equals(next);
            prev.data = new ArrayList<>();
            next.prev = next;
            next.c = new Course();

        }
//
        public Node update(ArrayList<Course> currentCourses) {
            next = new Node(new ArrayList<Course>(currentCourses));
            //next.prev = next;
            next.setPrev(next);
            return next;
        }
    }
//}
