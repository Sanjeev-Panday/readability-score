package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Meta data class to store meta data of the input text
 */
class MetaData {
    public int sentences;
    public int words;
    public int characters;
    public int syllables;
    public int polysyllables;
}

/**
 * Main class
 */
public class Main {
    // Regular exp to calculate sentence count
    private static final String SENT_SPLIT_REGX = "[.!?]";
    // Regular exp to calculate word count
    private static final String WORD_SPLIT_REGX = "\\s+";
    // Map to store score to age mapping ( age upper bound )
    private static final Map<Integer, Integer> map = new HashMap<>();
    // Vowels
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

    /**
     *
     * @param args input argument array
     * @throws FileNotFoundException File not found exception
     */
    public static void main(String[] args) throws FileNotFoundException {

        Scanner sc = new Scanner(new File(args[0]));
        MetaData metaData = getMetaData(sc.nextLine());

        System.out.println("Words: " + metaData.words);
        System.out.println("Sentences: " + metaData.sentences);
        System.out.println("Characters: " + metaData.characters);
        System.out.println("Syllables: " + metaData.syllables);
        System.out.println("Polysyllables: " + metaData.polysyllables);

        double ari = automaticReadabilityScore(metaData);
        int ariAge = getAge(ari);

        double fk = fleshKincaidScore(metaData);
        int fkAge  = getAge(fk);

        double smog = simpleMeasureOfGobbledygook(metaData);
        int smogAge  = getAge(smog);

        double cl = colemanLiauIndex(metaData);
        int clAge  = getAge(cl);

        double avgAge = (fkAge + ariAge + smogAge + clAge) * 1.0 / 4;

        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
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
     * Calculate count of sentences, words, characters, syllables and polysyllables
     * present the input text.
     * @param  input @link String
     * @return MetaData
     */
    public static MetaData getMetaData(String input) {
        MetaData metaData = new MetaData();
        String[] sentArray = input.split(SENT_SPLIT_REGX);
        metaData.sentences = sentArray.length;

        for (String sentence : sentArray) {
            String[] wordsArray = sentence.trim().split(WORD_SPLIT_REGX);
            metaData.words += wordsArray.length;
            for (String word : wordsArray) {
                int count = countSyllables(word.toLowerCase());
                metaData.syllables += count;
                if (count > 2) {
                    metaData.polysyllables++;
                }
            }
        }
        metaData.characters = metaData.characters + Arrays.stream(input.split("\\s+")).mapToInt(String::length).reduce(0, Integer::sum);
        return metaData;
    }

    /**
     * Gets the age for the score.
     * @param score score of the text
     * @return corresponding age
     */
    private static int getAge(double score) {
        int key = (int) Math.round(score);
        if(key > 13) key = 14;
        return map.get(key);
    }
    /**
     * @param word word to count syllables
     * @return syllable count
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

    private static double automaticReadabilityScore(MetaData metaData) {
        double score = 4.71 * (metaData.characters * 1.0 / metaData.words)
                + 0.5 * (metaData.words * 1.0 / metaData.sentences) - 21.43;
        return Math.floor(score * 100) / 100.0 ;
    }

    private static double fleshKincaidScore(final MetaData metaData) {
        double score = 0.39 * ((metaData.words * 1.0) / metaData.sentences)
                + 11.8 * ((metaData.syllables * 1.0) / metaData.words) - 15.59;
        return Math.floor(score * 100) / 100.0;
    }

    private static double simpleMeasureOfGobbledygook(final MetaData metaData) {
        double score = 1.043 * Math.sqrt(metaData.polysyllables * (30.0 / metaData.sentences)) + 3.1291;
        return Math.floor(score * 100) / 100.0;
    }

    private static double colemanLiauIndex(final MetaData metaData) {
        double l = (metaData.characters * 100.0 / metaData.words);
        double s = (metaData.sentences * 100.0) / metaData.words;
        double score = 0.0588 * l - 0.296 * s - 15.8;
        return Math.floor(score * 100) / 100.0;
    }
}
