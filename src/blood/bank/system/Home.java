package blood.bank.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame implements ActionListener {
    JRadioButton r1, r2, r3, r4, r5, r6, r7, r8;
    JButton next;

    public Home() {
        super("Home");

        JLabel label1 = new JLabel("Blood Bank Management System");
        label1.setBounds(420, 20, 400, 40);
        label1.setFont(new Font("Raleway", Font.BOLD, 25));
        add(label1);

        r1 = new JRadioButton("Donor Management");
        r1.setFocusPainted(false);
        r1.setBackground(new Color(255, 255, 255));
        r1.setFont(new Font("Raleway", Font.BOLD, 14));
        r1.setBounds(420, 100, 200, 30);
        add(r1);

        r2 = new JRadioButton("Recipient Management");
        r2.setFocusPainted(false);
        r2.setBackground(new Color(255, 255, 255));
        r2.setFont(new Font("Raleway", Font.BOLD, 14));
        r2.setBounds(420, 130, 200, 30);
        add(r2);

        // r3=new JRadioButton("Blood Inventory Management");
        // r3.setFocusPainted(false);
        // r3.setBackground(new Color(255,255,255));
        // r3.setFont(new Font("Raleway",Font.BOLD,14));
        // r3.setBounds(420,160,300,30);
        // add(r3);
        //
        // r4=new JRadioButton("Donation Management");
        // r4.setFocusPainted(false);
        // r4.setBackground(new Color(255,255,255));
        // r4.setFont(new Font("Raleway",Font.BOLD,14));
        // r4.setBounds(420,190,200,30);
        // add(r4);
        //
        // r5=new JRadioButton("Transfusion Management");
        // r5.setFocusPainted(false);
        // r5.setBackground(new Color(255,255,255));
        // r5.setFont(new Font("Raleway",Font.BOLD,14));
        // r5.setBounds(420,220,300,30);
        // add(r5);

        r3 = new JRadioButton("Cross-Examination Management");
        r3.setFocusPainted(false);
        r3.setBackground(new Color(255, 255, 255));
        r3.setFont(new Font("Raleway", Font.BOLD, 14));
        r3.setBounds(420, 160, 300, 30);
        add(r3);

        r4 = new JRadioButton("View Records");
        r4.setFocusPainted(false);
        r4.setBackground(new Color(255, 255, 255));
        r4.setFont(new Font("Raleway", Font.BOLD, 14));
        r4.setBounds(420, 190, 200, 30);
        add(r4);

        // r8=new JRadioButton("Reporting");
        // r8.setFocusPainted(false);
        // r8.setBackground(new Color(255,255,255));
        // r8.setFont(new Font("Raleway",Font.BOLD,14));
        // r8.setBounds(420,310,200,30);
        // add(r8);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(r1);
        buttonGroup.add(r2);
        buttonGroup.add(r3);
        buttonGroup.add(r4);
        // buttonGroup.add(r5);
        // buttonGroup.add(r6);
        // buttonGroup.add(r7);
        // buttonGroup.add(r8);

        next = new JButton("Next");
        next.setFont(new Font("Raleway", Font.BOLD, 14));
        next.setBackground(Color.BLACK);
        next.setForeground(Color.WHITE);
        next.setBounds(720, 350, 80, 30);
        next.addActionListener(this);
        add(next);

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
            boolean notSelected = true;
            for (JRadioButton button : new JRadioButton[] { r1, r2, r3, r4 }) {
                if (button.isSelected()) {
                    notSelected = false;
                    break;
                }
            }
            if (e.getSource() == next) {
                if (notSelected == true) {
                    JOptionPane.showMessageDialog(null, "Please Select an Option");
                } else {
                    if (r1.isSelected()) {
                        new donor_management();
                    } else if (r2.isSelected()) {
                        new recipient_management();
                    } else if (r3.isSelected()) {
                        new CrossMatch.Test().setVisible(true);
                    } else if (r4.isSelected()) {
                        new PreDonationHistory();
                    }
                    setVisible(false);
                }
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Home();
    }
}
