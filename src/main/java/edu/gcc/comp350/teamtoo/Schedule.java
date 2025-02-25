package edu.gcc.comp350.teamtoo;


import java.lang.reflect.Array;
import java.util.ArrayList;
public class Schedule
{
    private ArrayList<Course> courses;
    private int scheduleID;

    //used for undo and redo
    private ScheduleHistory history;


    public Schedule(){
        courses = new ArrayList<Course>();
        // set schedule ID here
        history = new ScheduleHistory();
    }


    public void loadSchedule() {}
    public void saveSchedule() {}
    public void printSchedule() {}
    public boolean hasConflict() {
        return false;
    }
    public void removeCourse() {
        //do all of the needed stuff
        history.Update(); //call this at the end
    }
    public void addCourse() {
        //do all of the needed stuff
        history.Update(); //call this at the end
    }
    public void undo() {history.getPrev();}
    public void redo() {history.getNext();}



    /**
     * Essentially works as a LinkedList to keep track of past and present versions
     * of the schedule. I made my own version because the Java version of LinkedLists
     * as far I could research didn't quite work the way I needed them to.
     */
    private class ScheduleHistory{
        private Node head;
        private Node tail;
        private Node curPosition;
        public ScheduleHistory(){
            head = new Node(new ArrayList<Course>(courses));
            tail = head;
            curPosition = head;
        }

        /**
         * Adds any changes to the current schedule as the next Node from where
         * ScheduleHistory currently points, and then moves the pointer to that new node
         * Note that should there exist any changes that occur later than where the new change is
         * inserted, those changes will be lost, much like how many other undo/redo systems operate
         */
        public void Update(){
            curPosition.update();
            curPosition = curPosition.next;
            tail = curPosition;
        }

        /**
         * Slides the pointer in history to one instance earlier. Essentially undoes one action
         * @return 0 if everything works, 1 if there is no existing previous instance
         */
        public int getPrev(){
            if(curPosition.prev != null){
                curPosition = curPosition.prev;
                courses = curPosition.data;
                return 0;
            }
            else{
                return 1;
            }
        }

        /**
         * Checks if there exists any history for getPrev to slide back to
         * @return 0 if there is and 1 if there isn't
         */
        public int peekPrev() {
            if (curPosition.prev != null) {
                return 0;
            } else {
                return 1;
            }
        }
        /**
         * Slides the pointer in history to one instance later. Essentially redoes one action
         * @return 0 if everything works, 1 if there is no existing later instance
         */
        public int getNext(){
            if(curPosition.next != null){
                curPosition = curPosition.next;
                courses = curPosition.data;
                return 0;
            }
            else{
                return 1;
            }
        }
        /**
         * Checks if there exists any history for getNext to slide forward to
         * @return 0 if there is and 1 if there isn't
         */
        public int peekNext(){
            if(curPosition.next != null){
                return 0;
            }
            else{
                return 1;
            }
        }

        /**
         * This is a helper class. This is where each individual Schedule instance will be kept
         * alongside pointers to the next and previous Schedule instance in history.
         */
        private class Node {
            public ArrayList<Course> data;
            private Node next;
            private Node prev;
            public Node(ArrayList<Course> courses){
                data = courses;
                next = null;
                prev = null;
            }

            public Node update(){
                next = new Node(new ArrayList<Course>(courses));
                next.prev = this;
                return next;
            }
        }
    }
}


