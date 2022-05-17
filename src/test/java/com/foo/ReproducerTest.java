package com.foo;

import com.antwerkz.bottlerocket.BottleRocket;
import com.antwerkz.bottlerocket.BottleRocketTest;
import com.github.zafarkhaja.semver.Version;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.experimental.filters.Filters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ReproducerTest extends BottleRocketTest {
    private Datastore datastore;

    public ReproducerTest() {
        MongoClient mongo = getMongoClient();
        MongoDatabase database = getDatabase();
        database.drop();
        datastore = Morphia.createDatastore(mongo, getDatabase().getName());
    }

    @NotNull
    @Override
    public String databaseName() {
        return "test_datastore";
    }

    @Nullable
    @Override
    public Version version() {
        return BottleRocket.DEFAULT_VERSION;
    }

    @Test
    public void reproduce() {
        SaveData toSave = new SaveData("testing_object", new double[]{1,2,3}, List.of(
                new SaveData.ChildData(new double[]{4,5,6}, new ArrayList<>()),
                new SaveData.ChildData(new double[]{7,8,9}, new ArrayList<>()),
                new SaveData.ChildData(new double[]{10,11,12}, new ArrayList<>())
        ));
        //System.out.println(toSave);
        //System.out.println(toSave.hashCode());

        //make sure the child objects are not the same
        assertNotSame(toSave.elements.get(0), toSave.elements.get(1));
        assertNotSame(toSave.elements.get(0), toSave.elements.get(2));
        assertNotSame(toSave.elements.get(1), toSave.elements.get(2));

        //make sure the child position arrays are not the same
        assertFalse(Arrays.equals(toSave.elements.get(0).child_position, toSave.elements.get(1).child_position));
        assertFalse(Arrays.equals(toSave.elements.get(0).child_position, toSave.elements.get(2).child_position));
        assertFalse(Arrays.equals(toSave.elements.get(1).child_position, toSave.elements.get(2).child_position));

        datastore.save(toSave);


        SaveData loadedData = datastore.find(SaveData.class).filter(Filters.eq("_id", "testing_object")).first();

        //System.out.println(loadedData);
        //System.out.println(loadedData.hashCode());

        //make sure the child objects are not the same
        assertNotSame(loadedData.elements.get(0), loadedData.elements.get(1));
        assertNotSame(loadedData.elements.get(0), loadedData.elements.get(2));
        assertNotSame(loadedData.elements.get(1), loadedData.elements.get(2));

        //make sure the child position arrays are not the same
        assertFalse(Arrays.equals(loadedData.elements.get(0).child_position, loadedData.elements.get(1).child_position));
        assertFalse(Arrays.equals(loadedData.elements.get(0).child_position, loadedData.elements.get(2).child_position));
        assertFalse(Arrays.equals(loadedData.elements.get(1).child_position, loadedData.elements.get(2).child_position));
    }

}
