import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.convert.ConversionService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

// i use framework micronaut

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlTypeConverterUtils {

    static {
        ConversionService.SHARED.addConverter(String.class, ZoneId.class, ZoneId::of);
        ConversionService.SHARED.addConverter(String.class, LocalTime.class, LocalTime::parse);
        ConversionService.SHARED.addConverter(Time.class, LocalTime.class, Time::toLocalTime);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Convert from sql tuple value to field value
     *
     * @param value value from sql column
     * @param field field of POJO class
     * @return converted value
     */
    public static Object convert(Object value, Field field) {
        // Value is null
        if (Objects.isNull(value)) {
            if (List.class == field.getType()) {
                return List.of();
            }
            if (Set.class == field.getType()) {
                return Set.of();
            }
            return null;
        }
        if (field.getType().isAssignableFrom(value.getClass())) {
            return value;
        }
        if (field.getType().isPrimitive()) {
            // convert primitive value
            if (Number.class.isAssignableFrom(value.getClass())) {
                Number number = (Number) value;
                switch (field.getType().getName()) {
                    case "int":
                        return number.intValue();
                    case "long":
                        return number.longValue();
                    case "float":
                        return number.floatValue();
                    case "double":
                        return number.doubleValue();
                }
            }
            if (value instanceof Boolean || value instanceof Character) {
                return value;
            }
        }
        if (value instanceof CharSequence && !CharSequence.class.isAssignableFrom(field.getType())) {
            String string = value.toString();
            if (string.charAt(0) == '[' || string.charAt(0) == '{') {
                try {
                    value = MAPPER.readValue(string, JsonNode.class);
                } catch (Exception ignored) {

                }
            }
        }

        if (value instanceof JsonNode) {
            JsonNode jsonNode = (JsonNode) value;
            if (jsonNode.isArray()) {
                if (List.class.isAssignableFrom(field.getType())) {
                    List list = new ArrayList();
                    for (JsonNode node : jsonNode) {
                        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                        Class<?> genericType = (Class<?>) stringListType.getActualTypeArguments()[0];
                        ConversionService.SHARED.convert(node, genericType)
                                .ifPresent(list::add);
                    }
                    return list;
                }
                if (field.getType().isArray()) {
                    Class<?> componentType = field.getType().getComponentType();
                    Object arr = Array.newInstance(componentType, jsonNode.size());
                    for (int i = 0; i < jsonNode.size(); i++) {
                        Optional<?> converted = ConversionService.SHARED.convert(jsonNode.get(i), componentType);
                        if (converted.isPresent()) {
                            Array.set(arr, i, converted.get());
                        }
                    }
                    return arr;
                }
            }
            if (jsonNode.isObject()) {
                Map<String, Object> map = new LinkedHashMap<>();
                Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> next = fields.next();
                    map.put(toSnakeCase(next.getKey()), next.getValue());
                    map.put(next.getKey(), next.getValue());
                }
                Optional<?> converted = ConversionService.SHARED.convert(map, field.getType());
                if (converted.isPresent() && field.getType().isAssignableFrom(converted.get().getClass())) {
                    return converted.get();
                }
            }
        }

        Optional<?> convert = ConversionService.SHARED.convert(value, field.getType());

        if (convert.isPresent() && field.getType().isAssignableFrom(convert.get().getClass())) {
            return convert.get();
        } else {
            log.error("can not convert value ({})[{}] to [{}]", value.getClass().getName(), value, field.getDeclaringClass().getName() + "#" + field.getName());
        }

        return null;
    }

    private static String toSnakeCase(String src) {
        return src.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}