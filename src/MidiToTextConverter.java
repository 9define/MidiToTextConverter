import javax.sound.midi.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Converts the given midi file to a text file formatted in the way we are able to interpret,
 */
public class MidiToTextConverter {

    /**
     * Parse the given file.
     *
     * @param args The file to parse.
     */
    public static void main(String[] args) {
        // create a file from the input path
        File input = new File(args[0]);

        // create a text file to write to
        File output = new File(args[1]);

        // run the conversion
        convertMidiToText(input, output);
    }

    /**
     * Converts the given midi file to a text file in the style that the MusicEditors can accept.
     * @param input The midi file to parse.
     * @param output The text file to write.
     */
    private static void convertMidiToText(File input, File output) {
        float programStartTime = (float) System.nanoTime();
        try {
            float start = (float) System.nanoTime();

            // make a sequence from the given file
            Sequence sequence = MidiSystem.getSequence(input);

            System.out.println("Loading the file took " + ((System.nanoTime() - start) / 1000000000) + " seconds.");

            // make a sequencer
            Sequencer sequencer = MidiSystem.getSequencer();

            // set the sequencer's sequence
            sequencer.setSequence(sequence);

            // set the sequencer's tick rate
            sequencer.setTempoInMPQ(sequence.getMicrosecondLength() / sequence.getTickLength());

            // store the notes that have been turned on/off so far
            SortedMap<Long, SortedSet<Update>> onNotesMap = new TreeMap<Long, SortedSet<Update>>(),
                    offNotesMap = new TreeMap<Long, SortedSet<Update>>();

            System.out.println("Sequencer MPQ: " + sequencer.getTempoInMPQ());

            start = (float) System.nanoTime();

            // parse all midi messages into update lists
            parseMessages(onNotesMap, offNotesMap, sequence);

            System.out.println("All updates generated! Took "
                    + ((System.nanoTime() - start) / 1000000000) + " seconds.");

            start = (float) System.nanoTime();

            // for all pairs of messages, merge them into a tone
            SortedSet<Tone> tones = generateTones(onNotesMap, offNotesMap);

            System.out.println("All tones generated! Took "
                    + ((System.nanoTime() - start) / 1000000000) + " seconds.");

            // delete all updates, as they are now irrelevant
            onNotesMap = null;
            offNotesMap = null;

            // get the buffered writer for the output file
            BufferedWriter buffy = Files.newBufferedWriter(output.toPath());

            // write the tempo line
            buffy.write("tempo " + (int)sequencer.getTempoInMPQ() + "\n");

            start = (float) System.nanoTime();

            // write all tones to the text file
            tones.forEach(t -> {
                try {
                    buffy.write(t.toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("All tones written!");

            // close the output buffer
            buffy.close();

            System.out.println("File written! Took " + ((System.nanoTime() - start) / 1000000000) + " seconds.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Total time: " + ((System.nanoTime() - programStartTime) / 1000000000) + " seconds.");
    }

    /**
     * Convert all messages in the {@code Sequence} into {@code Update}s and add them to the appropriate maps.
     * @param onNotesMap Map of &qt;on&qt; {@code Update}s to add to.
     * @param offNotesMap Map of &qt;off&qt; {@code Update}s to add to.
     * @param sequence The {@code Sequence} to pull midi messages from.
     */
    private static void parseMessages(SortedMap<Long, SortedSet<Update>> onNotesMap,
                                      SortedMap<Long, SortedSet<Update>> offNotesMap,
                                      Sequence sequence) {
        // loop through all the tracks in the sequence
        for (Track track : sequence.getTracks()) {
            // loop through every message in the track
            for (int i = 0; i < track.size(); i++) {
                // get the current note (midievent)
                MidiEvent currentNote = track.get(i);

                // store the current beat
                long beat = currentNote.getTick();

                // get note information and store it in a message
                MidiMessage currentNoteMessage = currentNote.getMessage();

                // make sure that this message is a ShortMessage
                if (currentNoteMessage instanceof ShortMessage) {
                    // convert the message into a ShortMessage
                    ShortMessage shortMessage = (ShortMessage) currentNoteMessage;

                    // save the instrument used to play the note
                    int instrument = shortMessage.getChannel() + 1;

                    // get and store the note's semitone
                    int semitone = shortMessage.getData1();

                    // get and store the note's velocity/"volume"
                    int volume = shortMessage.getData2();

                    // make the new update
                    Update update = new Update(beat, instrument,
                            semitone, volume);

                    // add the update to the appropriate list
                    if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
                        addToMap(onNotesMap, update);

                    } else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF
                            || shortMessage.getData2() == 0) {
                        addToMap(offNotesMap, update);
                    }
                }
            }
        }
    }

    /**
     * Adds the given {@code Update} to the given map of set of updates.
     * @param map The map to add to.
     * @param toAdd The update to place in the given map.
     */
    private static void addToMap(SortedMap<Long, SortedSet<Update>> map, Update toAdd) {
        // if the given key is missing from the map, add it
        if (!map.containsKey(toAdd.getBeat())) {
            map.put(toAdd.getBeat(), new TreeSet<Update>());
        }

        // place the given update in the correct set in the map
        map.get(toAdd.getBeat()).add(toAdd);
    }

    /**
     * Find an on/off {@code Update} pairing and write it to the text file.
     * @param onNotesMap The set of &qt;on&qt; {@code Update}s to use.
     * @param offNotesMap The set of &qt;off&qt; {@code Update}s to use.
     */
    private static SortedSet<Tone> generateTones(SortedMap<Long, SortedSet<Update>> onNotesMap,
                                                 SortedMap<Long, SortedSet<Update>> offNotesMap) {
        // store all the tones to eventually return
        SortedSet<Tone> tones = new TreeSet<Tone>();

        // find the pair of each on note to its corresponding off note
        onNotesMap.entrySet().forEach(entry -> {
            // pair each 'on' note in this entry's set with the corresponding 'off' note
            entry.getValue().forEach(on -> {
                Update off = null;

                for (long i = on.getBeat(); i < offNotesMap.lastKey() && off == null; i++) {
                    if (offNotesMap.containsKey(i)) {
                        for (Update u : offNotesMap.get(i)) {
                            // if the channel and key are the same, and on comes before u
                            if (!u.hasBeenUsed()
                                    && on.getInstrument() == u.getInstrument()
                                    && on.getKey() == u.getKey()
                                    && on.getBeat() <= u.getBeat()) {
                                off = u;
                                u.use();
                                break;
                            }
                        }
                    }
                }

                tones.add(new Tone(on, off));
            });
        });

      return tones;
    }
}

