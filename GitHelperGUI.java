import git.tools.client.GitSubprocessClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;


public class GitHelperGUI extends JFrame implements ActionListener {
    private JButton btnSubmit;
    private JTextField txtRepoPath;

    public GitHelperGUI() {
        super("Git Helper");

        // Set up the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Set up the text field and button
        txtRepoPath = new JTextField();
        btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(this);

        // Add components to the content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(new JLabel("Enter the path of project:"));
        panel.add(txtRepoPath);
        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.add(btnSubmit, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        String repoPath = txtRepoPath.getText();

        File gitIgnore = new File(repoPath,".gitIgnore");
        File readMe = new File(repoPath, "README.md");

        GitSubprocessClient gitSubprocessClient = new GitSubprocessClient(repoPath);
        gitSubprocessClient.gitInit();

        try{
            gitIgnore.createNewFile();
            PrintWriter writeGitIgnore = new PrintWriter(gitIgnore);
            writeGitIgnore.println("bin/\n*.class\n.project\n*.iml\n.settings/\n.classpath\n.DS_Store\n.idea/\nout\n.metadata/");
            writeGitIgnore.flush();
        }
        catch(Exception ex){
            System.out.println("Could not create gitIgnore file");
        }

        try{
            readMe.createNewFile();
            PrintWriter writeReadMe = new PrintWriter(readMe);
            writeReadMe.println("## " + "repo name here");
            writeReadMe.flush();
        }
        catch(Exception ex){
            System.out.println("Could not create README file");
        }

        gitSubprocessClient.gitAddAll();
        gitSubprocessClient.gitCommit("initial commit");

        JOptionPane.showMessageDialog(null, "Git repository created successfully!");
    }

    public static void main(String[] args) {
        // Create and display the GUI
        GitHelperGUI gui = new GitHelperGUI();
        gui.setVisible(true);
    }
}
