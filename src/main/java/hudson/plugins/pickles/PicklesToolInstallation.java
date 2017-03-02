package hudson.plugins.pickles;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Created by User on 2017-02-26.
 */
public final class PicklesToolInstallation extends ToolInstallation  implements NodeSpecific<PicklesToolInstallation>, EnvironmentSpecific<PicklesToolInstallation> {

    @DataBoundConstructor
    public PicklesToolInstallation(String name, String home) {
        super(name, home, null);
    }

    @Override
    public PicklesToolInstallation forEnvironment(EnvVars envVars) {
        return new PicklesToolInstallation(getName(), envVars.expand(getHome()));
    }

    @Override
    public PicklesToolInstallation forNode(Node node, TaskListener taskListener) throws IOException, InterruptedException {
        return new PicklesToolInstallation(getName(), translateFor(node, taskListener));
    }

    @Extension @Symbol("pickles")
    public static class PicklesToolInstallationDescriptor extends ToolDescriptor<PicklesToolInstallation> {

        @Override
        public String getDisplayName() {
            return "Pickles";
        }

        @Override
        public PicklesToolInstallation[] getInstallations() {
            return getDescriptor().getInstallations();
        }

        @Override
        public void setInstallations(PicklesToolInstallation... installations) {
            getDescriptor().setInstallations(installations);
            save();
        }

        private PicklesNotifier.PicklesNotifierDescriptor getDescriptor() {
            Jenkins jenkins = Jenkins.getInstance();
            if (jenkins == null)
                throw new NullPointerException("Jenkins instance is null");

            PicklesNotifier.PicklesNotifierDescriptor descriptor =  jenkins.getDescriptorByType(PicklesNotifier.PicklesNotifierDescriptor.class);
            if (descriptor == null) {
                // To stick with current behavior and meet findbugs requirements
                throw new NullPointerException("PicklesNotifier.PicklesNotifierDescriptor is null");
            }
            return descriptor;
        }
    }
}
