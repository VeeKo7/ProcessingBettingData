/**
 * Player class
 */

public class Player {
    private String playerId;
    private long balance;
    private int totalBets;
    private int wonBets;

    //constructor with playerId
    public Player(String playerId) {
    }

    //parameterized constructor
    public Player(String playerId, long balance, int totalBets, int wonBets) {
        this.playerId = playerId;
        this.balance = 0;
        this.totalBets = 0;
        this.wonBets = 0;
    }

    //Getters
    public String getPlayerId() {
        return playerId;
    }

    public long getBalance() {
        return balance;
    }

    public int getTotalBets() {
        return totalBets;
    }

    public int getWonBets() {
        return wonBets;
    }

    //methods

    /**
     * method receives certain amount of money to the account
     * @param amount that is deposited to the account
     */
    public void deposit(long amount) {
        /* balance receives certain amount
         * of money to it's account
         */
        balance += amount;
    }

    /**
     * method withdraws money from the account
     * @param amount that needs to be deducted from balance
     * @return true if amount is greater or even
     * to the balance and false if amount taken is
     * less than the balance
     */
    public boolean withdraw(long amount) {
        /* If balance is greater or equal to the amount
         * minus the amount from balance.
         */
        if(balance >= amount) {
            balance -= amount;
        }
        /*
        * If balance is smaller than the amount
        * on the balance then return false.
        * */
        return false;
    }

    /**
     * method to place bet
     * @param amount that will be added or deducted to the balance
     * depending on the condition met.
     * @param won is true or false
     */
    public void placeBet(long amount, boolean won) {
        //increase amount of bets
        totalBets++;
        /*
        * If player wins increase wonBets
        * and add amount to the balance.
        * Otherwise, deduct amount
        * from the balance.
        * */
        if (won) {
            wonBets++;
            balance += amount;
        } else {
            balance -= amount;
        }
    }

    /**
     * method returns win rate
     * @return win rate
     */
    public double getWinRate() {
        /*
        * if totalBets is greater than 0 then
        * return result (win rate) of totalBets divided by wonBets
        * otherwise return 0
        * */
        return totalBets > 0 ? (double) wonBets / totalBets : 0;
    }
}
