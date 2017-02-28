# Pickles Notifier

Pickles notifier is going to be a [Jenkins][1] plugin that will take the test results from [Specflow][3] and generate output using [Pickles][2]. Primarily this is intended for the .NET audiance using Jenkins as their build host who also use Specflow testing.

# Things to do still
(For most up to date check the issues)
- <s> Create the jelly config file for the pickles installation </s>
- <s> Create immutable classes for the pickles installation </s>
- Edit the jelly config file for the build steps
    - <s>Make a drop down to select installation</s>
    - Textbox for Feature File Directory
    - Textbox for Output Directory
    - Dropdown for output generation type
    - Textbox for link results files (with matching help html to show using a semi-colon ';' to seperate multiple files)
    - Advanced Section:
        - Checkbox for Use experimental features
        - Checkbox for Include comments (default to checked)
        - Dropdown for test results format
- Wire up the runner to use the selected installation

[1]: http:jenkins.io
[2]: http://www.picklesdoc.com/
[3]: http://specflow.org/