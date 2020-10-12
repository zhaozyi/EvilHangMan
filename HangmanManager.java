/*  Student information for assignment:
 *
 *  On my honor, Ziyi Zhao, this programming assignment is my own work
 *  and I have not provided this code to any other student.
 *
 *  Name:
 *  email address:
 *  UTEID:
 *  Section 5 digit ID: 
 *  Grader name:
 *  Number of slip days used on this assignment:
 */

// add imports as necessary

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 *
 */
public class HangmanManager {
	
	// private class that is used to sort the word/family lists from hardest to easiest
	private static class PatternSorter implements Comparable<PatternSorter> {
		private ArrayList<String> words;
		private String pattern;
		
		public PatternSorter(ArrayList<String> words, String pattern) {
			this.words = words;
			this.pattern = pattern;
		}
		
		// method that does the comparison
		public int compareTo(PatternSorter other) {
			if (words.size() == other.words.size()) {
				return pattern.compareTo(other.pattern);
			} else {
				return other.words.size() - words.size();
			}
		}
	}

    // instance vars
	private Set<String> words;
	private HangmanDifficulty diff;
	private int wordLength;
	private int numGuesses;
	private String currPattern;
	private int times;
	private ArrayList<Character> guesses;
	private ArrayList<String> activeWords;
	private ArrayList<char[]> patterns;

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) {
    	if (words == null || words.size() <= 0) {
    		throw new IllegalArgumentException();
    	}
    	this.words = new HashSet<String>();
    	for (String s : words) {
    		this.words.add(s);
    	}
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases. 
     * Debugging is off.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
    	if (words == null || words.size() <= 0) {
    		throw new IllegalArgumentException();
    	}
    	this.words = new HashSet<String>();
    	for (String s : words) {
    		this.activeWords.add(s);
    	}
    }


    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     * @param length The given length to check.
     * @return the number of words in the original Dictionary with the given length
     */
    public int numWords(int length) {
    	int num = 0;
    	for (String s : words) {
    		if (s.length() == length) {
    			num++;
    		}
    	}
        return num;
    }

    /**
     * Get for a new round of Hangman. Think of a round as a complete game of Hangman.
     * @param wordLen the length of the word to pick this time. numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the player loses the round. numGuesses >= 1
     * @param diff The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
    	// initialize
    	final char UNDISPLAYED = '-';
    	this.wordLength = wordLen;
    	this.diff = diff;
    	this.numGuesses = numGuesses;
    	this.currPattern = "";
    	this.times = 0;
    	this.patterns = new ArrayList<char[]>();
    	this.guesses = new ArrayList<Character>();
    	this.activeWords = new ArrayList<String>();
    	// initialize current pattern
    	for (int i = 0; i < wordLen; i++) {
    		currPattern += UNDISPLAYED;
    	}
    	// initialize active words and patterns
    	for (String s : this.words) {
    		if (s.length() == wordLen) {
    			activeWords.add(s);
        		char[] ch = new char[s.length()];
        		for (int i = 0; i < ch.length; i++) {
        			ch[i] = UNDISPLAYED;
        		}
        		this.patterns.add(ch);
    		}
    	}
    }


    /**
     * The number of words still possible (live) based on the guesses so far. Guesses will eliminate possible words.
     * @return the number of words that are still possibilities based on the original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        return this.activeWords.size();
    }


    /**
     * Get the number of wrong guesses the user has left in this round (game) of Hangman.
     * @return the number of wrong guesses the user has left in this round (game) of Hangman.
     */
    public int getGuessesLeft() {
        return this.numGuesses;
    }


    /**
     * Return a String that contains the letters the user has guessed so far during this round.
     * The String is in alphabetical order. The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     * @return a String that contains the letters the user has guessed so far during this round.
     */
    public String getGuessesMade() {
    	Collections.sort(this.guesses);
    	return this.guesses.toString();
    }


    /**
     * Check the status of a character.
     * @param guess The character to check.
     * @return true if guess has been used or guessed this round of Hangman, false otherwise.
     */
    public boolean alreadyGuessed(char guess) {
        return this.guesses.contains(guess);
    }


    /**
     * Get the current pattern. The pattern contains '-''s for unrevealed (or guessed)
     * characters and the actual character for "correctly guessed" characters.
     * @return the current pattern.
     */
    public String getPattern() {
        return this.currPattern;
    }


    // pre: !alreadyGuessed(ch)
    // post: return a tree map with the resulting patterns and the number of
    // words in each of the new patterns.
    // the return value is for testing and debugging purposes
    /**
     * Update the game status (pattern, wrong guesses, word list), based on the give
     * guess.
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     * words in each of the new patterns.
     * The return value is for testing and debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) {
    	// check precondition: alreadyGuessed(guess)
    	if (alreadyGuessed(guess)) {
    		throw new IllegalStateException("guess another letter");
    	}
    	String previous = this.currPattern;
    	this.times++;
    	// update letters that have been guessed so far
    	this.guesses.add(guess);
    	// split words into word families
    	TreeMap<String, ArrayList<String>> wordFamilies = new TreeMap<String, ArrayList<String>>();
    	updatePatterns(guess);
    	ArrayList<String> allPatterns = convertChar();
    	TreeMap<String, Integer> results = getWordsCount(wordFamilies, allPatterns);
    	// sort the wordFamilies based on difficulties
    	ArrayList<PatternSorter> wordFamiliesSorter = new ArrayList<PatternSorter>();
    	for (String s : wordFamilies.keySet()) {
    		PatternSorter ps = new PatternSorter(wordFamilies.get(s), s);
    		wordFamiliesSorter.add(ps);
    	}
    	Collections.sort(wordFamiliesSorter);
    	// update the active word list and remove irrelevant patterns
    	updateActiveWords(wordFamiliesSorter);
    	cleanPatterns();
    	// compare previous pattern and updated pattern, updating number of guesses left
    	if (previous.equals(this.currPattern)) {
    		this.numGuesses--;
    	}
        return results;
    }
    
    // change the char if the char in a word matches the guess
    private void updatePatterns(char guess) {
    	final char UNDISPLAYED = '-';
    	for (int i = 0; i < this.activeWords.size(); i++) {
    		String word = this.activeWords.get(i);
    		for (int j = 0; j < word.length(); j++) {
    			if (word.charAt(j) == guess) {
    				this.patterns.get(i)[j] = guess;
    			}
    		}
    	}
    }
    
    // remove irrelevant patterns for next guess
    private void cleanPatterns() {
    	for (int i = this.patterns.size() - 1; i >= 0; i--) {
    		char[] curr = this.patterns.get(i);
    		String s = "";
    		for (int j = 0; j < curr.length; j++) {
    			s += curr[j];
    		}
    		if (!s.equals(this.currPattern)) {
    			this.patterns.remove(i);
    		}
    	}
    }
    
    // converts array of characters in patterns into strings
    private ArrayList<String> convertChar() {
    	ArrayList<String> patterns = new ArrayList<String>();
    	for (int i = 0; i < this.patterns.size(); i++) {
    		String s = "";
    		for (int j = 0; j < this.patterns.get(i).length; j++) {
    			s += this.patterns.get(i)[j];
    		}
    		patterns.add(s);
    	}
    	return patterns;
    }
    
    // gets the number of words for each pattern(return a map)
    private TreeMap<String, Integer> getWordsCount(TreeMap<String, ArrayList<String>> wordFamilies, ArrayList<String> allPatterns) {
    	// get all the patterns first
    	for (int i = 0; i < allPatterns.size(); i++) {
    		wordFamilies.put(allPatterns.get(i), new ArrayList<String>());
    	}
    	// add the words into each pattern
    	for (String s : wordFamilies.keySet()) {
    		for (int i = 0; i < allPatterns.size(); i++) {
    			if (s.equals(allPatterns.get(i))) {
    				ArrayList<String> curr = wordFamilies.get(s);
    				curr.add(this.activeWords.get(i));
    			}
    		}
    	}
    	// get the number of words in each pattern
    	TreeMap<String, Integer> results = new TreeMap<String, Integer>();
    	for (String s : wordFamilies.keySet()) {
    		results.put(s, wordFamilies.get(s).size());
    	}
    	return results;
    }
    
    // update active words and the current pattern based on the difficulty the user enters
    private void updateActiveWords(ArrayList<PatternSorter> wordFamiliesSorter) {
    	final int onePattern = 1;
    	final int medium = 4;
    	final int easy = 2;
    	PatternSorter hardest = wordFamiliesSorter.get(0);
    	// possibilities of picking the hardest word/famliy list 
    	boolean isHardest = wordFamiliesSorter.size() == onePattern || this.diff == HangmanDifficulty.HARD ||
    			(this.diff == HangmanDifficulty.MEDIUM && times % medium != 0) || 
    			(this.diff == HangmanDifficulty.EASY && times % easy != 0);
    	if (isHardest) {
    		this.currPattern = hardest.pattern;
    		this.activeWords.clear();
    		this.activeWords.addAll(hardest.words);
    	} else { // otherwise pick the second hardest list
    		int index = 1;
    		PatternSorter secondHardest = wordFamiliesSorter.get(index);
    		this.currPattern = secondHardest.pattern;
    		this.activeWords.clear();
    		this.activeWords.addAll(secondHardest.words);
    	}
    } 

    /**
     * Return the secret word this HangmanManager finally ended up picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
    	int index = (int) (Math.random() * this.activeWords.size());
        return this.activeWords.get(index);
    }
}
