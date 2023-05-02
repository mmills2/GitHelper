package GitHelper;

import java.util.Scanner;
import java.io.File;
import git.tools.client.GitSubprocessClient;

public class gitHelper {
    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path of project");
        String repoPath = scanner.nextLine();

        File gitIgnore = new File(repoPath,"gitIgnore");
        File readMe = new File(repoPath, "README.md");

        GitSubprocessClient gitSubprocessClient = new GitSubprocessClient(repoPath);
        gitSubprocessClient.gitInit();
        
    }
}
