//package org.example;
//
//import java.util.Objects;
//
//public class Position {
//    public int start;
//    public int end;
//
//    public Position() {
//    }
//
//    public Position(int start, int end) {
//        this.start = start;
//        this.end = end;
//    }
//
//    public int getStart() {
//        return start;
//    }
//
//    public void setStart(int start) {
//        this.start = start;
//    }
//
//    public int getEnd() {
//        return end;
//    }
//
//    public void setEnd(int end) {
//        this.end = end;
//    }
//
//    @Override
//    public String toString() {
//        return "Position{" +
//                "start=" + start +
//                ", end=" + end +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof Position position)) return false;
//        return start == position.start && end == position.end;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(start, end);
//    }
//}
