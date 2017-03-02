package hudson.plugins.pickles.utilities;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.plugins.pickles.PicklesNotifier;
import hudson.plugins.pickles.PicklesToolInstallation;
import hudson.util.ArgumentListBuilder;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by snyder on 2017-03-01.
 */
public class FindPicklesInstallation {
    private final PicklesNotifier picklesNotifier;
    private final AbstractBuild build;
    private final Launcher launcher;
    private final BuildListener listener;

    public FindPicklesInstallation(PicklesNotifier picklesNotifier, AbstractBuild build, Launcher launcher, BuildListener listener){
        this.picklesNotifier = picklesNotifier;
        this.build = build;
        this.launcher = launcher;
        this.listener = listener;
    }

    public boolean addToArgumentList(ArgumentListBuilder args) throws InterruptedException, IOException {
        String execName = "pickles.exe";
        PicklesToolInstallation ai = getInstallation(listener.getLogger());

        if (ai == null) {
            listener.getLogger().println("[pickles] Could not find Pickles installation using default: " + execName);
            args.add(execName);
            return true;
        }

        Node node = Computer.currentComputer().getNode();
        if (node == null) {
            listener.fatalError("[pickles] Node was not found for current computer");
            return false;
        }

        EnvVars env = build.getEnvironment(listener);
        ai = ai.forNode(node, listener).forEnvironment(env);
        execName = getToolFullPath(ai.getHome(), execName);
        FilePath exec = new FilePath(launcher.getChannel(), execName);

        try {
            if (!exec.exists()) {
                listener.fatalError("[pickles] %s doesn't exist", execName);
                return false;
            }
        } catch (IOException e) {
            listener.fatalError("[pickles] Failed checking for existence of %s", execName);
            return false;
        }

        listener.getLogger().println("[pickles] setting path of exe to: " + execName);
        args.add(execName);
        return true;
    }

    private PicklesToolInstallation getInstallation(PrintStream logger){
        final PicklesNotifier.PicklesNotifierDescriptor descriptor = (PicklesNotifier.PicklesNotifierDescriptor) picklesNotifier.getDescriptor();
        final String picklesInstallationName = picklesNotifier.getPicklesInstallationName();
        if(picklesInstallationName == null){
            logger.println("[pickles] No installation was chosen, using default");
            return null;
        }

        for(PicklesToolInstallation i : descriptor.getInstallations()) {
            if (i.getName().equals(picklesInstallationName)) {
                logger.println("[pickles] using installation " + i.getHome());
                return i;
            }
        }

        return null;
    }

    /**
     * Get the full path of the tool to run.
     * If given path is a directory, this will append the executable name.
     */
    private String getToolFullPath(String pathToTool, String execName) throws IOException, InterruptedException {
        FilePath exec = new FilePath(launcher.getChannel(), pathToTool);
        if (exec.isDirectory()) {
            if (!pathToTool.endsWith("\\")) {
                pathToTool = pathToTool + "\\";
            }

            pathToTool = pathToTool + execName;
        }

        return pathToTool;
    }
}
