package searchengine.model;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Morphology {

    private static final String[] PARTS = new String[] {"МЕЖД", "ПРЕДЛ", "СОЮЗ", "ЧАСТ"};
    private static final String WORD_REGEX = "([^а-яё\\s])";
    private static final int MIN_LETTERS = 3;

    private final LuceneMorphology luceneMorphology;

    private static volatile Morphology instance;

    public static Morphology getInstance() throws IOException {
        if (instance == null) {
            synchronized(Morphology.class) {
                if (instance == null) {
                    instance = new Morphology();
                }
            }
        }
        return instance;
    }
    private Morphology () throws IOException {
        this.luceneMorphology = new RussianLuceneMorphology();
    }

    public HashMap<String, Integer> getLemmas(String html) throws IOException {
        String text = Jsoup.parse(html).text().toLowerCase().replaceAll("ё", "e");
        HashMap<String, Integer> lemmas = new HashMap<>();
        String[] words = textToWords(text);
        for (String word : words) {
            if (word.length() < MIN_LETTERS) {
                continue;
            }
            List<String> wordForms = luceneMorphology.getMorphInfo(word);
            if (inParts(wordForms)) {
                continue;
            }
            List<String> normalForms = luceneMorphology.getNormalForms(word);
            for (String normalForm : normalForms) {
                if (lemmas.containsKey(normalForm)) {
                    lemmas.put(normalForm, lemmas.get(normalForm) + 1);
                } else {
                    lemmas.put(normalForm, 1);
                }
            }
        }
        return lemmas;
    }

    private String[] textToWords (String text) {
        String[] result = text.toLowerCase().replaceAll(WORD_REGEX, " ").trim().split("\\s+");
        return result;
    }

    public boolean inParts(List<String> list) {
        boolean result = false;
        for (String word : list) {
            for (String part : PARTS) {
                if (word.toUpperCase().contains(part)) {
                    return true;
                }
            }
        }
        return result;
    }
}
