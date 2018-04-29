package com.artificial.developmentkit;

import com.artificial.cachereader.fs.RT4CacheSystem;
import com.artificial.cachereader.wrappers.Wrapper;
import com.artificial.cachereader.wrappers.WrapperLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        Gson gson = new GsonBuilder().create();
        gson.toJson(CACHED_DISPLAY_NAMES, System.out);
    }
}