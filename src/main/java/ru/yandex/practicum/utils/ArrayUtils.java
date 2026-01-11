package ru.yandex.practicum.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.model.Search;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayUtils {

    /**
     * Разделяет входную строку на две:
     * - строку без тегов (слова без # склеиваются через пробел)
     * - строку с тегами (теги в одинарных кавычках, через запятую)
     *
     * @param input входная строка
     * @return массив из двух строк: [строка_без_тегов, строка_с_тегами]
     */
    public static Search splitStringAndTags(String input, JdbcTemplate jdbcTemplate) {
        if (input == null || input.trim().isEmpty()) {
            return new Search("", "");
        }

        String[] words = input.trim().split("\\s+");
        StringBuilder searchString = new StringBuilder();
        List<String> tags = new ArrayList<>();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (word.startsWith("#")) {
                // Удаляем символ # и добавляем в список тегов
                String tag = word.substring(1);
                if (!tag.isEmpty()) {
                    tags.add(tag);
                }
            } else {
                // Добавляем слово в строку поиска
                if (!searchString.isEmpty()) {
                    searchString.append(" ");
                }
                searchString.append(word);
            }
        }

        return new Search(searchString.toString(), String.join(",",tags));
    }

    public static List<String> sqlArrayToList(Array sqlArray) throws java.sql.SQLException {
        if (sqlArray == null) {
            return null;
        }

        // Получаем массив объектов из SQL-массива
        Object array = sqlArray.getArray();

        return Arrays.asList((String[]) array);
    }

    public static Array listToSqlArray(Connection conn, String typeName, List<String> list)
            throws SQLException {

        if (list == null || list.isEmpty()) {
            return conn.createArrayOf(typeName, new Object[0]);
        }

        // Преобразуем List в массив Object[]
        Object[] array = list.toArray();

        return conn.createArrayOf(typeName, array);
    }

    public static Array listToSqlArray(JdbcTemplate jdbcTemplate, String sqlTypeName, List<?> list) {
        return jdbcTemplate.execute((Connection conn) -> {
            if (list == null || list.isEmpty()) {
                return conn.createArrayOf(sqlTypeName, new Object[0]);
            }
            Object[] array = list.toArray();
            return conn.createArrayOf(sqlTypeName, array);
        });
    }

    /**
     * Разбивает список на страницы.
     * @param list Исходный список
     * @param pageNumber Номер страницы (начиная с 1)
     * @param pageSize Размер страницы (количество элементов на странице)
     * @return Подсписок для заданной страницы или пустой список, если страница вне диапазона
     */
    public static <T> List<T> paginate(List<T> list, int pageNumber, int pageSize) {
        // Проверка на null и пустые входные данные
        if (list == null || list.isEmpty() || pageSize <= 0 || pageNumber < 1) {
            return Collections.emptyList();
        }

        // Вычисляем индексы
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, list.size());

        // Возвращаем подсписок или пустой список, если индексы некорректны
        return fromIndex >= list.size() ? Collections.emptyList() :
                list.subList(fromIndex, toIndex);
    }
}
