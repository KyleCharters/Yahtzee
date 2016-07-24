package com.github.kylecharters;

import java.util.HashSet;

/**
 * Utility class for determining the score in each section of a roll
 * 
 * @author Kyle
 *
 */
public class Rolls{
	
	/**
	 * Calculate the score of one of the upper values
	 * @param array Array of dice rolls
	 * @param value Which scoring area in upper values
	 * @return The score
	 */
	public static int upper(int[] array, int value){
		//Counts the amount of a single dice
		return countSingle(array, value) * value;
	}
	
	/**
	 * Checks to see if a roll has multiple values (Three and Four of a kind)
	 * @param array Array of dice values
	 * @param minimum The minimum of the amount of any dice
	 * @return The score of the "of kind" score
	 */
	public static int ofKind(int[] array, int minimum){
		//Check if there is a minimum of any dice
		for(int amount : count(array))
			if(amount >= minimum)
				return sum(array);
		return 0;
	}
	
	/**
	 * Checks to see if a roll is a full house
	 * @param array Array of dice values
	 * @return The score of the full house
	 */
	public static int fullHouse(int[] array){
		//Check to see if there is both a count of three and two
		boolean three = false, two = false;
		for(int amount : count(array)){
			if(amount == 2){
				two = true;
			}else if(amount == 3){
				three = true;
			}
		}
		if(two && three)
			return 25;
		return 0;
	}
	
	/**
	 * Checks to see if a roll is a straight
	 * @param array Array of dice values
	 * @param large True if Large Straight, false if Small
	 * @return The score of the straight
	 */
	public static int straight(int[] array, boolean large){
		//Turn the array into a set, this removes duplicates and sorts the values
		HashSet<Integer> set = new HashSet<Integer>();
		for(int value : array) set.add(value);
		//If the array isn't large enough for the straight, return 0
		if(set.size() < (large ? 5 : 4)) return 0;
		//Turn the set back into the array for iteration
		array = new int[set.size()];
		int i = 0;
		for(Integer value : set) array[i++] = value;
		
		//Keep track of largest streak and current streak
		int largestStreak = 1, streak = 1;
		
		for(i = 1; i < set.size() ; i++){
			//Check if last and current number is consecutive
			if(array[i - 1] == array[i] - 1){
				streak += 1;
			}else{
				//Record the largest streak
				if(largestStreak < streak) largestStreak = streak;
				streak = 1;
			}
		}
		//Set largest streak if it did not encounter a non-consecutive number
		if(largestStreak < streak) largestStreak = streak;
		
		if(large){
			return largestStreak == 5 ? 40 : 0;
		}else{
			return largestStreak >= 4 ? 30 : 0;
		}
	}
	
	/**
	 * Checks if a roll is a yahtzee
	 * @param array Array of dice values
	 * @return The score of the yahtzee
	 */
	public static int yahtzee(int[] array){
		//Check any dice count is 5
		for(int amount : count(array))
			if(amount == 5)
				return 50;
		return 0;
	}
	
	/**
	 * Adds up all values in the array
	 * @param array Array of integers
	 * @return The sum of all values
	 */
	public static int sum(int[] array){
		int count = 0;
		for(int entry : array)
			count += entry;
		
		return count;
	}
	
	/**
	 * Counts the amount of a single value
	 * @param array Array of integers
	 * @param value Which value to count
	 * @return
	 */
	public static int countSingle(int[] array, int value){
		int count = 0;
		for(int entry : array)
			if(entry == value)
				count++;
		
		return count;
	}
	
	/**
	 * Counts the values in an array
	 * @param array Array of integers, maximum 5 values
	 * @return An array of the count of all integers
	 */
	public static int[] count(int[] array){
		int[] counts = new int[6];
		for(int i = 0; i < 5; i++){
			counts[array[i] - 1] += 1;
		}
		return counts;
	}
}
