import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.Scanner;

public class CreditCardApp {
	private Scanner input = new Scanner(System.in);
	private User currentUser;

	private void addCreditCard() throws IOException {
		Integer issuanceMonth = null, issuanceYear = null, expirationMonth = null, expirationYear = null;
		CreditCardType type = null;
		LocalDate issuanceDate, expirationDate;

		// all these inputs have double input validation. the inner loop ensures that
		// they enter a number so it doesn't throw
		// a number format exception. and then the outer loop ensures they entered a
		// number that fits the criteria

		// get the issuance year
		do {
			String year;
			do {
				System.out.println("What year was this card issued");
				year = input.nextLine();
				// loop until they input a number so that when it tries to convert it to an
				// integer it doesnt throw an exception
			} while (!year.matches("[0-9]+"));
			issuanceYear = Integer.parseInt(year);
			// loop until they inputed a valid year
		} while (issuanceYear > LocalDate.now().getYear() || issuanceYear < 2000);

		// get the issuance month
		do {
			String month;
			do {
				System.out.println("What month of the year was this card issued (enter number)");
				month = input.nextLine();
			} while (!month.matches("[0-9]+"));
			issuanceMonth = Integer.parseInt(month);
		} while (issuanceMonth > 12 || issuanceMonth < 1);

		// set these values to the issuance date. it expires on the first of the month
		issuanceDate = LocalDate.of(issuanceYear, issuanceMonth, 1);

		// get the expiration year
		do {
			String year;
			do {
				System.out.println("What year does this card expire");
				year = input.nextLine();
				// loop until they input a number so that when it tries to convert it to an
				// integer it doesnt throw an exception
			} while (!year.matches("[0-9]+"));
			expirationYear = Integer.parseInt(year);
			// loop until they inputed a valid year
		} while (expirationYear > 2050);

		// get the expiration month
		do {
			String month;
			do {
				System.out.println("What month of the year does this card expire (enter number)");
				month = input.nextLine();
			} while (!month.matches("[0-9]+"));
			expirationMonth = Integer.parseInt(month);
		} while (expirationMonth > 12 || expirationMonth < 1);

		expirationDate = LocalDate.of(expirationYear, expirationMonth, 1);

		// if the expiration date comes before the issuance date, don't let them
		// continue
		if (expirationDate.compareTo(issuanceDate) < 0) {
			System.out.println(
					"You entered an expiration date that's before the issuance date.\nCan't Process your request\n");
			return;
		}

		// get the card type
		System.out.println("Enter card Type (visa, mastercard or amex)");
		do {
			switch (input.nextLine().toUpperCase()) {
			case "VISA":
				type = CreditCardType.VISA;
				break;
			case "MASTERCARD":
				type = CreditCardType.MasterCard;
				break;
			case "AMEX":
				type = CreditCardType.AMEX;
				break;
			}
		} while (type == null);// loop until they entered a valid card type

		CreditCard newCard = new CreditCard(issuanceDate, expirationDate, type);
		System.out.println("The id of your new credit card is " + newCard.getCreditCardId() + "\n");
		currentUser.addCard(newCard);
	}

	private void displayCardTransactions(String idOfCard) {
		if (currentUser.numOfTransactionsOnCard(idOfCard) == 0) {
			System.out.println("No transactions on this card\n");
			return;
		}
		System.out.println("Transactions on card " + idOfCard + ":\n" + currentUser.transactionsOnCardToString(idOfCard));
		System.out.println();
	}

	private void displayCreditLimitOfCreditCard(String idOfCard) {
		System.out.printf("The current credit limit on card with id %s  is $%,.2f%n%n", idOfCard,
				currentUser.getAvailCredit(idOfCard));
	}

	private void displayCurrentBalanceOfCreditCard(String idOfCard) {
		System.out.printf("The current balance on card with id %s  is $%,.2f%n%n", idOfCard,
				currentUser.getCardBalance(idOfCard));
	}

	/**
	 * display the largest purchase
	 * 
	 * @param u the user
	 */
	private void displayLargestPurchase() {
		// get the largest purchase. purchases are immutable so its okay that it's not
		// returning a deep copy of it
		Purchase p = currentUser.getLargestPurchase();
		// if the method returned null, it means there are no purchase
		if (p == null) {
			System.out.println("You have not made any purchases\n");
			return;
		}

		// Display this purchase
		System.out.printf("Your largest purchase was for %s, paid to %s for the amount of $%,.2f%n",
				p.getPurchaseType(), p.getVendorName(), p.getAmount());
	}

	private void displayMainMenu() {
		System.out.println("1.	Add a new CreditCard\r\n" + "2.	Remove a CreditCard\r\n"
				+ "3.	Display total outstanding balances\r\n" + "4.	Display total available credit\r\n"
				+ "5.	Display largest purchase \r\n" + "6.	Display most recent payment\r\n"
				+ "7.	Display total spent on certain category of purchase\r\n"
				+ "8.	Manage a specific Credit Card\r\n" + "9.	Exit the System\r\n");
	}

	private void displayMostRecentPayment() {
		Payment p = currentUser.getMostRecentPayment();
		if (p == null) {
			System.out.println("You have not made any payments");
			return;
		}

		System.out.printf("Youre last payment was on %s at %02d:%02d, paid via %s in the amount of $%,.2f%n%n",
				p.getTransactionDateAndTime().toLocalDate(), p.getTransactionDateAndTime().toLocalTime().getHour(),
				p.getTransactionDateAndTime().toLocalTime().getMinute(), p.getPaymentType(), p.getAmount());

	}

	private void displayMostRecentPurchase(String idOfCard) {
		// get the most recent purchase for the card with this idOfCard. note that
		// purchases
		// are immutable so
		// its okay that it's returning a purchase object
		Purchase p = currentUser.mostRecentPurchaseOnCard(idOfCard);
		// if there is no purchases, just return
		if (p == null) {
			System.out.println("You have no purchases on this card\n");
			return;
		}
		// display the details of the most recent purchase
		System.out.printf("Your most recent purchase was in the amount of $%,.2f from %s for %s on %s%n%n",
				p.getAmount(), p.getVendorName(), p.getPurchaseType().toString().toLowerCase(),
				p.transactionDateAndTime.toLocalDate());
	}

	private void displayTotalSpentOnCertainCategoryOfPurchase() {
		System.out.println("Which purchase type do you want to look up?");
		String response;
		do {
			System.out.println(
					"Press\n1 for car\n2 for clothing\n3 for groceries\n4 for lodging\n5 for restaurant\n6 for Travel\n7 for utilities\n8 for food");
			response = input.nextLine();
		} while (!response.matches("[1-8]"));

		PurchaseType typeLookingUp;
		switch (Integer.parseInt(response)) {
		case 1:
			typeLookingUp = PurchaseType.CAR;
			break;
		case 2:
			typeLookingUp = PurchaseType.CLOTHING;
			break;
		case 3:
			typeLookingUp = PurchaseType.GROCERIES;
			break;
		case 4:
			typeLookingUp = PurchaseType.LODGING;
			break;
		case 5:
			typeLookingUp = PurchaseType.RESTAURANT;
			break;
		case 6:
			typeLookingUp = PurchaseType.TRAVEL;
			break;
		case 7:
			typeLookingUp = PurchaseType.UTILITIES;
			break;
		case 8:
			typeLookingUp = PurchaseType.FOOD;
			break;
		default:
			typeLookingUp = null;

		}
		double totalSpent = currentUser.getTotalSpentOnCertainCategoryOfExpense(typeLookingUp);
		System.out.printf("You've spent a toal of $%,.2f on %s%n%n", totalSpent,
				typeLookingUp.toString().toLowerCase());
	}

	private PurchaseType getPurchaseType() {
		String choice;
		do {
			System.out.println("What are you paying for?\n"
					+ "Choose from car, clothing, food, groceries, lodging, restaurant, travel, or utilities: ");
			choice = input.nextLine().toUpperCase();
		} while (!choice.equals("CAR") && !choice.equals("CLOTHING") && !choice.equals("FOOD")
				&& !choice.equals("GROCERIES") && !choice.equals("LODGING") && !choice.equals("RESTAURANT")
				&& !choice.equals("TRAVEL") && !choice.equals("UTILITIES"));
		return PurchaseType.valueOf(choice);

	}

	private Vendor getVendor() {
		// get the vendor name
		System.out.println("We need to know who this purchase is to");
		System.out.println("Enter vendor's name: ");
		String name = input.next();
		input.nextLine();

		// get the street address
		System.out.println("Enter their street address: ");
		String street = input.next();
		input.nextLine();

		// get the name of the city
		System.out.println("Enter their city: ");
		String city = input.next();
		input.nextLine();

		// get the state..make sure its a correct state enum
		String stateAsString;
		USState state;
		do {
			System.out.println("Enter state abbreviation: ");
			stateAsString = input.next().toUpperCase();
		} while (!stateAsString.equals("AK") && !stateAsString.equals("AL") && !stateAsString.equals("AR")
				&& !stateAsString.equals("AS") && !stateAsString.equals("AZ") && !stateAsString.equals("CA")
				&& !stateAsString.equals("CO") && !stateAsString.equals("CT") && !stateAsString.equals("DC")
				&& !stateAsString.equals("DE") && !stateAsString.equals("FL") && !stateAsString.equals("GA")
				&& !stateAsString.equals("GU") && !stateAsString.equals("HI") && !stateAsString.equals("IA")
				&& !stateAsString.equals("ID") && !stateAsString.equals("IL") && !stateAsString.equals("IN")
				&& !stateAsString.equals("KS") && !stateAsString.equals("KY") && !stateAsString.equals("LA")
				&& !stateAsString.equals("MA") && !stateAsString.equals("MD") && !stateAsString.equals("ME")
				&& !stateAsString.equals("MI") && !stateAsString.equals("MN") && !stateAsString.equals("MO")
				&& !stateAsString.equals("MP") && !stateAsString.equals("MS") && !stateAsString.equals("MT")
				&& !stateAsString.equals("NC") && !stateAsString.equals("ND") && !stateAsString.equals("NE")
				&& !stateAsString.equals("NH") && !stateAsString.equals("NJ") && !stateAsString.equals("NM")
				&& !stateAsString.equals("NV") && !stateAsString.equals("NY") && !stateAsString.equals("OH")
				&& !stateAsString.equals("OK") && !stateAsString.equals("OR") && !stateAsString.equals("PA")
				&& !stateAsString.equals("PR") && !stateAsString.equals("RI") && !stateAsString.equals("SC")
				&& !stateAsString.equals("SD") && !stateAsString.equals("TN") && !stateAsString.equals("TX")
				&& !stateAsString.equals("UM") && !stateAsString.equals("UT") && !stateAsString.equals("VA")
				&& !stateAsString.equals("VI") && !stateAsString.equals("VT") && !stateAsString.equals("WA")
				&& !stateAsString.equals("WI") && !stateAsString.equals("WV") && !stateAsString.equals("WY"));
		// clear the buffer
		input.nextLine();
		state = USState.valueOf(stateAsString);

		// get the zip code
		String zip;

		do {
			System.out.println("Enter 5 digit zip code of vendor: ");

			zip = input.nextLine();

		} while (!zip.matches("[0-9]{5}"));// make sure they enter a 5 digit number

		// return a new vendor object with this name an address with this infor
		return new Vendor(name, new Address(street, city, state, zip));

	}

	private User loadUserFromFile() throws IOException, ClassNotFoundException {

		try (// read in the user from the file and return it
				ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream("UserInfo.txt"))) {
			return (User) objectIn.readObject();
		}
	}

	private int mainMenuChoice() {
		String choice;
		// its in a loop for input validation. will loop until they enter a number
		// within the correct range
		do {
			System.out.println("Enter your choice: ");
			choice = input.nextLine();

		} while (!choice.matches("[1-9]"));

		return Integer.parseInt(choice);

	}

	private void makePayment(String idOfCard) throws IOException {
		//check if the balance is less than one penny. dont simply check if it eqyals zero becuase doubles
		//can get messed up with the decimal places
		if (currentUser.getCardBalance(idOfCard) < .01) {
			System.out.println("You don't have a balance\n");
			return;
		}

		double amount;
		// double input validation. the inner loop is ensuring that they enter a format
		// that can be parsed to a double
		// so that no exceptions are thrown
		do {
			String entry;

			do {
				System.out.printf(
						"Enter how much you are paying.\n Please note you cannot pay more than your balance.\n"
						+ "Your balance is $%,.2f     $",currentUser.getCardBalance(idOfCard));
				entry = input.nextLine();
			} while (!(entry.matches("[0-9]*.[0-9]*")));// make sure they enter a value that can be parsed to a double..
														// we dont want exceptions being thrown if they enter anything
														// else
			amount = Double.parseDouble(entry);
			//cannot pay 0 or less and cannot pay more than balance. leeway tho in case
			//there are issues with the double
		} while (amount <= 0 || amount >currentUser.getCardBalance(idOfCard)+.0001);

		PaymentType type;
		String paymentTypeAsString;
		do {
			System.out.println("Enter payment method (check/online)");
			paymentTypeAsString = input.nextLine().toUpperCase();
		} while (!paymentTypeAsString.equals("ONLINE") && !paymentTypeAsString.equals("CHECK"));

		type = PaymentType.valueOf(paymentTypeAsString);

		currentUser.payBill(idOfCard, amount, type);
		System.out.println("Payment sucessful!\n");
	}

	private void makePurchase(String idOfCard) throws IOException {
		double amount;
		double interestFee;
		// double input validation. the inner loop is ensuring that they enter a format
		// that can be parsed to a double
		// so that no exceptions are thrown
		do {
			String entry;

			do {
				System.out.println(
						"Enter how much you are paying them (must be less than or equal to ur credit limit): \n"
						+ "Please note the minimum purchase you can make is $1.00");

				entry = input.nextLine();
			} while (!(entry.matches("[0-9]*.[0-9]*")));
			amount = Double.parseDouble(entry);
		} while (amount < 1);

		interestFee = amount * .01;
		Fee newInterestFee = new Fee(interestFee, FeeType.interest);
		Purchase newPurchase = new Purchase(amount, getPurchaseType(), getVendor(), newInterestFee);
		boolean success = currentUser.addPurchase(idOfCard, newPurchase);

		if (!success) {
			System.out.println("Purchase wasn't successful\n");
		} else {
			System.out.println("Purchase was successful! Transaction id is " + newPurchase.getTransactionID() + "\n");
		}
	}

	private void manageSpecificCreditCard() throws IOException {
		boolean stayInManaginingSpecificCardMenu = true;

		// if they have no cards in the system, there are no cards to manage so just
		// return
		if (currentUser.numberOfCards() == 0) {
			System.out.println("You have no cards in our system\n\n");
			return;
		}

		// display all the credit cards in the system
		System.out.println("These are your current Credit Cards: \n " + currentUser.creditCardsToString());

		// get the id of the card they want to manage
		System.out.println("Enter the id of the card you want to manage:");
		String idOfCard = input.nextLine();
		// loop until they enter an id of a card that they have
		while (!currentUser.containsCard(idOfCard)) {
			System.out.println("You do not have a card with this id. reenter id");
			idOfCard = input.nextLine();
		}

		// once we have a card to manage, stay in this menu until they choose to go back
		while (stayInManaginingSpecificCardMenu) {

			// display their options
			System.out.println(
					"1. Display current balance\n" + "2. Display current credit limit\n" + "3. Make a Purchase\n"
							+ "4. Pay bill\n" + "5. Display most recent Purchase\n" + "6. Display transactions\n"
							+ "7. Report Lost\n" + "8. Mark as cancelled\n" + "9. Go back to main menu\n");

			// get what choice they are choosing
			String choice;
			do {
				System.out.println("Enter your choice");
				choice = input.nextLine();
				// continue looping until they enter a number from 1-9, or they enter 10
			} while (!choice.matches("[1-9]"));

			// convert their choice to an integer and based on what they entered, do
			// something specific
			switch (Integer.parseInt(choice)) {
			case 1:
				this.displayCurrentBalanceOfCreditCard(idOfCard);
				break;
			case 2:
				this.displayCreditLimitOfCreditCard(idOfCard);
				break;
			case 3:
				makePurchase(idOfCard);
				saveUserToFile();
				break;
			case 4:
				makePayment(idOfCard);
				saveUserToFile();
				break;
			case 5:
				displayMostRecentPurchase(idOfCard);
				break;
			case 6:
				displayCardTransactions(idOfCard);
				break;
			case 7:
				currentUser.markCardAsLost(idOfCard);
				System.out.println("Marked as lost. You cannot use this card until further notice\n");
				saveUserToFile();
				break;
			case 8:
				currentUser.markCardAsCancelled(idOfCard);
				System.out.println("Success. You can no longer use this card\n");
				// when they makr a card cancelled, they are changing something about the user
				// object,
				// so we must save the current status of the user object to the file
				saveUserToFile();
				break;
			case 9:
				stayInManaginingSpecificCardMenu = false;
				break;

			}
		}
	}

	private void removeCreditCard() {
		// cant remove cards if there are none
		if (currentUser.numberOfCards() == 0) {
			System.out.println("There are no cards to remove\n");
			return;
		}
		// show them which cards they can remove
		System.out.println("These are your current Credit Cards: \n" + currentUser.creditCardsToString());

		System.out.println("Enter id of the card you'd like to remove.");
		String idOfCard = input.nextLine();
		// if they entered a card that doesn't exist, just leave the method
		if (!currentUser.containsCard(idOfCard)) {
			System.out.println("No card exists with this id\n");
			return;
		}
		currentUser.removeCard(idOfCard);

		System.out.println("Sucessfully removed card " + idOfCard);
		System.out.println("\n");

	}

	public void runApp() throws IOException {
		boolean continueRunning = true;

		while (continueRunning) {
			displayMainMenu();
			switch (mainMenuChoice()) {
			case 1:
				addCreditCard();
				// when we add a credit card to this user, we are adding something to the user
				// object
				// so we need to save that to the file in case they decide to close the program,
				// its saved
				saveUserToFile();
				break;
			case 2:
				removeCreditCard();
				// when we remove a credit card to this user, we are removing something to the
				// user object
				// so we need to save that to the file in case they decide to close the program,
				// its saved
				saveUserToFile();
				break;
			case 3:
				System.out.printf("$%,.2f", this.currentUser.totalBalance());
				System.out.println();
				break;
			case 4:
				System.out.printf("The total availalbe credit for all credit cards you have is $%,.2f" + "	%n%n",
						currentUser.totalAvailCredit());
				break;
			case 5:
				displayLargestPurchase();
				break;
			case 6:
				displayMostRecentPayment();
				break;
			case 7:
				displayTotalSpentOnCertainCategoryOfPurchase();
				break;
			case 8:
				manageSpecificCreditCard();
				break;
			case 9:
				System.out.println("\nThanks for using our app!");
				continueRunning = false;
			}
		}
	}

	private void saveUserToFile() throws IOException {

		try (// write this user to the file
				ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream("UserInfo.txt"))) {
			objectOut.writeObject(currentUser);
		}

	}

	public void startupApp() throws IOException, ClassNotFoundException {
		// when starting up the program, we need to initialize the static id counter in
		// the credit card and transaction classes based on the last given out id which
		// is saved in the file
		CreditCard.initializeIdCounter();
		Transaction.initializeIdCounter();


		// if the user file is empty, it's the first time they are using the program
		// so get their info and set it to current user
		if (new File("UserInfo.txt").length() == 0) {
			System.out.println("Welcome to our app! Enter your name: ");
			String name = input.nextLine();
			System.out.println("What Bank do you use? ");
			String bankName = input.nextLine();
			System.out.println("What is your bank account number? ");
			String acctNumber = input.nextLine();

			this.currentUser = new User(name, bankName, acctNumber);
			saveUserToFile();

			// this is not their first time using the app so load the user information from
			// the file
		} else {
			this.currentUser = loadUserFromFile();
			System.out.println(
					"Welcome back to our app " + currentUser.getName() + "! We picked up from where you left off.\n");

		}

		// before we begin the program, check if any cards expired since last time we
		// ran it and if yes, make sure to indicate its expired
		this.currentUser.checkIfAnyCardsExpired();

	}
}
