package pl.agh.edu;

import robocode.AdvancedRobot;

import java.util.*;

public class KBandit {
    private Map<AdvancedRobot, Double> Q;
    private Map<AdvancedRobot, Integer> N;
    private Double epsilon;
    private AdvancedRobot bot;

    public KBandit(List<AdvancedRobot> bots) {
        this.Q = new HashMap<>();
        this.N = new HashMap<>();
        for(AdvancedRobot bot: bots) {
           this.Q.put(bot, 0.0);
           this.N.put(bot, 0);
        }
        this.epsilon = 0.2;
    }

    public Map<AdvancedRobot, Double> getQ() {
        return this.Q;
    }

    public Map<AdvancedRobot, Integer> getN() {
        return this.N;
    }

    private int maxIndex(Collection<Double> collection) {
        Double m = Double.NEGATIVE_INFINITY;
        int index = -1;
        int i = 0;
        for (Double element: collection) {
            if (element > m) {
                m = element;
                index = i;
            }
            i++;
        }
        return index;
    }

    public AdvancedRobot chooseBot() {
        AdvancedRobot bestBot = new ArrayList<>(this.Q.keySet()).get(maxIndex(this.Q.values()));
        List<AdvancedRobot> notCheckedBots = new ArrayList<>();
        for (Map.Entry<AdvancedRobot, Integer> entry: this.N.entrySet()) {
            if (entry.getValue() == 0) {
                notCheckedBots.add(entry.getKey());
            }
        }
        List<AdvancedRobot> preferredBots = new ArrayList<>();
        preferredBots.add(bestBot);
        preferredBots.addAll(notCheckedBots);
        List<AdvancedRobot> notPreferredBots = new ArrayList<>();
        for (Map.Entry<AdvancedRobot, Double> entry: this.Q.entrySet()) {
            if (!preferredBots.contains(entry.getKey())) {
                notPreferredBots.add(entry.getKey());
            }
        }

        if (notPreferredBots.isEmpty()) {
            notPreferredBots = preferredBots;
        }

        Random rand = new Random();

        if (rand.nextDouble() > this.epsilon) {
            this.bot = preferredBots.get(rand.nextInt(preferredBots.size()));
        } else {
            this.bot = notPreferredBots.get(rand.nextInt(notPreferredBots.size()));
        }

        return this.bot;
    }

    public AdvancedRobot getCurrentBot() {
        return this.bot;
    }

    public void learn(Integer score) {
        this.N.put(this.bot, this.N.get(bot) + 1);
        Double q = this.Q.get(this.bot);
        Integer n = this.N.get(this.bot);
        this.Q.put(this.bot, q + (1 / n) * (score - q));
    }
}
