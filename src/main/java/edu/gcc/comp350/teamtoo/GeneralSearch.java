package edu.gcc.comp350.teamtoo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeneralSearch {

    private ArrayList<Course> courses;
    private ArrayList<Course> searchResults;
    // private String term;

    public GeneralSearch() {
        this.courses = new ArrayList<>();
        this.searchResults = new ArrayList<>();
    }

    public GeneralSearch(ArrayList<Course> courses) {
        this.courses = courses;
        this.searchResults = new ArrayList<>();
    }

    public GeneralSearch(CourseRegistry courseRegistry) {
        this.courses = courseRegistry.getCourses();
        this.searchResults = new ArrayList<>();
    }

    /*
    public GeneralSearch(CourseRegistry courseRegistry, Schedule schedule) {
        this.courses = courseRegistry.getCourses();
        this.searchResults = new ArrayList<>();
        this.term = schedule.getTerm();
     */

    public ArrayList<Course> getSearchResults() {
        return searchResults;
    }

    public ArrayList<Course> searchCourses(String searchInput, String term) {
        searchResults.clear();

        // Validate search input
        if (searchInput == null || searchInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Search input cannot be null or empty.");
        }

        // Validate term format (YYYY_Season)
        if (!term.matches("\\d{4}_(Fall|Spring)")) {
            throw new IllegalArgumentException("Invalid term format. Expected format: YYYY_Season (e.g., 2023_Fall)");
        }

        // Step 1: Exact Match Search (Only for the specified term)
        for (Course course : courses) {
            if (course.getSemester().equals(term) && course.getName().equalsIgnoreCase(searchInput)) {
                searchResults.add(course);
            }
        }

        // If exact match found, return results
        if (!searchResults.isEmpty()) {
            return searchResults;
        }

        // Step 2: N-Gram Similarity Matching (Only for the specified term)
        int n = 2; // Bi-grams
        double threshold = 0.25; // Adjust for sensitivity

        Map<Course, Double> similarityScores = new HashMap<>();

        for (Course course : courses) {
            if (course.getSemester().equals(term)) {  // Filter by term
                double similarity = nGramSimilarity(searchInput, course.getName(), n);
                if (similarity >= threshold) {
                    similarityScores.put(course, similarity);
                }
            }
        }

        // Sort by similarity score (highest first)
        similarityScores.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(entry -> searchResults.add(entry.getKey()));

        return searchResults;
    }

    public ArrayList<Course> searchCourses(String searchInput) {
        searchResults.clear();

        // Validate search input
        if (searchInput == null || searchInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Search input cannot be null or empty.");
        }

        int n = 2; // Bi-grams
        double baseThreshold = 0.2; // Base threshold
        double lengthFactor = 0.03; // Increase threshold with search length

        // Adjust threshold dynamically
        double threshold = baseThreshold + (searchInput.length() * lengthFactor);

        // If the input is long (>= 6), increase the threshold to reduce false matches
        if (searchInput.length() >= 6) {
            threshold = Math.max(0.7, threshold);
        } else if (searchInput.length() <= 4) {
            threshold = Math.max(0.75, threshold); // Stricter for short words like "Econ"
        }
        threshold = Math.min(0.85, threshold); // Cap the threshold for more selective fuzzy matching

        Map<Course, Double> similarityScores = new HashMap<>();
        ArrayList<Course> exactMatches = new ArrayList<>();

        String searchLower = searchInput.toLowerCase();

        for (Course course : courses) {
            String courseName = course.getName().toLowerCase();

            // Check for exact match first
            boolean isExactMatch = courseName.startsWith(searchLower) || courseName.contains(" " + searchLower + " ");

            if (isExactMatch) {
                exactMatches.add(course);
            }

            // Word boundary check for short search inputs to reduce accidental partial matches
            boolean hasWholeWordMatch = courseName.matches(".*\\b" + searchLower + "\\b.*");

            // Always check for fuzzy matches, but make sure the match is relevant enough
            if (searchInput.length() <= 4 || hasWholeWordMatch) {
                double similarity = nGramSimilarity(searchInput, courseName, n);
                if (similarity >= threshold) {
                    similarityScores.put(course, similarity);
                }
            }
        }

        // Sort exact matches first (best matches)
        searchResults.addAll(exactMatches);

        // Then sort similarity-based matches
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
        //Test Code
        CourseRegistry courseRegistry = new CourseRegistry();
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");
        GeneralSearch genSearch = new GeneralSearch(courseRegistry);
        ArrayList<Course> results = genSearch.searchCourses("Comp");
        for (Course course : results) {
            System.out.println(course.getName() + " - " + course.getSection() + " - " + course.getSemester());
        }
        System.out.println("Search Results: " + results.size());
    }
}



