/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;
import java.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
    
    public static void main(String[] args) {
        new Yahtzee().start(args);
    }
    
    public void run() {
        IODialog dialog = getDialog();
        nPlayers = dialog.readInt("Enter number of players");
	if(nPlayers > MAX_PLAYERS) {
		nPlayers = dialog.readInt("The maximum number of players is " 
					  + MAX_PLAYERS + ". Try again");
	}
        playerNames = new String[nPlayers];
        for (int i = 1; i <= nPlayers; i++) {
            playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
        }
        display = new YahtzeeDisplay(getGCanvas(), playerNames);
	individualScores = new int[nPlayers + 1][N_CATEGORIES + 1];
	usedCategories = new int[nPlayers + 1][N_CATEGORIES + 1];
        playGame();
    }

    private void playGame() {
        for(int i = 0; i < N_SCORING_CATEGORIES; i++) {
		for(int j = 1; j <= nPlayers; j++) {
			rollDice(j);
			rollDiceAgain(j);
			rollDiceAgain(j);
			display.printMessage("Select a category for this roll.");
			category = display.waitForPlayerToSelectCategory();
			// checks whether category is already chosen
			if(usedCategories[j][category] == 0) {
				categoryScore(j, category);
			} else {
				display.printMessage("Please select an empty category.");
				category = display.waitForPlayerToSelectCategory();
				categoryScore(j, category);
			}
			total(j);
		}
	}
	winner();
    }

    // rolls the dice if player clicks 'Roll Dice' 
    private void rollDice(int player) {
	display.printMessage(playerNames[player - 1] + "'s turn! Click " 
			     + "\"Roll Dice\" " + "button to roll the dice.");
	for(int i = 0; i < N_DICE; i++) {
		int dieResult = rgen.nextInt(1,6);
		rollDice[i] = dieResult;
	}
	display.waitForPlayerToClickRoll(player);
	display.displayDice(rollDice);	
    }

    // rolls the dice for the second and third time
    private void rollDiceAgain(int player) {
	display.printMessage("Select the dice you wish to re-roll and click " 
			     + "\"Roll again\".");
	display.waitForPlayerToSelectDice();
	for(int i = 0; i < N_DICE; i++) {
		if(display.isDieSelected(i) == true) {
			int dieResult = rgen.nextInt(1,6);
			rollDice[i] = dieResult;
		}
	}
	display.displayDice(rollDice);	
    }

    // checks whether dice match selected category
    private boolean categoryCheck(int[] dice, int category) {
	boolean checker = false;
	if(category >= ONES && category <= SIXES || category == CHANCE) {
		checker = true;
	} else {
		ArrayList<Integer> ones = new ArrayList<Integer>();
		ArrayList<Integer> twos = new ArrayList<Integer>();
		ArrayList<Integer> threes = new ArrayList<Integer>();	
		ArrayList<Integer> fours = new ArrayList<Integer>();
		ArrayList<Integer> fives = new ArrayList<Integer>();	
		ArrayList<Integer> sixes = new ArrayList<Integer>();
		for(int i = 0; i < N_DICE; i++) {
			if(dice[i] == 1) {
				ones.add(1);
			}
			if(dice[i] == 2) {
				twos.add(1);
			}
			if(dice[i] == 3) {
				threes.add(1);
			}
			if(dice[i] == 4) {
				fours.add(1);
			}
			if(dice[i] == 5) {
				fives.add(1);
			}
			if(dice[i] == 6) {	
				sixes.add(1);
			}
		}
		if(category == THREE_OF_A_KIND) {
			if(ones.size() >= 3 || twos.size() >= 3 
			   || threes.size() >= 3 || fours.size() >= 3 
			   || fives.size() >= 3 || sixes.size() >= 3) {
				checker = true;
			}
		}
		if(category == FOUR_OF_A_KIND) {
			if(ones.size() >= 4 || twos.size() >= 4 
			   || threes.size() >= 4 || fours.size() >= 4
			   || fives.size() >= 4 || sixes.size() >= 4) {
				checker = true;
			} 
		}
		if(category == FULL_HOUSE) {
			if(ones.size() == 2 || twos.size() == 2 
			   || threes.size() == 2 || fours.size() == 2
			   || fives.size() == 2 || sixes.size() == 2) {
				if(ones.size() == 3 || twos.size() == 3 
				   || threes.size() == 3 || fours.size() == 3
				   || fives.size() == 3 || sixes.size() == 3) {
					checker = true;
				} 
			}
		}
		if(category == SMALL_STRAIGHT) {
			if(ones.size() >= 1 && twos.size() >= 1 
			   && threes.size() >= 1 && fours.size() >= 1) {
				checker = true;
			}
			if(twos.size() >= 1 && threes.size() >= 1 
			   && fours.size() >= 1 && fives.size() >= 1) {
				checker = true;
			}
			if(threes.size() >= 1 && fours.size() >=1 
			   && fives.size() >= 1 && sixes.size() >= 1) {
				checker = true;
			}
		}
		if(category == LARGE_STRAIGHT) {
			if(ones.size() >= 1 && twos.size() >= 1 && threes.size() >= 1
			   && fours.size() >= 1 && fives.size() >= 1) {
				checker = true;
			}
			if(twos.size() >= 1 && threes.size() >= 1 && fours.size() >= 1
			   && fives.size() >= 1 && sixes.size() >= 1) {
				checker = true;
			}	
		}
		if(category == YAHTZEE) {
			if(ones.size() == 1 && twos.size() == 1 && threes.size() == 1
			   && fours.size() == 1 && fives.size() == 1 
			   && sixes.size() == 1) {
				checker = true;
			}
		}
	}
	return checker;		
    } 

    // calculates and updates score for selected category
    private void categoryScore(int player, int category) {
	int score = 0;
	if(categoryCheck(rollDice, category) == true) {
		if(category >= ONES && category <= SIXES) {				
			for(int i = 0; i < N_DICE; i++){
				if(rollDice[i] == category) {
					score += category;
				} 
			}
			display.updateScorecard(category, player, score);
		}
		if(category == THREE_OF_A_KIND || category == FOUR_OF_A_KIND 
		   || category == CHANCE) {
			for(int i = 0; i < N_DICE; i++) {
				score += rollDice[i];
				display.updateScorecard(category, player, score);
			}
		}
		if(category == FULL_HOUSE) {
			score = 25;
			display.updateScorecard(category, player, score);
		}
		if(category == SMALL_STRAIGHT) {
			score = 30;
			display.updateScorecard(category, player, score);
		}
		if(category == LARGE_STRAIGHT) {
			score = 40;
			display.updateScorecard(category, player, score);
		}
		if(category == YAHTZEE) {
			score = 50;
			display.updateScorecard(category, player, score);
		}
		individualScores[player][category] = score;
	} else {
		display.updateScorecard(category, player, 0);
		individualScores[player][category] = 0;
	}
	usedCategories[player][category] = 1;
    }

    // calculates and updates the total score
    private void total(int player) {
	int upperScore = 0;
	int lowerScore = 0;
	int totalScore = 0;
	for(int i = ONES; i <= SIXES; i++) {
		if(individualScores[player][i] != 0) {
			upperScore += individualScores[player][i];
		}
	}
	for(int i = THREE_OF_A_KIND; i <= CHANCE; i++) {
		if(individualScores[player][i] != 0) {
			lowerScore += individualScores[player][i];
		}
	}
	totalScore = upperScore + lowerScore;
	individualScores[player][UPPER_SCORE] = upperScore;
	individualScores[player][LOWER_SCORE] = lowerScore;
	individualScores[player][TOTAL] = totalScore; 
	display.updateScorecard(TOTAL, player, totalScore);
    }	

    // calculates the winner of the game
    private void winner() {
	for(int i = 1; i <= nPlayers; i++) {
		display.updateScorecard(UPPER_SCORE, i, 
		                        individualScores[i][UPPER_SCORE]);
		if(individualScores[i][UPPER_SCORE] >= 63) {
			individualScores[i][UPPER_BONUS] = 35;
		} else {
			individualScores[i][UPPER_BONUS] = 0;
		}
		display.updateScorecard(UPPER_BONUS, i,
				        individualScores[i][UPPER_BONUS]);
		display.updateScorecard(LOWER_SCORE, i, 
					individualScores[i][LOWER_SCORE]);
		total(i);
	}
	int bestScore = 0;
	int nWinner = 0;
	for(int i = 1; i <= nPlayers; i++) {
		int totalScore = individualScores[i][TOTAL];
		if(totalScore > bestScore) {
			bestScore = totalScore;
			nWinner = i - 1;
		}
	}
	display.printMessage("Congratulations, " + playerNames[nWinner] 
                             + ", you're the winner with a total score of " 
			     + bestScore + "!");
    } 

    // Private instance variables
    private int nPlayers;
    private String[] playerNames;
    private YahtzeeDisplay display;
    private RandomGenerator rgen = new RandomGenerator();
    // stores values of the dice
    private int[] rollDice = new int[N_DICE];
    // stores individual scores for each category
    private int[][] individualScores;
    private int category;
    // stores individual categories that are taken
    private int[][] usedCategories; 

}
