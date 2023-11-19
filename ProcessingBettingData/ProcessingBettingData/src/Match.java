import java.util.UUID;

public class Match {
    private UUID matchId;
    private double rateA;
    private double rateB;
    private String result;

    public Match(UUID matchId, double rateA, double rateB, String result) {
        this.matchId = matchId;
        this.rateA = rateA;
        this.rateB = rateB;
        this.result = result;
    }

    public UUID getMatchId() {
        return matchId;
    }

    public double getRateA() {
        return rateA;
    }

    public double getRateB() {
        return rateB;
    }

    public String getResult() {
        return result;
    }
}
