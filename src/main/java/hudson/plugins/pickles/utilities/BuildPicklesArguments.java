package hudson.plugins.pickles.utilities;

import hudson.EnvVars;
import hudson.Util;
import hudson.plugins.pickles.PicklesNotifier;
import hudson.util.ArgumentListBuilder;

/**
 * Created by User on 2017-02-26.
 */
public class BuildPicklesArguments {

    private final PicklesNotifier picklesArguments;
    private final EnvVars env;

    public BuildPicklesArguments(PicklesNotifier picklesNotifier, EnvVars env) {
        this.picklesArguments = picklesNotifier;
        this.env = env;
    }

    public void buildArguments(ArgumentListBuilder args){
        safeAdd(args, "feature-directory", picklesArguments.getFeatureDirectory());
        safeAdd(args, "output-directory", picklesArguments.getOutputDirectory());
        safeAdd(args, "documentation-format", picklesArguments.getDocumentationFormat());
        safeAdd(args, "system-under-test-name", picklesArguments.getSystemUnderTestName());
        safeAdd(args, "system-under-test-version", picklesArguments.getSystemUnderTestVersion());
        safeAdd(args, "link-results-file", picklesArguments.getLinkResultsFiles());
        safeAdd(args, "test-results-format", picklesArguments.getTestResultFormat());

        if(!picklesArguments.getEnableComments())
            args.add("--enableComments=false");
        if(picklesArguments.getIncludeExperimentalFeatures())
            args.add("--include-experimental-features");
    }

    private void safeAdd(ArgumentListBuilder args, String switchString, String value) {
        if (value != null && !value.isEmpty()) {
            String expanded = Util.replaceMacro(value, env);
            args.addKeyValuePair("--", switchString, expanded, false);
        }
    }
}
