import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shimon on 24/08/15.
 *
 * Jazzignore2gitignore - Helps you convert an RTC sandbox to a git local repository,
 * by finding all *.jazzignore files and converting it into one *.gitginore file.
 *
 * You should do the following (to convert a RTC sandbox to git repo):
 * 1. copy your RTC sandbox to a side directory, [dir]
 * 2. Run java -jar Jazzignore2Gitignore [dir]
 *      This will create a *.gitignore and remove all *.jazzignore
 * 3. Get rid of any jazz/rtc metadata files and create a remote git repo and commit/push your code to it.
 *
 */
public class Jazzignore2Gitignore {

    private static final String[] JAZZIGNORE_EXTENSIONS = new String[]{"jazzignore"};

    private final String _root;

    public static void main(String[] args) throws IOException {
        if(args.length !=1){
            System.err.println("Error: please specify a single parameter indicating your RTC sandbox");

        }
        String jazzRoot = args[0];
        Jazzignore2Gitignore j2g = new Jazzignore2Gitignore(jazzRoot);
        j2g.generateGitignoreAndRemoveJazzignore();
    }

    public Jazzignore2Gitignore(String jazzRoot) {
        _root = jazzRoot;
    }

    private void generateGitignoreAndRemoveJazzignore() throws IOException {
        File dir = new File(_root);
        List<File> files = (List<File>) FileUtils.listFiles(dir, JAZZIGNORE_EXTENSIONS , true);


        List<String> totalIgnorePatterns = new LinkedList<>();

        files.forEach((file) -> {
            try {
                List<String> ignoredPatterns = readContent(file);
                totalIgnorePatterns.addAll(ignoredPatterns);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.println(totalIgnorePatterns);

        files.forEach((file) -> {file.delete();});

        File gitignore = new File(_root, ".gitignore");
        gitignore.createNewFile();
        FileUtils.writeLines(gitignore, totalIgnorePatterns);
    }

    private List<String> readContent(File file) throws IOException {
        List<String> lines = FileUtils.readLines(file);

        return lines.stream().map((line) -> {
            return this.getValue(file, line);
        }).collect(Collectors.toList());
    }

    private String getValue(File file, String s) {
        File path = new File(file.getParent(), s);
        String relative = new File(_root).toURI().relativize(path.toURI()).getPath();
        return relative;
    }

}
