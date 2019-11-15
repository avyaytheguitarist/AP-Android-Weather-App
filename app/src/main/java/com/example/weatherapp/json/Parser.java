package com.example.weatherapp.json;
import java.io.IOException;
import java.io.StringReader;
import java.lang.String;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * @author Avyay Natarajan
 */
final class Parser {

    private static Pattern json_whitespace_compressor =
        Pattern.compile("(?!\\s+(?=(?:(?:[^\"]*\"){2})*[^\"]*\"[^\"]*$))(\\s+)");
    private static Pattern numeric_pattern = Pattern.compile("^[.]|[0-9]+$");

    private static boolean only_contains_numerics(String s)
    {
        return numeric_pattern.matcher(s).find();
    }

    static JSON.Value parseValue(String source)
    {
        source = json_whitespace_compressor.matcher(source).replaceAll("");
        if (source.startsWith("{") && source.endsWith("}"))
            return parseObject(source);
        else if (source.startsWith("[") && source.endsWith("]"))
            return parseArray(source);
        else if (source.startsWith("\"") && source.endsWith("\""))
            return parseString(source);
        else if (source.startsWith("\'") && source.endsWith("\'"))
            return parseString(source);
        else if (only_contains_numerics(source))
            return parseNumber(source);
        return null;
    }

    static JSON.Object parseObject(String source)
    {
        JSON.Object out = new JSON.Object();
        StringReader reader = new StringReader(source);
        try
        {
            int layer = 0;
            if (!reader.ready())
                return null;
            int ch = reader.read();

            /**
             * stage:
             * 0 = reading key
             * 1 = reading value
             */
            int stage = -1;
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            boolean set_encloser = false;
            char encloser;

            while ((ch = reader.read()) != -1)
            {
                char current = (char) ch;

                if (current == '\"') {
                    if (stage == -1)
                    {
                        stage = 0;
                    } else if (stage == 0)
                    {
                        stage = 1;
                        set_encloser = true;
                    }
                    continue;
                }

                switch (stage)
                {
                    case 0: {
                        key.append(current);
                        break;
                    }
                    case 1: {
                        if (set_encloser) {
                            if (current == ':')
                                value = new StringBuilder();
                            else if (current == '[' || current == '{' || current == '\"') {
                                set_encloser = false;
                                encloser = current;
                            } else
                            {
                                value.append(current);
                            }
                        } else
                            value.append(current);
                        break;
                    }
                }
            }
        }
        catch (IOException e) { }
        finally {
            reader.close();
        }
        return null;
    }

    static JSON.Array parseArray(String source)
    {
        // TODO implement
        return null;
    }

    static JSON.String parseString(String source)
    {
        return new JSON.String(source.substring(1, source.length() - 1));
    }

    static JSON.Value parseNumber(String source)
    {
        if (source.contains("."))
            return new JSON.Float(Float.parseFloat(source));
        return new JSON.Int(Integer.parseInt(source));
    }
}