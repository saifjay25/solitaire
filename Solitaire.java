package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() { 
		CardNode prev=deckRear;
		CardNode ptr=deckRear.next;
		CardNode ptr2=ptr.next;
		while(ptr.cardValue!=27){
			prev=ptr;
			ptr=ptr2;
			ptr2=ptr2.next;
			if (ptr.cardValue==27){
				prev.next=ptr.next;
				ptr.next=ptr2.next;
				ptr2.next=ptr;
			}
		}
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		CardNode prev=deckRear;
		CardNode ptr=deckRear.next;
		CardNode ptr2=ptr.next.next;
		while(ptr.cardValue!=28){
			prev=ptr;
			ptr=ptr.next;
			ptr2=ptr2.next;
			if (ptr.cardValue==28){
				prev.next=ptr.next;
				ptr.next=ptr2.next;
				ptr2.next=ptr;
			}
		}
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		CardNode prev=deckRear;
		CardNode prev2=deckRear;
		CardNode front=deckRear.next;
		CardNode ptr=deckRear.next;
		CardNode ptr2=deckRear.next;
		if (front.cardValue==27||front.cardValue==28){ //if the front is equal to 27 or 28
			if (deckRear.cardValue==28||deckRear.cardValue==27){
				return;
			}
			while (ptr.next.cardValue!=28 && ptr.next.cardValue!=27){
				prev=ptr;
				ptr=ptr.next;
				if (ptr.next.cardValue==28||ptr.next.cardValue==27){
					prev=ptr;
					ptr=ptr.next;
					prev.next=ptr.next;
					ptr.next=deckRear.next;
					deckRear.next=ptr;
					deckRear=ptr;
					break;
				}
			}
		}
		else if (deckRear.cardValue==28||deckRear.cardValue==27){ //if the rear is equal to 28 or 27
			if (front.cardValue==27||front.cardValue==28){
				return;
			}
			while (ptr.next.cardValue!=28 && ptr.next.cardValue!=27){
				prev=ptr;
				ptr=ptr.next;
				if (ptr.next.cardValue==28||ptr.next.cardValue==27){
					prev=ptr;
					ptr=ptr.next;
					prev.next=ptr.next;
					ptr.next=front;
					deckRear.next=ptr;
					front=ptr;
					break;
				}
			}
		}
		else{													//if 27 or 28 is somewhere in the middle
			while (ptr.cardValue!=27&&ptr.cardValue!=28){
				prev=ptr;
				ptr=ptr.next;
				if (ptr.cardValue==27||ptr.cardValue==28){
					prev2=ptr.next;
					ptr2=ptr.next.next;
					break;
				}
			}
			while(prev2.cardValue!=27&&prev2.cardValue!=28){
				prev2=ptr2;
				ptr2=ptr2.next;
				if (prev.cardValue==27||prev2.cardValue==28){
					deckRear.next=ptr;
					prev.next=ptr2;
					prev2.next=front;
					deckRear=prev;
					front=ptr2;
					break;
				}
			}
		}
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {
		CardNode front= deckRear.next;
		int count=1;
		CardNode countend=deckRear.next;
		CardNode ptr=front.next;
		CardNode rear= deckRear.next;
		int value=deckRear.cardValue;
		if (deckRear.cardValue==28||deckRear.cardValue==27){
			return;
		}
		while (rear.next!=deckRear){
			rear=rear.next;
		}
		while(count<value){
			countend=ptr;
			ptr=ptr.next;
			count++;
		}
		deckRear.next=ptr;
		rear.next=front;
		countend.next=deckRear;
		front=ptr;
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		jokerA();
		jokerB();
		tripleCut();
		countCut();
		CardNode ptr=deckRear.next;
		int count=1;
		int value=deckRear.next.cardValue;
		if (value==28){
			value=27;
		}
		while (count<value){
			ptr=ptr.next;
			count++;
		}
		int key=ptr.cardValue;
		if (key==27||key==28){
			getKey();
		}
	    return key;
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		String encrypt="";
		int i=message.length();
		int j=0;
		while (j<i){
			char ch=message.charAt(j);
			Character.toUpperCase(ch);
			if (isUpperCase(ch)==true){
				int c=0;
				int d=getNumericValue(ch, c);
				int key= getKey();
				int total=key+d;
				if (total>26){
					total=total-26;
				}
				char letter=getCharacterValue(total);
				encrypt+=letter;
			}
			j++;
		}
	    return encrypt;
	}
	private char getCharacterValue(int total) {
		char letter = 0;
		if (total==1){letter='A';}
		if (total==2){letter='B';}
		if (total==3){letter='C';}
		if (total==4){letter='D';}
		if (total==5){letter='E';}
		if (total==6){letter='F';}
		if (total==7){letter='G';}
		if (total==8){letter='H';}
		if (total==9){letter='I';}
		if (total==10){letter='J';}
		if (total==11){letter='K';}
		if (total==12){letter='L';}
		if (total==13){letter='M';}
		if (total==14){letter='N';}
		if (total==15){letter='O';}
		if (total==16){letter='P';}
		if (total==17){letter='Q';}
		if (total==18){letter='R';}
		if (total==19){letter='S';}
		if (total==20){letter='T';}
		if (total==21){letter='U';}
		if (total==22){letter='V';}
		if (total==23){letter='W';}
		if (total==24){letter='X';}
		if (total==25){letter='Y';}
		if (total==26){letter='Z';}
		return letter;
	}

	private boolean isUpperCase(char ch) {
		if (ch>='A'&&ch<='z'){
			return true;
		}
		return false;
	}

	private int getNumericValue(char ch,int c) {
		if (ch=='A'||ch=='a'){c=1;}
		if (ch=='B'||ch=='b'){c=2;}
		if (ch=='C'||ch=='c'){c=3;}
		if (ch=='D'||ch=='d'){c=4;}
		if (ch=='E'||ch=='e'){c=5;}
		if (ch=='F'||ch=='f'){c=6;}
		if (ch=='G'||ch=='g'){c=7;}
		if (ch=='H'||ch=='h'){c=8;}
		if (ch=='I'||ch=='i'){c=9;}
		if (ch=='J'||ch=='j'){c=10;}
		if (ch=='K'||ch=='k'){c=11;}
		if (ch=='L'||ch=='l'){c=12;}
		if (ch=='M'||ch=='m'){c=13;}
		if (ch=='N'||ch=='n'){c=14;}
		if (ch=='O'||ch=='o'){c=15;}
		if (ch=='P'||ch=='p'){c=16;}
		if (ch=='Q'||ch=='q'){c=17;}
		if (ch=='R'||ch=='r'){c=18;}
		if (ch=='S'||ch=='s'){c=19;}
		if (ch=='T'||ch=='t'){c=20;}
		if (ch=='U'||ch=='u'){c=21;}
		if (ch=='V'||ch=='v'){c=22;}
		if (ch=='W'||ch=='w'){c=23;}
		if (ch=='X'||ch=='x'){c=24;}
		if (ch=='Y'||ch=='y'){c=25;}
		if (ch=='Z'||ch=='z'){c=26;}
		return c;
	}

	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {
		String decrypt="";
		int i=message.length();
		int j=0;
		while (j<i){
			char ch=message.charAt(j);
			if (isUpperCase(ch)==true){
				int c=0;
				int d=getNumericValue(ch, c);
				int key= getKey();
				int total=d-key;
				if (d<=key){
					total=(26+d)-key;
				}
				char letter=getCharacterValue(total);
				decrypt+=letter;
			}
			j++;
		}
	    return decrypt;
	}
}
