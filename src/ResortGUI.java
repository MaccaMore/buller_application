import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;



public class ResortGUI extends JFrame implements ActionListener {
    // Resources
    File package_file = new File("resources/packages.dat");
    ImageIcon icon = new ImageIcon("resources/buller.png");


    // Constants
    private static Border failedBorder = BorderFactory.createLineBorder(Color.RED);
    private static Border successBorder = BorderFactory.createLineBorder(Color.white,0);


    // Lists for objects
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Accommodation> accommodations = new ArrayList<>();
    private ArrayList<TravelPackage> packages = new ArrayList<>();

    // Models to display Lists and Tables

    private String[] accommColumns = {"A.ID", "Type", "Price (Per night)", "Address", "City", "Postcode","Available"};
    private DefaultTableModel accommTableModel = new DefaultTableModel(accommColumns, 0);

    private String[] customerColumns = {"C.ID", "Name", "Phone Number", "Age", "Skill Level"};

    private DefaultTableModel customerTableModel = new DefaultTableModel(customerColumns, 0);
    private String[] packageColumns = {"P.ID","Customer", "Start Date", "Duration", "Address", "Season Pass", "Lift Pass", "Lessons", "Total Price"};

    private DefaultTableModel packageTableModel = new DefaultTableModel(packageColumns,0);
    private JTable accommTable;
    private DefaultListModel<Customer> customerListModel = new DefaultListModel<>();
    private DefaultListModel<Accommodation> accommListModel = new DefaultListModel<>();


    // JPanel components for visibility
    private Font boldFont;
    private Font italicFont;

    // Accommodation view components
    private JTextField accommPerNightField;
    private JCheckBox accommCheckboxHotel,accommCheckboxApartment,accommCheckboxLodge, accommCheckboxAvailable;
    private JButton accommButtonSearch;

    // Customer view components
    private JTextField customerNameField, customerPhoneField;
    private JTextArea customerHelpArea;
    private JSpinner customerAgeSpinner;
    private JComboBox customerSkillCombobox;
    private JTable customerTable;

    private JButton customerClearButton, customerAddButton, customerUpdateButton, customerDeleteButton;
    private int customerSelectedID = 0;

    // Package Create View Components
    private JButton clearCreatePackageButton, calculatePackageCostButton, createPackageButton;
    private JTextField packageTotalCostField;
    private JLabel packageStartDateLabel, packageDurationLabel, packageTotalCostLabel;
    private JSpinner packageDurationSpinner;
    private JList<Customer> packageCustomerList;
    private JList<Accommodation> packageCreateAccommList;
    private JTextArea packageHelpArea;
    private JSpinner daySpinner, monthSpinner, yearSpinner;

    // Package Modify View Components
    private JTable packageTable;
    private JButton packageAddPassButton, packageAddLessonButton, packageDeleteButton, packageClearButton, packageHelpButton;
    private JSpinner packageAddPassSpinner, packageAddLessonSpinner;
    private JCheckBox packageSeasonPassCheckbox;

    // Save view components
    private JButton saveButton, loadButton;
    private JTextArea saveTextArea;
    private JProgressBar saveProgress;




    public ResortGUI() {
        try {
            populateLists();
            createFonts();
            createGUI();
            setTabletNotEditable();
            readPackages();
        } catch (Exception x) {
            exceptionDialog("Error creating GUI" + x);
        }
    }

    /*      ~~~~~~~~~~~~~
            GUI FUNCTIONS
            ~~~~~~~~~~~~~
    */
    private void createGUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Accommodation", accommView());
        tabbedPane.addTab("Customers", customerView());
        tabbedPane.add("Packages", packageView());
        tabbedPane.add("Save",saveView());

        this.add(tabbedPane);
        this.setIconImage(icon.getImage());
    }

    // Accommodation view functions
    private JPanel accommView(){
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(accommOutputPanel(), BorderLayout.CENTER);
        panel.add(accommInputPanel(), BorderLayout.SOUTH);
        displayAccomm();

        return panel;
    }

    private JScrollPane accommOutputPanel(){
        accommTable = new JTable(accommTableModel);
        JScrollPane scrollPane = new JScrollPane(accommTable);

        // This allows user to sort by price etc
        TableRowSorter<TableModel> sortAccomm = new TableRowSorter<>(accommTableModel);
        accommTable.setRowSorter(sortAccomm);

        // ID and Address are short and long fields compared to others
        accommTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        accommTable.getColumnModel().getColumn(3).setPreferredWidth(200);


        scrollPane.setBorder(BorderFactory.createTitledBorder("Accommodations"));

        return scrollPane;
    }

    private JPanel accommInputPanel(){
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search Accommodations"));


        JLabel accommPerNightLabel = new JLabel("Max per night: $");
        accommPerNightLabel.setFont(italicFont);
        accommPerNightField = new JTextField(6);

        accommCheckboxHotel = new JCheckBox("Hotel");
        accommCheckboxApartment = new JCheckBox("Chalet");
        accommCheckboxLodge = new JCheckBox("Lodge");
        accommCheckboxHotel.setSelected(true);
        accommCheckboxApartment.setSelected(true);
        accommCheckboxLodge.setSelected(true);

        accommCheckboxAvailable = new JCheckBox("Only Available");
        accommButtonSearch = new JButton("Search");
        accommButtonSearch.addActionListener(this);

        panel.add(accommPerNightLabel);
        panel.add(accommPerNightField);
        panel.add(accommCheckboxHotel);
        panel.add(accommCheckboxApartment);
        panel.add(accommCheckboxLodge);
        panel.add(accommCheckboxAvailable);
        panel.add(accommButtonSearch);

        return panel;
    }

    // Customer view functions
    private JPanel customerView(){
        JPanel panel = new JPanel(new GridLayout(1,2));
        // I wanted to ensure that there are never any duplicate customers added by accident.
        // We will disable the add button if modifying
        panel.add(customerCreatePanel(), BorderLayout.EAST);
        panel.add(customerOutputPanel(), BorderLayout.CENTER);
        updateCustomerModels();
        customerButtonsActive(false);
        return panel;
    }

    private JPanel customerCreatePanel(){
        JPanel panel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        customerClearButton = new JButton("Cancel");
        customerClearButton.addActionListener(this);
        customerAddButton = new JButton("Create new");
        customerAddButton.addActionListener(this);

        buttonPanel.setBorder(BorderFactory.createTitledBorder("Add Customer"));
        buttonPanel.add(customerClearButton);
        buttonPanel.add(customerAddButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        customerHelpArea = new JTextArea(" ");
        customerHelpArea.setOpaque(false);
        customerHelpArea.setFont(italicFont);
        customerHelpArea.setForeground(Color.GRAY);
        customerHelpArea.setBorder(BorderFactory.createTitledBorder("Help"));

        panel.add(customerInputPanel(), BorderLayout.NORTH);
        panel.add(customerHelpArea, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;

    }

    private JPanel customerInputPanel(){
        JPanel panel = new JPanel(new GridLayout(7,3));
        panel.setBorder(BorderFactory.createTitledBorder("Input Details"));


        JLabel nameLabel = new JLabel("Name (John Doe):");
        nameLabel.setFont(italicFont);
        customerNameField = new JTextField(15);
        panel.add(nameLabel);
        panel.add(customerNameField);
        panel.add(new JSeparator());
        panel.add(new JSeparator());


        JLabel phoneLabel = new JLabel("Phone # (0412345678):");
        phoneLabel.setFont(italicFont);
        customerPhoneField =  new JTextField(5);
        customerPhoneField.setPreferredSize(new Dimension(150, 20));
        panel.add(phoneLabel);
        panel.add(customerPhoneField);
        panel.add(new JSeparator());
        panel.add(new JSeparator());

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(italicFont);
        customerAgeSpinner = new JSpinner(new SpinnerNumberModel(1,1,100,1));
        panel.add(ageLabel);
        panel.add(customerAgeSpinner);
        panel.add(new JSeparator());
        panel.add(new JSeparator());

        JLabel skillLabel = new JLabel("Skill Level:");
        skillLabel.setFont(italicFont);
        String[] skillLevels = {"Beginner", "Intermediate", "Expert"};
        customerSkillCombobox = new JComboBox(skillLevels);
        panel.add(skillLabel);
        panel.add(customerSkillCombobox);


        return panel;
    }

    private JPanel customerOutputPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        customerTable = new JTable(customerTableModel);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customers"));

        TableRowSorter<TableModel> sortCust = new TableRowSorter<>(customerTableModel);
        customerTable.setRowSorter(sortCust);

        customerTable.getColumnModel().getColumn(0).setPreferredWidth(1);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(5);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Modify Customers"));
        customerUpdateButton = new JButton("Update");
        customerDeleteButton = new JButton("Delete");
        customerUpdateButton.addActionListener(this);
        customerDeleteButton.addActionListener(this);
        buttonPanel.add(customerUpdateButton);
        buttonPanel.add(customerDeleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Hmm, we should only interact with the model of the table never the Table itself.
        // get the model, add a Listener for when its clicked, run function
        customerTable.getSelectionModel().addListSelectionListener(this::customerModifying);


        return panel;
    }

    // Package view functions
    private JTabbedPane packageView(){
        JTabbedPane packageTabs = new JTabbedPane();

        packageTabs.addTab("Create Package",packageCreateTab());
        packageTabs.addTab("Modify Packages",packageModifyTab());

        return packageTabs;
    }

    // Create Package View
    private JPanel packageCreateTab(){
        JPanel panel = new JPanel(new GridLayout(1,2));
        panel.add(packageCreateSelection());
        panel.add(packageCreateDetails());

        return panel;

    }

    private JPanel packageCreateSelection(){
        JPanel panel = new JPanel(new GridLayout(1,2));

        packageCustomerList = new JList<>(customerListModel);
        packageCustomerList.setBorder(BorderFactory.createTitledBorder("Select Customer"));
        JScrollPane customerScrollPane = new JScrollPane(packageCustomerList);
        panel.add(customerScrollPane);

        packageCreateAccommList = new JList<>(accommListModel);
        packageCreateAccommList.setBorder(BorderFactory.createTitledBorder("Select Accommodation"));
        JScrollPane accommScrollPane = new JScrollPane(packageCreateAccommList);
        panel.add(accommScrollPane);

        return panel;
    }

    private JPanel packageCreateDetails(){
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(packageInputPanel(), BorderLayout.NORTH);

        packageHelpTextSetup();
        panel.add(packageHelpArea, BorderLayout.CENTER);

        panel.add(packageCreateButtons(),BorderLayout.SOUTH);



        return panel;
    }

    private JPanel packageInputPanel(){
        JPanel inputPanel = new JPanel(new GridLayout(3,2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Details"));

        packageStartDateLabel = new JLabel("Start Date (Day/Month/Year):");
        packageStartDateLabel.setFont(italicFont);
        inputPanel.add(packageStartDateLabel);
        inputPanel.add(dateSelector());


        packageDurationLabel = new JLabel("Duration In Days:");
        packageDurationLabel.setFont(italicFont);
        packageDurationSpinner = new JSpinner(new SpinnerNumberModel(1,1,120,1));
        inputPanel.add(packageDurationLabel);
        inputPanel.add(packageDurationSpinner);


        packageTotalCostLabel = new JLabel("Base Package Cost:");
        packageTotalCostLabel.setFont(italicFont);
        packageTotalCostField = new JTextField("Press Calculate");
        packageTotalCostField.setEnabled(false);
        inputPanel.add(packageTotalCostLabel);
        inputPanel.add(packageTotalCostField);

        return inputPanel;
    }

    private void packageHelpTextSetup(){
        String packageHelpText = """

                1. Select a customer and accommodation from the lists.
                2. Input date and length of stay.
                3. Click create to make the package, or calculate to get cost.
                ‣ For more information about Accommodations or Customers, please view in their respective tabs""";

        packageHelpArea = new JTextArea(packageHelpText);
        packageHelpArea.setWrapStyleWord(true);
        packageHelpArea.setLineWrap(true);
        packageHelpArea.setOpaque(false);
        packageHelpArea.setFont(italicFont);
        packageHelpArea.setForeground(Color.GRAY);
        packageHelpArea.setBorder(BorderFactory.createTitledBorder("Help"));

    }
    private JPanel packageCreateButtons(){
        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createTitledBorder("Create Package"));

        clearCreatePackageButton = new JButton("Cancel");
        clearCreatePackageButton.addActionListener(this);
        panel.add(clearCreatePackageButton);

        calculatePackageCostButton = new JButton("Calculate");
        calculatePackageCostButton.addActionListener(this);
        panel.add(calculatePackageCostButton);

        createPackageButton = new JButton("Create");
        createPackageButton.addActionListener(this);
        panel.add(createPackageButton);



        return panel;
    }

    // Package Modify Tab
    private JPanel packageModifyTab(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(packageTablePanel(), BorderLayout.CENTER);
        panel.add(packageModifyButtons(),BorderLayout.SOUTH);
        return panel;
    }

    private JPanel packageTablePanel(){
        JPanel panel = new JPanel(new BorderLayout());
        packageTable = new JTable(packageTableModel);

        packageTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        packageTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        packageTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        packageTable.getColumnModel().getColumn(4).setPreferredWidth(240);
        packageTable.getColumnModel().getColumn(5).setPreferredWidth(90);


        JScrollPane scrollPane = new JScrollPane(packageTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Packages"));
        updatePackageTable();
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel packageModifyButtons(){
        JPanel panel = new JPanel(new GridLayout(1,3));

        panel.add(packageLiftPassButtons());
        panel.add(packageLessonButtons());
        panel.add(packageAdminButtons());

        return panel;
    }

    private JPanel packageLessonButtons(){
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Lessons"));

        packageAddLessonSpinner = new JSpinner(new SpinnerNumberModel(0,0,120,1));
        panel.add(packageAddLessonSpinner);

        packageAddLessonButton = new JButton("Set");
        packageAddLessonButton.addActionListener(this);
        panel.add(packageAddLessonButton);


        return panel;
    }

    private JPanel packageLiftPassButtons(){
        JPanel panel = new JPanel();
        packageAddPassSpinner = new JSpinner(new SpinnerNumberModel(0,0,99,1));
        panel.setBorder(BorderFactory.createTitledBorder("Lift Passes"));
        packageAddPassButton = new JButton("Set");
        packageAddPassButton.addActionListener(this);
        JLabel seasonpass = new JLabel("Season Pass? ");
        packageSeasonPassCheckbox = new JCheckBox();
        packageSeasonPassCheckbox.addItemListener(e -> {packageAddPassSpinner.setEnabled(!packageSeasonPassCheckbox.isSelected());
        });

        panel.add(seasonpass);
        panel.add(packageSeasonPassCheckbox);
        panel.add(packageAddPassSpinner);
        panel.add(packageAddPassButton);
        return panel;
    }

    private JPanel packageAdminButtons(){
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Modify"));

        packageClearButton = new JButton("Clear");
        packageClearButton.addActionListener(this);

        packageHelpButton = new JButton("Help");
        packageHelpButton.addActionListener(this);

        packageDeleteButton = new JButton("Delete");
        packageDeleteButton.addActionListener(this);

        panel.add(packageHelpButton);
        panel.add(packageClearButton);
        panel.add(packageDeleteButton);


        return panel;
    }

    // Save file tab
    private JPanel saveView(){
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(saveOutputPanel(), BorderLayout.CENTER);

        panel.add(saveInputPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane saveOutputPanel() {
        JScrollPane scrollPane = new JScrollPane();
        saveTextArea = new JTextArea("");
        scrollPane.setViewportView(saveTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));
        return scrollPane;
    }

    private JPanel saveInputPanel(){
        JPanel savePanel = new JPanel(new BorderLayout());
        savePanel.add(saveHelpArea(),BorderLayout.CENTER);

        JPanel saveButtonPanel = new JPanel(new BorderLayout());
        saveButtonPanel.setBorder((BorderFactory.createTitledBorder("Save")));

        JPanel saveButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButtons.add(saveButton);
        saveButtons.add(new JSeparator(SwingConstants.VERTICAL));
        loadButton = new JButton("Load");
        loadButton.addActionListener(this);
        saveButtons.add(loadButton);
        saveButtonPanel.add(saveButtons, BorderLayout.SOUTH);

        saveProgress = new JProgressBar(0,100);
        saveButtonPanel.add(saveProgress, BorderLayout.NORTH);

        savePanel.add(saveButtonPanel, BorderLayout.EAST);

        return savePanel;
    }

    private JTextArea saveHelpArea(){
        JTextArea saveHelpArea = new JTextArea("    Press save button to either create a new save file or overwrite.\n" +
                "    Press load button to load packages from file." +
                "\n    Package filename: " + package_file);
        saveHelpArea.setBorder(BorderFactory.createTitledBorder("Help"));
        saveHelpArea.setOpaque(false);
        saveHelpArea.setFont(italicFont);
        saveHelpArea.setForeground(Color.GRAY);
        return saveHelpArea;
    }

    /*    ~~~~~~~~~~~~~
          MODEL FUNCTIONS
          ~~~~~~~~~~~~~
  */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == accommButtonSearch) {
            displayAccomm();
        }
        if (e.getSource() == customerAddButton){
            addCustomer();
        }
        if (e.getSource() == customerClearButton){
            clearCustomerFields();
        }
        if (e.getSource() == customerUpdateButton){
            updateCustomer();
        }
        if (e.getSource() == customerDeleteButton){
            deleteCustomer();
        }
        if (e.getSource() == clearCreatePackageButton){
            clearCreatePackage();
        }
        if (e.getSource() == createPackageButton){
            addPackage();
        }
        if (e.getSource() ==  calculatePackageCostButton){
            calculatePackageCost();
        }
        if (e.getSource() == packageAddLessonButton){
            addLessons();
        }
        if (e.getSource() == packageAddPassButton){
            addLiftPass();
        }
        if (e.getSource() == packageClearButton){
            clearModifyPackage();
        }
        if (e.getSource() == packageDeleteButton){
            deletePackage();
        }
        if (e.getSource() == packageHelpButton){
            packageHelpPopup();
        }
        if (e.getSource() == saveButton){
            savePackages();
        }
        if (e.getSource() == loadButton){
            readPackages();
        }
    }


    private void displayAccomm(){
        try {
            // The only thing that can fail here is searchAccommodations
            ArrayList<Accommodation> sortedAccommodations = searchAccommodation();
            // No exception has been thrown so clear what is in the table and continue.
            updateAccommTable(sortedAccommodations);
            // Remove any failed border for input
            accommPerNightField.setBorder(successBorder);
        } catch (Exception x) {
            accommPerNightField.setBorder(failedBorder);
            exceptionDialog("‣ You must only enter digits and decimals. For example: 999");
        }
    }
    private ArrayList<Accommodation> searchAccommodation(){
        ArrayList<Accommodation> sortedAccommodations = new ArrayList<>();
        // Declare for visibility
        double inputPricePerNight;
        // If the field is blank set it to very high
        if (accommPerNightField.getText().equals("")) {
            inputPricePerNight = 99999.0;
        } else {
            // Otherwise, convert it from string to Double
            inputPricePerNight = Double.parseDouble(accommPerNightField.getText());
        }

        // Go through all accommodations stored and check they match selections
        for (Accommodation accomm : accommodations) {
            // Types of accommodation, (For loop and a list of types maybe better here?)
            if ((accomm.getType().equals("Hotel") && accommCheckboxHotel.isSelected()) ||
                    accomm.getType().equals("Lodge") && accommCheckboxLodge.isSelected() ||
                    accomm.getType().equals("Apartment") && accommCheckboxApartment.isSelected()) {
                // check if price is below max price
                if (accomm.getPricePerNight() <= inputPricePerNight) {
                    // Finally check if ensure that accom is available if checkbox selected
                    if (accommCheckboxAvailable.isSelected() && accomm.isAvailable()) {
                        sortedAccommodations.add(accomm);
                    } else if (!accommCheckboxAvailable.isSelected()) {
                        sortedAccommodations.add(accomm);
                    }

                }

            }
        }
        if (sortedAccommodations.isEmpty()){
            warningDialog("‣ No accommodations with your input criteria exist.");
        }
        return sortedAccommodations;
    }

    private void customerModifying(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                customerSelectedID = (int) customerTable.getValueAt(selectedRow,0);
                String name = (String) customerTable.getValueAt(selectedRow,1);
                String phone = (String) customerTable.getValueAt(selectedRow,2);
                int age = (int) customerTable.getValueAt(selectedRow,3);
                String skill = (String) customerTable.getValueAt(selectedRow,4);
                customerNameField.setText(name);
                customerPhoneField.setText(phone);
                customerAgeSpinner.setValue(age);
                customerSkillCombobox.setSelectedItem(skill);
                customerButtonsActive(true);
            }
        }
    }

    private void addCustomer() {
        if (customerInputValid()){
            try {
                Customer newCustomer = createCustomerFromInputs();
                customers.add(newCustomer);
                clearCustomerFields();
                updateCustomerModels();
            } catch (Exception e) {
                exceptionDialog("Error trying to create customer. No customer created.\n" + e);
            }
        }
    }

    private Customer createCustomerFromInputs(){
        String name = customerNameField.getText();
        String ph_number = customerPhoneField.getText();
        int age = (int) customerAgeSpinner.getValue();
        String skill_level = (String) customerSkillCombobox.getSelectedItem();
        return new Customer(name, ph_number, age, skill_level);
    }

    private void updateCustomer(){
        if (customerInputValid()) {
            customerNameField.setBorder(successBorder);
            customerPhoneField.setBorder(successBorder);
            customerAgeSpinner.setBorder(successBorder);
            if (confirmCustomerChanges()) {
                try {
                    // We want to only change the customer, not make a new one.
                    // Making a new one
                    for (Customer customer : customers) {
                        if (customerSelectedID == customer.getId()) {
                            customer.setName(customerNameField.getText());
                            customer.setPhNumber(customerPhoneField.getText());
                            customer.setAge((Integer) customerAgeSpinner.getValue());
                            customer.setSkillLevel((String) customerSkillCombobox.getSelectedItem());
                        }
                    }
                    // Update the customer if it exists in a travel package
                    // TravelPackage holds customer objects, so to ensure that the object is up-to-date with changes.
                    for (TravelPackage travelPackage : packages){
                        if (travelPackage.getCustomerID() == customerSelectedID){
                            travelPackage.setCustomer(getCustomerByID(customerSelectedID));
                        }
                    }
                } catch (Exception e) {
                    exceptionDialog("Error trying to modify customer\n" + e);
                }
                updatePackageTotalPrice();
                updatePackageTable();
                updateCustomerModels();
                clearCustomerFields();
            }
        }
    }

    private boolean confirmCustomerChanges(){
        Customer oldCustomer = getCustomerByID(customerSelectedID);
        String message = "Are these changes correct?\n" +
                oldCustomer.getName() + " → " + customerNameField.getText() + "\n" +
                oldCustomer.getPhNumber() + " → " + customerPhoneField.getText() + "\n" +
                oldCustomer.getAge() + " → " + customerAgeSpinner.getValue() + "\n"+
                oldCustomer.getSkillLevel() + " → " + customerSkillCombobox.getSelectedItem();
        return confirmationDialog(message);
    }

    private void deleteCustomer(){
        Customer oldCustomer = getCustomerByID(customerSelectedID);
        boolean hasPackage = false;
        for (TravelPackage travelPackage : packages) {
            if (travelPackage.getCustomerID() == customerSelectedID) {
                hasPackage = true;
                exceptionDialog("‣ Customer has a current package. Delete the package with ID: " + travelPackage.getPackageID() + " before deleting customer.");
            }
        }
            if (!hasPackage){
                String message = "Are you sure you want to delete:\n" +
                        oldCustomer.getName() + "\n" +
                        oldCustomer.getPhNumber() + "\n" +
                        oldCustomer.getAge() + "\n" +
                        oldCustomer.getSkillLevel();
                boolean confirmed = confirmationDialog(message);
                if (confirmed){
                    for (int i = 0; i < customers.size(); i++) {
                        Customer customer = customers.get(i);
                        if (customerSelectedID == customer.getId()){
                            customers.remove(i);
                            updateCustomerModels();
                            clearCustomerFields();
                            break;
                        }
                    }
                }
            }
        }




    // Reset input fields to make it clear the program did something.
    // Also benefits by ensuring 2 duplicate people cannot be made by accident.

    private void clearCustomerFields(){
        customerNameField.setBorder(successBorder);
        customerPhoneField.setBorder(successBorder);
        customerAgeSpinner.setBorder(successBorder);
        customerNameField.setText("");
        customerPhoneField.setText("");
        customerAgeSpinner.setValue(1);
        customerTable.clearSelection();
        customerSkillCombobox.setSelectedIndex(0);
        customerButtonsActive(false);
    }
    // Straight up do not allow a user to misinterpret what they are allowed to do.

    private void customerButtonsActive(boolean updating){
        if (updating){
            customerNameField.setBorder(successBorder);
            customerPhoneField.setBorder(successBorder);
            customerAddButton.setEnabled(false);
            customerDeleteButton.setEnabled(true);
            customerUpdateButton.setEnabled(true);
            customerHelpArea.setText("\nModifying customer. Change details and click update. \n" +
                    "Press cancel to create new customer.");
        }
        if (!updating){
            customerNameField.setBorder(successBorder);
            customerPhoneField.setBorder(successBorder);
            customerAddButton.setEnabled(true);
            customerDeleteButton.setEnabled(false);
            customerUpdateButton.setEnabled(false);
            customerHelpArea.setText("\nCreating customer. Enter details and click create new. \n" +
                    "Or select a customer from the list to modify.");


        }
    }
    private void clearCreatePackage(){
        packageCustomerList.setBackground(null);
        packageCustomerList.clearSelection();

        packageCreateAccommList.setBackground(null);
        packageCreateAccommList.clearSelection();

        daySpinner.setValue(1);
        monthSpinner.setValue(5);
        yearSpinner.setValue(2024);

        packageDurationSpinner.setValue(1);

        packageTotalCostField.setText("Press Calculate");



    }

    private void addPackage(){
        if (packageCreateInputValid()){
            Customer customer = customerListModel.getElementAt(packageCustomerList.getSelectedIndex());
            Accommodation accommodation = accommListModel.getElementAt(packageCreateAccommList.getSelectedIndex());
            String startDate = daySpinner.getValue() + "-" + monthSpinner.getValue() + "-" + yearSpinner.getValue();
            int duration = (int) packageDurationSpinner.getValue();

            TravelPackage newPackage = new TravelPackage(customer,startDate,duration,accommodation);
            packages.add(newPackage);
            refreshAccommodationAvailability();
            successDialog("New package created!\n\n" + newPackage +  "\nAdd lift passes and lessons in the modify view.");
            updatePackageTable();
            clearCreatePackage();

        }
    }


    // If you were to load a file with less packages, availability of the accommodation would remain.
    // This function fixes this.
    private void refreshAccommodationAvailability(){
        // Get all accommodation IDs that are stored in packages loaded
        ArrayList<Integer> packageAccommodationIDs = new ArrayList<>();
        for (TravelPackage travelPackage : packages) {
            packageAccommodationIDs.add(travelPackage.getAccomId());
        }

        // Go through accommodations and if the ID matches stored ID, set to false.
        // If not found in list, it is available.
        for (Accommodation accommodation : accommodations) {
            Integer accommodationID = accommodation.getId();

            if (packageAccommodationIDs.contains(accommodationID)) {
                accommodation.setAvailable(false);
            } else {
                accommodation.setAvailable(true);
            }
        }
        updateAccommJList();
        updateAccommTable(accommodations);
    }

    private void calculatePackageCost(){
        if (packageCreateInputValid()){
            // I think there must be a better way to do this.
            // We make a dummy package here and get total price from that
            Customer customer = customerListModel.getElementAt(packageCustomerList.getSelectedIndex());
            Accommodation accommodation = accommListModel.getElementAt(packageCreateAccommList.getSelectedIndex());
            String startDate = "null";
            int duration = (int) packageDurationSpinner.getValue();

            TravelPackage dummyPackage = new TravelPackage(customer,startDate,duration,accommodation, true);

            String totalCost = String.valueOf(dummyPackage.getTotalPrice());

            packageTotalCostField.setText("$ " +totalCost);

        }

    }

    // I am so sorry for this. What a mess of a method. Maybe should split up.
    private void addLiftPass(){
        // Check that the user has selected a package
        if (!packageIsSelected()) {
            return;
        }
        // Get the values from the table, they are not objects of TravelPackage in a JTable but are instead an array of strings.
        int packageID = getSelectedPackageID();
        int daysToAdd = (int) packageAddPassSpinner.getValue();
        TravelPackage selectedPackage = getPackageByID(packageID);

        // If season pass we simply set to 99 days which will automatically set to season pass.
        if (packageSeasonPassCheckbox.isSelected()) {
            selectedPackage.setLiftPassDays(TravelPackage.SEASON_PASS_DAYS);
            successDialog("Season pass set to package ID [" + packageID + "] for: $" + TravelPackage.SEASON_PASS_COST +
                    "\n New total cost of package: $" + selectedPackage.getTotalPrice());
                    clearModifyPackageInputs();
                    updatePackageTable();
                    return;
        }

        // Customer can never add more pass days than they are staying.
        if (selectedPackage.getStayDuration() < daysToAdd) {
            packageAddPassSpinner.getEditor().getComponent(0).setBackground(new Color(255, 180, 180));
            exceptionDialog("‣ Cannot set more lift passes than stay duration, max: " + selectedPackage.getStayDuration());
            return;
            }


        if (selectedPackage.getLessons() > daysToAdd) {
            exceptionDialog("‣ Cannot set less lift passes than lessons booked, min: " + selectedPackage.getLessons());
            packageAddLessonSpinner.getEditor().getComponent(0).setBackground(new Color(255, 180, 180));
            packageAddPassSpinner.getEditor().getComponent(0).setBackground(new Color(255, 180, 180));
            return;
        }

        // If season pass hasn't been selected, TravelPackage will automatically calculate if it is cheaper for the season pass.
        selectedPackage.setLiftPassDays(daysToAdd);
        if (selectedPackage.getSeasonPass()) {
            successDialog("Season pass is calculated to be cheaper for the package ID [" + packageID +
                    "]\nSeason pass cost: $" + TravelPackage.SEASON_PASS_COST +
                    "  |  Lift pass cost for " + daysToAdd + " days is: $" + daysToAdd * TravelPackage.DAILY_PASS_COST +
                    ".\nSeason pass automatically added for $" + TravelPackage.SEASON_PASS_COST +
                    "\nNew total cost of package: $ " + selectedPackage.getTotalPrice());

        } else {
            successDialog(daysToAdd + " day lift passes added to package ID [" + packageID +
                    "] for: $" + daysToAdd * TravelPackage.DAILY_PASS_COST +
                    "\nNew total cost of package: " + selectedPackage.getTotalPrice());

        }
        clearModifyPackageInputs();
        updatePackageTable();
    }




    private void addLessons(){
        if (!packageIsSelected()) {
            return;
        }
        int packageID = getSelectedPackageID();
        int lessonsToAdd = (int) packageAddLessonSpinner.getValue();
        for (TravelPackage travelPackage : packages){
            if (packageID == travelPackage.getPackageID()){
                if (travelPackage.getLiftPassDays() < lessonsToAdd && !travelPackage.getSeasonPass()) {
                    exceptionDialog("‣ cannot add more lessons than lift passes. Max: " + travelPackage.getLiftPassDays() +
                            "\n Please remove lessons or add more lift passes");
                    packageAddLessonSpinner.getEditor().getComponent(0).setBackground(new Color(255, 180, 180));
                    packageAddPassSpinner.getEditor().getComponent(0).setBackground(new Color(255, 180, 180));

                } else if (travelPackage.getSeasonPass() && travelPackage.getStayDuration() < lessonsToAdd) {
                    exceptionDialog("‣ cannot add more lessons than days stayed. Max: " + travelPackage.getStayDuration() +
                            "\n Please remove lessons.");
                    packageAddLessonSpinner.getEditor().getComponent(0).setBackground(new Color(255, 180, 180));
                } else {
                    travelPackage.setLessons(lessonsToAdd);
                    successDialog(lessonsToAdd + " lessons added to package ID [" + packageID + "]" +
                            "\n" + travelPackage.getCustomerName() + " skill level: " + travelPackage.getCustomerSkill() +
                            "\nCost per lesson: $" + travelPackage.getLessonCost() +
                            "\nTotal cost added: $" + travelPackage.getTotalLessonCost(lessonsToAdd) +
                            "\nNew total cost of package: $" + travelPackage.getTotalPrice());
                    clearModifyPackageInputs();
                    updatePackageTable();
                }
                break;
            }
        }
    }

    private void deletePackage(){
        if (packageIsSelected()) {
            int packageID = getSelectedPackageID();
            for (TravelPackage travelPackage : packages) {
                if (packageID == travelPackage.getPackageID()) {
                    if (confirmationDialog("Are you sure you want to delete package?:\n" + travelPackage)) {
                        packages.remove(travelPackage);
                        refreshAccommodationAvailability();
                        updateAccommJList();
                        updateAccommTable(accommodations);
                        updatePackageTable();
                        clearModifyPackage();
                    }
                    break;
                }
            }
        }
    }

    private void packageHelpPopup(){
        String helpMessage = "1. Select a package from the list.\n" +
                "2. Enter amount of lift passes for package, or select Season pass checkbox. Press set.\n" +
                "3. Enter amount of lessons for package. Automatically calculates cost from customer skill level\n" +
                "‣ Press clear to clear any selections and input fields\n" +
                "‣ Or select a package, and click delete to remove the package.";
        JOptionPane.showMessageDialog(this, helpMessage, "Package Help" , JOptionPane.INFORMATION_MESSAGE, icon);
    }

    
    private boolean packageCreateInputValid(){
        boolean inputValid = true;
        String errorString = "";

        Customer customer = packageCustomerList.getSelectedValue();
        if (customer == null){
            inputValid = false;
            errorString = errorString + "‣ Customer not selected please select from list\n";
            packageCustomerList.setBackground(new Color(255,180,180));
        }

        Accommodation accommodation = packageCreateAccommList.getSelectedValue();
        if (accommodation == null){
            inputValid = false;
            errorString = errorString +"‣ Accommodation not selected please select from list\n";
            packageCreateAccommList.setBackground(new Color(255,200,200));
        }

        if (!inputValid){
            exceptionDialog(errorString);
        }

        return inputValid;
    }

    private boolean customerInputValid(){
        // We want to find all errors at once rather than 1 at a time.
        boolean inputValid = true;
        String errorString = "The following issues were found:\n";
        String name = "";
        String ph_number = "";
        try {
            name = customerNameField.getText();
            ph_number = customerPhoneField.getText();
            ph_number = ph_number.replaceAll("\\s", "");
        } catch (Exception x) {
            exceptionDialog(" "+x);
        }

        if (name.isEmpty()){
            inputValid = false;
            customerNameField.setBorder(failedBorder);
            errorString = errorString + "‣ Name field is empty\n";
        }

        if(!name.matches("[a-zA-Z\\s]*")){
            inputValid = false;
            customerNameField.setBorder(failedBorder);
            errorString = errorString + "‣ Name must only contain letters\n";
        }

        if (ph_number.length() < 10 ){
            inputValid = false;
            customerPhoneField.setBorder(failedBorder);
            errorString = errorString + "‣ Phone number too short, must be at least 8 digits\n";

        } else if (ph_number.length() > 11) {
            inputValid = false;
            customerPhoneField.setBorder(failedBorder);
            errorString = errorString + "‣ Phone number too long, must be maximum 11 digits\n";
        }
        if (!ph_number.matches("[0-9]+")){
            inputValid = false;
            customerPhoneField.setBorder(failedBorder);
            errorString = errorString + "‣ Phone number must only contain digits\n";
        }

        if (!inputValid){
            exceptionDialog(errorString);
        }
        return inputValid;
    }

    private Customer getCustomerByID(int id) {
        for (Customer customer : customers) {
            if (customer.getId() == id) {
                return customer;
            }
        }
        return null;
    }

    private TravelPackage getPackageByID(int packageID) {
        for (TravelPackage travelPackage : packages) {
            // Find the package in the list
            if (packageID == travelPackage.getPackageID()) {
                return travelPackage;
            }
        }
        return null;
    }

    private int getSelectedPackageID(){
        return (int) packageTable.getValueAt(packageTable.getSelectedRow(), 0);
    }

    private boolean packageIsSelected() {
        if (packageTable.getSelectedRow() == -1) {
            exceptionDialog("‣ No package selected, please select one from the list.");
            packageTable.setBackground(new Color(255, 180, 180));
            return false;
        }
        return true;
    }

    private void updatePackageTotalPrice(){
        for (TravelPackage travelPackage : packages){
            travelPackage.setTotalPrice();
        }
    }

    // Any time we make a change to customers list, we call this to update the models used for Table and JList
    // When Tables and JLists use a model, anytime the model updates, they do too.
    // This saves repainting them every time.

    private void updateCustomerModels(){
        updateCustomerTable();
        updateCustomerJList();
        updateAccommJList();
    }

    private void updateAccommTable(ArrayList<Accommodation> accommodationsUpdate){
        // Clear the table
        accommTableModel.setRowCount(0);
        for (Accommodation accomm: accommodationsUpdate){
            // Documentation says that JTable expects a vector, or 1D/2D array.
            Object[] data = {accomm.getId(), accomm.getType(), "$"+accomm.getPricePerNight(), accomm.getAddress(),
                    accomm.getCity(), accomm.getPostCode(), accomm.isAvailable()};
            // For 1D array we can use addRow, which saves us needing a 2D array "Object[][]" with visibility outside.
            accommTableModel.addRow(data);
        }
    }
    private void updateCustomerTable(){
        customerTableModel.setRowCount(0);
        for (Customer customer : customers) {
            Object[] data = {customer.getId(), customer.getName(), customer.getPhNumber(), customer.getAge(), customer.getSkillLevel()};
            customerTableModel.addRow(data);
        }
    }

    private void updatePackageTable(){
        int savedRow = packageTable.getSelectedRow();
        packageTableModel.setRowCount(0);
        for (TravelPackage travelPackage : packages){
            Object[] data = {travelPackage.getPackageID(), travelPackage.getCustomerName(), travelPackage.getStartDate(),
                    travelPackage.getStayDuration() + " days",travelPackage.getAccommAddress(),travelPackage.getSeasonPass(),
                    travelPackage.getLiftPassDays() + " days", travelPackage.getLessons(), "$" + travelPackage.getTotalPrice()};
            packageTableModel.addRow(data);
        }
        packageTable.setBackground(null);
        // When a user adds lessons and lift passes we want to keep the package selected
        if (savedRow != -1 && savedRow < packageTable.getRowCount()) {
            packageTable.setRowSelectionInterval(savedRow, savedRow);
        }
    }

    private void updateCustomerJList(){
        customerListModel.clear();
        for (Customer customer : customers){
            customerListModel.addElement(customer);
        }
    }

    private void updateAccommJList(){
        accommListModel.clear();
        for (Accommodation accommodation : accommodations){
            if (accommodation.isAvailable()){
                accommListModel.addElement(accommodation);
            }
        }
    }

    private void clearModifyPackage(){
        clearModifyPackageInputs();
        packageTable.clearSelection();
    }

    private void clearModifyPackageInputs(){
        packageAddPassSpinner.getEditor().getComponent(0).setBackground(Color.white);
        packageAddPassSpinner.setValue(0);

        packageAddLessonSpinner.getEditor().getComponent(0).setBackground(Color.white);
        packageAddLessonSpinner.setValue(0);

        packageSeasonPassCheckbox.setSelected(false);

        packageTable.setBackground(Color.WHITE);
    }


    // - Function to save packages to packages.dat -

    private void savePackages() {
        simulateProgressBar();
        try {
            // We don't need to append because we load on program start
            FileOutputStream fos = new FileOutputStream(package_file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            saveTextArea.setText("");
            String savedItemText = "SAVED \n";

            ArrayList<TravelPackage> backupPackages = packages;
            int counter = 0;
            for (TravelPackage travelPackage : packages){
                oos.writeObject(travelPackage);
                savedItemText = savedItemText + travelPackage + "\n  ________________  \n";
                counter++;

            }
            if (counter == 0){
                if (confirmationDialog("Overwriting save file with empty packages. Are you sure?\n‣ Press no to undo operation.")) {
                    warningDialog("‣ Save operation successful, save file is now empty.");
                    } else {
                    for (TravelPackage travelPackage : backupPackages) {
                    oos.writeObject(travelPackage);
                    savedItemText = savedItemText + travelPackage + "\n  ________________  \n";
                    counter++;
                    }

                }
            } else {
            successDialog("["+ counter + "] packages saved successfully!");
            }
            saveTextArea.setText(savedItemText);
            oos.close();
        } catch (Exception e) {
            exceptionDialog(String.valueOf(e));
        }

    }

    // - Function to load data from packages.dat -
    // This function is automatically run at start to ensure that no data is overwritten.

    private void readPackages() {
        simulateProgressBar();
        try {
            FileInputStream fis = new FileInputStream(package_file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<TravelPackage> loaded_packages = new ArrayList<>();
            int counter = 0;
            while (true) {
                try {
                    TravelPackage travelPackage = (TravelPackage) ois.readObject();
                    loaded_packages.add(travelPackage);
                    for (Accommodation accommodation : accommodations){
                        if (travelPackage.getAccomId() == accommodation.getId()){
                            accommodation.setAvailable(false);
                        }
                    }
                    counter++;

                } catch (EOFException e) {
                    packages = loaded_packages;
                    if (counter == 0){
                        warningDialog("‣ Load operation successful but there were no Travel Packages found.");
                    } else {
                        successDialog("[" + counter + "] packages loaded successfully!");
                    }
                    updateApplicationState();
                    break;
                }
            }
        } catch (Exception e) {
            exceptionDialog(e + "\nNo previous save data found, either corrupt or not created. \n" +
                    "‣ Please create new save data under save tab.");
        }
    }

    // After loading, we update all lists of items, and tables relating.
    private void updateApplicationState(){
        saveTextAreaLoaded();
        updateNextIDs();
        updateCustomersFromPackage();
        updatePackageTable();
        updateCustomerModels();
        refreshAccommodationAvailability();
        updateAccommTable(accommodations);
        System.out.println(customers);
        System.out.println(packages);
    }

    // With save load methods, customer objects would not line up with IDs in the application.
    // With this method if a customer or package is deleted, that ID can never be used again I guess?
    // It just occurred to me that it might be easier to save the accommodation and customer lists and use that to save and load.
    private void updateNextIDs(){
        int maxLoadedCustomerID = Customer.STARTING_ID;
        int maxLoadedPackageID = TravelPackage.STARTING_ID;
        for (TravelPackage travelPackage : packages){
            if (travelPackage.getPackageID() > maxLoadedPackageID){
                maxLoadedPackageID = travelPackage.getPackageID();
            }

            if (travelPackage.getCustomerID() > maxLoadedCustomerID){
                maxLoadedCustomerID = travelPackage.getCustomerID();
            }
        }
        for (Customer customer:customers){
            if (customer.getId() > maxLoadedCustomerID){
                maxLoadedCustomerID = customer.getId();
            }
        }
        TravelPackage.setNextID(maxLoadedPackageID);
        Customer.setNextID(maxLoadedCustomerID);

    }

    private void updateCustomersFromPackage(){
        // We update customers list. Remove duplicate and replace with one from TravelPackage. If does not exist add.
        // Iterate through travel packages to update the customers list
        for (TravelPackage travelPackage : packages) {
            int customerID = travelPackage.getCustomerID();
            Customer customerFromPackage = travelPackage.getCustomerObject();
            boolean found = false;
            for (Customer customer : customers) {
                if (customer.getId() == customerID) {
                    customers.remove(customer);
                    customers.add(customerFromPackage);
                    found = true;
                    break;
                }
            }
            if (!found) {
                customers.add(customerFromPackage);
            }
        }
    }
    private void saveTextAreaLoaded(){
        saveTextArea.setText("");
        String loadedItemText = "LOADED \n";
        for (TravelPackage travelPackage : packages){
            loadedItemText = loadedItemText + travelPackage + "\n ________________ \n";
        }
        saveTextArea.setText(loadedItemText);
    }

    private void setTabletNotEditable(){
        // Default behaviour for tables is not what I wanted and caused problems with some of my functions.
        // Make it so user cannot edit the fields directly.
        customerTable.setDefaultEditor(Object.class, null);
        packageTable.setDefaultEditor(Object.class, null);
        accommTable.setDefaultEditor(Object.class, null);
        // Make it so user cannot reorder the table headers.
        customerTable.getTableHeader().setReorderingAllowed(false);
        packageTable.getTableHeader().setReorderingAllowed(false);
        accommTable.getTableHeader().setReorderingAllowed(false);
        // Make it so user can only select a single thing from a table.
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accommTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void simulateProgressBar() {
        // This is my first time playing wiht threads. This literally does nothing but show a fake progress bar.
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(5);
                    saveProgress.setValue(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void exceptionDialog(String error) {
        System.err.println(error);
        JOptionPane.showMessageDialog(this, "⛔ Error:\n" + error, "Error!",JOptionPane.ERROR_MESSAGE, icon);

    }

    private void warningDialog(String warning) {
        System.err.println(warning);
        JOptionPane.showMessageDialog(this,"\uD83D\uDEB8 Warning:\n" +  warning, "Warning!", JOptionPane.WARNING_MESSAGE, icon);
    }

    private boolean confirmationDialog(String message){
        int option = JOptionPane.showConfirmDialog(this, "\uD83D\uDEB8 Confirm:\n"+message, "Confirm changes", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
        return option == JOptionPane.YES_OPTION;
    }

    private void successDialog(String message){
        System.out.println(message);
        JOptionPane.showMessageDialog(this, "✅ Success: \n" + message,"Success!",JOptionPane.INFORMATION_MESSAGE, icon);
    }

    private JPanel dateSelector(){
        JPanel panel = new JPanel();
        // Should maybe make these spinners not use magic numbers
        daySpinner = new JSpinner(new SpinnerNumberModel(1,1,31,1));
        monthSpinner = new JSpinner(new SpinnerNumberModel(5,5,11,1));
        yearSpinner =  new JSpinner(new SpinnerNumberModel(2024,2024,2030,1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(yearSpinner, "#");
        yearSpinner.setEditor(editor);
        panel.add(daySpinner);
        panel.add(monthSpinner);
        panel.add(yearSpinner);
        return panel;
    }

    private void createFonts(){
        // I want to ensure that when I change Italic or bold texts they are the same across the app
        // I think I could also create my own Font class for this?
        // Default fonts may be different for other Kernels
        JLabel emptyLabel = new JLabel("");
        Font defaultFont = emptyLabel.getFont();
        italicFont = defaultFont.deriveFont(Font.ITALIC);
        boldFont = defaultFont.deriveFont(Font.BOLD);

    }
    private void populateLists(){
        accommodations.add(new Accommodation("Apartment", "3 Wooly Mammoth Way", "Mt Buller", "3723", 170.0, true));
        accommodations.add(new Accommodation("Hotel", "123 Snowy Peak Rd", "Mt Buller", "3723", 200.0, true));
        accommodations.add(new Accommodation("Lodge", "4 Frosty Lane", "Mt Buller", "3723", 180.0, true));
        accommodations.add(new Accommodation("Apartment", "7 Icicle Street", "Mt Buller", "3723", 220.0, true));
        accommodations.add(new Accommodation("Lodge", "98 Blizzard Way", "Mt Buller", "3723", 150.0, true));
        accommodations.add(new Accommodation("Lodge", "3 Frostbite Ave", "Mt Buller", "3723", 250.0, true));
        accommodations.add(new Accommodation("Apartment", "11 Snowflake Pd", "Mt Buller", "3723", 280.0, true));
        accommodations.add(new Accommodation("Lodge", "222 Alpine View Dr", "Mt Buller", "3723", 190.0, true));
        accommodations.add(new Accommodation("Hotel", "3 Snowdrift Rd", "Mt Buller", "3723", 170.0, true));
        accommodations.add(new Accommodation("Lodge", "44 Iceberg Rd", "Mt Buller", "3723", 210.0, true));
        accommodations.add(new Accommodation("Hotel", "69 Chill Ave", "Mt Buller", "3723", 160.0, true));


        customers.add(new Customer("Ashley McKinnon", "0422223112", 26, "Expert"));
        customers.add(new Customer("David Beckham", "0422123123", 26, "Beginner"));
        customers.add(new Customer("John Davies", "0444444411", 26, "Intermediate"));

    }
}
