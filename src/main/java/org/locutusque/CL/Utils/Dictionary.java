package org.locutusque.CL.Utils;

import org.locutusque.CL.Dynamic;
import org.locutusque.CL.Exceptions.IllegalParameterError;

/**
 * A dictionary-like data structure that stores key-value pairs, with a similar implementation to that of Python's or, more specifically, a JSON-formatted HashMap.
 *
 * @param <K> The type of keys.
 * @param <V> The type of values.
 */
public class Dictionary<K, V> extends Dynamic {
    private Object[] keys;
    private Object[] values;
    private int size;

    /**
     * Constructs an empty dictionary.
     */
    public Dictionary() {
        keys = new Object[10];
        values = new Object[10];
        size = 0;
    }

    /**
     * Constructs a dictionary by accepting key-value pairs.
     *
     * @param keyValuePairs an array of key-value pairs.
     * @throws IllegalParameterError if the number of arguments is not even.
     */
    public Dictionary(Object... keyValuePairs) throws IllegalParameterError {
        keys = new Object[keyValuePairs.length / 2]; // Initialize keys with appropriate size
        values = new Object[keyValuePairs.length / 2]; // Initialize values with appropriate size
        size = 0;

        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalParameterError("Invalid number of arguments", 1);
        }

        for (int i = 0; i < keyValuePairs.length; i += 2) {
            @SuppressWarnings("unchecked")
            K key = (K) keyValuePairs[i];
            Object value = keyValuePairs[i + 1];
            put(key, (V) value);
        }
    }


    /**
     * Associates the specified value with the specified key in this dictionary.
     *
     * @param key   The key.
     * @param value The value.
     */
    public void put(K key, V value) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                values[i] = value;
                return;
            }
        }

        if (size == keys.length) {
            expandCapacity();
        }

        if (value instanceof Dictionary) {
            @SuppressWarnings("unchecked")
            Dictionary<K, V> nestedDictionary = (Dictionary<K, V>) value;
            values[size] = nestedDictionary;
        } else {
            values[size] = value;
        }

        keys[size] = key;
        size++;
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key The key.
     * @return The value associated with the key, or null if the key is not found.
     */
    @SuppressWarnings("unchecked")
    public V get(K key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                Object value = values[i];
                if (value instanceof Dictionary) {
                    return (V) value;
                } else {
                    return (V) value;
                }
            }
        }
        return null;
    }

    /**
     * Removes the key-value pair associated with the specified key from the dictionary.
     *
     * @param key The key to be removed.
     */
    public void remove(K key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                if (size - 1 - i >= 0) {
                    System.arraycopy(keys, i + 1, keys, i, size - 1 - i);
                    System.arraycopy(values, i + 1, values, i, size - 1 - i);
                }
                size--;
                return;
            }
        }
    }

    /**
     * Checks if the dictionary contains the specified key.
     *
     * @param key The key to check.
     * @return true if the key is found, false otherwise.
     */
    public boolean containsKey(K key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the dictionary contains the specified value.
     *
     * @param value The value to check.
     * @return true if the value is found, false otherwise.
     */
    public boolean containsValue(V value) {
        for (int i = 0; i < size; i++) {
            if (values[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of key-value pairs in the dictionary.
     *
     * @return The size of the dictionary.
     */
    public int size() {
        return size;
    }

    /**
     * Removes all key-value pairs from the dictionary, making it empty.
     */
    public void clear() {
        keys = new Object[10];
        values = new Object[10];
        size = 0;
    }

    /**
     * Checks if the dictionary is empty.
     *
     * @return true if the dictionary is empty, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns a string representation of the dictionary.
     *
     * @return A string representation of the dictionary.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 0; i < size; i++) {
            builder.append("\"").append(keys[i]).append("\"").append(": ");
            Object value = values[i];
            if (value instanceof Dictionary) {
                builder.append(((Dictionary<?, ?>) value).toString());
            } else {
                builder.append("\"").append(values[i]).append("\"");
            }
            if (i < size - 1) {
                builder.append(", ");
            }
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Parses a string representation of a dictionary and returns the corresponding Dictionary object.
     * Note: This method does not parse nested dictionaries correctly. To handle nested dictionaries,
     * please use the put method to manually add them to the dictionary.
     *
     * @param str the string representation of the dictionary.
     * @param <K> the type of keys.
     * @param <V> the type of values.
     * @return the Dictionary object parsed from the string representation.
     */
    public static <K, V> Dictionary<K, V> fromString(String str) {
        Dictionary<K, V> dictionary = new Dictionary<>();

        // Remove the braces from the string representation
        str = str.substring(1, str.length() - 1);

        // Split the string into key-value pairs
        String[] pairs = str.split(", ");

        for (String pair : pairs) {
            // Split each pair into key and value
            String[] keyValue = pair.split(": ");

            // Remove any leading/trailing whitespaces and quotes from the key
            String key = keyValue[0].trim().replace("\"", "");

            // Remove any leading/trailing whitespaces
            String value = keyValue[1].trim();

            if (value.startsWith("{") && value.endsWith("}")) {
                // The value is a nested dictionary
                Dictionary<?, ?> nestedDictionary = Dictionary.fromString(value);
                System.out.println("Nested Dictionary: " + nestedDictionary);
                dictionary.put((K) key, (V) nestedDictionary);
            } else {
                // The value is a simple value
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    // Remove surrounding quotes if present
                    value = value.substring(1, value.length() - 1);
                }
                @SuppressWarnings("unchecked")
                K parsedKey = (K) key;
                @SuppressWarnings("unchecked")
                V parsedValue = (V) value;
                dictionary.put(parsedKey, parsedValue);
            }
        }

        return dictionary;
    }

    /**
     * Expands the capacity of the dictionary by doubling the size of the underlying arrays.
     * This method is called when the dictionary is full and needs to accommodate more key-value pairs.
     */
    private void expandCapacity() {
        int newCapacity = keys.length * 2;
        Object[] newKeys = new Object[newCapacity];
        Object[] newValues = new Object[newCapacity];
        System.arraycopy(keys, 0, newKeys, 0, size);
        System.arraycopy(values, 0, newValues, 0, size);
        keys = newKeys;
        values = newValues;
    }

    /**
     * Creates a new dictionary with the specified key-value pairs.
     *
     * @param keyValuePairs The key-value pairs.
     * @param <K>           The type of keys.
     * @param <V>           The type of values.
     * @return A new dictionary with the specified key-value pairs.
     * @throws IllegalParameterError if the number of arguments is not even.
     */
    public static <K, V> Dictionary<K, V> of(Object... keyValuePairs) throws IllegalParameterError {
        return new Dictionary<>(keyValuePairs);
    }

    /**
     * Main method for testing the Dictionary class.
     *
     * @param args Command-line arguments.
     * @throws IllegalParameterError if the number of arguments is not even.
     */
    public static void main(String[] args) throws IllegalParameterError {
        Dictionary<String, Object> dict1 = new Dictionary<>();
        dict1.put("apple", 1);
        dict1.put("banana", 2);
        dict1.put("orange", 3);

        Dictionary<String, Object> nestedDict = new Dictionary<>();
        nestedDict.put("nestedKey", "nestedValue");
        dict1.put("nestedDict", nestedDict);

        System.out.println(dict1.get("apple"));  // Output: 1
        System.out.println(dict1.containsKey("banana"));  // Output: true
        System.out.println(dict1.containsValue(3));  // Output: true
        System.out.println(dict1.size());  // Output: 4

        Dictionary<String, Object> removedDict = (Dictionary<String, Object>) dict1.get("nestedDict");
        dict1.remove("nestedDict");
        System.out.println(dict1.size());  // Output: 3
        dict1.clear();
        System.out.println(dict1.isEmpty());  // Output: true

        Dictionary<String, Object> dict2 = Dictionary.fromString("{apple: 1, banana: 2, orange: 3}");
        dict2.put("nestedDict", new Dictionary<>("nestedKey", "nestedValue"));
        System.out.println(dict2);  // Output: {apple: 1, banana: 2, orange: 3, nestedDict: {nestedKey: nestedValue}}
    }
}
