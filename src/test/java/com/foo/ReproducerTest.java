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
    public static final String TESTING_ID = "testing_object";
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
        trySave();
        tryLoad();
    }

    public void trySave() {
        SaveData toSave = new SaveData(TESTING_ID, new double[]{1,2,3}, List.of(
                new SaveData.ChildData(new double[]{4,5,6}, new ArrayList<>()),
                new SaveData.ChildData(new double[]{7,8,9}, new ArrayList<>()),
                new SaveData.ChildData(new double[]{10,11,12}, new ArrayList<>())
        ));

        // Make sure the child objects are not the same
        assertNotSame(toSave.elements.get(0), toSave.elements.get(1));
        assertNotSame(toSave.elements.get(0), toSave.elements.get(2));
        assertNotSame(toSave.elements.get(1), toSave.elements.get(2));

        // Make sure the child position arrays are not the same
        assertFalse(Arrays.equals(toSave.elements.get(0).child_position, toSave.elements.get(1).child_position));
        assertFalse(Arrays.equals(toSave.elements.get(0).child_position, toSave.elements.get(2).child_position));
        assertFalse(Arrays.equals(toSave.elements.get(1).child_position, toSave.elements.get(2).child_position));

        datastore.save(toSave);
    }

    public void tryLoad() {
        SaveData loadedData = datastore.find(SaveData.class).filter(Filters.eq("_id", TESTING_ID)).first();

        // Make sure the query actually worked
        assertNotNull(loadedData);

        // Make sure the child objects are not the same
        assertNotSame(loadedData.elements.get(0), loadedData.elements.get(1));
        assertNotSame(loadedData.elements.get(0), loadedData.elements.get(2));
        assertNotSame(loadedData.elements.get(1), loadedData.elements.get(2));

        // Make sure the child position arrays are not the same
        assertFalse(Arrays.equals(loadedData.elements.get(0).child_position, loadedData.elements.get(1).child_position));
        assertFalse(Arrays.equals(loadedData.elements.get(0).child_position, loadedData.elements.get(2).child_position));
        assertFalse(Arrays.equals(loadedData.elements.get(1).child_position, loadedData.elements.get(2).child_position));
    }

}
