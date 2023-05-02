import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import git.tools.client.GitSubprocessClient;

public class GitHelperGUI {

    private JFrame frame;
    private JTextField txtRepoPath;
    private JLabel lblStatus;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GitHelperGUI window = new GitHelperGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public GitHelperGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblRepoPath = new JLabel("Enter the path of project:");
        lblRepoPath.setBounds(25, 30, 200, 20);
        frame.getContentPane().add(lblRepoPath);

        txtRepoPath = new JTextField();
        txtRepoPath.setBounds(25, 55, 250, 20);
        frame.getContentPane().add(txtRepoPath);
        txtRepoPath.setColumns(10);

        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.setBounds(290, 55, 100, 20);
        frame.getContentPane().add(btnBrowse);
        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    txtRepoPath.setText(file.getAbsolutePath());
                }
            }
        });

        JButton btnCreateRepo = new JButton("Create Repo");
        btnCreateRepo.setBounds(25, 90, 150, 25);
        frame.getContentPane().add(btnCreateRepo);
        btnCreateRepo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createRepo();
            }
        });

        lblStatus = new JLabel("");
        lblStatus.setBounds(25, 140, 350, 20);
        frame.getContentPane().add(lblStatus);
    }

    private void createRepo() {
        String repoPath = txtRepoPath.getText();
        File gitIgnore = new File(repoPath, ".gitIgnore");
        File readMe = new File(repoPath, "README.md");

        GitSubprocessClient gitSubprocessClient = new GitSubprocessClient(repoPath);
        gitSubprocessClient.gitInit();

        try {
            gitIgnore.createNewFile();
            PrintWriter writeGitIgnore = new PrintWriter(gitIgnore);
            writeGitIgnore.println(
                    "bin/\n*.class\n.project\n*.iml\n.settings/\n.classpath\n.DS_Store\n.idea/\nout\n.metadata/");
            writeGitIgnore.flush();
        } catch (Exception e) {
            lblStatus.setText("Could not create gitIgnore file");
        }

        try {
            readMe.createNewFile();
            PrintWriter writeReadMe = new PrintWriter(readMe);
            writeReadMe.println("## " + "repo name here");
            writeReadMe.flush();
        } catch (Exception e) {
            lblStatus.setText("Could not create README file");
        }

        gitSubprocessClient.gitAddAll();
        gitSubprocessClient.gitCommit("initial commit");

        lblStatus.setText("Repo created successfully");
    }
}