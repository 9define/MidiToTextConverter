import java.util.Objects;

/**
 * An entire note. Start and end beats, channel, volume, velocity, and some functionality.
 */
class Tone implements Comparable<Tone> {

  /**
   * Start and end beats for this {@code Tone}.
   */
  private long start, end;

  /**
   * The instrument used to play the note.
   */
  private int instrument;

  /**
   * The key of the note. Octave and pitch can be extrapolated from this.
   */
  private int key;

  /**
   * The update's volume.
   */
  private int velocity;

  /**
   * Make a {@code Tone} from the given {@code Update}s.
   * @param on The &qt;on&qt; {@code Update} that signfies the beginning of a note.
   * @param off The &qt;off&qt; {@code Update} that signfies the end of a note.
   */
  Tone(Update on, Update off) {
    if (off != null) {
      this.start = on.getBeat();
      this.end = off.getBeat();
      this.instrument = on.getInstrument();
      this.key = on.getKey();
      this.velocity = on.getVelocity();
    }
  }

  /**
   * Format the {@code Toneß} in text-file style:
   * &qt;note [start] [end] [channel] [key] [velocity]&qt;
   * @return The formatted {@code Tone} string.
   */
  @Override
  public String toString() {
    return "note " + this.start + " " + this.end + " "
            + (this.instrument + 1) + " " + this.key + " " + this.velocity;
  }

  /**
   * Determines if the given object is the same as this {@code Tone}.
   * @param o The object to check against this update.
   * @return <b>True</b> <i>IFF</i> {@code o} is an {@code Tone} and {@code o} has the
   * same beat, key, and instrument as this {@code Tone}, <b>false</b> otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof Tone)) {
      return false;
    } else {
      // cast o into an Tone object, compare fields
      Tone t = (Tone) o;

      return this.start == t.start
              && this.end == t.end
              && this.instrument == t.instrument
              && this.key == t.key
              && this.velocity == t.velocity;
    }
  }

  /**
   * Hashes this {@code Tone} object to a unique identifier.
   * @return A unique identifier (as an int) for this {@code Tone} object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.start, this.end, this.key, this.instrument);
  }

  /**
   * Compares the given {@code Tone} object to this {@code Tone} object.
   * @param other The {@code Tone} object to compare to.
   * @return <b>-1</b> if the given {@code Tone} comes &qt;before&qt; this {@code Tone},
   *  <b>0</b> if the given {@code Tone} object is &qt;equal&qt; to this {@code Tone} object,
   *  and <b>1</b> if the given {@code Tone} object comes &qt;after&qt; this {@code Tone} object.
   */
  @Override
  public int compareTo(Tone other) {
    // if the two update objects are the same, 0
    if (this.equals(other)) {
      return 0;
    } else { // not the same data, compare fields
      // compare start beats
      if (this.start < other.start) {
        return -1;
      } else if (this.start > other.start) {
        return 1;
      } else {
        // compare end beats
        if (this.end < other.end) {
          return -1;
        } else if (this.end > other.end) {
          return 1;
        } else {
          // compare keys
          if (this.key < other.key) {
            return - 1;
          } else if (this.key > other.key) {
            return 1;
          } else {
            // compare channels
            if (this.instrument < other.instrument) {
              return - 1;
            } else if (this.instrument > other.instrument) {
              return 1;
            } else {
              return 0;
            }
          }
        }
      }
    }
  }
}
