package edu.gcc.comp350.teamtoo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class generalSearch {

    private ArrayList<Course> courses;
    private ArrayList<Course> searchResults;

    public generalSearch() {
        this.courses = new ArrayList<>();
        this.searchResults = new ArrayList<>();
    }

    public generalSearch(ArrayList<Course> courses) {
        this.courses = courses;
        this.searchResults = new ArrayList<>();
    }

    public generalSearch(CourseRegistry courseRegistry) {
        this.courses = courseRegistry.getCourses();
        this.searchResults = new ArrayList<>();
    }

    public ArrayList<Course> getSearchResults() {
        return searchResults;
    }

    public ArrayList<Course> searchCourses(String searchInput) {
        searchResults.clear();

        // Step 1: Exact Match Search
        for (Course course : courses) {
            if (course.getName().equalsIgnoreCase(searchInput)) {
                searchResults.add(course);
            }
        }

        // If exact match found, return results immediately
        if (!searchResults.isEmpty()) {
            return searchResults;
        }

        // Step 2: N-Gram Similarity Matching
        int n = 2; // Using bi-grams for better typo handling
        double threshold = 0.4; // Adjust based on desired strictness

        Map<Course, Double> similarityScores = new HashMap<>();

        for (Course course : courses) {
            double similarity = nGramSimilarity(searchInput, course.getName(), n);
            if (similarity >= threshold) {
                similarityScores.put(course, similarity);
            }
        }

        // Sort results by similarity score (highest first)
        similarityScores.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(entry -> searchResults.add(entry.getKey()));

        return searchResults;
    }

    /**
     * Computes N-Gram similarity between two strings.
     * Uses Jaccard similarity over N-grams.
     */
    private double nGramSimilarity(String s1, String s2, int n) {
        ArrayList<String> ngrams1 = getNGrams(s1, n);
        ArrayList<String> ngrams2 = getNGrams(s2, n);

        int intersection = 0;
        for (String gram : ngrams1) {
            if (ngrams2.contains(gram)) {
                intersection++;
            }
        }

        int union = ngrams1.size() + ngrams2.size() - intersection;
        return union == 0 ? 0 : (double) intersection / union;
    }

    /**
     * Generates a list of N-Grams from a string.
     */
    private ArrayList<String> getNGrams(String text, int n) {
        ArrayList<String> ngrams = new ArrayList<>();
        text = text.toLowerCase().replaceAll("\\s+", " "); // Normalize spaces
        for (int i = 0; i <= text.length() - n; i++) {
            ngrams.add(text.substring(i, i + n));
        }
        return ngrams;
    }

    public static void main(String[] args) {
        CourseRegistry courseRegistry = new CourseRegistry();
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");
        generalSearch genSearch = new generalSearch(courseRegistry);
        ArrayList<Course> results = genSearch.searchCourses("DATA ANALYTICS FO");
        for (Course course : results) {
            System.out.println(course.getName() + " - " + course.getSection());
        }
        System.out.println("Search Results: " + results.size());
    }
}



