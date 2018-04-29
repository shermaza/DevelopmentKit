package com.artificial.developmentkit;

import com.artificial.cachereader.fs.RT4CacheSystem;
import com.artificial.cachereader.wrappers.Wrapper;
import com.artificial.cachereader.wrappers.WrapperLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class DevelopmentKitMain {
    private static final List<Wrapper<?>> CACHED_DEFINITIONS = new ArrayList<>();
    private static final List<String> CACHED_DISPLAY_NAMES = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        RT4CacheSystem cache = new RT4CacheSystem();
        TypeLoader.RT4 typeLoader = TypeLoader.RT4.values()[0];

        final WrapperLoader<?, ?> loader = cache.getLoader(typeLoader.getWrapperClass());
        final DefaultListModel<String> listModel = new DefaultListModel<>();

        for (int i = 0; i < 200000; i++) {
            if (loader.canLoad(i)) {
                final Wrapper<?> def;
                CACHED_DEFINITIONS.add(def = loader.load(i));
                final StringBuilder builder = new StringBuilder();
                if (def.getDeclaredFields().containsKey("name")) {
                    builder.append(def.getDeclaredFields().get("name")).append(" (").append(i).append(")");
                } else {
                    builder.append(i);
                }
                listModel.addElement(builder.toString());
                CACHED_DISPLAY_NAMES.add(builder.toString());
            }
        }

        // Break JSON files into a small amount items each
        int JSON_SIZE = 25000;
        int iterations = (CACHED_DEFINITIONS.size() / JSON_SIZE);


        // For each iteration, write a JSON file with those items
        for (int i = 1; i <= iterations + 1; i++) {

            HashMap<Integer, HashMap> items = new HashMap<>();

            // Determine the start item for the iteration
            int start = (i-1) * JSON_SIZE;

            // Determine the end item for the iteration
            int end = start + JSON_SIZE;
            if (end > CACHED_DEFINITIONS.size()){
                end = CACHED_DEFINITIONS.size();
            }

            int cur = start;


            while (cur < end) {
                Wrapper<?> def = CACHED_DEFINITIONS.get(cur);
                cur++;
                if (def == null)
                    continue;

                final HashMap<String, Object> properties = new HashMap<>();

                int skip = 0;

                for (final Map.Entry<String, Object> entry : def.getDeclaredFields().entrySet()) {
                    if (entry.getKey().equals("name") ||entry.getKey().equals("stackable") || entry.getKey().equals("members") || entry.getKey().equals("price") || entry.getKey().equals("noted")) {
                        if(entry.getKey().equals("name") && (entry.getValue().equals("Null") || entry.getValue().equals("null"))){
                            skip = 1;
                        }
                        properties.put(entry.getKey(), entry.getValue());
                    }
                }

                if(skip == 1) {
                    continue;
                }

                items.put(def.id(), properties);
            }

            Writer writer = new FileWriter("items" + i + ".json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(items, writer);
            writer.close();
        }
    }
}