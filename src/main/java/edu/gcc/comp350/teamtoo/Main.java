package edu.gcc.comp350.teamtoo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
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

        JTextArea courseListTextArea = new JTextArea("Available courses will appear here.");
        courseListTextArea.setEditable(false);
        courseListTextArea.setBorder(BorderFactory.createTitledBorder("Course List"));

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
                showCourseSelectionView(mainPanel, frame);
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

    private static void showCourseSelectionView(JPanel mainPanel, JFrame frame) {
        JPanel courseSelectionPanel = new JPanel();
        courseSelectionPanel.setLayout(new BoxLayout(courseSelectionPanel, BoxLayout.Y_AXIS));

        // Header label aligned to the left
        JLabel selectionLabel = new JLabel("Select a course to add:");
        selectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Course options as radio buttons (only one can be selected)
        ButtonGroup courseGroup = new ButtonGroup(); // Ensures single selection
        JRadioButton course1 = new JRadioButton("Course 1: Introduction to Java");
        JRadioButton course2 = new JRadioButton("Course 2: Advanced Java Programming");
        JRadioButton course3 = new JRadioButton("Course 3: Software Engineering");
        course1.setAlignmentX(Component.LEFT_ALIGNMENT);
        course2.setAlignmentX(Component.LEFT_ALIGNMENT);
        course3.setAlignmentX(Component.LEFT_ALIGNMENT);

        courseGroup.add(course1);
        courseGroup.add(course2);
        courseGroup.add(course3);

        // Add Selected Course button aligned to the left
        JButton addCourseButton = new JButton("Add Selected Course");
        addCourseButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Back, Course Info, and Review buttons
        JButton backButton = new JButton("Back");
        JButton courseInfoButton = new JButton("Course Info");
        JButton reviewButton = new JButton("Review");

        // Align buttons in a horizontal layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align buttons to the left
        buttonPanel.add(backButton);
        buttonPanel.add(courseInfoButton);
        buttonPanel.add(reviewButton);

        // Add components to the panel
        courseSelectionPanel.add(selectionLabel); // Header
        courseSelectionPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        courseSelectionPanel.add(course1);
        courseSelectionPanel.add(course2);
        courseSelectionPanel.add(course3);
        courseSelectionPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        courseSelectionPanel.add(addCourseButton);
        courseSelectionPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        courseSelectionPanel.add(buttonPanel);

        // Back button action
        backButton.addActionListener(e -> showAddCourseView(mainPanel, frame));

        // Course Info button action
        courseInfoButton.addActionListener(e -> {
            if (course1.isSelected()) {
                showCourseInfoView(mainPanel, frame, "Course 1: Introduction to Java");
            } else if (course2.isSelected()) {
                showCourseInfoView(mainPanel, frame, "Course 2: Advanced Java Programming");
            } else if (course3.isSelected()) {
                showCourseInfoView(mainPanel, frame, "Course 3: Software Engineering");
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a course to view its info.");
            }
        });

        // Review button action (same as the ribbon Review button at the top)
        reviewButton.addActionListener(e -> reviewButtonClicked(mainPanel, frame));

        // Add the updated panel to the main panel
        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(courseSelectionPanel, BorderLayout.CENTER);

        // Refresh the frame
        frame.revalidate();
        frame.repaint();
    }

    private static void showCourseInfoView(JPanel mainPanel, JFrame frame, String courseName) {
        JPanel courseInfoPanel = new JPanel();
        courseInfoPanel.setLayout(new BoxLayout(courseInfoPanel, BoxLayout.Y_AXIS));

        JLabel headingLabel = new JLabel(courseName); // Displays the course name
        JButton backButton = new JButton("Back");

        headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.addActionListener(e -> showCourseSelectionView(mainPanel, frame));

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
        // Main panel for the Review screen
        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));

        // Warnings panel at the top
        JTextArea warningsTextArea = new JTextArea("Warnings will appear here.");
        warningsTextArea.setEditable(false);
        warningsTextArea.setBorder(BorderFactory.createTitledBorder("Warnings"));
        //warningsTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Schedule display area
        JTextArea scheduleTextArea = new JTextArea("Scheduled classes will appear here.");
        scheduleTextArea.setEditable(false);
        scheduleTextArea.setBorder(BorderFactory.createTitledBorder("Schedule"));
        //scheduleTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align buttons to the left

        JButton removeCourseButton = new JButton("Remove Course");
        JButton undoButton = new JButton("Undo");
        JButton courseInfoButton = new JButton("Course Info");
        JButton removeAllButton = new JButton("Remove All");

        // Add buttons to the panel
        buttonsPanel.add(removeCourseButton);
        buttonsPanel.add(undoButton);
        buttonsPanel.add(courseInfoButton);
        buttonsPanel.add(removeAllButton);

        // Button click actions
        removeCourseButton.addActionListener(e -> {
            System.out.println("Remove Course button clicked.");
            // TODO: Add logic to remove the selected course from the schedule
        });

        undoButton.addActionListener(e -> {
            System.out.println("Undo button clicked.");
            // TODO: Add logic to undo the last change (if any exists)
        });

        courseInfoButton.addActionListener(e -> {
            System.out.println("Course Info button clicked.");
            // TODO: Add logic to show information about a specific course
        });

        removeAllButton.addActionListener(e -> {
            System.out.println("Remove All button clicked.");
            // TODO: Add logic to remove all courses from the schedule
        });

        // Add components to the review panel
        reviewPanel.add(warningsTextArea, BorderLayout.NORTH); // Warnings section
        reviewPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        reviewPanel.add(scheduleTextArea, BorderLayout.CENTER); // Schedule section
        reviewPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        reviewPanel.add(buttonsPanel); // Buttons section

        // Add the review panel to the main panel
        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(reviewPanel, BorderLayout.CENTER);

        // Refresh the frame
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