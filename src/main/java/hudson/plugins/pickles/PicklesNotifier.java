package hudson.plugins.pickles;
/**
 * Created by User on 2/23/2017.
 */
import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.tools.ToolInstallation;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class PicklesNotifier extends Notifier {
    @DataBoundConstructor
    public PicklesNotifier(){}

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return super.perform(build, launcher, listener);
    }

    @Extension @Symbol("pickles")
    public static final class PicklesNotifierDescriptor extends BuildStepDescriptor<Publisher> {
        @CopyOnWrite
        private volatile PicklesToolInstallation[] installations = new PicklesToolInstallation[0];

        public PicklesNotifierDescriptor() {
            super(PicklesNotifier.class);
            load();
        }

        /**
         * Human readable name of this kind of configurable object.
         */
        @Override
        public String getDisplayName() {
            return "Pickles";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public PicklesToolInstallation[] getInstallations() {
            return installations;
        }

        public void setInstallations(PicklesToolInstallation[] installations) {
            this.installations = installations;
            save();
        }

        public PicklesToolInstallation.PicklesToolInstallationDescriptor getToolDescriptor() {
            return ToolInstallation.all().get(PicklesToolInstallation.PicklesToolInstallationDescriptor.class);
        }
    }
}
