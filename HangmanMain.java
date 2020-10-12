import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *  Class HangmanMain is the driver program for the Hangman program.  It reads 
 *  a dictionary of words to be used during the game and then plays a game with
 *  the user.
 *   
 *   <br><br>This is a cheating version of hangman that delays picking a word
 *   to keep its options open.  You can change the setting for DEBUG to see
 *   how many options are still left on each turn and what patterns are
 *   being generated from the guess
 *   
 *   Based on a program by Stuart Reges, modified my Mike Scott.
 */


public class HangmanMain  {

    /* Name of the dictionary file. 
       change to dictionary.txt for full version of game. */
    private static final String DICTIONARY_FILE = "smallDictionary.txt";
    // Used to tell HangmanManager if it should output debugging information.
    private static final boolean DEBUG = true;  
    private static final int MAX_GUESSES = 25;

	// Run the game with a human user.
    public static void main(String[] args) {
        System.out.println("Welcome to the CS314 hangman game.");
        System.out.println();

        // read in the dictionary and create the Hangman manager
        Set<String> dictionary = getDictionary();
        HangmanManager hangman = new HangmanManager(dictionary, DEBUG);
        if (DEBUG) {
            showWordCounts(hangman);
        }

        Scanner keyboard = new Scanner(System.in);
        // play games until user wants to quit
        do {
            setGameParameters(hangman, keyboard);
            playGame(keyboard, hangman);
            showResults(hangman);
        } while(playAgain(keyboard));
        keyboard.close();
    }


    /**
     * Check to see if the user wants to play another game.
     * @param keyboard We assume the Scanner is connected to standard input
     * @return true if the user wants to play another game, false otherwise.
     */
    private static boolean playAgain(Scanner keyboard) {
        System.out.println();
        System.out.print("Another game? Enter y for another game, "
                + "anything else to quit: ");
        String answer = keyboard.nextLine();
        return answer.length() > 0 && answer.toLowerCase().charAt(0) == 'y';
    }


    /*
     * Get user choices for the current game of Hangman.
     * pre: hangman != null and initialized with correct dictionary,
     * keyboard connect to standard input
     */
    private static void setGameParameters(HangmanManager hangman,
            Scanner keyboard) {
        int wordLength = 0;
        
        do {
            System.out.print("What length word do you want to use? ");
            wordLength = Integer.parseInt(keyboard.nextLine());
        } while(!atLeastOneWord(hangman, wordLength));

        // determine number of wrong guesses
        int numGuesses = 0;
        do {
            System.out.print("How many wrong answers allowed? ");
            numGuesses = Integer.parseInt(keyboard.nextLine());
        } while(!validChoice(numGuesses, 1, MAX_GUESSES, 
                "number of wrong guesses"));

        HangmanDifficulty difficulty = getDifficulty(keyboard);
        hangman.prepForRound(wordLength, numGuesses, difficulty);
    }

    // determine difficulty level from user. They must enter a valid choice.
    // pre: keyboard != null
    private static HangmanDifficulty getDifficulty(Scanner keyboard) {
        int diffChoiceAsInt = HangmanDifficulty.EASY.ordinal();
        do {
            System.out.println("What difficulty level do you want?");
            // we number difficulties 1 to 3 for user
            System.out.print("Enter a number between " 
                    + (HangmanDifficulty.EASY.ordinal() + 1) 
                    + "(EASIEST) " + "and " 
                    + (HangmanDifficulty.HARD.ordinal() + 1) 
                    + "(HARDEST) : ");
            diffChoiceAsInt = Integer.parseInt(keyboard.nextLine());
            
        } while(!validChoice(diffChoiceAsInt, HangmanDifficulty.minPossible(), 
                HangmanDifficulty.maxPossible(), "difficulty"));
        
        return HangmanDifficulty.values()[diffChoiceAsInt - 1];    
    }

    // Determine if choice is within the range [min, max]
    private static boolean validChoice(int choice, int min, int max, 
    		String explanation) {
    		
        boolean valid = (min <= choice) && (choice <= max);
        if (!valid) {
            System.out.println(choice + " is not a valid number for " 
            		+ explanation);
            System.out.println("Pick a number between " + min + " and " 
            		+ max + ".");
        }
        return valid;
    }


    // check to ensure there is at least one word of 
    // the given length in the manager
    private static boolean atLeastOneWord(HangmanManager hangman, 
    		int wordLength) {
    		
        int numWords = hangman.numWords(wordLength);
        if (numWords == 0) {
            System.out.println();
            System.out.println("I don't know any words with " 
                    + wordLength + " letters. Enter another number.");
        }
        return numWords != 0;
    }


    // open the dictionary file. Return a list containing
    // the words in the dictionary file.
    // If the dictionary file is not found the program ends
    private static Set<String> getDictionary() {
        Set<String> dictionary = new TreeSet<>();
        try {
            Scanner input = new Scanner(new File(DICTIONARY_FILE));

            while (input.hasNext())
                dictionary.add(input.next().toLowerCase());
            input.close();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find this file: " + DICTIONARY_FILE);
            System.out.print("Program running in this directory: ");
            System.out.println(System.getProperty("user.dir"));
            System.out.println("Be sure the dictionary file is in "
                    + "that directory");
            System.out.println("Exiting");
            System.exit(-1);
        }
        return Collections.unmodifiableSet(dictionary);
    }


    // Plays one game with the user
    private static void playGame(Scanner keyboard, HangmanManager hangman) {
        // keep asking for guesses as long as 
        // user has guesses left and puzzle not solved the puzzle
        final String UNKNOWN = "-";
        while (hangman.getGuessesLeft() > 0 
        		&& hangman.getPattern().contains(UNKNOWN)) {
        		
            System.out.println("guesses left: " + hangman.getGuessesLeft());

            // debugging
            if (DEBUG) {
                System.out.println("DEBUGGING: words left : " 
                		+ hangman.numWordsCurrent());
            }
            System.out.println("guessed so far : " + hangman.getGuessesMade());
            System.out.println("current word : " + hangman.getPattern());
            char guess = getLetter(keyboard, hangman);
            TreeMap<String, Integer> results = hangman.makeGuess(guess);
            if (DEBUG) {
                showPatterns(results);
            }
            showResultOfGuess(hangman, guess);
        }
    }

    // shows the result of the user guess
    private static void showResultOfGuess(HangmanManager hangman, char guess) {
        int count = getCount(hangman.getPattern(), guess);
        if (count == 0) {
            System.out.println("Sorry, there are no " + guess + "'s");
        } else if (count == 1) {
            System.out.println("Yes, there is one " + guess);
        } else {
            System.out.println("Yes, there are " + count + " " + guess + "'s");
        }
        System.out.println();   
    }


    // pre, keyboard != null, hangman != null
    private static char getLetter(Scanner keyboard, HangmanManager manager) {
        if (keyboard == null || manager == null) {
            throw new IllegalArgumentException("Parameters to method "
                    + "may not be null.");
        }
        boolean alreadyGuessed = true;
        char guess = ' ';
        while (alreadyGuessed) {
            System.out.print("Your guess? ");
            String result = keyboard.nextLine().toLowerCase();
            while (result == null || result.length() == 0 
                    || !isEnglishLetter(result.charAt(0))) {
                
                System.out.println("That is not an English letter.");
                System.out.print("Your guess? ");
                result = keyboard.nextLine().toLowerCase();
            }
            guess = result.charAt(0);
            alreadyGuessed = manager.alreadyGuessed(guess);
            if (manager.alreadyGuessed(guess)) {
                System.out.println("You already guessed that! "
                        + "Pick a new letter please.");
            }
        }
        System.out.println("the guess: " + guess + ".");
        assert isEnglishLetter(guess) && !manager.alreadyGuessed(guess) 
            : "something wrong with my logic in getting guess. " + guess;
        return guess;
    }

    // return true if ch is an English letter, A-Z or a-z
    private static boolean isEnglishLetter(char ch) {
        return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z');
    }


    // debugging method to show current patterns and number of words for each 
    // pre: results != null
    private static void showPatterns(TreeMap<String, Integer> results) {
        if (results == null) {
            throw new IllegalArgumentException("The map may not be null.");
        }
        System.out.println();
        System.out.println("DEBUGGING: Based on guess here "
                + "are resulting patterns and number");
        System.out.println("of words in each pattern: ");
        for (String key : results.keySet()) {
            System.out.println("pattern: " + key 
                    + ", number of words: " + results.get(key));
        }
        System.out.println("END DEBUGGING");
        System.out.println();
    }


    // pre: pattern != null
    // return the number of times the guess occurs in the pattern
    private static int getCount(String pattern, char guess) {
        if (pattern == null ) {
            throw new IllegalArgumentException("Violation of "
                    + "precondition in getCount.");
        }
        int result = 0;
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == guess) {
                result++;
            }
        }
        return result;
    }


    // reports the results of the game, including showing the answer
    private static void showResults(HangmanManager hangman) {
        // if the game is over, get the secret word
        String answer = hangman.getSecretWord();
        System.out.println("answer = " + answer);
        if (hangman.getGuessesLeft() > 0) {
            System.out.println("You beat me");
        } else {
            System.out.println("Sorry, you lose");
        }
    }


    // helper method for debugging. Display number of words of length
    // dictionary.txt has words from length 2 to 25
    private static void showWordCounts(HangmanManager hangman) {
        // why 25? Should this vary with the dictionary??
        final int MAX_LETTERS_PER_WORD = 25;
        for (int i = 2; i < MAX_LETTERS_PER_WORD; i++) {
            System.out.println(i + " " + hangman.numWords(i));
        }
    }
}
