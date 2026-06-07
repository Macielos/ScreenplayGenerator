Screenplay Generator and other tools for Warcraft 3 LUA Dialog System available here:
https://github.com/Macielos/Warcraft3LuaLibs/

1. Screenplay Generator

It converts human-friendly list of dialog lines into still quite human friendly, but more time-consuming to prepare 
LUA configuration. 

Just put your dialog file in src/main/resources/input.txt (replace content of the current one), build and run the application 
(no parameters required and your configuration will be created in src/main/resources/output/screenplay_[current date].lua).

The format is pretty straightforward, a list of lines:
Actor1: Hey.
Actor2: Hello.

And if a line should display a list of choices:
Actor1: [choices]Option1|Option2|Option3...

If you don't use choices, the resulting configuration is an already working linear dialog, for which you may add 
branches, transitions, funcs/triggers etc. If you include choices, you need to fill in to which line a dialog has to go
after picking an option (in ScreenplaySystem:goTo(TODO) replace TODO with a line index). See screenplay examples on repo 
linked above. Also remember to change your screenplay name as they need to be unique within the map.

2. Screenplay Message Text Collector: translates a screenplay with the convention above, collecting all messages and sounds into single script files. 

ScreenplayMessages file is a QoL feature for translators, so they don't have to edit each screenplay manually with each new version

ScreenplaySounds automates adding voice over to your screenplays - no need to manually create sound variables in sound editor, just import sound files under a proper path and the tool will link sound files to your screenplay messages.
Sound files should be imported as: war3mapImported\<screenplay name><msg index> or war3mapImported\<screenplay name>_<choice index>. Indexes must always be 3 digits:
e.g.
war3mapImported\intro001 - "intro" screenplay, message 1
war3mapImported\intro002 - "intro" screenplay, message 2
war3mapImported\intro004_001 - "intro" screenplay, message 4, choice 1

If you want to use different in-game path (e.g. war3campImported), rename the input folder or if you want some custom nested path, change INGAME_SOUND_FILES_PATH constant in a class ScreenplayMessageTextCollector. 

NOTES: 

- I didn't use a LUA parser, instead I base on that input screenplay file must be a properly formatted LUA file (with 4 spaces, not tabs). 
- Java 18 or later is required. 
- Sorry if any of this seem non-intuitive, I wrote them mostly for myself :P. Feel free to ask or propose suggestions on hiveworkshop.com
