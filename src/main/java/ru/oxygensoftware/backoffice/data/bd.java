package ru.oxygensoftware.backoffice.data;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by Dmitry Raguzin
 * Date: 12.11.15
 */
public class bd {
    public static void main(String... args) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("/home/raguzin/projects/backoffice/state.sql", "UTF-8");
        Arrays.asList(State.values()).stream().forEach(state ->
                writer.println("INSERT INTO state VALUES('" + state.name() + "');"));
        writer.close();
    }
}
