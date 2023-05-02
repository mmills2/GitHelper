import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import git.tools.client.GitSubprocessClient;

public class GitHelperGUI {

    private JFrame frame;
    private JTextField txtRepoPath;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtDescription;
    private JLabel lblStatus;
    private JCheckBox chkPublic;

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
        frame.setBounds(100, 100, 450, 400);
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

        JLabel lblUsername = new JLabel("GitHub username:");
        lblUsername.setBounds(25, 95, 200, 20);
        frame.getContentPane().add(lblUsername);

        JTextField txtUsername = new JTextField();
        txtUsername.setBounds(25, 120, 250, 20);
        frame.getContentPane().add(txtUsername);
        txtUsername.setColumns(10);

        JLabel lblPassword = new JLabel("GitHub password:");
        lblPassword.setBounds(25, 155, 200, 20);
        frame.getContentPane().add(lblPassword);

        JTextField txtPassword = new JTextField();
        txtPassword.setBounds(25, 180, 250, 20);
        frame.getContentPane().add(txtPassword);
        txtPassword.setColumns(10);

        JLabel lblDescription = new JLabel("Project description:");
        lblDescription.setBounds(25, 215, 200, 20);
        frame.getContentPane().add(lblDescription);

        JTextField txtDescription = new JTextField();
        txtDescription.setBounds(25, 240, 250, 20);
        frame.getContentPane().add(txtDescription);
        txtDescription.setColumns(10);

        JLabel lblVisibility = new JLabel("Repository visibility:");
        lblVisibility.setBounds(25, 275, 200, 20);
        frame.getContentPane().add(lblVisibility);

        String[] visibilityOptions = { "Public", "Private" };
        JComboBox<String> cmbVisibility = new JComboBox<>(visibilityOptions);
        cmbVisibility.setBounds(25, 300, 250, 20);
        frame.getContentPane().add(cmbVisibility);

        JButton btnCreateRepo = new JButton("Create Repo");
        btnCreateRepo.setBounds(25, 340, 150, 25);
        frame.getContentPane().add(btnCreateRepo);
        btnCreateRepo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = txtPassword.getText();
                String description = txtDescription.getText();
                boolean isPublic = cmbVisibility.getSelectedItem().toString().equals("Public");

                createRepo(username, password, description, isPublic);
            }
        });

        lblStatus = new JLabel("");
        lblStatus.setBounds(25, 380, 350, 20);
        frame.getContentPane().add(lblStatus);
    }

    private void createRepo(String username, String password, String description, boolean isPrivate) {
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

        // Create a new GitHub repository using the given credentials and repository
        // information
        GitHubClient client = new GitHubClient();
        client.setCredentials(username, password);

        RepositoryService service = new RepositoryService();
        Repository repository = new Repository();
        repository.setName("repo name here");
        repository.setDescription(description);
        repository.setPrivate(isPrivate);

        try {
            service.createRepository(repository);
            lblStatus.setText("GitHub repo created successfully");
        } catch (IOException e) {
            lblStatus.setText("Error creating GitHub repo");
        }
    }

}