package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator.Type;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.ActionListener;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration.GameSettings;

@Log4j2
public class Game implements ModelGameListener {
    private final GameSettings settings;
    private final Road road;
    private final ActionListener listener;
    @Getter
    private boolean loop;
    public Game(GameSettings settings, ActionListener actionListener) {
        this.road = new Road(settings,this);
        this.settings = settings;
        this.listener = actionListener;
    }
    public void start() {
        // CR: move to config
        int updateCooldown = settings.updateCooldown();
        int enemySpawnCooldown = settings.enemySpawnCooldown();
        loop = true;
        long lastUpdate = System.currentTimeMillis();
        long enemyLastSpawn = System.currentTimeMillis();
        while (loop) {
            long updateTimePassed = System.currentTimeMillis() - lastUpdate;
            long enemySpawnTimePassed = System.currentTimeMillis() - enemyLastSpawn;
            if (updateTimePassed > updateCooldown) {
                log.info("Time " + lastUpdate);
                road.update();
                listener.update(new GameDTO(settings.roadLength(),road.getEntitiesObjects(), road.getDefeatedEnemy(), road.getMainTowerHealth()));
                lastUpdate = System.currentTimeMillis();
            }
            if (enemySpawnTimePassed > enemySpawnCooldown) {
                log.info("Time " + enemyLastSpawn);
                road.insert(EntityCreator.create(Type.DEFAULT_ENEMY), settings.roadLength() - 1);
                enemyLastSpawn = System.currentTimeMillis();
            }
        }
    }
    @Override
    public void end() {
        loop = false;
        road.clear();
        listener.end();
    }

    // CR: do not add methods for test only
    public void createTower(int cell) throws EntityCreationException {
        road.insert(EntityCreator.create(Type.DEFAULT_TOWER), cell);
    }
    // CR: do not add methods for test only
//    public int getRoadLen() {
//        return road.getLength();
//    }
}
