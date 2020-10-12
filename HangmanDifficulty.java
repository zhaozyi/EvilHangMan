/**
 * This enumerated type represents the difficulty levels for 
 * Evil Hangman.
 * <br>
 * <br>EASY alternates between the hardest word and the second hardest word.
 * <br>MEDIUM picks the hardest word for three rounds, then the second hardest word,
 * then the hardest word for three rounds, then the second hardest word, and so forth.
 * HARD always picks the hardest word.
 * @author scottm
 *
 */
public enum HangmanDifficulty {
    EASY, MEDIUM, HARD; 
    
    /**
     * Get the lowest possible int (ordinal value) for this Enum using 1 (not zero) based
     * indexing.
     * @return the lowest possible ordinal for this Enum using 1 based indexing.
     */
    public static int minPossible() {
        return EASY.ordinal() + 1;
    }
    
    /**
     * Get the highest possible int (ordinal value) for this Enum using 1 (not zero) based
     * indexing.
     * @return the highest possible ordinal for this Enum using 1 based indexing.
     */
    public static int maxPossible() {
        return HARD.ordinal() + 1;
    }
}
