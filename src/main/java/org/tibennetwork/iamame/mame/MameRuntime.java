package org.tibennetwork.iamame.mame;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.ParseException;
import org.tibennetwork.iamame.IaMame;
import org.tibennetwork.iamame.internetarchive.NoWritableRomPathException;

/**
 * Mame binary facade
 */
public class MameRuntime {

    /**
     * The path of the Mame binary executable
     */
    private String binPath;

    /**
     * Directory containing the Mame 
     * executable
     */
    private String binDirectory;

    private List<File> romsPaths;

    public MameRuntime (String binPath) 
            throws IOException, InterruptedException, ParseException {
        this.binPath = binPath;
        this.binDirectory = new File(binPath).getParent();
        IaMame.debug(String.format(
            "Mame binary directory: %s", 
            this.binDirectory));
        this.getRomsPathsFromBinary();
    }
  
    public List<File> getRomsPaths() {
        return this.romsPaths;        
    }

    /**
     * Executes Mame using the given arguments.
     * The containing (java) process is locked until
     * Mame terminates. cess so execution is transparent
     * for the user.
     */
    public void execute (String[] rawArgs) 
        throws IOException, InterruptedException {
        
        List<String> args = new ArrayList<>();
        args.add(this.binPath);
        args.addAll(Arrays.asList(rawArgs));
        
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(new File(this.binDirectory));
        builder.inheritIO();
        Process mameProcess = builder.start();
        mameProcess.waitFor();
    }

    public InputStream executeAndReturnStdoutAsInputStream (String[] rawArgs)
        throws IOException, InterruptedException, MameExecutionException {
        
        List<String> args = new ArrayList<>();
        args.add(this.binPath);
        args.addAll(Arrays.asList(rawArgs));
        
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(new File(this.binDirectory));
        Process mameProcess = builder.start();
    
        return mameProcess.getInputStream();
    
    } 

    /**
     * Executes Mame using the given arguments and return
     * the stdout of the command as a List of String
     */
    public List<String> executeAndReturnStdout (String[] rawArgs)
        throws IOException, InterruptedException, MameExecutionException {
        
        List<String> args = new ArrayList<>();
        args.add(this.binPath);
        args.addAll(Arrays.asList(rawArgs));
        
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(new File(this.binDirectory));
        Process mameProcess = builder.start();

        BufferedReader mameRuntimeStdout = new BufferedReader(
            new InputStreamReader(mameProcess.getInputStream()));

        List<String> stdout = new ArrayList<>();
        String s; 
        
        while ((s = mameRuntimeStdout.readLine()) != null) {
            stdout.add(s);
        }
        
        int exitValue = mameProcess.waitFor();

        if (exitValue != 0) {
            throw new MameExecutionException(
                    String.format(
                        "Mame process returned exit value %s", 
                        exitValue));
        }

        return stdout;            
    }

    /**
     * Determines the path among the list of romPat were roms 
     * and software should be put.
     */
    public File getWritableRomPath () throws NoWritableRomPathException {
        for (File f: romsPaths) {
            if (f.isDirectory() && f.canWrite()) {
                return f;
            }
        } 

        throw new NoWritableRomPathException (
            "No writable path to write roms into");
    }

    private void getRomsPathsFromBinary () 
            throws IOException, InterruptedException, ParseException {

        String[] rawArgs = {"-showconfig"};
        List<String> mameStdout = null;

        try {
            mameStdout = this.executeAndReturnStdout(rawArgs);
        } catch( MameExecutionException e) {
            throw (RuntimeException) 
                new RuntimeException("Unhandled error occured while " 
                    + "launching mame to fetch rompaths")
                    .initCause(e);
        }
             
        this.romsPaths = new ArrayList<File>();

        Pattern p = Pattern.compile("^rompath +(.*)$");
        for (String s : mameStdout) {

            Matcher m = p.matcher(s);
            if (m.matches()) {

                // Change temporary the current working dir
                // to mame binary directory to find paths relative to 
                // it
                String originalCwd = System.getProperty("user.dir");
                System.setProperty("user.dir", this.binDirectory);

                for(String path : m.group(1).split(";")) {

                    // Handle $HOME syntax
                    path = path.replace("$HOME", 
                        System.getProperty("user.home"));

                    File dir = new File(path);

                    dir = new File(dir.getAbsolutePath());
                    if(dir.isDirectory()) {
                        IaMame.debug("rompath found: " + dir);
                        this.romsPaths.add(dir);
                    }
                }

                // Revert back the original cwd
                System.setProperty("user.dir", originalCwd);

                break;
            }
        }
        
    }

}
