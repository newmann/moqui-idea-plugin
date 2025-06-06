## Welcome to Moqui Idea Plugin

This tool supports IntelliJ IDEA 2023.1~2024.3, Community Edition,
The Ultimate version has not been tested and should still be supported.

Support the XSD standard defined in Moqui Framework 3, which is version 3.

Originally recommended to execute [./gradlew setupIntellij](https://moqui.org/m/docs/framework/IDE+Setup/IntelliJ+IDEA+Setup) command allows Idea to support Moqui's XML recognition. After installing this plugin, it will automatically install without the need to execute this command.

If this command has already been executed, it is necessary to manually delete the definitions added by the process. These definitions can be deleted under Languages&Frameworks=>schemas and DTDs.

No other configuration is required.

The supported functions include:
1. Auto Complete prompt
2. Jump (Ctrl+B, Ctrl+click)
3. Quick documentation for entity and service
4. Display the usage quantities of Entity, Service, and Transition
5. Support automatic prompts for tags and attributes in entity face xml files
6. Rename
7. Automatic error prompt
8. Folding form list and form corner


[![license](https://img.shields.io/badge/license-CC0%201.0%20Universal-blue.svg)](https://github.com/newmann/moqui-idea-plugin/blob/master/LICENSE.md)