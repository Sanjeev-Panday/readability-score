package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    private static final String SENT_SPLIT_REGX = "\\.|!|\\?";
    private static final String WORD_SPLIT_REGX = "\\s+";
    private static final Map<Integer, Integer> map = new HashMap<>();
    private static final double FIRST_COEF = 4.71D;
    private static final double SECOND_COEF = 0.5D;
    private static final double THIRD_COEF = 21.43d;
    private static final Pattern pattern = Pattern.compile("[aeiouyAEIOUY]", Pattern.CASE_INSENSITIVE);
    private static final List<Character> vowels = Arrays.asList('a', 'e', 'i', 'o', 'u', 'y');

    static {
        map.put(1, 6);
        map.put(2, 7);
        map.put(3, 9);
        map.put(4, 10);
        map.put(5, 11);
        map.put(6, 12);
        map.put(7, 13);
        map.put(8, 14);
        map.put(9, 15);
        map.put(10, 16);
        map.put(11, 17);
        map.put(12, 18);
        map.put(13, 24);
        map.put(14, 24);
    }

    private static int polysyllables;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(args[0]));

        String inputString = sc.nextLine();
        String[] sentences = inputString.split(SENT_SPLIT_REGX);
        int numOfSent = sentences.length;
        int noOfWords = 0;
        int noOfChars = 0;
        int syllableCount = 0;
        int polysyllables = 0;
        for (String sentence : sentences) {
            String[] words = sentence.trim().split(WORD_SPLIT_REGX);
            noOfWords += words.length;
            for (String word : words) {
                int count = countSyllables(word.toLowerCase());
                syllableCount += count;
                if (count > 2) {
                    polysyllables++;
                }
            }

        }
        noOfChars += Arrays.stream(inputString.split("\\s+")).mapToInt(x -> x.length()).reduce(0, (sum, x) -> sum + x);

        double score = FIRST_COEF * (noOfChars * 1.0 / noOfWords) + SECOND_COEF * (noOfWords * 1.0 / numOfSent) - THIRD_COEF;

        System.out.println("Words: " + noOfWords);
        System.out.println("Sentences: " + numOfSent);
        System.out.println("Characters: " + noOfChars);
        System.out.println("Syllables: " + syllableCount);
        System.out.println("Polysyllables: " + polysyllables);
        int key;
        double ari = Math.floor(score * 100) / 100.0 ;
        key = (int) Math.round(ari);
        if(key > 13) key = 14;
        int ariAge  = map.get(key);

        double fk = fleshKincaidScore(numOfSent, noOfWords, syllableCount);
        key = (int) Math.round(fk);
        if(key > 13) key = 14;
        int fkAge  = map.get(key);

        double smog = simpleMeasureOfGobbledygook(numOfSent, polysyllables);
        key = (int)Math.round(smog);
        if(key > 13) key = 14;
        int smogAge  = map.get(key);

        double cl = colemanLiauIndex((noOfChars * 100.0 / noOfWords), (numOfSent * 100.0) / noOfWords);
        key = (int) Math.round(cl);
        if(key > 13) key = 14;
        int clAge  = map.get(key);

        double avgAge = (fkAge + ariAge + smogAge + clAge) * 1.0 / 4;

        System.out.printf("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        Scanner scForInput = new Scanner(System.in);
        String input = scForInput.next();
        System.out.println();
        switch (input) {
            case "ARI":

                System.out.println("Automated Readability Index: " + ari + " (about " + ariAge + " year olds)");

                break;
            case "FK":

                System.out.println("Flesch–Kincaid readability tests: " + fk + " (about " + fkAge + " year olds).");

                break;
            case "SMOG":
                score = simpleMeasureOfGobbledygook(numOfSent, polysyllables);
                key = (int) Math.ceil(score);
                System.out.println("Simple Measure of Gobbledygook: " + smog + " (about " + smogAge + " year olds).");
                break;
            case "CL":
                System.out.println("Coleman–Liau index: " + cl + " (about " + clAge + " year olds).");
                break;
            case "all":

                System.out.println("Automated Readability Index: " + ari + " (about " + ariAge + " year olds).");

                System.out.println("Flesch–Kincaid readability tests: " + fk + " (about " + fkAge + " year olds).");

                System.out.println("Simple Measure of Gobbledygook: " + smog + " (about " + smogAge + " year olds).");

                System.out.println("Coleman–Liau index: " + cl + " (about " + clAge + " year olds).");

                break;
        }
        System.out.println("This text should be understood in average by " + avgAge + " year olds.");

    }

    /**
     * @param word
     * @return
     */
    private static int countSyllables(String word) {
        int counter = 0;
        boolean prevVowel = false;
        for (int i = 0; i < word.length(); i++) {
            boolean isVowel = vowels.contains(word.charAt(i));
            if (isVowel && !prevVowel) {
                counter++;
            }
            prevVowel = isVowel;
        }

        if (word.charAt(word.length() - 1) == 'e') counter = counter - 1;
        return counter > 0 ? counter : 1;
    }

    private static double fleshKincaidScore(int sents, int words, int syllables) {
        double score = 0.39 * ((words * 1.0) / sents) + 11.8 * ((syllables * 1.0) / words) - 15.59;
        return Math.floor(score * 100) / 100.0;
    }

    private static double simpleMeasureOfGobbledygook(int sentences, int polysyllables) {
        double score = 1.043 * Math.sqrt(polysyllables * (30.0 / sentences)) + 3.1291;
        return Math.floor(score * 100) / 100.0;
    }

    private static double colemanLiauIndex(double l, double s) {
        double score = 0.0588 * l - 0.296 * s - 15.8;
        return Math.floor(score * 100) / 100.0;
    }
}
