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
        double threshold = 0;


        if (searchInput.length() <= 4) {
            threshold = .35;
        } else {
            threshold = .30;
        }
        System.out.println(threshold);
        // Cap the threshold for more selective fuzzy matching

        Map<Course, Double> similarityScores = new HashMap<>();
        ArrayList<Course> exactMatches = new ArrayList<>();

        String searchLower = searchInput.toLowerCase();

        searchLower = searchLower.replaceFirst("(\\d)", " $1");
        searchLower = searchLower.replaceAll("00", "");

        for (Course course : courses) {
            boolean courseAdded = false;

            //cycle through course name
            String courseName = course.getName().toLowerCase();

            // Check for exact match first
            boolean isExactMatch = courseName.startsWith(searchLower) || containsSubstringInWords(courseName, searchLower);

            if (isExactMatch) {
                exactMatches.add(course);
                courseAdded = true;
            } else {
                double similarity = nGramSimilarity(searchInput, courseName, n);
                if (similarity >= threshold) {
                    similarityScores.put(course, similarity);
                    courseAdded = true;
                }
            }

            //cycle through course id
            //pars numbers and remove 00s
            if (!courseAdded) {
                String courseId = course.getCourseID().toLowerCase();

                // Check for exact match first
                isExactMatch = courseId.startsWith(searchLower);

                if (isExactMatch) {
                    exactMatches.add(course);
                    courseAdded = true;
                }
            }

            //cycle through faculty
            if (!courseAdded) {
                String facultyLower = course.getFaculty().toLowerCase();

                // Check for exact match first
                isExactMatch = facultyLower.startsWith(searchLower) || containsSubstringInWords(facultyLower, searchLower);
                if (isExactMatch) {
                    exactMatches.add(course);
                    courseAdded = true;
                } else {
                    double similarity = nGramSimilarity(searchInput, facultyLower, n);
                    if (similarity >= threshold) {
                        similarityScores.put(course, similarity);
                        courseAdded = true;
                    }
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


    public static boolean containsSubstringInWords(String mainString, String subString) {
        if (subString == null || subString.isEmpty()) {
            throw new IllegalArgumentException("The substring cannot be null or empty.");
        }

        // Normalize the substring by removing whitespace and converting to lowercase
        String normalizedSubString = subString.replaceAll("\\s+", "").toLowerCase();

        // Split the main string into words
        String[] words = mainString.split("\\s+");

        // Check each word individually
        for (String word : words) {
            String normalizedWord = word.toLowerCase();
            if (normalizedWord.contains(normalizedSubString)) {
                return true;
            }
        }

        return false;
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
        /*CourseRegistry courseRegistry = new CourseRegistry();
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");
        GeneralSearch genSearch = new GeneralSearch(courseRegistry);
        ArrayList<Course> results = genSearch.searchCourses("Comp");
        for (Course course : results) {
            System.out.println(course.getName() + " - " + course.getSection() + " - " + course.getSemester());
        }
        System.out.println("Search Results: " + results.size());
        System.out.println("Search Results: " + genSearch.getSearchResults().size());
         */

        String course = "COMP350";

        String courseId = "COMP350";

        // add a space before the first number and remove 00s
        courseId = courseId.replaceFirst("(\\d)", " $1");
        courseId = courseId.replaceAll(" 00", " ");
        System.out.println(courseId);
    }
}



