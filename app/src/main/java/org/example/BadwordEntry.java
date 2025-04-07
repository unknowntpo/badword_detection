package org.example;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class BadwordEntry {
    //        [
//          {"keyword": ["damn", "fuck"] , position: {start: 0, end: 3} }
//          {"keyword": ["damn"] , position: {start: 10, end: 13} }
//        ]
    public List<String> keyword;
    public Position position;

    public BadwordEntry() {
    }

    public BadwordEntry(List<String> keyword, Position position) {
        this.keyword = keyword;
        this.position = position;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "BadwordEntry{" +
                "keyword=" + keyword +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BadwordEntry that)) return false;
        return Objects.equals(keyword, that.keyword) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, position);
    }
}
