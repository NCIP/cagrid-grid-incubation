package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.StreamGobbler.LogPriority;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  FixXmiExecutor
 *  Executes SDK 3.1 / 3.2 / 3.2.1's fix-xmi ant target
 * 
 * @author David Ervin
 * 
 * @created Oct 29, 2007 2:11:31 PM
 * @version $Id: FixXmiExecutor.java,v 1.9 2008/04/28 19:31:07 dervin Exp $ 
 */
public class FixXmiExecutor {
    public static final Log LOG = LogFactory.getLog(FixXmiExecutor.class);
    
    // ant tasks
    public static final String FIX_XMI_TASK = "fix-xmi";
    public static final String COMPILE_GENERATOR_TASK = "compile-generator";
    
    // properties for the ant tasks
    public static final String MODEL_DIR_PROPERTY = "dir.model";
    public static final String MODEL_FILENAME_PROPERTY = "model_filename";
    public static final String FIXED_MODEL_FILENAME_PROPERTY = "fixed_filename";
    public static final String PREPROCESSOR_PROPERTY = "xmi_preprocessor";
    
    // the EA xmi preprocessor class
    public static final String EA_XMI_PREPROCESSOR = 
        "gov.nih.nci.codegen.core.util.EAXMIPreprocessor";
    
    private static Boolean isWindows = null;
    
    private FixXmiExecutor() {
        // prevent instantiation
    }
    
    
    /**
     * Runs the SDK's fix-xmi target against the specified model
     * @param originalModel
     *      The file containing the original XMI model from EA
     * @param sdkDir
     *      The caCORE SDK base directory
     * @return
     *      The file containing the 'fixed' model
     */
    public static File fixEaXmiModel(File originalModel, File sdkDir) throws IOException,
        InterruptedException {
        File cleanModelFile = cleanXmi(originalModel);
        StringBuilder command = new StringBuilder();
        // get the base ant command
        command.append(getAntCall(sdkDir.getAbsolutePath())).append(" ");
        // add properties and their values
        command.append("-D").append(MODEL_DIR_PROPERTY).append("=");
        if (osIsWindows()) {
            command.append("\"");
        }
        command.append(cleanModelFile.getAbsoluteFile().getParent());
        if (osIsWindows()) {
            command.append("\"");
        }
        command.append(" ");
        command.append("-D").append(MODEL_FILENAME_PROPERTY)
            .append("=").append(cleanModelFile.getName()).append(" ");
        command.append("-D").append(FIXED_MODEL_FILENAME_PROPERTY)
            .append("=").append("fixed_").append(originalModel.getName()).append(" ");
        command.append("-D").append(PREPROCESSOR_PROPERTY)
            .append("=").append(EA_XMI_PREPROCESSOR);
        // execute the command
        LOG.debug("Executing " + command.toString());
        Process proc = Runtime.getRuntime().exec(command.toString());
        // streams to LOG
        new StreamGobbler(proc.getInputStream(), StreamGobbler.TYPE_OUT,
            LOG, LogPriority.DEBUG).start();
        new StreamGobbler(proc.getErrorStream(), StreamGobbler.TYPE_ERR,
            LOG, LogPriority.DEBUG).start();
        LOG.debug("Waiting");
        proc.waitFor();
        if (proc.exitValue() == 0) {
            return new File(originalModel.getParent() + File.separator + "fixed_" + originalModel.getName());
        } else {
            throw new RuntimeException("Error executing fix-xmi command:\n" + command.toString());
        }
    }
    
    
    private static String getAntCall(String buildFileDir) {
        StringBuilder cmd = new StringBuilder();
        if (osIsWindows()) {
            cmd.append("java.exe ");
            cmd.append("-classpath \"").append(getAntLauncherJarLocation(System.getProperty("java.class.path")));
            cmd.append("\" org.apache.tools.ant.launch.Launcher -buildfile \"").append(buildFileDir);
            cmd.append(File.separator).append("build.xml\"");
        } else {
            // escape out the spaces.....
            buildFileDir = buildFileDir.replaceAll("\\s", "\\ ");
            cmd.append("java ");
            cmd.append("-classpath ").append(getAntLauncherJarLocation(System.getProperty("java.class.path")));
            cmd.append(" org.apache.tools.ant.launch.Launcher -buildfile ").append(buildFileDir);
            cmd.append(File.separator).append("build.xml");
        }
        // add targets
        cmd.append(" ")/*.append(COMPILE_GENERATOR_TASK).append(" ")*/.append(FIX_XMI_TASK);
        return cmd.toString();
    }
    
    
    private static String getAntLauncherJarLocation(String path) {
        StringTokenizer pathTokenizer = new StringTokenizer(path, File.pathSeparator);
        while (pathTokenizer.hasMoreTokens()) {
            String pathElement = pathTokenizer.nextToken();
            if ((pathElement.indexOf("ant-launcher") != -1) && pathElement.endsWith(".jar")) {
                return pathElement;
            }
        }
        return null;
    }
    
    
    private static File cleanXmi(File originalXmi) throws IOException {
        LOG.debug("Clean XMI");
        File cleanedFile = new File(originalXmi.getParentFile(), "cleaned_" + originalXmi.getName());
        StringBuffer xmiContents = Utils.fileToStringBuffer(originalXmi);
        XmiCleaner.cleanXmi(xmiContents);
        Utils.stringBufferToFile(xmiContents, cleanedFile.getAbsolutePath());
        return cleanedFile;
    }
    
    
    private static boolean osIsWindows() {
        if (isWindows == null) {
            String os = System.getProperty("os.name").toLowerCase();
            isWindows = Boolean.valueOf(os.contains("windows"));
        }
        return isWindows.booleanValue();
    }
}
