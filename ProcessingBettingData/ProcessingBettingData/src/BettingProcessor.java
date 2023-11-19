import exception.MatchNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * class BettingProcessor
 * */

public class BettingProcessor {
    private static final Logger logger =
            Logger.getLogger(BettingProcessor.class.getName());
    /*
    * created a hashmap with players
    * */
    private static Map<String, Player> players = new HashMap<>();
    private static Map<UUID, Match> matches = new HashMap<>();
    private static Map<UUID, String> matchResults = new HashMap<>();
    private static long casinoBalance = 0;

    public static void main(String[] args) {
        readPlayerData("D:\\PlaytechTask\\ProcessingBettingData\\ProcessingBettingData\\src\\files\\inputFiles\\player_data.txt");
        readMatchData("D:\\PlaytechTask\\ProcessingBettingData\\ProcessingBettingData\\src\\files\\inputFiles\\match_data.txt");
        writeResults("D:\\PlaytechTask\\ProcessingBettingData\\ProcessingBettingData\\src\\files\\outputFiles\\result.txt");
    }

    /**
     * method reads player data from the file "player_data.txt"
     * and processes each line of the file to extract
     * relevant information like playerId, operation,
     * matchId, coinAmount, side
     */
    private static void readPlayerData(String fileName) {
        /*
         * BufferedReader is initialized with a new instance of
         * FileReader that reads from the file specified by the
         * fileName
         * */
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            /*
             * while line is not equal to null
             * we split line where "," into an array
             * */
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String playerId = values[0];
                String operation = values[1];
                /*
                 * if it exists and is not empty string,
                 * otherwise null.
                 * fromString() takes a String as input and converts
                 * it into a UUID object.
                 * */
                UUID matchId =
                        values.length > 2 && !values[2].equals("") ? UUID.fromString(values[2]) : null;
                /*
                 * if it exists, otherwise set to 0.
                 * parseLong(string) converts string into long value.
                 * */
                long coinAmount = values.length > 3 ? Long.parseLong(values[3]) : 0;
                String side = values.length > 4 ? values[4] : null;

                processPlayerAction(playerId, operation, matchId, coinAmount, side);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method reads match data from a file "match_data.txt"
     * @param fileName
     * */
    private static void readMatchData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String [] values = line.split(",");
                UUID matchId = UUID.fromString(values[0]);
                double rateA = Double.parseDouble(values[1]);
                double rateB = Double.parseDouble(values[2]);
                String result = values[3];

                processMatchResult(matchId, rateA, rateB, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method handles various functions related to casino game
     * or betting system for a player.
     * @param playerId - represents the unique identifier of the player.
     * @param operation - represents the type of operation to be performed
     *                  "DEPOSIT", "BET", "WITHDRAW".
     * @param matchId - represents the unique identifier of a match or game.
     * @param coinAmount - represents the amount of coins involved in the operation.
     * @param side - represents the side or choice related to the operation.
     */
    private static void processPlayerAction (String playerId, String operation,
                                             UUID matchId, long coinAmount,
                                             String side) {
        System.out.println("Inside processPlayerAction method");
        /*
        * method first checks if the 'players' hashmap contains
        * the specified 'playerId' key
        * If not, then it adds a new entry to the hashmap with the
        * 'playerId' as the key and a new 'Player' object as the value.
        * */
        if (!players.containsKey(playerId)) {
            players.put(playerId, new Player(playerId));
            System.out.println("Inside if statement");
        }
        /*
        * It then retrieves the 'Player' object associated with the
        * 'playerId' from the 'players' hashmap.
        * */
        Player player = players.get(playerId);
        System.out.println("Retrieved player object");

        /*
        * The method uses a switch statement to perform different actions.
        * For "DEPOSIT", it calls the 'deposit' method on the 'Player' object.
        * For "WITHDRAW", it calls the 'withdraw' method on the 'Player' object
        * and updates 'casinoBalance' accordingly.
        * For "BET", it checks if the bet is valid with 'isValidBet' method,
        * if so, updates the player's bet using the 'placeBet' method,
        * and it adjusts the 'casinoBalance' based on the result of the bet.
        * */
        switch (operation) {
            case "DEPOSIT":
                player.deposit(coinAmount);
                System.out.println("Inside switch, DEPOSIT");
                break;
            case "WITHDRAW":
                if (player.withdraw(coinAmount)) {
                    casinoBalance -= coinAmount;
                    System.out.println("Inside switch, WITHDRAW");
                }
                break;
            case "BET":
                if (isValidBet(player, coinAmount, side)) {
                    System.out.println("Inside switch, BET");
                    player.placeBet(coinAmount, side.equals(getMatchResult(matchId)));
                    casinoBalance += calculateCasinoBalanceChange(coinAmount, side);
                }
                break;
        }
    }

    /**
     * method checks if bet is valid
     * @param player
     * @param coinAmount
     * @param side
     * @return true if bet is valid and false if not.
     */
    private static boolean isValidBet(Player player, long coinAmount, String side) {
        return player.getBalance() >= coinAmount && !side.equals("");
    }

    /**
     * method stores the match results along
     * with their rates and results
     * @param matchId
     * @param rateA
     * @param rateB
     * @param result
     * */
    private static void processMatchResult(UUID matchId, double rateA,
                                           double rateB, String result) {
        /*
        * If match id is not already present,
        * store the result
        * */
        if (!matches.containsKey(matchId)) {
            matches.put(matchId, new Match(matchId, rateA, rateB, result));
        } else {
            //Logging an error with a duplicate match id
            System.err.println("Duplicate match ID: " + matchId);
        }
    }

    /**
     * method uses the 'matches' map to retrieve the 'Match'
     * object associated with the given 'matchId'
     * @param matchId
     * @return if the match is found it returns the results
     * */
    private static String getMatchResult (UUID matchId) {
        /*
        * Retrieving 'Match' object from the 'matches' map
        * using the provided 'matchId'.
        * */
        Match match = matches.get(matchId);

        if (match != null) {
            return match.getResult();
        } else {
            throw new MatchNotFoundException("Match not found for ID: "
                    + matchId);
        }
    }

    /**
     * method calculates the change in the casino's balance based
     * on the amount of coins 'coinAmount' and side of a match 'side'
     * that a player has bet on.
     * @param coinAmount - represents the amount of coins in player's bet
     * @param side - represents the side of the match on which a player
     *             has placed a bet.
     * @return
     * */
    private static long calculateCasinoBalanceChange(long coinAmount, String side) {
        /*
        * if the side is equal to 'A' then expression
        * '(long)(coinAmount * 0.5)' is evaluated. This calculates half
        * of the 'coinAmount' (50% of the bet amount) and returns it as a 'long'
        * */
        return "A".equals(side) ? (long)(coinAmount * 0.5) : -coinAmount;
    }

    /**
     * method is responsible for creating a 'FileWriter'
     * to write the results of the betting processing
     * @param fileName
     * */
    private static void writeResults(String fileName) {
        /*
        * try ensures FileWriter is properly closed
        * after writing the results.
        * */
        try (FileWriter writer = new FileWriter(fileName)) {
            /*
            * Info about players who performed legitimate actions,
            * including their playerId, final balance, and win rate.
            * */
            writeLegitimatePlayers(writer);
            /*
            * Info about players who performed illegal actions,
            * including their first illegal operation.
            * */
            writeIllegitimatePlayers(writer);
            /*
            * Info about the change in the casino's balance,
            * considering only the bets made by players.
            * */
            writeCasinoBalanceChange(writer);
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("An error occurred while writing results. " +
                    "Please check the console for details.");
        }
    }

    /**
     * method is responsible about writing info
     * about legitimate players into a 'FileWriter'
     * @param writer represents a parameter of 'FileWriter' object
     */
    private static void writeLegitimatePlayers(FileWriter writer) throws IOException {
        writer.write("Legitimate Players:\n");
        //Iterate over all legitimate players in the players map
        for (Player player : players.values()) {
            //Write player info in a formatted way
            writer.write(String.format("%s %d %.2f\n", player.getPlayerId(),
                    player.getBalance(), player.getWinRate()));
        }
        //Add a newline to separate this section from the next
        writer.write("\n");
    }

    /**
     * method is responsible about writing info
     * about illegitimate players
     * (those who performed illegal actions) into a 'FileWriter'
     * @param writer represents a parameter of 'FileWriter' object
     */
    private static void writeIllegitimatePlayers(FileWriter writer) throws IOException {
        writer.write("Illegitimate Players");
        //Iterate over all players in the players map
        for (Player player : players.values()) {
            //checks if player has not placed any bets
            if (player.getTotalBets() == 0) {
                //write info for players with no bets
                writer.write(String.format("%s %s %s %s %s\n", player.getPlayerId(),
                        "BET", "null", "null", "null"));
            }
        }
        //Add a newline to separate this section from the next
        writer.write("\n");
    }

    /**
     * method is responsible for writing info
     * about the change in the casino's balance
     * into a 'FileWriter'.
     * @param writer
     */
    private static void writeCasinoBalanceChange(FileWriter writer) throws IOException {
        writer.write("Casino Balance Change:\n");
        //writes the current balance to the casino to the file
        writer.write(String.format("%d\n", casinoBalance));
    }
}
