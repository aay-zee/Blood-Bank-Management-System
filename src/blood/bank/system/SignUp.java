package blood.bank.system;

import javax.swing.*;

import CacheManager.Connect;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class SignUp extends JFrame implements ActionListener {

    JTextField textFID, textName, textEmail, textContact, textAddress;
    JPasswordField textPass;
    JButton next, showPassword;
    ImageIcon showIcon, hideIcon;
    boolean passwordVisible = false;

    SignUp() {
        super("SignUp Page");

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/slogo.png"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(25, 10, 100, 100);
        add(image);

        JLabel label1 = new JLabel("User SignUp");
        label1.setBounds(300, 20, 600, 60);
        label1.setFont(new Font("Raleway", Font.BOLD, 38));
        add(label1);

        JLabel label2 = new JLabel("Enter Details");
        label2.setFont(new Font("Raleway", Font.BOLD, 22));
        label2.setBounds(345, 70, 600, 30);
        add(label2);

        JLabel labelFID = new JLabel("User ID: ");
        labelFID.setFont(new Font("Raleway", Font.BOLD, 20));
        labelFID.setBounds(100, 190, 150, 30);
        add(labelFID);

        textFID = new JTextField();
        textFID.setFont(new Font("Raleway", Font.BOLD, 14));
        textFID.setBounds(300, 190, 400, 30);
        add(textFID);

        JLabel labelName = new JLabel("Name (Username): ");
        labelName.setFont(new Font("Raleway", Font.BOLD, 20));
        labelName.setBounds(100, 240, 250, 30);
        add(labelName);

        textName = new JTextField();
        textName.setFont(new Font("Raleway", Font.BOLD, 14));
        textName.setBounds(300, 240, 400, 30);
        add(textName);

        JLabel email = new JLabel("Email: ");
        email.setFont(new Font("Raleway", Font.BOLD, 20));
        email.setBounds(100, 290, 150, 30);
        add(email);

        textEmail = new JTextField();
        textEmail.setFont(new Font("Raleway", Font.BOLD, 14));
        textEmail.setBounds(300, 290, 400, 30);
        add(textEmail);

        JLabel pass = new JLabel("Password: ");
        pass.setFont(new Font("Raleway", Font.BOLD, 20));
        pass.setBounds(100, 340, 150, 30);
        add(pass);

        textPass = new JPasswordField(20);
        textPass.setFont(new Font("Raleway", Font.BOLD, 14));
        textPass.setBounds(300, 340, 400, 30);
        textPass.setEchoChar('*');
        add(textPass);

        ImageIcon Icon1 = new ImageIcon(ClassLoader.getSystemResource("icon/show.png"));
        Image img1 = Icon1.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        showIcon = new ImageIcon(img1);

        ImageIcon Icon2 = new ImageIcon(ClassLoader.getSystemResource("icon/hide.png"));
        Image img2 = Icon2.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        hideIcon = new ImageIcon(img2);

        showPassword = new JButton(showIcon);
        showPassword.setBounds(700, 340, 30, 30);
        showPassword.addActionListener(this);
        // showPassword.setBorderPainted(false);
        // showPassword.setContentAreaFilled(false);
        // showPassword.setFocusPainted(false);
        showPassword.setBackground(Color.WHITE);
        add(showPassword);

        // showPassword = new JCheckBox("Show Password");
        // showPassword.setFont(new Font("Raleway", Font.BOLD, 14));
        // showPassword.setBounds(620, 340, 150, 30);
        // showPassword.addActionListener(this);
        // add(showPassword);

        JLabel contact = new JLabel("Contact: ");
        contact.setFont(new Font("Raleway", Font.BOLD, 20));
        contact.setBounds(100, 390, 150, 30);
        add(contact);

        textContact = new JTextField();
        textContact.setFont(new Font("Raleway", Font.BOLD, 14));
        textContact.setBounds(300, 390, 400, 30);
        add(textContact);

        JLabel address = new JLabel("Address: ");
        address.setFont(new Font("Raleway", Font.BOLD, 20));
        address.setBounds(100, 440, 150, 30);
        add(address);

        textAddress = new JTextField();
        textAddress.setFont(new Font("Raleway", Font.BOLD, 14));
        textAddress.setBounds(300, 440, 400, 30);
        add(textAddress);

        next = new JButton("Next");
        next.setFont(new Font("Raleway", Font.BOLD, 14));
        next.setBackground(Color.BLACK);
        next.setForeground(Color.WHITE);
        next.setBounds(620, 500, 80, 30);
        next.addActionListener(this);
        add(next);

        setLayout(null);
        // getContentPane().setBackground(new Color(246, 205, 205));
        getContentPane().setBackground(new Color(255, 255, 255));
        setSize(850, 700);
        setLocation(360, 40);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String facilityID = textFID.getText();
        String username = textName.getText();
        String email = textEmail.getText();
        String password = Arrays.toString(textPass.getPassword());
        String contact = textContact.getText();
        String address = textAddress.getText();
        try {
            if (e.getSource() == showPassword) {
                if (passwordVisible) {
                    textPass.setEchoChar('*');
                    passwordVisible = false;
                    showPassword.setIcon(showIcon);
                } else {
                    textPass.setEchoChar((char) 0);
                    passwordVisible = true;
                    showPassword.setIcon(hideIcon);
                }
                // if(showPassword.isSelected()){
                // textPass.setEchoChar((char)0);
                // }
                // else{
                // textPass.setEchoChar('*');
                // }
            } else if (e.getSource() == next) {
                if (textFID.getText().isEmpty() || textName.getText().isEmpty() || textEmail.getText().isEmpty()
                        || textPass.getPassword().length == 0 || textContact.getText().isEmpty()
                        || textAddress.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in all the Details.");
                } else {
                    Connect connect2 = new Connect();
                    String query = "INSERT INTO Admin VALUES('" + facilityID + "','" + username + "','" + email + "','"
                            + password + "','" + contact + "','" + address + "')";
                    connect2.getStatement().executeUpdate(query);
                    setVisible(false);
                    new Login();
                }
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SignUp();
    }
}
