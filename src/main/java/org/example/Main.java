package org.example;

import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {

        List<Future<Integer>> threads = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        final ExecutorService threadPool = Executors.newFixedThreadPool(8);

        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        for (String text : texts) {
            threads.add(threadPool.submit(() -> processingString(text)));
        }
        threads.forEach(x -> {
            try {
                result.add(x.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        Optional<Integer> max = result.stream().max(Integer::compareTo);
        if (max.isPresent()) {
            Integer x = max.get();
            System.out.println("Максимальный интервал значений среди всех строк - " + x);
        }

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
        threadPool.shutdown();
    }


    private static int processingString(String text) {
        int maxSize = 0;
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < text.length(); j++) {
                if (i >= j) {
                    continue;
                }
                boolean bFound = false;
                for (int k = i; k < j; k++) {
                    if (text.charAt(k) == 'b') {
                        bFound = true;
                        break;
                    }
                }
                if (!bFound && maxSize < j - i) {
                    maxSize = j - i;
                }
            }
        }
        System.out.println(text.substring(0, 100) + " -> " + maxSize);
        return maxSize;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}