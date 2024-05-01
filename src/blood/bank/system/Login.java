package blood.bank.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

public class Login extends JFrame implements ActionListener {

    JTextField textUser;
    JPasswordField textPass;

    JButton button1,button2,button3;

    Login() {
        super("Blood Bank Management System");


        JLabel label1=new JLabel("Blood Bank Management System");
        label1.setBounds(420,20,400,40);
        label1.setFont(new Font("Raleway",Font.BOLD,25));
        add(label1);

        JLabel label2=new JLabel("Username: ");
        label2.setFont(new Font("Raleway",Font.BOLD,15));
        //label2.setForeground(Color.WHITE);
        label2.setBounds(450,140,375,30);
        add(label2);

        textUser=new JTextField(15);
        textUser.setBounds(550,140,230,30);
        textUser.setFont(new Font("Arial",Font.BOLD,14));
        add(textUser);

        JLabel label3=new JLabel("Password: ");
        label3.setFont(new Font("Raleway",Font.BOLD,15));
        //label2.setForeground(Color.WHITE);
        label3.setBounds(450,190,375,30);
        add(label3);

        textPass=new JPasswordField(15);
        textPass.setBounds(550,190,230,30);
        textPass.setFont(new Font("Arial",Font.BOLD,14));
        textPass.setEchoChar('*');
        add(textPass);

        button1=new JButton("Sign In");
        button1.setFont(new Font("Arial",Font.BOLD,14));
        button1.setForeground(Color.WHITE);
        button1.setBackground(Color.BLACK);
        button1.setBounds(550,240,100,30);
        button1.addActionListener(this);
        add(button1);

        button2=new JButton("Clear");
        button2.setFont(new Font("Arial",Font.BOLD,14));
        button2.setForeground(Color.WHITE);
        button2.setBackground(Color.BLACK);
        button2.setBounds(680,240,100,30);
        button2.addActionListener(this);
        add(button2);


        button3=new JButton("Sign Up");
        button3.setFont(new Font("Arial",Font.BOLD,14));
        button3.setForeground(Color.WHITE);
        button3.setBackground(Color.BLACK);
        button3.setBounds(550,290,230,30);
        button3.addActionListener(this);
        add(button3);

//        JPanel panel = new JPanel();
//        panel.setLayout(new GridLayout(4, 2, 10, 10));
//        panel.add(label1);
//        panel.add(new JLabel()); // Empty label for spacing
//        panel.add(label2);
//        panel.add(textUser);
//        panel.add(label3);
//        panel.add(textPass);
//        panel.add(button1);
//        panel.add(button2);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        buttonPanel.add(button3);
//
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        mainPanel.add(panel, BorderLayout.CENTER);
//        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
//
//        add(mainPanel);



        ImageIcon i1=new ImageIcon(ClassLoader.getSystemResource("icon/LOGO.png"));
        Image i2=i1.getImage().getScaledInstance(850,480,Image.SCALE_DEFAULT);
        ImageIcon i3=new ImageIcon(i2);
        JLabel image=new JLabel(i3);
        image.setBounds(0,0,850,480);
        add(image);


        setLayout(null);
        setSize(850,480);
        setLocation(360,200);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            if(e.getSource()==button1) {
                if (textUser.getText().isEmpty() && textPass.getPassword().length == 0) {
                    JOptionPane.showMessageDialog(null, "Fill in all the Details");
                }
                else {
                    if (textUser.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Username Field cannot be Empty");
                    } else if (textPass.getPassword().length == 0) {
                        JOptionPane.showMessageDialog(null, "Password Field cannot be Empty");
                    } else {
                        if(isUser(textUser.getText(), Arrays.toString(textPass.getPassword()))) {
                            setVisible(false);
                            new Home();
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"User Does not Exist");
                        }
                    }
                }
            }
            else if(e.getSource()==button3){
                setVisible(false);
                new SignUp();
            }
            else{
                textUser.setText("");
                textPass.setText("");
            }
        }catch(Exception d){
            d.printStackTrace();
        }
    }

    public boolean isUser(String username,String password){
        try{
            Connect connect1=new Connect();
            String query = "SELECT * FROM SignUp WHERE Name = ? AND Password = ? ";
                try (PreparedStatement preparedStatement = connect1.connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        return resultSet.next(); // Returns true if user exists
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new Login();
    }

}
