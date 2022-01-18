package pl.agh.edu.robots;

import robocode.*;


public class DefensiveBot extends AdvancedRobot {
    @Override
    public String toString() {
        return "DefensiveBot";
    }


    public void run() {
        while (true) {
            ahead(100);
            turnLeft(180);
            ahead(100);
            turnLeft(45);
        }
    }

    public void onScannedRobot(ScannedRobotEvent enemy) {
        if (enemy.getEnergy() > getEnergy()) {
            turnLeft(180);
            setAhead(80);
        } else {
            fire(enemy.getDistance()/400 + 1);
        }
    }

    public void onHitWall(HitWallEvent e) {
        turnLeft(30);
        setMaxVelocity(-1 * getVelocity());
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnRight(45);
        back(100);
    }

    public void onHitRobot(HitRobotEvent e) {
        if (e.getBearing() > -90 && e.getBearing() <= 90) {
            back(100);
        } else {
            ahead(100);
        }
    }

}
