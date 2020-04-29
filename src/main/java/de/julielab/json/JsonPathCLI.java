package de.julielab.json;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import de.julielab.java.utilities.FileUtilities;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAwareEx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Command line tool for easy JsonPath access (https://github.com/json-path/JsonPath) for files with multiple JSON
 * entries following the same schema. This tool is meant to access a field value 'for each' base JsonPath.
 * </p>
 *
 * @author faessler
 */
public class JsonPathCLI {

    /**
     * Could be used for element type checks if needed.
     */
    private static Set<Class<?>> wrapperTypes = getWrapperTypes();

    public static void main(String[] args) {

        if (args.length == 1 && (args[0].toLowerCase().equals("help") || args[0].toLowerCase().equals("h"))) {
            System.err.println("Help on JsonPath expression syntax: http://goessner.net/articles/JsonPath/, https://github.com/json-path/JsonPath");
            System.err.println("To see tool usage, start the tool without any parameters.");
            System.exit(1);
        }

        if (args.length < 2) {
            System.err.println("Usage: " + JsonPathCLI.class.getSimpleName() + " <json file> <'for each' json path> <json paths evaluated on 'for each' outcomes>");
            System.exit(1);
        }

        String jsonFilePath = args[0];

        try {
            InputStream is = FileUtilities.getInputStreamFromFile(new File(jsonFilePath));
            ReadContext parse = JsonPath.parse(is);
            JsonPathCLI cli = new JsonPathCLI();
            List<List<?>> values = cli.applyJsonPath(args, parse);
            for (List<?> recordValue : values) {
                System.out.println(recordValue.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            }
        } catch (IOException e) {
            System.err.println("IOException occured: " + e.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    public List<List<?>> applyJsonPath(String[] args, ReadContext parse) {
        String currentJsonPath = args[1];
        ReadContext currentParse = parse;
        List<List<?>> ret = new ArrayList<>();
        try {
            Object read = currentParse.read(currentJsonPath);

            if (null == read)
                return ret;

            if (read.getClass().equals(JSONArray.class) && 2 < args.length) {
                List<Object> obLst = (List<Object>) read;
                if (obLst.size() > 0) {
                    for (Object ob : obLst) {
                        List<Object> elements = new ArrayList<>();
                        if (!wrapperTypes.contains(ob.getClass())) {
                            for (int j = 2; j < args.length; j++) {
                                try {
                                    if (ob instanceof  JSONAwareEx) {
                                        JSONAwareEx ex = (JSONAwareEx) ob;
                                        currentParse = JsonPath.parse(ex.toJSONString());
                                    } else {
                                        currentParse = JsonPath.parse(ob);
                                    }
                                    currentJsonPath = args[j];
                                    Object innerRead = currentParse.read(currentJsonPath);
                                    elements.add(innerRead);
                                } catch (PathNotFoundException e) {
                                    System.err.println("Inner JsonPath \"" + currentJsonPath + "\" was not found in " + args[0] + ". JSON was: \"" + currentParse.json()
                                            + "\". The record is omitted.");
                                    elements.clear();
                                    break;
                                }
                            }
                        } else {
                            elements.add(ob);
                        }
                        if (!elements.isEmpty())
                            ret.add(elements);
                    }
                }
            } else {
                // This case happens when there is only the "for each" JSONPath or this path did not return an array.
                System.out.println(read);
            }
        } catch (PathNotFoundException e) {
            System.err.println("JsonPath \"" + currentJsonPath + "\" was not found in " + args[0] + ". JSON was: \"" + currentParse.json() + "\".");
        }
        return ret;
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        return ret;
    }

}
