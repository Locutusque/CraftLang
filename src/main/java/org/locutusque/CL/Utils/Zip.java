package org.locutusque.CL.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Zip<T> implements Iterable<List<T>> {

    private List<List<T>> result;

    public Zip(List<List<?>> lists) {
        result = new ArrayList<>();

        // Find the shortest list
        int minLength = Integer.MAX_VALUE;
        for (List<?> list : lists) {
            if (list.size() < minLength) {
                minLength = list.size();
            }
        }

        // Create the zipped lists
        for (int i = 0; i < minLength; i++) {
            List<T> zippedList = new ArrayList<>();
            for (List<?> list : lists) {
                zippedList.add((T) list.get(i));
            }
            result.add(zippedList);
        }
    }

    public List<List<T>> getResult() {
        return result;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return result.iterator();
    }

    public static void main(String[] args) {
        // Example usage
        List<Integer> numbers = List.of(1, 2, 3);
        List<String> letters = List.of("A", "B", "C");
        List<Boolean> flags = List.of(true, false, true);

        List<List<?>> inputLists = new ArrayList<>();
        inputLists.add(numbers);
        inputLists.add(letters);
        inputLists.add(flags);

        for (List<?> zippedList : new Zip<>(inputLists)) {
            System.out.println(zippedList);
        }
    }
}


