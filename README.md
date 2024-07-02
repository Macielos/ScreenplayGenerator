Screenplay Generator for Warcraft 3 LUA Dialog System available here:
https://github.com/Macielos/Warcraft3LuaLibs/

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

Sorry if it seems non-intuitive, I wrote it mostly for myself :P. 

NOTE: Java 18 or later is required. 