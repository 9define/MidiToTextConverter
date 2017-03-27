import java.util.Objects;

/**
 * Essentially one half of a tone (the start or end note/message).
 */
class Update implements Comparable<Update> {

  /**
   * Store the update's beat (start or end).
   */
  private long beat;

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
   * Whether or not this update has been paired with another update yet.
   */
  private boolean used;

  /**
   * Make a new update.
   * @param beat The start/end beat.
   * @param instr The instrument to play.
   * @param key The pitch/octave at which to play the note.
   * @param vol The volume to play at.
   */
  Update(long beat, int instr, int key, int vol) {
    this.beat = beat;
    this.instrument = instr;
    this.key = key;
    this.velocity = vol;
    this.used = false;
  }

  /**
   * @return The update's beat.
   */
  long getBeat() {
    return this.beat;
  }

  /**
   * @return The instrument to play.
   */
  int getInstrument() {
    return this.instrument;
  }

  /**
   * @return The key at which to play the note.
   */
  int getKey() {
    return this.key;
  }

  /**
   * @return The note's velocity/volume.
   */
  int getVelocity() {
    return this.velocity;
  }

  /**
   * Mark this {@code Update} as used.
   */
  void use() {
    this.used = true;
  }

  /**
   * Determine if this {@code Update} has been used yet or not.
   * @return <b>True</b> <i>IFF</i> this {@code Update} has been used, <b>false</b> otherwise.
   */
  boolean hasBeenUsed() {
    return this.used;
  }

  /**
   * @return The string representation of the update.
   */
  @Override
  public String toString() {
    return "@" + this.beat + " Channel: " + this.instrument
            + ", key=" + this.key + " velocity: " + this.velocity;
  }

  /**
   * Determines if the given object is the same as this {@code Update}.
   * @param o The object to check against this update.
   * @return <b>True</b> <i>IFF</i> {@code o} is an {@code Upate} and {@code o} has the same beat, key,
   * and instrument as this {@code Update}, <b>false</b> otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof Update)) {
      return false;
    } else {
      // cast o into an Update object, compare fields
      Update u = (Update) o;

      return this.beat == u.beat && this.key == u.key && this.instrument == u.instrument;
    }
  }

  /**
   * Hashes this {@code Update} object to a unique identifier.
   * @return A unique identifier (as an int) for this {@code Update} object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.beat, this.key, this.instrument);
  }

  /**
   * Compares the given {@code Update} object to this {@code Update} object.
   * @param other The {@code Update} object to compare to.
   * @return <b>-1</b> if the given {@code Update} comes &qt;before&qt; this {@code Update},
   *  <b>0</b> if the given {@code Update} object is &qt;equal&qt; to this {@code Update} object,
   *  and <b>1</b> if the given {@code Update} object comes &qt;after&qt; this {@code Update} object.
   */
  @Override
  public int compareTo(Update other) {
    // if the two update objects are the same, 0
    if (this.equals(other)) {
      return 0;
    } else { // not the same data, compare fields
      // compare beats
      if (this.beat < other.beat) {
        return -1;
      } else if (this.beat > other.beat) {
        return 1;
      } else {
        // compare keys
        if (this.key < other.key) {
          return -1;
        } else if (this.key > other.key) {
          return 1;
        } else {
          // compare channels
          if (this.instrument < other.instrument) {
            return -1;
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