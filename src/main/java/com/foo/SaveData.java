package com.foo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.util.Arrays;
import java.util.List;

@Entity("saved_stuff")
public class SaveData {
    @Id
    public final String saveName;
    public final double[] position;
    public final List<ChildData> elements;

    public SaveData(String saveName, double[] position, List<ChildData> elements) {
        this.saveName = saveName;
        this.position = position;
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "SaveData{" +
                "saveName='" + saveName + '\'' +
                ", position=" + Arrays.toString(position) +
                ", elements=" + elements +
                '}';
    }

    @Entity
    public static class ChildData {
        public final double[] child_position;
        public final List<ChildData> child_elements;

        public ChildData(double[] child_position, List<ChildData> child_elements) {
            this.child_position = child_position;
            this.child_elements = child_elements;
        }

        @Override
        public String toString() {
            return "\nChildData{" +
                    "child_position=" + Arrays.toString(child_position) +
                    ", child_elements=" + child_elements +
                    '}';
        }
    }
}
