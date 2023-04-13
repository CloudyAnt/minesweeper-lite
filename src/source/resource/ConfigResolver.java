package source.resource;

import org.yaml.snakeyaml.Yaml;
import source.constant.SystemInfo;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigResolver {
    private static final Map<String, Object> defaultConfigs;
    private static final Map<String, Object> systemSpecificConfigs;

    static {
        Yaml yaml = new Yaml();

        // load default profile
        String defaultProfile = "app.yml";
        defaultConfigs = yaml.load(Resource.getInputStream(defaultProfile));

        // load system specific profile
        SystemInfo.OS os = SystemInfo.os;
        String systemSpecificProfile = "";
        if (SystemInfo.OS.WINDOWS.equals(os)) {
            systemSpecificProfile = "app-win.yml";
        } else if (SystemInfo.OS.MAC.equals(os)) {
            systemSpecificProfile = "app-mac.yml";
        }
        systemSpecificConfigs = "".equals(systemSpecificProfile) ? Collections.emptyMap() :
                yaml.load(Resource.getInputStream(systemSpecificProfile));
    }

    private ConfigResolver() {
    }

    public static Map<String, Object> resolveMap(String key) {
        Map<String, Object> context = new HashMap<>();
        Object o = defaultConfigs.get(key);
        if (o instanceof Map) {
            ((Map<?, ?>) o).forEach((k, v) -> context.put((String) k, v));
        }

        o = systemSpecificConfigs.get(key);
        if (o instanceof Map) {
            ((Map<?, ?>) o).forEach((k, v) -> context.put((String) k, v));
        }
        return context;
    }

    public static Map<String, Object> resolveOneLevelMap(String prefix) {
        Map<String, Object> context = new HashMap<>(resolveOneLevelMap(defaultConfigs, prefix));
        context.putAll(resolveOneLevelMap(systemSpecificConfigs, prefix));
        return context;
    }

    private static Map<String, Object> resolveOneLevelMap(Map<String, Object> configs, String prefix) {
        Object o = configs.get(prefix);

        Map<String, Object> context = new HashMap<>();
        if (o == null) {
            return context;
        }

        handleObject(context, "", o);
        return context;
    }

    private static void handleObject(Map<String, Object> context, String key, Object o) {
        if (o instanceof Map) {
            handleResolvableMap(context, key, (Map<?, ?>) o);
        } else if (o instanceof String) {
            handleValueString(context, key, (String) o);
        } else if (o instanceof Boolean || o instanceof Integer) {
            context.put(key, o);
        }
    }

    private static void handleResolvableMap(Map<String, Object> context, String key, Map<?, ?> value) {
        HashMap<String, Object> map = new HashMap<>();
        value.forEach((k, v) -> map.put((String) k, v));
        cursiveResolve(context, map, key);
    }

    private static void cursiveResolve(Map<String, Object> context, Map<String, Object> configs, String prefix) {
        Set<String> keys = configs.keySet();
        if (!"".equals(prefix)) {
            prefix += ".";
        }

        for (String key : keys) {
            String keyStr = prefix + key;

            Object value = configs.get(key);
            handleObject(context, keyStr, value);
        }
    }

    private static void handleValueString(Map<String, Object> context, String keyStr, String value) {
        if (!value.contains(":")) {
            context.put(keyStr, value);
        } else {
            String[] valueParts = value.split(":");
            String valueType = valueParts[0];
            String valueIndication = valueParts[1];
            if ("c".equals(valueType)) {
                Color c;
                try {
                    c = (Color) (Color.class.getField(valueIndication).get(null));
                } catch (Exception e) {
                    c = null;
                }
                context.put(keyStr, c);
            } else if ("i".equals(valueType)) {
                context.put(keyStr, Resource.getIcon(valueIndication));
            }
        }
    }
}
