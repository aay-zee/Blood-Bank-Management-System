package CrossMatch;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.ImageIcon;

public class Tester extends javax.swing.JFrame {

    /**
     * Creates new form Test
     */
     public Tester() {
        initComponents();
        applyTableStyle(jTable1);
        Font font = new Font("Arial", Font.PLAIN, 25);
        Font headerFont = new Font("Arial", Font.BOLD, 22);
        txtSearch.setFont(font); // Set font size
        txtSearch.setForeground(Color.BLACK); // Set text color
        cmdSearch.setFont(font); // Set button font size
        cmdMatch.setFont(font); // Set button font size
        cmdDelete.setFont(font); // Set button font size
        cmdFilter.setFont(font); // Set button font size
        cmdSearch.setBackground(Color.BLACK); // Set button background color
        cmdMatch.setBackground(Color.BLACK); // Set button background color
        cmdDelete.setBackground(Color.BLACK); // Set button background color
        cmdFilter.setBackground(Color.BLACK); // Set button background color
        cmdSearch.setForeground(Color.WHITE); // Set button text color
        cmdMatch.setForeground(Color.WHITE); // Set button text color
        cmdDelete.setForeground(Color.WHITE); // Set button text color
        cmdFilter.setForeground(Color.WHITE); // Set button text color
        jTable1.getTableHeader().setFont(headerFont); // Set font size for column headers

        //add image
        try {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("C:\\Users\\AR Computers\\Desktop\\bbms\\360_F_276718846_1mDkxI8gb6FrfuwAiPb6OuB4M7BbeuoV.jpg")); // Replace "/image.jpg" with the relative path to your image file
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setVerticalAlignment(JLabel.BOTTOM);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            getContentPane().add(imageLabel, "South");
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    private void searchDonors() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        // Clear existing rows
        model.setRowCount(0);

        // Fetch recipient ID entered by user
        int recipientId;
        try {
            recipientId = Integer.parseInt(txtSearch.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(Tester.this, "Invalid recipient ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch data from the donor table based on the recipient ID
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BloodBankSystem", "root", "abc_123")) {
            // Query to fetch recipient's blood group and Rh Antigen based on recipient ID
            String recipientQuery = "SELECT BloodGroup, RhFactor FROM Recipient WHERE RecipientID = ?";
            try (PreparedStatement recipientStmt = conn.prepareStatement(recipientQuery)) {
                recipientStmt.setInt(1, recipientId);
                try (ResultSet recipientRs = recipientStmt.executeQuery()) {
                    if (recipientRs.next()) {
                        String recipientBloodGroup = recipientRs.getString("BloodGroup");
                        String recipientRhFactor = recipientRs.getString("RhFactor");
                        // Query to fetch donor information and blood expiry date based on recipient's blood group
                        String donorQuery = "SELECT d.DonorID, bi.Expiration, d.Age, d.BloodGroup, d.RhFactor FROM Donor d " +
                                "JOIN BloodInventory bi ON d.DonorID = bi.DonorID " +
                                "WHERE d.BloodGroup = ? AND d.RhFactor = ? ORDER BY bi.Expiration ASC";
                        try (PreparedStatement donorStmt = conn.prepareStatement(donorQuery)) {
                            donorStmt.setString(1, recipientBloodGroup);
                            donorStmt.setString(2, recipientRhFactor);
                            try (ResultSet donorRs = donorStmt.executeQuery()) {
                                while (donorRs.next()) {
                                    // Add row to the table model
                                    model.addRow(new Object[]{donorRs.getInt("DonorID"), donorRs.getDate("Expiration"),
                                            donorRs.getInt("Age"), donorRs.getString("BloodGroup"), donorRs.getString("RhFactor")});
                                }
                            }
                        }
                    } else {
                        // Recipient not found
                        JOptionPane.showMessageDialog(Tester.this, "Recipient not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(Tester.this, "Error fetching data from database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void matchDonorRecipient() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(Tester.this, "Please select a row.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch the recipient ID entered by the user
        int recipientId = Integer.parseInt(txtSearch.getText());

        // Fetch data from the selected row
        int donorId = (int) jTable1.getValueAt(selectedRow, 0);
        String bloodGroup = (String) jTable1.getValueAt(selectedRow, 3);
        String rhFactor = (String) jTable1.getValueAt(selectedRow, 4);

        // Insert data into the CrossMatch table
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BloodBankSystem", "root", "abc_123")) {
            String insertQuery = "INSERT INTO CrossMatch (DonorID, RecipientID, BloodGroup, RhFactor) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, donorId);
                insertStmt.setInt(2, recipientId);
                insertStmt.setString(3, bloodGroup);
                insertStmt.setString(4, rhFactor);
                insertStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(Tester.this, "Cross match is established successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Tester.this, "Error establishing cross match.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterDonorsByExpiration() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Clear existing rows

        // Fetch recipient ID entered by user
        int recipientId = Integer.parseInt(txtSearch.getText());

        // Fetch data from the donor table based on the recipient ID
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BloodBankSystem", "root", "abc_123")) {
            // Query to fetch recipient's blood group and Rh Antigen based on recipient ID
            String recipientQuery = "SELECT BloodGroup, RhFactor FROM Recipient WHERE RecipientID = ?";
            try (PreparedStatement recipientStmt = conn.prepareStatement(recipientQuery)) {
                recipientStmt.setInt(1, recipientId);
                try (ResultSet recipientRs = recipientStmt.executeQuery()) {
                    if (recipientRs.next()) {
                        String recipientBloodGroup = recipientRs.getString("BloodGroup");
                        String recipientRhFactor = recipientRs.getString("RhFactor");
                        // Query to fetch donor information and blood expiry date based on recipient's blood group
                        String donorQuery = "SELECT d.DonorID, bi.Expiration, d.Age, d.BloodGroup, d.RhFactor FROM Donor d " +
                                "JOIN BloodInventory bi ON d.DonorID = bi.DonorID " +
                                "WHERE d.BloodGroup = ? AND d.RhFactor = ? ORDER BY bi.Expiration ASC";
                        try (PreparedStatement donorStmt = conn.prepareStatement(donorQuery)) {
                            donorStmt.setString(1, recipientBloodGroup);
                            donorStmt.setString(2, recipientRhFactor);
                            try (ResultSet donorRs = donorStmt.executeQuery()) {
                                while (donorRs.next()) {
                                    // Add row to the table model
                                    model.addRow(new Object[]{donorRs.getInt("DonorID"), donorRs.getDate("Expiration"),
                                            donorRs.getInt("Age"), donorRs.getString("BloodGroup"), donorRs.getString("RhFactor")});
                                }
                            }
                        }
                    } else {
                        // Recipient not found
                        JOptionPane.showMessageDialog(Tester.this, "Recipient not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Tester.this, "Error fetching data from database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyTableStyle(JTable table) {
        JScrollPane scroll = (JScrollPane) table.getParent().getParent();
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Table.background;"
                + "track:$Table.background;"
                + "trackArc:999");


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
                    if (header == false) {
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });


        headerPanel.setBackground(Color.BLACK);

        headerLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Set header label font
        headerLabel.setForeground(Color.WHITE); // Set header label text color
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
            "[grow][fill][][]", // Adjusted constraints to allocate more space to the search bar and less space to the buttons
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
                "Donor ID ", "    Blood Expiration Date   ", "  Donor's Age ", "   Blood Group   ", "  Rh Factor  "
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
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(crazyPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(50, 50, 50))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
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
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Change Look and Feel
    }//GEN-LAST:event_jButton2ActionPerformed

    private void cmdSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSearchActionPerformed
        searchDonors();
    }//GEN-LAST:event_cmdSearchActionPerformed

    private void cmdMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdMatchActionPerformed
        matchDonorRecipient();
    }//GEN-LAST:event_cmdMatchActionPerformed

    private void cmdFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFilterActionPerformed
        filterDonorsByExpiration();
    }//GEN-LAST:event_cmdFilterActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tester().setVisible(true);
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
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
