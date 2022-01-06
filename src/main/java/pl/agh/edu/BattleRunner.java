package pl.agh.edu;

import pl.agh.edu.robots.OffensiveRobot;
import robocode.AdvancedRobot;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;

import java.util.ArrayList;
import java.util.List;

public class BattleRunner {

    public static void main(String[] args) {

        List<AdvancedRobot> bots = new ArrayList<>();
        bots.add(new OffensiveRobot());
        KBandit kBandit = new KBandit(bots);
        System.setProperty("TESTING", "true");
        RobocodeEngine.setLogMessagesEnabled(false);
        RobocodeEngine engine = new RobocodeEngine(new java.io.File("C:/robocode"));
        for (int i = 0; i < 100; i++) {
            AdvancedRobot currentBot = kBandit.chooseBot();
            System.out.println("Chosen bot: " + currentBot.getClass().getCanonicalName());
            engine.addBattleListener(new BattleObserver(kBandit));

            int numberOfRounds = 5;
            BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600);
            RobotSpecification[] selectedRobots = engine.getLocalRepository("sample.RamFire," + currentBot.getClass().getCanonicalName());

            BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);

            engine.runBattle(battleSpec, true);
        }
        engine.close();
        System.exit(0);
    }
}

class BattleObserver extends BattleAdaptor {

    private KBandit kBandit;

    public BattleObserver(KBandit kBandit) {
        this.kBandit = kBandit;
    }

    public void onBattleCompleted(BattleCompletedEvent e) {
        System.out.println("-- Battle has completed --");

        System.out.println("Battle results:");
        for (robocode.BattleResults result : e.getSortedResults()) {
            System.out.println("  " + result.getTeamLeaderName() + ": " + result.getScore());
            if (result.getTeamLeaderName().equals(this.kBandit.getCurrentBot().getClass().getCanonicalName())) {
                kBandit.learn(result.getScore());
            }
        }
    }

    public void onBattleMessage(BattleMessageEvent e) {
        System.out.println("Msg> " + e.getMessage());
    }

    public void onBattleError(BattleErrorEvent e) {
        System.out.println("Err> " + e.getError());
    }
}
