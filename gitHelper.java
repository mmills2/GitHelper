//package GitHelper;

import git.tools.client.GitSubprocessClient;
import github.tools.client.RequestParams;
import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class gitHelper {
    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path of project");
        String repoPath = scanner.nextLine();

        System.out.println("Enter the name of your repo");
        String repoName = scanner.nextLine();

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
            writeReadMe.println("## " + repoName);
            writeReadMe.flush();
        }
        catch(Exception e){
            System.out.println("Could not create README file");
        }

        // Git initial commit
        gitSubprocessClient.gitAddAll();
        gitSubprocessClient.gitCommit("initial commit");

        // Getting github username
        System.out.println("What is your GitHub username");
        String username = scanner.nextLine();

        // Getting github token
        System.out.println("What is your GitHub token");
        String token = scanner.nextLine();

        // Initializing github repo creation
        GitHubApiClient gitHubApiClient = new GitHubApiClient(username, token);
        RequestParams requestParams = new RequestParams();
        requestParams.addParam("name", repoName);

        // Getting repo description
        System.out.println("Type a description for your repo");
        String description = scanner.nextLine();
        requestParams.addParam("description", description);

        // Getting private or not
        System.out.println("Would you like your repo to be private? Y/N");
        String answer = scanner.nextLine();
        boolean priv = false;
        if(answer.equals("Y")){
            priv = true;
        }
        else if(answer.equals("N")){
            priv = false;
        }
        requestParams.addParam("private", priv);

        // Creating github repo with specified parameters
        CreateRepoResponse createRepo = gitHubApiClient.createRepo(requestParams);
        
    }
}
