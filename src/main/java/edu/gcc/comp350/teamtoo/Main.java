package edu.gcc.comp350.teamtoo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final List<String> selectedCourses = new ArrayList<>();

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        JFrame frame = new JFrame("Course Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel ribbonPanel = createRibbonPanel(mainPanel, frame);
        mainPanel.add(ribbonPanel, BorderLayout.NORTH);

        showHomeView(mainPanel, frame);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JPanel createRibbonPanel(JPanel mainPanel, JFrame frame) {
        JPanel ribbonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton homeButton = new JButton("Home");
        JButton addCourseButton = new JButton("Add Course");
        JButton reviewButton = new JButton("Review");
        JButton courseDirectoryButton = new JButton("Course Directory");

        homeButton.addActionListener(e -> homeButtonClicked(mainPanel, frame));
        addCourseButton.addActionListener(e -> addCourseButtonClicked(mainPanel, frame));
        reviewButton.addActionListener(e -> reviewButtonClicked(mainPanel, frame));
        courseDirectoryButton.addActionListener(e -> courseDirectoryButtonClicked(mainPanel, frame));

        ribbonPanel.add(homeButton);
        ribbonPanel.add(addCourseButton);
        ribbonPanel.add(reviewButton);
        ribbonPanel.add(courseDirectoryButton);

        return ribbonPanel;
    }

    private static void homeButtonClicked(JPanel mainPanel, JFrame frame) {
        System.out.println("Home");
        showHomeView(mainPanel, frame);
    }

    private static void addCourseButtonClicked(JPanel mainPanel, JFrame frame) {
        System.out.println("Add Course");
        showAddCourseView(mainPanel, frame);
    }

    private static void reviewButtonClicked(JPanel mainPanel, JFrame frame) {
        System.out.println("Review button clicked.");
        showReviewView(mainPanel, frame);
    }

    private static void courseDirectoryButtonClicked(JPanel mainPanel, JFrame frame) {
        System.out.println("Course Directory");
        showCourseDirectoryView(mainPanel, frame);
    }

    private static void showHomeView(JPanel mainPanel, JFrame frame) {
        JPanel homePanel = new JPanel(new BorderLayout());

        JTextArea warningsTextArea = new JTextArea("Warnings will appear here.");
        warningsTextArea.setEditable(false);
        warningsTextArea.setBorder(BorderFactory.createTitledBorder("Warnings"));

        JTextArea courseListTextArea = new JTextArea();
        courseListTextArea.setEditable(false);
        courseListTextArea.setBorder(BorderFactory.createTitledBorder("Course List"));

        // Update displayed courses
        StringBuilder courseText = new StringBuilder();
        if (selectedCourses.isEmpty()) {
            courseText.append("No courses added yet.");
        } else {
            for (String course : selectedCourses) {
                courseText.append(course).append("\n");
            }
        }
        courseListTextArea.setText(courseText.toString());

        homePanel.add(warningsTextArea, BorderLayout.NORTH);
        homePanel.add(courseListTextArea, BorderLayout.CENTER);

        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(homePanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    private static void showCourseDirectoryView(JPanel mainPanel, JFrame frame) {
        JPanel courseDirectoryPanel = new JPanel();
        courseDirectoryPanel.setLayout(new BoxLayout(courseDirectoryPanel, BoxLayout.Y_AXIS));

        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(courseDirectoryPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    private static void showAddCourseView(JPanel mainPanel, JFrame frame) {
        JPanel addCoursePanel = new JPanel();
        addCoursePanel.setLayout(new BoxLayout(addCoursePanel, BoxLayout.Y_AXIS));

        // Align General Search to the left
        JPanel generalSearchWrapper = new JPanel();
        generalSearchWrapper.setLayout(new BoxLayout(generalSearchWrapper, BoxLayout.Y_AXIS));
        generalSearchWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel generalSearchLabel = new JLabel("General Search");
        generalSearchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField searchField = new JTextField(20);
        JButton generalSearchButton = new JButton("Search");
        JPanel generalSearchPanel = new JPanel();
        generalSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        generalSearchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        generalSearchPanel.add(searchField);
        generalSearchPanel.add(generalSearchButton);

        generalSearchWrapper.add(generalSearchLabel);
        generalSearchWrapper.add(generalSearchPanel);

        addCoursePanel.add(generalSearchWrapper);

        // Align Advanced Search to the left
        JPanel advancedSearchWrapper = new JPanel();
        advancedSearchWrapper.setLayout(new BoxLayout(advancedSearchWrapper, BoxLayout.Y_AXIS));
        advancedSearchWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel advancedSearchLabel = new JLabel("Advanced Search");
        advancedSearchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel advancedSearchPanel = new JPanel(new GridBagLayout());
        advancedSearchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1: Time
        JCheckBox timeCheckBox = new JCheckBox("Time");
        JComboBox<String> startTimeDropdown = new JComboBox<>(generateTimeOptions().toArray(new String[0]));
        JComboBox<String> startAmPmCombo = new JComboBox<>(new String[]{"AM", "PM"});
        JComboBox<String> endTimeDropdown = new JComboBox<>(generateTimeOptions().toArray(new String[0]));
        JComboBox<String> endAmPmCombo = new JComboBox<>(new String[]{"AM", "PM"});
        JPanel timePanel = new JPanel(new FlowLayout());
        timePanel.add(new JLabel("Start:"));
        timePanel.add(startTimeDropdown);
        timePanel.add(startAmPmCombo);
        timePanel.add(new JLabel("End:"));
        timePanel.add(endTimeDropdown);
        timePanel.add(endAmPmCombo);

        gbc.gridx = 0;
        gbc.gridy = 0;
        advancedSearchPanel.add(timeCheckBox, gbc);
        gbc.gridx = 1;
        advancedSearchPanel.add(timePanel, gbc);

        // Row 2: Days of the Week
        JCheckBox daysCheckBox = new JCheckBox("Days of the Week");
        JRadioButton mwfButton = new JRadioButton("MWF");
        JRadioButton trButton = new JRadioButton("TR");
        ButtonGroup daysGroup = new ButtonGroup();
        daysGroup.add(mwfButton);
        daysGroup.add(trButton);
        JPanel daysPanel = new JPanel(new FlowLayout());
        daysPanel.add(mwfButton);
        daysPanel.add(trButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        advancedSearchPanel.add(daysCheckBox, gbc);
        gbc.gridx = 1;
        advancedSearchPanel.add(daysPanel, gbc);

        // Row 3: Course Code
        JCheckBox courseCodeCheckBox = new JCheckBox("Course Code");
        JTextField courseCodeField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 2;
        advancedSearchPanel.add(courseCodeCheckBox, gbc);
        gbc.gridx = 1;
        advancedSearchPanel.add(courseCodeField, gbc);

        // Row 4: Course Keyword
        JCheckBox keywordCheckBox = new JCheckBox("Course Keyword");
        JTextField keywordField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 3;
        advancedSearchPanel.add(keywordCheckBox, gbc);
        gbc.gridx = 1;
        advancedSearchPanel.add(keywordField, gbc);

        // Row 5: Semester
        JCheckBox semesterCheckBox = new JCheckBox("Semester");
        JComboBox<String> semesterCombo = new JComboBox<>(new String[]{"Fall", "Spring"});
        JComboBox<Integer> yearCombo = new JComboBox<>();
        for (int year = 2020; year <= 2030; year++) {
            yearCombo.addItem(year);
        }
        JPanel semesterPanel = new JPanel(new FlowLayout());
        semesterPanel.add(semesterCombo);
        semesterPanel.add(yearCombo);

        gbc.gridx = 0;
        gbc.gridy = 4;
        advancedSearchPanel.add(semesterCheckBox, gbc);
        gbc.gridx = 1;
        advancedSearchPanel.add(semesterPanel, gbc);

        advancedSearchWrapper.add(advancedSearchLabel);
        advancedSearchWrapper.add(advancedSearchPanel); // No Space Now!

        addCoursePanel.add(advancedSearchWrapper);

        // Error message and bottom Search button
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        JButton advancedSearchButton = new JButton("Search");
        addCoursePanel.add(errorLabel);
        addCoursePanel.add(advancedSearchButton);

        // Action Listeners
        generalSearchButton.addActionListener(e -> {
            System.out.println("General Search executed: " + searchField.getText());
            generalSearchPanel.removeAll();
            JLabel searchResultLabel = new JLabel("Search result: " + searchField.getText());
            generalSearchPanel.add(searchResultLabel);
            generalSearchPanel.revalidate();
            generalSearchPanel.repaint();
        });

        advancedSearchButton.addActionListener(e -> {
            String errorMessage = validateAdvancedSearch(timeCheckBox, startTimeDropdown, endTimeDropdown, startAmPmCombo, endAmPmCombo,daysCheckBox,
                    mwfButton, trButton, courseCodeCheckBox, courseCodeField, keywordCheckBox, keywordField,
                    semesterCheckBox, semesterCombo, yearCombo);

            if (!errorMessage.isEmpty()) {
                errorLabel.setText(errorMessage);
            } else {
                showCourseSelectionView(mainPanel, frame, new CourseRegistry());
            }
        });

        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(addCoursePanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    private static String validateAdvancedSearch(JCheckBox timeCheckBox, JComboBox<String> startTimeDropdown,
                                                 JComboBox<String> endTimeDropdown, JComboBox<String> startAmPmCombo, JComboBox<String> endAmPmCombo,JCheckBox daysCheckBox,
                                                 JRadioButton mwfButton, JRadioButton trButton,
                                                 JCheckBox courseCodeCheckBox, JTextField courseCodeField,
                                                 JCheckBox keywordCheckBox, JTextField keywordField,
                                                 JCheckBox semesterCheckBox, JComboBox<String> semesterCombo,
                                                 JComboBox<Integer> yearCombo) {
        StringBuilder errorMessage = new StringBuilder();

        if (timeCheckBox.isSelected()) {
            // Retrieve selected times
            String startTime = startTimeDropdown.getSelectedItem() + " " + startAmPmCombo.getSelectedItem();
            String endTime = endTimeDropdown.getSelectedItem() + " " + endAmPmCombo.getSelectedItem();

            // Convert times to minutes and validate order
            int startTimeInMinutes = parseTimeToMinutes(startTime);
            int endTimeInMinutes = parseTimeToMinutes(endTime);

            if (startTimeInMinutes >= endTimeInMinutes) {
                errorMessage.append("End time must be later than start time.\n");
            }
        }


        if (daysCheckBox.isSelected() && !mwfButton.isSelected() && !trButton.isSelected()) {
            errorMessage.append("You must select either MWF or TR for Days of Week.\n");
        }

        if (courseCodeCheckBox.isSelected() && courseCodeField.getText().trim().isEmpty()) {
            errorMessage.append("Course Code cannot be empty.\n");
        }

        if (keywordCheckBox.isSelected() && keywordField.getText().trim().isEmpty()) {
            errorMessage.append("Course Keyword cannot be empty.\n");
        }

        if (semesterCheckBox.isSelected() && (semesterCombo.getSelectedItem() == null || yearCombo.getSelectedItem() == null)) {
            errorMessage.append("You must select a semester and year.\n");
        }

        return errorMessage.toString().trim();
    }

    private static void showCourseSelectionView(JPanel mainPanel, JFrame frame, CourseRegistry courseRegistry) {
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");
        // Create a panel for course selection with a somewhat nice looking layout
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Add padding to the panel

        // Title label with styling
        JLabel label = new JLabel("Select a course to add:");
        label.setFont(new Font("Arial", Font.BOLD, 16));  // Make the label text bold and larger
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectionPanel.add(label);

        // Dropdown for courses from CourseRegistry
        JComboBox<Course> courseDropdown = new JComboBox<>();

        // Populate the dropdown with courses from courseRegistry
        for (Course course : courseRegistry.getCourses()) {
            courseDropdown.addItem(course);  // Add the Course object itself to the JComboBox
        }

        // Set the renderer to display only the course name in the dropdown
        // This was so much more trouble than it was worth but I couldn't find a workaround.
        courseDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // Use the course name for display
                if (value instanceof Course) {
                    value = ((Course) value).getName();  // Display course name in the dropdown
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Add the dropdown to the selection panel with alignment
        courseDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectionPanel.add(courseDropdown);

        // Add a button to add the selected course with some padding
        JButton addButton = new JButton("Add Selected Course");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));  // Make button text readable
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectionPanel.add(Box.createVerticalStrut(10));  // Add vertical space between components
        selectionPanel.add(addButton);

        addButton.addActionListener(e -> {
            Course selectedCourse = (Course) courseDropdown.getSelectedItem();
            if (selectedCourse != null) {
                // Add the course name to selectedCourses (assuming 'getName' returns the name of the course)
                selectedCourses.add(selectedCourse.getName());  // Add course name to list

                // Show a confirmation message
                JOptionPane.showMessageDialog(frame, "Added: " + selectedCourse.getName());

                // Refresh home and review views
                showHomeView(mainPanel, frame);
                showReviewView(mainPanel, frame);
            }
        });

        // Clear existing panels and add new ones
        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(selectionPanel, BorderLayout.CENTER);

        // Revalidate and repaint the frame to update the UI
        frame.revalidate();
        frame.repaint();

        // Open the dropdown after the UI has been fully rendered
        SwingUtilities.invokeLater(() -> courseDropdown.showPopup());
    }


    private static void showCourseInfoView(JPanel mainPanel, JFrame frame, String courseName) {
        JPanel courseInfoPanel = new JPanel();
        courseInfoPanel.setLayout(new BoxLayout(courseInfoPanel, BoxLayout.Y_AXIS));

        JLabel headingLabel = new JLabel(courseName); // Displays the course name
        JButton backButton = new JButton("Back");

        headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.addActionListener(e -> showCourseSelectionView(mainPanel, frame, new CourseRegistry()));

        courseInfoPanel.add(Box.createVerticalStrut(50)); // Add some spacing
        courseInfoPanel.add(headingLabel);
        courseInfoPanel.add(Box.createVerticalStrut(20));
        courseInfoPanel.add(backButton);

        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(courseInfoPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    private static void showReviewView(JPanel mainPanel, JFrame frame) {
        /*
        Tried a few different things to get the vertical spacing between courses to fix itself but it won't work lol
         */
        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));

        // Label for the review section
        JLabel label = new JLabel("Review your courses:");
        label.setFont(new Font("Arial", Font.BOLD, 16));  // Bold and bigger text for the label
        reviewPanel.add(label);

        if (selectedCourses.isEmpty()) {
            reviewPanel.add(new JLabel("No courses added yet."));
        } else {
            for (String course : selectedCourses) {
                JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));  // Adjust spacing between components in the panel
                coursePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));  // Reduce vertical spacing between panels

                JLabel courseLabel = new JLabel(course);
                courseLabel.setFont(new Font("Arial", Font.PLAIN, 14));  // Make the course name slightly smaller

                JButton removeButton = new JButton("Remove");
                removeButton.setFont(new Font("Arial", Font.PLAIN, 12));  // Adjust button text size

                // Action to remove the course
                removeButton.addActionListener(e -> {
                    selectedCourses.remove(course);
                    showReviewView(mainPanel, frame); // Refresh review page
                });

                // Add the course label and remove button to the course panel
                coursePanel.add(courseLabel);
                coursePanel.add(removeButton);

                // Add the coursePanel to the reviewPanel
                reviewPanel.add(coursePanel);
            }
        }

        // Clear existing panels and add new ones
        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(reviewPanel, BorderLayout.CENTER);

        // Revalidate and repaint the frame to update the UI
        frame.revalidate();
        frame.repaint();
    }

    private static List<String> generateTimeOptions() {
        List<String> times = new ArrayList<>();
        for (int hour = 1; hour <= 12; hour++) {
            times.add(hour + ":00");
            times.add(hour + ":30");
        }
        return times;
    }

    private static int parseTimeToMinutes(String time) {
        // Example format: "1:30 PM"
        String[] parts = time.split(" ");
        String[] timeParts = parts[0].split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Adjust for AM/PM
        if (parts[1].equals("PM") && hour != 12) {
            hour += 12;
        } else if (parts[1].equals("AM") && hour == 12) {
            hour = 0;
        }

        return hour * 60 + minute; // Convert to total minutes
    }
}