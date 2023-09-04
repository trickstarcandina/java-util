import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import javax.persistence.Tuple;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ConvertDtoUtils<E> {

    E target;
    private final Supplier<E> supplier;

    private static final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new Jdk8Module())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE).build();

    public ConvertDtoUtils(Supplier<E> supplier) {
        this.supplier = supplier;
    }

    public E convertTupleToObject(Tuple tuple) {
        target = supplier.get();
        for (Field field : getFields(target)) {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            boolean accessible = field.canAccess(target);
            field.setAccessible(true);
            Object value = getVal(tuple, field.getName());
            if (Objects.isNull(value)) value = getVal(tuple, toSnakeCase(field.getName()));

            try {
                Object convertedValue = SqlTypeConverter.convert(value, field);
                if (convertedValue != null) {
                    field.set(target, convertedValue);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            field.setAccessible(accessible);
        }
        return target;
    }

    public static List<Field> getFields(Object object) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = object.getClass();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    @SneakyThrows
    public static <T> List<T> convert(List<Tuple> tuples, Supplier<T> supplier) {

        ConvertDtoUtils<T> convertDtoUtils = new ConvertDtoUtils<>(supplier);
        List<T> list = new ArrayList<>();
        tuples.forEach(obj -> list.add(convertDtoUtils.convertTupleToObject(obj)));
        return list;
    }

    @SneakyThrows
    public static <T> T convert(Tuple tuple, Supplier<T> supplier) {
        ConvertDtoUtils<T> convertDtoUtils = new ConvertDtoUtils<>(supplier);
        return convertDtoUtils.convertTupleToObject(tuple);
    }

    private static String toSnakeCase(String src) {
        return src.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private static Object getVal(Tuple tuple, String alias) {
        try {
            return tuple.get(alias);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}