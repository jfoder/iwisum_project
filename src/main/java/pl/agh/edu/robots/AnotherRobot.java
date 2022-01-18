package pl.agh.edu.robots;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

public class AnotherRobot extends AdvancedRobot {
    //gun variables
    private static int currentEnemyVelocity;

    //movement variables
    private static double turn = 2;
    private int turnDirection = 1;
    private int moveDirection = 1;
    private double oldEnemyHeading;
    private double oldEnergy = 100;

    public void run() {
        setBodyColor(Color.blue);
        setGunColor(Color.blue);
        setRadarColor(Color.black);
        setScanColor(Color.yellow);

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            turnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
    }

    public void onScannedRobot(ScannedRobotEvent enemy) {
        double absoluteBearing = enemy.getBearingRadians() + getHeadingRadians();

        if (oldEnergy - enemy.getEnergy() >= 0.1) {
            if (Math.random() > 0.5) {
                turnDirection *= -1;
            }
            if (Math.random() > 0.75) {
                moveDirection *= -1;
            }
        }

        setMaxTurnRate(turn);
        setMaxVelocity(12 - turn);
        setAhead(90 * moveDirection);
        setTurnLeft(90 * turnDirection);
        oldEnergy = enemy.getEnergy();

        if (enemy.getVelocity() < -2) {
            currentEnemyVelocity = 0;
        } else if (enemy.getVelocity() > 2) {
            currentEnemyVelocity = 1;
        } else if (enemy.getVelocity() <= 2 && enemy.getVelocity() >= -2) {
            if (currentEnemyVelocity == 0) {
                currentEnemyVelocity = 2;
            } else if (currentEnemyVelocity == 1) {
                currentEnemyVelocity = 3;
            }
        }

        double enemyX = getX() + enemy.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + enemy.getDistance() * Math.cos(absoluteBearing);
        double enemyHeading = enemy.getHeadingRadians();
        double enemyHeadingChange = enemyHeading - oldEnemyHeading;
        oldEnemyHeading = enemyHeading;
        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight(),
                battleFieldWidth = getBattleFieldWidth();
        double predictedX = enemyX, predictedY = enemyY;
        while ((++deltaTime) * 9 < Point2D.Double.distance(getX(), getY(), predictedX, predictedY)) {
            predictedX += Math.sin(enemyHeading) * 4;
            predictedY += Math.cos(enemyHeading) * 4;

            enemyHeading += enemyHeadingChange;

            predictedX = Math.min(Math.max(18.0, predictedX), battleFieldWidth - 18.0);
            predictedY = Math.min(Math.max(18.0, predictedY), battleFieldHeight - 18.0);
        }
        double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));

        setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()) * 2);
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));

        fire(3);
    }

    @Override
    public String toString() {
        return "AnotherRobot";
    }
}
