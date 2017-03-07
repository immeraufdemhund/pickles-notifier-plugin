package hudson.plugins.pickles;
/**
 * Created by User on 2/23/2017.
 */

import hudson.CopyOnWrite;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.plugins.pickles.utilities.BuildPicklesArguments;
import hudson.plugins.pickles.utilities.FindPicklesInstallation;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.Arrays;

public class PicklesNotifier extends Notifier {

    @DataBoundConstructor
    public PicklesNotifier(){ }

    private String picklesInstallationName;
    private String featureDirectory;
    private String outputDirectory;
    private String testResultFormat;
    private String documentationFormat;
    private String systemUnderTestName;
    private String systemUnderTestVersion;
    private String linkResultsFiles;
    private Boolean enableComments;
    private Boolean includeExperimentalFeatures;

    public String getFeatureDirectory() { return featureDirectory; }
    public String getOutputDirectory() { return outputDirectory; }
    public String getTestResultFormat(){ return testResultFormat; }
    public String getDocumentationFormat() { return documentationFormat; }
    public String getSystemUnderTestName() { return systemUnderTestName; }
    public String getSystemUnderTestVersion() { return systemUnderTestVersion; }
    public String getLinkResultsFiles() { return linkResultsFiles; }
    public Boolean getEnableComments() { return enableComments; }
    public Boolean getIncludeExperimentalFeatures() { return includeExperimentalFeatures; }
    public String getPicklesInstallationName() { return picklesInstallationName; }

    @DataBoundSetter public void setFeatureDirectory(String featureDirectory) { this.featureDirectory = featureDirectory; }
    @DataBoundSetter public void setOutputDirectory(String outputDirectory) { this.outputDirectory = outputDirectory; }
    @DataBoundSetter public void setTestResultFormat(String testResultFormat) { this.testResultFormat = testResultFormat; }
    @DataBoundSetter public void setDocumentationFormat(String documentationFormat) { this.documentationFormat = documentationFormat; }
    @DataBoundSetter public void setSystemUnderTestName(String systemUnderTestName) { this.systemUnderTestName = systemUnderTestName; }
    @DataBoundSetter public void setSystemUnderTestVersion(String systemUnderTestVersion) { this.systemUnderTestVersion = systemUnderTestVersion; }
    @DataBoundSetter public void setLinkResultsFiles(String linkResultsFiles) { this.linkResultsFiles = linkResultsFiles; }
    @DataBoundSetter public void setEnableComments(Boolean enableComments) { this.enableComments = enableComments; }
    @DataBoundSetter public void setIncludeExperimentalFeatures(Boolean includeExperimentalFeatures) { this.includeExperimentalFeatures = includeExperimentalFeatures; }
    @DataBoundSetter public void setPicklesInstallationName(String picklesInstallationName) { this.picklesInstallationName = picklesInstallationName; }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        ArgumentListBuilder args = new ArgumentListBuilder();

        EnvVars env = build.getEnvironment(listener);
        env.putAll(build.getBuildVariables());

        FindPicklesInstallation findPicklesInstallation = new FindPicklesInstallation(this, build, launcher, listener);
        BuildPicklesArguments buildPicklesArguments = new BuildPicklesArguments(this, env);

        if(!findPicklesInstallation.addToArgumentList(args))
            return false;

        buildPicklesArguments.buildArguments(args);

        if(!launcher.isUnix())
            args = args.toWindowsCommand();

        int errorCode = launcher.decorateFor(build.getBuiltOn()).launch()
                .cmds(args)
                .envs(env)
                .stdout(listener)
                .pwd(build.getModuleRoot())
                .join();
        if(errorCode != 0){
            listener.error("[pickles] Pickles did not successfully complete. Error Code:" + errorCode);
        }
        return true;
    }

    @Extension @Symbol("pickles")
    public static final class PicklesNotifierDescriptor extends BuildStepDescriptor<Publisher> {
        @CopyOnWrite
        private volatile PicklesToolInstallation[] installations = new PicklesToolInstallation[0];

        public PicklesNotifierDescriptor() {
            super(PicklesNotifier.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Pickles";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public PicklesToolInstallation[] getInstallations() {
            return Arrays.copyOf(installations, installations.length);
        }

        public void setInstallations(PicklesToolInstallation... installations) {
            this.installations = installations;
            save();
        }

        public PicklesToolInstallation.PicklesToolInstallationDescriptor getToolDescriptor() {
            return ToolInstallation.all().get(PicklesToolInstallation.PicklesToolInstallationDescriptor.class);
        }
    }
}
