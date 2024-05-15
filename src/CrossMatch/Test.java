package CrossMatch;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import blood.bank.system.Home;

public class Test extends javax.swing.JFrame {

    private JPanel spacerPanel;
    //test form
    public Test() {
        initComponents();
        applyTableStyle(jTable1);
        Font font = new Font("Arial", Font.PLAIN, 30);
        Font headerFont = new Font("Arial", Font.BOLD, 30);
        txtSearch.setFont(font); 
        txtSearch.setForeground(Color.BLACK); 
        cmdSearch.setFont(font); 
        cmdMatch.setFont(font); 
        cmdDelete.setFont(font); 
        cmdFilter.setFont(font); 
        cmdSearch.setBackground(Color.BLACK); 
        cmdMatch.setBackground(Color.BLACK); 
        cmdDelete.setBackground(Color.BLACK); 
        cmdFilter.setBackground(Color.BLACK); 
        cmdSearch.setForeground(Color.WHITE); 
        cmdMatch.setForeground(Color.WHITE); 
        cmdDelete.setForeground(Color.WHITE); 
        cmdFilter.setForeground(Color.WHITE); 

        jTable1.getTableHeader().setFont(headerFont); 
  
        // Adjusting layout constraints to move the table columns down
        jScrollPane1.setLocation(10, 200); // Adjust the Y coordinate as needed

        // Setting the background color of the table
        jTable1.setBackground(Color.WHITE);

        // Adjusting size and font for the search bar
txtSearch.setFont(new Font("Arial", Font.PLAIN, 28)); 
txtSearch.setPreferredSize(new Dimension(400, 35)); 

// Adjusting size and font for the buttons
cmdSearch.setFont(new Font("Arial", Font.PLAIN, 28)); 
cmdSearch.setPreferredSize(new Dimension(120, 60)); 
cmdMatch.setFont(new Font("Arial", Font.PLAIN, 28)); 
cmdMatch.setPreferredSize(new Dimension(120, 60)); 
cmdDelete.setFont(new Font("Arial", Font.PLAIN, 28)); 
cmdDelete.setPreferredSize(new Dimension(120, 60)); 
cmdFilter.setFont(new Font("Arial", Font.PLAIN, 28)); 
cmdFilter.setPreferredSize(new Dimension(120, 60)); 

  // Create spacer panel and set preferred size to add space at the top
spacerPanel = new JPanel();
spacerPanel.setPreferredSize(new Dimension(800, 100)); // Adjust height as needed
spacerPanel.setBackground(Color.WHITE);
spacerPanel.setLayout(new FlowLayout()); // Set layout for spacer panel
add(spacerPanel, BorderLayout.NORTH);

// Adding image
        try {
//            // Load image
//            String imagePath = "C:\\Users\\AR Computers\\Desktop\\Blood-Bank-Management-System\\src\\images\\img2.jpeg";
//            File imageFile = new File(imagePath);
            //if (imageFile.exists()) {
                ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/img2.jpeg"));
                Image i1=imageIcon.getImage().getScaledInstance(850,450,Image.SCALE_SMOOTH);
                ImageIcon i2=new ImageIcon(i1);
                JLabel imageLabel = new JLabel(i2);
                imageLabel.setVerticalAlignment(JLabel.BOTTOM);
                imageLabel.setHorizontalAlignment(JLabel.CENTER);

                // Create spacer panel and set preferred size to add space at the top
                JPanel spacerPanel = new JPanel();
                spacerPanel.setPreferredSize(new Dimension(800, 50)); // Adjust height as needed
                spacerPanel.setBackground(Color.WHITE);
                spacerPanel.setLayout(new FlowLayout()); // Set layout for spacer panel

                // getContentPane().add(spacerPanel, BorderLayout.NORTH);
                // getContentPane().add(imageLabel, BorderLayout.NORTH);

                spacerPanel.add(imageLabel);
                add(spacerPanel);


//            } else {
//                System.err.println("Image file not found.");
//            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    // public static void main(String[] args) 
    // {
    //     new Test();
    // }

    private void searchDonors() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        int recipientId;
        try {
            recipientId = Integer.parseInt(txtSearch.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(Test.this, "Invalid recipient ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BloodBankSystem", "root", "abc_123")) {
            String recipientQuery = "SELECT BloodGroup, RhFactor FROM Recipient WHERE RecipientID = ?";
            try (PreparedStatement recipientStmt = conn.prepareStatement(recipientQuery)) {
                recipientStmt.setInt(1, recipientId);
                try (ResultSet recipientRs = recipientStmt.executeQuery()) {
                    if (recipientRs.next()) {
                        String recipientBloodGroup = recipientRs.getString("BloodGroup");
                        String recipientRhFactor = recipientRs.getString("RhFactor");
                        String donorQuery = "SELECT d.DonorID, bi.Expiration, d.Age, d.BloodGroup, d.RhFactor FROM Donor d " +
                                "JOIN BloodInventory bi ON d.DonorID = bi.DonorID " +
                                "WHERE d.BloodGroup = ? AND d.RhFactor = ? ORDER BY bi.Expiration ASC";
                        try (PreparedStatement donorStmt = conn.prepareStatement(donorQuery)) {
                            donorStmt.setString(1, recipientBloodGroup);
                            donorStmt.setString(2, recipientRhFactor);
                            try (ResultSet donorRs = donorStmt.executeQuery()) {
                                while (donorRs.next())
                                 {
                                    model.addRow(new Object[]{donorRs.getInt("DonorID"), donorRs.getDate("Expiration"),
                                            donorRs.getInt("Age"), donorRs.getString("BloodGroup"), donorRs.getString("RhFactor")});
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(Test.this, "Recipient not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(Test.this, "Error fetching data from database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void matchDonorRecipient() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(Test.this, "Please select a row.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int recipientId = Integer.parseInt(txtSearch.getText());

        int donorId = (int) jTable1.getValueAt(selectedRow, 0);
        String bloodGroup = (String) jTable1.getValueAt(selectedRow, 3);
        String rhFactor = (String) jTable1.getValueAt(selectedRow, 4);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BloodBankSystem", "root", "abc_123")) {
            String insertQuery = "INSERT INTO CrossMatch (DonorID, RecipientID, BloodGroup, RhFactor) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, donorId);
                insertStmt.setInt(2, recipientId);
                insertStmt.setString(3, bloodGroup);
                insertStmt.setString(4, rhFactor);
                insertStmt.executeUpdate();
            }

            String deleteRecipientQuery = "DELETE FROM Recipient WHERE RecipientID = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRecipientQuery)) {
                deleteStmt.setInt(1, recipientId);
                deleteStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(Test.this, "Cross match is established successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Test.this, "Error establishing cross match.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterDonorsByExpiration() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        int recipientId = Integer.parseInt(txtSearch.getText());

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BloodBankSystem", "root", "abc_123")) {
            String recipientQuery = "SELECT BloodGroup, RhFactor FROM Recipient WHERE RecipientID = ?";
            try (PreparedStatement recipientStmt = conn.prepareStatement(recipientQuery)) {
                recipientStmt.setInt(1, recipientId);
                try (ResultSet recipientRs = recipientStmt.executeQuery()) {
                    if (recipientRs.next()) {
                        String recipientBloodGroup = recipientRs.getString("BloodGroup");
                        String recipientRhFactor = recipientRs.getString("RhFactor");
                        String donorQuery = "SELECT d.DonorID, bi.Expiration, d.Age, d.BloodGroup, d.RhFactor FROM Donor d " +
                                "JOIN BloodInventory bi ON d.DonorID = bi.DonorID " +
                                "WHERE d.BloodGroup = ? AND d.RhFactor = ? ORDER BY bi.Expiration ASC";
                        try (PreparedStatement donorStmt = conn.prepareStatement(donorQuery)) {
                            donorStmt.setString(1, recipientBloodGroup);
                            donorStmt.setString(2, recipientRhFactor);
                            try (ResultSet donorRs = donorStmt.executeQuery()) {
                                while (donorRs.next()) {
                                    model.addRow(new Object[]{donorRs.getInt("DonorID"), donorRs.getDate("Expiration"),
                                            donorRs.getInt("Age"), donorRs.getString("BloodGroup"), donorRs.getString("RhFactor")});
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(Test.this, "Recipient not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Test.this, "Error fetching data from database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyTableStyle(JTable table) {
        JScrollPane scroll = (JScrollPane) table.getParent().getParent();
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().putClientProperty("JScrollBar.allowsAbsolutePositioning", Boolean.TRUE);
        scroll.getVerticalScrollBar().putClientProperty("JScrollBar.background", new ColorUIResource(Color.BLACK));
        scroll.getVerticalScrollBar().putClientProperty("JScrollBar.foreground", new ColorUIResource(Color.WHITE));
        scroll.getVerticalScrollBar().putClientProperty("JScrollBar.opaque", Boolean.TRUE);

        table.getTableHeader().setDefaultRenderer(getAlignmentCellRender(table.getTableHeader().getDefaultRenderer(), true));
        table.setDefaultRenderer(Object.class, getAlignmentCellRender(table.getDefaultRenderer(Object.class), false));
    }

    private TableCellRenderer getAlignmentCellRender(TableCellRenderer oldRender, boolean header) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component com = oldRender.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (com instanceof JLabel) {
                    JLabel label = (JLabel) com;
                    if (column == 0 || column == 4) {
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                    } else if (column == 2 || column == 3) {
                        label.setHorizontalAlignment(SwingConstants.TRAILING);
                    } else {
                        label.setHorizontalAlignment(SwingConstants.LEADING);
                    }
                    if (!header) {
                        if (column == 4) {
                            if (Double.parseDouble(value.toString()) > 0) {
                                com.setForeground(new Color(17, 182, 60));
                                label.setText("+" + value);
                            } else {
                                com.setForeground(new Color(202, 48, 48));
                            }
                        } else {
                            if (isSelected) {
                                com.setForeground(table.getSelectionForeground());
                            } else {
                                com.setForeground(table.getForeground());
                            }
                        }
                    }
                }
                return com;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        headerPanel = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        crazyPanel1 = new raven.crazypanel.CrazyPanel();
        crazyPanel2 = new raven.crazypanel.CrazyPanel();
        txtSearch = new javax.swing.JTextField();
        cmdSearch = new javax.swing.JButton();
        cmdMatch = new javax.swing.JButton();
        cmdDelete = new javax.swing.JButton();
        cmdFilter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton2.setText("Change Mode");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton2ActionPerformed(evt);
            }
        });
        //home button:
        cmdDelete.setText(" Home ");
cmdDelete.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent evt) {
        // Code to navigate to the home screen
        dispose(); // Close the current frame
        new blood.bank.system.Home().setVisible(true); // Open the Home screen
    }
});
crazyPanel2.add(cmdDelete);

        headerPanel.setBackground(Color.BLACK);

        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headerLabel.setText(" Cross Match");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        crazyPanel1.setFlatLafStyleComponent(new raven.crazypanel.FlatLafStyleComponent(
            "background:$Table.background;[light]border:0,0,0,0,shade(@background,5%),,20;[dark]border:0,0,0,0,tint(@background,5%),,20",
            null
        ));
        crazyPanel1.setMigLayoutConstraints(new raven.crazypanel.MigLayoutConstraints(
            "wrap,fill,insets 15",
            "[fill]",
            "[grow 0][fill]",
            null
        ));

        crazyPanel2.setFlatLafStyleComponent(new raven.crazypanel.FlatLafStyleComponent(
            "background:$Table.background",
            new String[]{
                "JTextField.placeholderText=Search;background:@background",
                "background:lighten(@background,8%);borderWidth:1",
                "background:lighten(@background,8%);borderWidth:1",
                "background:lighten(@background,8%);borderWidth:1"
            }
        ));
        crazyPanel2.setMigLayoutConstraints(new raven.crazypanel.MigLayoutConstraints(
            "",
            "[grow][fill][][]", 
            "",
            new String[]{
                "width 200"
            }
        ));
        crazyPanel2.add(txtSearch);

        cmdSearch.setText(" Search ");
        cmdSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSearchActionPerformed(evt);
            }
        });
        crazyPanel2.add(cmdSearch);

        cmdMatch.setText(" Match ");
        cmdMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdMatchActionPerformed(evt);
            }
        });
        crazyPanel2.add(cmdMatch);

        cmdDelete.setText(" Home ");
        crazyPanel2.add(cmdDelete);

        cmdFilter.setText(" Filter ");
        cmdFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdFilterActionPerformed(evt);
            }
        });
        crazyPanel2.add(cmdFilter);

        crazyPanel1.add(crazyPanel2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Donor ID", "    Blood Expiration Date   ", "  Donor's Age ", "   Blood Group   ", "  Rh Factor  "
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.sql.Date.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(crazyPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1001, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(crazyPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void cmdSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSearchActionPerformed
        searchDonors();
    }

    private void cmdMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdMatchActionPerformed
        matchDonorRecipient();
    }

    private void cmdFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFilterActionPerformed
        filterDonorsByExpiration();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Change Mode button action
    }

    public static void main(String args[]) {
          new Test();
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Test().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdDelete;
    private javax.swing.JButton cmdFilter;
    private javax.swing.JButton cmdMatch;
    private javax.swing.JButton cmdSearch;
    private raven.crazypanel.CrazyPanel crazyPanel1;
    private raven.crazypanel.CrazyPanel crazyPanel2;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}