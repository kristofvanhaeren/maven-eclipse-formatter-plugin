package net.nkr1pt.eclipseformatterplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Eclipse Formatter.
 *
 * @goal format
 *
 * @author Kristof Vanhaeren
 */
public class EclipseFormatter extends AbstractMojo {

    /**
     * @parameter default-value="default-formatting-rules"
     */
    private String formattingRules;
    /**
     * @parameter default-value="eclipse"
     */
    private String eclipse;
    /**
     * @parameter default-value="${project.basedir}/src"
     */
    private String sources;

    /**
     * Formats the source code using the Eclipse formatter.
     *
     * @throws MojoExecutionException mojo execution exception
     * @throws MojoFailureException mojo failure exception
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Executing Eclipse formatter plugin.");
        doFormat();
    }

    /**
     * doFormat.
     */
    private void doFormat() {
        try {
            File tempFile = File.createTempFile("formatting-rules", "");
            InputStream is = this.getClass().getResourceAsStream("/default-formatting-rules");
            tempFile.deleteOnExit();
            OutputStream out = new FileOutputStream(tempFile);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            is.close();
            out.flush();
            out.close();

            String formattingRulesLocation = tempFile.getAbsolutePath();
            String command =
                    eclipse
                    + " -nosplash "
                    + "-application org.eclipse.jdt.core.JavaCodeFormatter "
                    + "-verbose "
                    + "-config " + formattingRulesLocation + " "
                    + sources;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                getLog().info(readLine);
            }
        } catch (Exception e) {
            getLog().error(e);
        }
    }
}
