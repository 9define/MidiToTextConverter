<h1>MidiToTextConverter</h1>

How it works:
- Read the given midi file into the Java midi system sequencer.
- Convert all ShortMessages into 'Update' objects
- Pair the NOTE_ON and NOTE_OFF Updates and write them to a file, marking the used Updates as conversion goes along

I'm progressively working to improve this project, and the output it gives right now is accurate but not visually easy to work with when loaded into the MusicEditor so that's the main goal right now.

<h4>Usage:</h4>
<code>java -jar /path/to/MidiToTextConverter.jar /path/to/input.mid /path/to/output.txt</code>