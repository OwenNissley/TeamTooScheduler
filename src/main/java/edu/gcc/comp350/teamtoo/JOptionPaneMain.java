package edu.gcc.comp350.teamtoo;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JOptionPaneMain {
    private static Core core;

    //for semester stuff
    private static JComboBox<String> semesterCombo;
    private static JComboBox<Integer> yearCombo;

    public static void main(String[] args) {
        core = new Core();
       run();
    }

    public static void run() {
        // Add shutdown hook to save schedules when the program exits
        //automatically runs when the program is closed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveSchedulesIntoFile();
        }));

        JFrame frame = new JFrame("Course Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel ribbonPanel = createRibbonPanel(mainPanel, frame);
        mainPanel.add(ribbonPanel, BorderLayout.NORTH);

        //initialize semester stuff
        semesterCombo = new JComboBox<>(new String[]{"Fall", "Spring"});
        yearCombo = new JComboBox<>();
        for (int year = 2023; year <= 2025; year++) {
            yearCombo.addItem(year);
        }

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
        homePanel.setName("HomePanel");

        //create semester panel
        homePanel.add(createSemesterPanel(), BorderLayout.NORTH);

        //display warnings
        JTextArea warningsTextArea = new JTextArea("Warnings will appear here.");
        warningsTextArea.setEditable(false);
        warningsTextArea.setBorder(BorderFactory.createTitledBorder("Warnings"));

        //display conflicting courses
        StringBuilder warningText = new StringBuilder();
        if (core.getConflictingCourses().isEmpty()) {
            warningText.append("No conflicts found.");
        } else {
            warningText.append("Conflicting courses found: \n");
            for (Course course : core.getConflictingCourses()) {
                warningText.append(course).append("\n");
            }
        }
        warningsTextArea.setText(warningText.toString());

        //only add non-conflicting courses to the schedule
        ArrayList<Course> nonConflictingCourses = new ArrayList<>();
        for (Course course : core.getSchedule()) {
            if (!core.getConflictingCourses().contains(course)) {
                nonConflictingCourses.add(course);
            }
        }


       // Create the table and scroll pane
       JTable scheduleTable = new JTable(new ScheduleTableModel(nonConflictingCourses));
       scheduleTable.setRowHeight(50); // Set row height

       // Set column width
       for (int i = 0; i < scheduleTable.getColumnCount(); i++) {
           TableColumn column = scheduleTable.getColumnModel().getColumn(i);
           column.setPreferredWidth(150);
       }

       JScrollPane scrollPane = new JScrollPane(scheduleTable);
       scheduleTable.setFillsViewportHeight(true);

       // Add components to the home panel
        homePanel.add(warningsTextArea, BorderLayout.CENTER);
        homePanel.add(scrollPane, BorderLayout.SOUTH);
        mainPanel.removeAll();
        mainPanel.add(createRibbonPanel(mainPanel, frame), BorderLayout.NORTH);
        mainPanel.add(homePanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    private static void showCourseDirectoryView(JPanel mainPanel, JFrame frame) {
        mainPanel.removeAll(); // Clear only the content area (keeps the ribbon)
        mainPanel.setLayout(new BorderLayout());

        // Load courses from JSON
        CourseRegistry registry = new CourseRegistry();
        registry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");

        // Extract course names for JList
        ArrayList<Course> courses = registry.getCourses();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        for (Course course : courses) {
            courseListModel.addElement(course.getName());
        }

        // Course list in a scrollable pane
        JList<String> courseList = new JList<>(courseListModel);
        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setPreferredSize(new Dimension(250, 500));

        // Course details area
        JTextArea courseDetails = new JTextArea();
        courseDetails.setEditable(false);
        courseDetails.setLineWrap(true);
        courseDetails.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(courseDetails);

        // When a course is selected, show its details
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = courseList.getSelectedIndex();
                if (index >= 0) {
                    Course selectedCourse = courses.get(index);
                    courseDetails.setText(formatCourseDetails(selectedCourse));
                }
            }
        });

        // Layout: List on the left, details on the right
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.WEST);
        contentPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Create return button
        JButton returnButton = new JButton("Return to Home");
        returnButton.addActionListener(e -> showHomeView(mainPanel, frame));

        // Search field
        JTextField searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filterList() {
                String searchText = searchField.getText().toLowerCase();
                courseListModel.clear();
                for (Course course : courses) {
                    if (course.getName().toLowerCase().contains(searchText)) {
                        courseListModel.addElement(course.getName());
                    }
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterList();
            }
        });

        // Panel for search bar and return button
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Search: "));
        controlPanel.add(searchField);
        controlPanel.add(returnButton);

        // Add components to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Refresh UI
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Helper method to format course details; this is here because it's commented out in CourseRegistry and I didn't want to touch that class.
    private static String formatCourseDetails(Course course) {
        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(course.getName()).append("\n");
        details.append("Number: ").append(course.getNumber()).append("\n");
        details.append("Credits: ").append(course.getCredits()).append("\n");
        details.append("Faculty: ").append(course.getFaculty()).append("\n");
        details.append("Location: ").append(course.getLocation()).append("\n");
        details.append("Semester: ").append(course.getSemester()).append("\n");
        details.append("Section: ").append(course.getSection()).append("\n");
        details.append("Subject: ").append(course.getSubject()).append("\n");
        details.append("Seats: ").append(course.getOpenSeats()).append("/").append(course.getTotalSeats()).append("\n");
        details.append("Lab: ").append(course.isLab() ? "Yes" : "No").append("\n");
        details.append("Status: ").append(course.isOpen() ? "Open" : "Closed").append("\n");

        // Add time slots
        details.append("\nSchedule:\n");
        for (Course.TimeSlot slot : course.getTimes()) {
            details.append("  ").append(slot.getDay()).append(": ")
                    .append(slot.getStartTime()).append(" - ")
                    .append(slot.getEndTime()).append("\n");
        }

        return details.toString();
    }

    private static void showAddCourseView(JPanel mainPanel, JFrame frame) {
        JPanel addCoursePanel = new JPanel();
        addCoursePanel.setName("AddCoursePanel");
        //addCoursePanel.setLayout(new BoxLayout(addCoursePanel, BoxLayout.Y_AXIS));

        // Create semester panel
        addCoursePanel.add(createSemesterPanel(), BorderLayout.NORTH);

        //instantly sort based on semester
        core.addFilter(new FilterSemester(yearCombo.getSelectedItem().toString(), semesterCombo.getSelectedItem().toString()));

        // Align General Search to the left
        JPanel generalSearchWrapper = new JPanel();
        //generalSearchWrapper.setLayout(new BoxLayout(generalSearchWrapper, BoxLayout.Y_AXIS));
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

        generalSearchWrapper.add(generalSearchLabel, BorderLayout.NORTH);
        generalSearchWrapper.add(generalSearchPanel, BorderLayout.SOUTH);

        addCoursePanel.add(generalSearchWrapper, BorderLayout.CENTER);

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
        /*
        JCheckBox keywordCheckBox = new JCheckBox("Course Keyword");
        JTextField keywordField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 3;
        advancedSearchPanel.add(keywordCheckBox, gbc);
        gbc.gridx = 1;
        advancedSearchPanel.add(keywordField, gbc);
        */

        advancedSearchWrapper.add(advancedSearchLabel);
        advancedSearchWrapper.add(advancedSearchPanel);

        addCoursePanel.add(advancedSearchWrapper);

        // Error message and bottom Search button
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        JButton advancedSearchButton = new JButton("Search");
        addCoursePanel.add(errorLabel);

        addCoursePanel.add(advancedSearchButton, BorderLayout.SOUTH);

        // Action Listeners
        generalSearchButton.addActionListener(e -> {
            if (/*!semesterCheckBox.isSelected() || */semesterCombo.getSelectedItem() == null || yearCombo.getSelectedItem() == null) {
                errorLabel.setText("You must select a semester and year.");
            }
            else if (searchField.getText().trim().isEmpty()) {
                errorLabel.setText("Search field cannot be empty.");
            } else {
                System.out.println("General Search executed: " + searchField.getText());

                // Call core.SearchGeneral() with the search term
                core.searchGeneral(searchField.getText());

                generalSearchPanel.removeAll();
                JLabel searchResultLabel = new JLabel("Search result: " + searchField.getText());
                generalSearchPanel.add(searchResultLabel);
                generalSearchPanel.revalidate();
                generalSearchPanel.repaint();
            }
        });

        advancedSearchButton.addActionListener(e -> {
            if (/*!semesterCheckBox.isSelected() || */semesterCombo.getSelectedItem() == null || yearCombo.getSelectedItem() == null) {
                errorLabel.setText("You must select a semester and year.");
            } else {
                String errorMessage = validateAdvancedSearch(timeCheckBox, startTimeDropdown, endTimeDropdown, startAmPmCombo, endAmPmCombo, daysCheckBox,
                        mwfButton, trButton, courseCodeCheckBox, courseCodeField, /*keywordCheckBox, keywordField,
                        /*semesterCheckBox, */semesterCombo, yearCombo);

                if (!errorMessage.isEmpty()) {
                    errorLabel.setText(errorMessage);
                } else {
                    //for each filter, call core.addFilter()

                    //time filter
                    if (timeCheckBox.isSelected()) {
                        // Retrieve selected times
                        String startTime = startTimeDropdown.getSelectedItem() + " " + startAmPmCombo.getSelectedItem();
                        String endTime = endTimeDropdown.getSelectedItem() + " " + endAmPmCombo.getSelectedItem();

                        // Convert times to minutes and validate order
                        //int startTimeInMinutes = parseTimeToMinutes(startTime);
                        //int endTimeInMinutes = parseTimeToMinutes(endTime);

                        core.addFilter(new FilterTime(startTime, endTime));
                    }

                    //days of the week filter
                    if (daysCheckBox.isSelected()) {
                        if (mwfButton.isSelected()) {
                            core.addFilter(new FilterDaysOfWeek("MWF"));
                        } else if (trButton.isSelected()) {
                            core.addFilter(new FilterDaysOfWeek("TR"));
                        }
                    }

                    //course code filter
                    if (courseCodeCheckBox.isSelected()) {
                        core.addFilter(new FilterCourseCode(Integer.parseInt(courseCodeField.getText())));
                    }


                    System.out.println("Advanced Search executed.");

                    // Call core.searchAdvanced()
                    core.searchAdvanced();

                    showCourseSelectionView(mainPanel, frame);
                }
            }
        });

        /*
        //when the semester or year fields are updated, call core.addFilter() with the new filter
        semesterCombo.addActionListener(e -> {
            core.addFilter(new FilterSemester(yearCombo.getSelectedItem().toString(), semesterCombo.getSelectedItem().toString()));
            core.updateSemester(yearCombo.getSelectedItem().toString() + "_" + semesterCombo.getSelectedItem().toString());
        });
        yearCombo.addActionListener(e -> {
            core.addFilter(new FilterSemester(yearCombo.getSelectedItem().toString(), semesterCombo.getSelectedItem().toString()));
            core.updateSemester(yearCombo.getSelectedItem().toString() + "_" + semesterCombo.getSelectedItem().toString());
        });
        */

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
                                                 /*JCheckBox keywordCheckBox, JTextField keywordField,
                                                 /*JCheckBox semesterCheckBox, */JComboBox<String> semesterCombo,
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

        //if course code is selected and not an integer
        if (courseCodeCheckBox.isSelected() && !courseCodeField.getText().matches("\\d+")) {
            errorMessage.append("Course Code must be an integer.\n");
        }

        //if (keywordCheckBox.isSelected() && keywordField.getText().trim().isEmpty()) {
        //    errorMessage.append("Course Keyword cannot be empty.\n");
        //}

        if (/*semesterCheckBox.isSelected() && */(semesterCombo.getSelectedItem() == null || yearCombo.getSelectedItem() == null)) {
            errorMessage.append("You must select a semester and year.\n");
        }

        return errorMessage.toString().trim();
    }

    private static void showCourseSelectionView(JPanel mainPanel, JFrame frame) {
        JPanel courseSelectionPanel = new JPanel();
        courseSelectionPanel.setName("CourseSelcectionPanel");
        courseSelectionPanel.setLayout(new BoxLayout(courseSelectionPanel, BoxLayout.Y_AXIS));

        // Header label aligned to the left
        JLabel selectionLabel = new JLabel("Select a course to add:");
        selectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Course options as radio buttons (only one can be selected)
        ButtonGroup courseGroup = new ButtonGroup(); // Ensures single selection

        // Update displayed courses
        ArrayList<JRadioButton> courseButtons = new ArrayList<>();
        if (core.getSearchResults().isEmpty()) {
            JRadioButton courseButton = new JRadioButton("No courses meet search requirements.");
            courseButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            courseGroup.add(courseButton);
        } else {
            for (Course course : core.getSearchResults()) {
                //if course in schedule, make the button text red
                if (core.getSchedule().contains(course)) {
                    JRadioButton courseButton = new JRadioButton("<html><font color='green'>" + course.toString() + "</font></html>");
                    courseButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    courseGroup.add(courseButton);
                    courseButtons.add(courseButton);
                }
                else if (core.getConflictingCoursesInSearchResults().contains(course))
                {
                    //if course has conflict, make the button text red
                    JRadioButton courseButton = new JRadioButton("<html><font color='red'>" + course.toString() + "</font></html>");
                    courseButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    courseGroup.add(courseButton);
                    courseButtons.add(courseButton);
                }
                else {
                    JRadioButton courseButton = new JRadioButton(course.toString());
                    courseButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    courseGroup.add(courseButton);
                    courseButtons.add(courseButton);
                }
            }
        }

        // Add Selected Course button aligned to the left
        JButton addCourseButton = new JButton("Add Selected Course");
        addCourseButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Back, Undo, Course Info, and Review buttons
        JButton backButton = new JButton("Back");
        JButton undoButton = new JButton("Undo");
        JButton courseInfoButton = new JButton("Course Info");
        JButton reviewButton = new JButton("Review");

        // Align buttons in a horizontal layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align buttons to the left
        buttonPanel.add(backButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(courseInfoButton);
        buttonPanel.add(reviewButton);

        // Add components to the panel
        courseSelectionPanel.add(selectionLabel); // Header
        courseSelectionPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        // Create a scrollable panel for the course buttons
        JPanel courseButtonPanel = new JPanel();
        courseButtonPanel.setLayout(new BoxLayout(courseButtonPanel, BoxLayout.Y_AXIS));
        for (JRadioButton courseButton : courseButtons) {
            courseButtonPanel.add(courseButton);
        }
        JScrollPane scrollPane = new JScrollPane(courseButtonPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200)); // Set fixed size for the scroll pane

        courseSelectionPanel.add(scrollPane);
        courseSelectionPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        courseSelectionPanel.add(addCourseButton);
        courseSelectionPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        courseSelectionPanel.add(buttonPanel);

        // AddCourse button action
        addCourseButton.addActionListener(e -> {
            for (JRadioButton courseButton : courseButtons) {
                if (courseButton.isSelected()) {
                    core.addCourse(courseButtons.indexOf(courseButton));
                    courseButtons.remove(courseButton);
                    //refresh page
                    showCourseSelectionView(mainPanel, frame);
                    break;
                }
            }
        });

        // Back button action
        backButton.addActionListener(e -> showAddCourseView(mainPanel, frame));

        // Undo button action
        undoButton.addActionListener(e -> {
            System.out.println("Undo button clicked.");
            core.undoAdd();
            //refresh
            showCourseSelectionView(mainPanel, frame);
        });

        // Course Info button action
        courseInfoButton.addActionListener(e -> {
            for (JRadioButton courseButton : courseButtons) {
                if (courseButton.isSelected()) {
                    showCourseInfoView(mainPanel, frame, courseButton.getText());
                    break;
                }
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

    //TYLER VERSION: COMMENTED OUT FOR INTEGRATION
    /*
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
    */

    private static void showCourseInfoView(JPanel mainPanel, JFrame frame, String courseName) {
        JPanel courseInfoPanel = new JPanel();
        courseInfoPanel.setName("CourseInfoPanel");
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
        reviewPanel.setName("ReviewPanel");
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));

        //create semester panel
        reviewPanel.add(createSemesterPanel(), BorderLayout.NORTH);

        // Warnings panel at the top
        JTextArea warningsTextArea = new JTextArea("Warnings will appear here.");
        warningsTextArea.setEditable(false);
        warningsTextArea.setBorder(BorderFactory.createTitledBorder("Warnings"));
        //warningsTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        //display conflicting courses
        StringBuilder warningText = new StringBuilder();
        if (core.getConflictingCourses().isEmpty()) {
            warningText.append("No conflicts found.");
        } else {
            warningText.append("Conflicting courses found: \n");
            for (Course course : core.getConflictingCourses()) {
                warningText.append(course).append("\n");
            }
        }
        warningsTextArea.setText(warningText.toString());

        //POSSIBLY NEED TO ADD STUFF HERE

        // Schedule display area
        //JTextArea scheduleTextArea = new JTextArea("Scheduled classes will appear here.");
        //scheduleTextArea.setEditable(false);
        //scheduleTextArea.setBorder(BorderFactory.createTitledBorder("Schedule"));
        //scheduleTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel scheduleLabel = new JLabel("Scheduled Classes: ");
        scheduleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Update displayed courses in buttons
        ButtonGroup courseGroup = new ButtonGroup(); // Ensures single selection
        ArrayList<JRadioButton> courseButtons = new ArrayList<>();
        if (core.getSchedule().isEmpty()) {
            JRadioButton courseButton = new JRadioButton("No courses added yet.");
            courseButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            courseGroup.add(courseButton);
        } else {
            for (Course course : core.getSchedule()) {
                //if course has conflict, make the button text red
                if (core.getConflictingCourses().contains(course)) {
                    JRadioButton courseButton = new JRadioButton("<html><font color='red'>" + course.toString() + "</font></html>");
                    courseButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    courseGroup.add(courseButton);
                    courseButtons.add(courseButton);
                } else {
                    JRadioButton courseButton = new JRadioButton(course.toString());
                    courseButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    courseGroup.add(courseButton);
                    courseButtons.add(courseButton);
                    //reviewPanel.add(courseButton);
                }
            }
        }

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align buttons to the left

        JButton removeCourseButton = new JButton("Remove Course");
        JButton undoButton = new JButton("Undo");
        JButton courseInfoButton = new JButton("Course Info");
        JButton removeAllButton = new JButton("Remove All");

        JComboBox<String> selectSavedSchedules = new JComboBox<>();
        addScheduleItems(selectSavedSchedules);
        JButton loadSavedScheduleButton = new JButton("<- Load Schedule");
        JButton newScheduleButton = new JButton("New Schedule");
        JButton deleteSavedScheduleButton = new JButton("Delete Current Schedule");




        // Add buttons to the panel
        buttonsPanel.add(removeCourseButton);
        buttonsPanel.add(undoButton);
        buttonsPanel.add(courseInfoButton);
        buttonsPanel.add(removeAllButton);

        buttonsPanel.add(selectSavedSchedules);
        buttonsPanel.add(loadSavedScheduleButton);
        buttonsPanel.add(newScheduleButton);
        buttonsPanel.add(deleteSavedScheduleButton);

        // Button click actions
        removeCourseButton.addActionListener(e -> {
            System.out.println("Remove Course button clicked.");
            for (JRadioButton courseButton : courseButtons) {
                if (courseButton.isSelected()) {
                    core.removeCourse(courseButtons.indexOf(courseButton));
                    courseButtons.remove(courseButton);
                    //refresh page
                    showReviewView(mainPanel, frame);
                    break;
                }
                //else {
                //    JOptionPane.showMessageDialog(frame, "Please select a course to remove.");
                //}
            }
        });

        undoButton.addActionListener(e -> {
            System.out.println("Undo button clicked.");
            core.undoRemove();
            //refresh
            showReviewView(mainPanel, frame);
        });

        courseInfoButton.addActionListener(e -> {
            System.out.println("Course Info button clicked.");
            //this should do the same as the courseInfoButton elsewhere
            for (JRadioButton courseButton : courseButtons) {
                if (courseButton.isSelected()) {
                    showCourseInfoView(mainPanel, frame, courseButton.getText());//NEED TO CHANGE THIS TO COURSE OBJECT OR SOMETHING
                    break;
                }
                //else {
                //    JOptionPane.showMessageDialog(frame, "Please select a course to view its info.");
                //}
            }
        });

        removeAllButton.addActionListener(e -> {
            System.out.println("Remove All button clicked.");
            core.removeAllCourses();
            showReviewView(mainPanel, frame);
        });

        newScheduleButton.addActionListener(e -> {
            System.out.println("New Schedule button clicked.");
            if(core.checkConflicts()){
                JOptionPane.showMessageDialog(frame, "You must resolve all conflicts before creating or loading a new schedule.");
            }
            else{
                core.newSchedule();
                showReviewView(mainPanel, frame);
            }
        });

        loadSavedScheduleButton.addActionListener(e -> {
            System.out.println("Load Saved Schedule button clicked.");
            if(core.checkConflicts()){
                JOptionPane.showMessageDialog(frame, "You must resolve all conflicts before creating or loading a new schedule.");
            }
            else{
                for(Schedule schedule : core.getSavedSchedules()){
                    if(selectSavedSchedules.getSelectedItem().toString().equals("Schedule " + schedule.getScheduleID())){
                        core.loadSchedule(schedule);
                        break;
                    }
                }
                showReviewView(mainPanel, frame);
            }
        });

        deleteSavedScheduleButton.addActionListener(e -> {
            System.out.println("Delete Saved Schedule button clicked.");
            int result = core.deleteSchedule();
            if(result == 1){
                JOptionPane.showMessageDialog(frame, "You must have at least one schedule");
            }
            else{
                showReviewView(mainPanel, frame);
            }
        });

        // Add components to the review panel
        reviewPanel.add(warningsTextArea, BorderLayout.CENTER); // Warnings section
        reviewPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        reviewPanel.add(scheduleLabel);//, BorderLayout.WEST); // Schedule section

        //add course buttons in scrollable window
        JPanel courseButtonPanel = new JPanel();
        courseButtonPanel.setLayout(new BoxLayout(courseButtonPanel, BoxLayout.Y_AXIS));
        for (JRadioButton courseButton : courseButtons) {
            courseButtonPanel.add(courseButton);
        }
        JScrollPane scrollPane = new JScrollPane(courseButtonPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200)); // Set fixed size for the scroll pane
        reviewPanel.add(scrollPane);

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
    //TYLER VERSION: COMMENTED OUT FOR INTEGRATION
    /*
    private static void showReviewView(JPanel mainPanel, JFrame frame) {
        //Tried a few different things to get the vertical spacing between courses to fix itself but it won't work lol

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
    */

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

    private static void saveSchedulesIntoFile(){
        core.saveSchedulesIntoFile();
    }

    private static void addScheduleItems(JComboBox<String> loadSavedSchedules){
        loadSavedSchedules.addItem("Schedule " + core.getCurrentSchedule().getScheduleID());
        for(Schedule schedule : core.getSavedSchedules()){
            if(schedule.equals(core.getCurrentSchedule())){
                continue;
            }
            loadSavedSchedules.addItem("Schedule " + schedule.getScheduleID());
        }
    }

    //display semesters
    public static JPanel createSemesterPanel()
    {
        JPanel semesterWrapper = new JPanel();
        //semesterWrapper.setLayout(new BoxLayout(semesterWrapper, BoxLayout.Y_AXIS));
        //semesterWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel semesterLabel = new JLabel("Semester");
        semesterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        //JCheckBox semesterCheckBox = new JCheckBox("Semester");
        /*
        JComboBox<String> semesterCombo = new JComboBox<>(new String[]{"Fall", "Spring"});
        JComboBox<Integer> yearCombo = new JComboBox<>();
        for (int year = 2023; year <= 2025; year++) {
            yearCombo.addItem(year);
        }
        */
        JPanel semesterPanel = new JPanel(new FlowLayout());
        semesterPanel.add(semesterCombo);
        semesterPanel.add(yearCombo);

        core.updateSemester(yearCombo.getSelectedItem().toString() + "_" + semesterCombo.getSelectedItem().toString());

        semesterWrapper.add(semesterLabel);
        //semesterWrapper.add(semesterCheckBox);
        semesterWrapper.add(semesterPanel);

        // add action listeners to semesterCombo and yearCombo
        // action listener for yearCombo
        semesterCombo.addActionListener(e -> {
            //update semester
            core.updateSemester(yearCombo.getSelectedItem().toString() + "_" + semesterCombo.getSelectedItem().toString());

            /*
            //if the current panel is the addCoursePanel, update the filters
            if (semesterWrapper.getParent() != null && semesterWrapper.getParent().getParent() instanceof JPanel) {
                JPanel parentPanel = (JPanel) semesterWrapper.getParent().getParent();
                if (parentPanel.getComponent(1) instanceof JPanel) {
                     core.addFilter(new FilterSemester(yearCombo.getSelectedItem().toString(), semesterCombo.getSelectedItem().toString()));
                 }
            }
            */


            // Refresh the current panel based on what panel is currently showing
            if (semesterWrapper.getParent() != null) {
                Component parent = semesterWrapper.getParent().getParent();
                if (parent instanceof JPanel) {
                    JPanel parentPanel = (JPanel) parent;
                    if (parentPanel.getComponent(1) instanceof JPanel) {
                        JPanel currentPanel = (JPanel) parentPanel.getComponent(1);
                        if (currentPanel.getName().equals("CourseInfoPanel")) {
                            showCourseInfoView(parentPanel, (JFrame) SwingUtilities.getWindowAncestor(parentPanel), "Course Name");
                        } else if (currentPanel.getName().equals("AddCoursePanel")) {
                            showAddCourseView(parentPanel, (JFrame) SwingUtilities.getWindowAncestor(parentPanel));
                        } else if (currentPanel.getName().equals("ReviewPanel")) {
                            showReviewView(parentPanel, (JFrame) SwingUtilities.getWindowAncestor(parentPanel));
                        } else if (currentPanel.getName().equals("HomePanel")) {
                            showHomeView(parentPanel, (JFrame) SwingUtilities.getWindowAncestor(parentPanel));
                        }
                    }
                }
            }
        });

        // action listener for semesterCombo
        yearCombo.addActionListener(e -> {
            //update semester
            core.updateSemester(yearCombo.getSelectedItem().toString() + "_" + semesterCombo.getSelectedItem().toString());

            /*
            //if the current panel is the addCoursePanel, update the filters
            if (semesterWrapper.getParent() != null && semesterWrapper.getParent().getParent() instanceof JPanel) {
                JPanel parentPanel = (JPanel) semesterWrapper.getParent().getParent();
                if (parentPanel.getComponent(1) instanceof JPanel) {
                    core.addFilter(new FilterSemester(yearCombo.getSelectedItem().toString(), semesterCombo.getSelectedItem().toString()));
                }
            }
            */

            // Refresh the current panel based on what panel is currently showing
            if (semesterWrapper.getParent() != null) {
                Component parent = semesterWrapper.getParent().getParent();
                if (parent instanceof JPanel) {
                    JPanel parentPanel = (JPanel) parent;
                    if (parentPanel.getComponent(1) instanceof JPanel) {
                        JPanel currentPanel = (JPanel) parentPanel.getComponent(1);
                        if (currentPanel.getName().equals("CourseInfoPanel")) {
                            showCourseInfoView(parentPanel, (JFrame) SwingUtilities.getWindowAncestor(parentPanel), "Course Name");
                        } else if (currentPanel.getName().equals("AddCoursePanel")) {
                            showAddCourseView(parentPanel, (JFrame) SwingUtilities.getWindowAncestor(parentPanel));
                        } else if (currentPanel.getName().equals("ReviewPanel")) {
                            showReviewView(parentPanel, (JFrame) SwingUtilities.getWindowAncestor(parentPanel));
                        } else if (currentPanel.getName().equals("HomePanel")) {
                            showHomeView(parentPanel, (JFrame) SwingUtilities.getWindowAncestor(parentPanel));
                        }
                    }
                }
            }
        });

        

        return semesterWrapper;
    }
}