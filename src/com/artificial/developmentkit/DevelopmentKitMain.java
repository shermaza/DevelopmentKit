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

        HashMap<Integer, HashMap> items = new HashMap<>();

        for (final Wrapper<?> def : CACHED_DEFINITIONS) {
            if (def == null)
                continue;
            final HashMap<String, Object> properties = new HashMap();

            for (final Map.Entry<String, Object> entry : def.getDeclaredFields().entrySet()) {
                if (entry.getKey().equals("name") ||entry.getKey().equals("stackable") || entry.getKey().equals("members") || entry.getKey().equals("price") || entry.getKey().equals("noted")) {
                    properties.put(entry.getKey(), entry.getValue());
                }
            }

            items.put(def.id(), properties);
        }

        Writer writer = new FileWriter("items.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(items, writer);
    }
}