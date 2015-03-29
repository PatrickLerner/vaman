# VamAn

<img src="https://raw.githubusercontent.com/PatrickLerner/vaman/master/etc/logo.png" align="right" />

VamAn is a character sheet analyzer for the [Storyteller System](https://en.wikipedia.org/wiki/Storytelling_System) used by Onyx Path / White Wolf in their role playing games. Primarily the focus of this project is to automatically analyze character sheets for the Vampire: the Masquerade game in its 20th Anniversary edition, published in 2011. The modularity of the project should, however, hopefully help in expanding it into other game lines in the future.

The analyzer requires a json file which describes how points are spend and what the conditions for spending points are (how many experience or freebie points per dot must be spend). A simple example for how this file should look can be seen in the character sheet for a character named ['Stina'](https://github.com/PatrickLerner/vaman/blob/master/src/test/resources/stina.json) in the resources directory. The assignment for the points should be fairly obvious and can be edited with a normal text editor.

Note that the character sheet defines a 'base', i.e. another file which specifies more about point cost, in this case the file ['vtm.json'](https://github.com/PatrickLerner/vaman/blob/master/src/test/resources/vtm.json) which must be located in the same directory. As the name suggests the vtm base file is a template for Vampire: the Masquerade and would have to be adjusted to be used for other game lines or for house rules which modify point costs or the amount of points character receive.

To run the program, it must be turned into a runable jar file using Eclipse, which then can be run from the command line with the character sheet file as a parameter to it:

	java -jar vaman.jar stina.json

The output will then specify how freebie and experience points need to be spend optimally to build this character (i.e. it finds the cheapest way to build it).

	freebie points (15/15)
		inClan               1x 7 fp
		backgrounds          3x 1 fp
		skills               1x 2 fp
		willpower            3x 1 fp


	total: 0 xp
