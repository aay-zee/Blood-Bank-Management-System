package blood.bank.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PreDonationHistory extends JFrame implements ActionListener {

    private JButton donors, recipients, matches, inventory;

    PreDonationHistory() {
        super("Pre Donation History");

        JLabel label1 = new JLabel("Blood Bank Management System");
        label1.setBounds(420, 20, 400, 40);
        label1.setFont(new Font("Raleway", Font.BOLD, 25));
        add(label1);

        donors = new JButton("View Donors");
        donors.setFont(new Font("Raleway", Font.BOLD, 14));
        donors.setBackground(Color.BLACK);
        donors.setForeground(Color.WHITE);
        donors.setBounds(515, 120, 200, 30);
        donors.addActionListener(this);
        add(donors);

        recipients = new JButton("View Recipients");
        recipients.setFont(new Font("Raleway", Font.BOLD, 14));
        recipients.setBackground(Color.BLACK);
        recipients.setForeground(Color.WHITE);
        recipients.setBounds(515, 170, 200, 30);
        recipients.addActionListener(this);
        add(recipients);

        matches = new JButton("View Donation History");
        matches.setFont(new Font("Raleway", Font.BOLD, 14));
        matches.setBackground(Color.BLACK);
        matches.setForeground(Color.WHITE);
        matches.setBounds(515, 220, 200, 30);
        matches.addActionListener(this);
        add(matches);

        inventory = new JButton("View Blood Inventory");
        inventory.setFont(new Font("Raleway", Font.BOLD, 14));
        inventory.setBackground(Color.BLACK);
        inventory.setForeground(Color.WHITE);
        inventory.setBounds(515, 270, 200, 30);
        inventory.addActionListener(this);
        add(inventory);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/LOGO.png"));
        Image i2 = i1.getImage().getScaledInstance(850, 480, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, 850, 480);
        add(image);

        setLayout(null);
        // getContentPane().setBackground(new Color(246, 205, 205));
        getContentPane().setBackground(new Color(255, 255, 255));
        setSize(850, 480);
        setLocation(360, 200);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == donors) {
                setVisible(false);
                new DonationHistory("donors");
            } else if (e.getSource() == recipients) {
                setVisible(false);
                new DonationHistory("recipients");
            } else if (e.getSource() == matches) {
                setVisible(false);
                new DonationHistory("matches");
            } else {
                setVisible(false);
                new DonationHistory("inventory");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PreDonationHistory();
    }
}
