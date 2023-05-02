import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import git.tools.client.GitSubprocessClient;
import github.tools.client.RequestParams;
import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.CreateRepoResponse;

public class GitHelperGUI extends JFrame implements ActionListener {

    private JTextField pathField;
    private JTextField nameField;
    private JTextField usernameField;
    private JTextField tokenField;
    private JTextField descriptionField;
    private JTextArea outputArea;

    public GitHelperGUI() {
        super("Git Helper");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel pathLabel = new JLabel("Project Path:");
        pathField = new JTextField();
        inputPanel.add(pathLabel);
        inputPanel.add(pathField);

        JLabel nameLabel = new JLabel("Repo Name:");
        nameField = new JTextField();
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        JLabel usernameLabel = new JLabel("GitHub Username:");
        usernameField = new JTextField();
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);

        JLabel tokenLabel = new JLabel("GitHub Token:");
        tokenField = new JTextField();
        inputPanel.add(tokenLabel);
        inputPanel.add(tokenField);

        JLabel descriptionLabel = new JLabel("Repo Description:");
        descriptionField = new JTextField();
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionField);

        JButton createButton = new JButton("Create Repo");
        createButton.addActionListener(this);
        inputPanel.add(createButton);

        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Create Repo")) {
            String repoPath = pathField.getText();
            String repoName = nameField.getText();
            String username = usernameField.getText();
            String token = tokenField.getText();
            String description = descriptionField.getText();

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
                outputArea.append(".gitIgnore file created.\n");
            } catch (Exception ex) {
                outputArea.append("Could not create .gitIgnore file: " + ex.getMessage() + "\n");
            }

            try {
                readMe.createNewFile();
                PrintWriter writeReadMe = new PrintWriter(readMe);
                writeReadMe.println("## " + repoName);
                writeReadMe.flush();
                outputArea.append("README file created.\n");
            } catch (Exception ex) {
                outputArea.append("Could not create README file: " + ex.getMessage() + "\n");
            }

            // Git initial commit
            gitSubprocessClient.gitAddAll();
            gitSubprocessClient.gitCommit("initial commit");

            // GitHub API client
            GitHubApiClient gitHubApiClient = new GitHubApiClient(username, token);

            // Create the repository on GitHub
            RequestParams createRepoParams = new RequestParams();
            createRepoParams.addParam("name", repoName);
            createRepoParams.addParam("description", description);

            CreateRepoResponse createRepoResponse = gitHubApiClient.createRepo(createRepoParams);
            outputArea.append("Repository " + repoName + " created on GitHub.\n");

            // Might need to include a try catch bloch right here
        }

    }

    public static void main(String[] args) {
        new GitHelperGUI();
    }
}
