package GitHelper;

import git.tools.client.GitSubprocessClient;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class gitHelper {

    private static final String GITHUB_API_URL = "https://api.github.com/user/repos";
    private static final String ACCESS_TOKEN = "your_access_token_here";

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path of project");
        String repoPath = scanner.nextLine();

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
        catch(Exception e){
            System.out.println("Could not create gitIgnore file");
        }

        try{
            readMe.createNewFile();
            PrintWriter writeReadMe = new PrintWriter(readMe);
            writeReadMe.println("## " + "repo name here");
            writeReadMe.flush();
        }
        catch(Exception e){
            System.out.println("Could not create README file");
        }

        gitSubprocessClient.gitAddAll();
        gitSubprocessClient.gitCommit("initial commit");

        // create GitHub repo using API
        System.out.println("Do you want to create the repo? y/n");
    }
}
