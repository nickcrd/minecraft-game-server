/*
 *
 * Copyright (c) 2019 NICKCONTROL. All rights reserved.
 *
 * This file/repository is proprietary code. You are expressly prohibited from disclosing, publishing,
 * reproducing, or transmitting the content, or substantially similar content, of this repository, in whole or in part,
 * in any form or by any means, verbal or written, electronic or mechanical, for any purpose.
 * By browsing the content of this file/repository, you agree not to disclose, publish, reproduce, or transmit the content,
 * or substantially similar content, of this file/repository, in whole or in part, in any form or by any means, verbal or written,
 * electronic or mechanical, for any purpose.
 *
 */

package com.nickcontrol.arcade.game;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GameProperties {

    private List<Material> allowBreak = new ArrayList<Material>();
    private List<Material> allowPlace = new ArrayList<Material>();

    private List<Material> denyBreak = new ArrayList<Material>();
    private List<Material> denyPlace = new ArrayList<Material>();

    private boolean allowBlockBreak = false;
    private boolean allowBlockPlace = false;

    private boolean frozenWhilePrepare = true;

    private boolean inventoryEdit = true;

    private boolean dropItems = false;
    private boolean pickupItems = false;
    private boolean deathDropItems = false;

    private boolean doRespawn = true;
    private int respawnTimer = 5;

    private int hungerSet = -1;

    private boolean pvpDamage = false;
    private boolean pveDamage = true;
    private boolean evpDamage = true;
    private boolean fallDamage = true;
    private boolean teamSelfDamage = true;
    private boolean teamOtherDamage = false;

    private int minimumPlayers = 2;
    private int recommendedPlayers = 8;
    private int maximumPlayers = 16;
    private boolean enforceMaximumPlayers = false;

    private boolean joinMidgame = false;

    private boolean mobSpawnOverride = false;

    public boolean isMobSpawnOverride() {
        return mobSpawnOverride;
    }

    public void setMobSpawnOverride(boolean mobSpawnOverride) {
        this.mobSpawnOverride = mobSpawnOverride;
    }

    private String[] desc = new String[] {" "};

    public List<Material> getAllowBreak() {
        return allowBreak;
    }

    public List<Material> getAllowPlace() {
        return allowPlace;
    }

    public List<Material> getDenyBreak() {
        return denyBreak;
    }

    public List<Material> getDenyPlace() {
        return denyPlace;
    }

    public boolean isAllowBlockBreak() {
        return allowBlockBreak;
    }

    public boolean isAllowBlockPlace() {
        return allowBlockPlace;
    }

    public boolean isFrozenWhilePrepare() {
        return frozenWhilePrepare;
    }

    public boolean isInventoryEdit() {
        return inventoryEdit;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public boolean isPickupItems() {
        return pickupItems;
    }

    public boolean isDeathDropItems() { return deathDropItems; }

    public int getHungerSet() {
        return hungerSet;
    }

    public boolean isPvpDamage() {
        return pvpDamage;
    }

    public boolean isPveDamage() {
        return pveDamage;
    }

    public boolean isEvpDamage() {
        return evpDamage;
    }

    public boolean isFallDamage() {
        return fallDamage;
    }

    public boolean isTeamSelfDamage() {
        return teamSelfDamage;
    }

    public boolean isTeamOtherDamage() {
        return teamOtherDamage;
    }

    public int getMinimumPlayers() {
        return minimumPlayers;
    }

    public int getRecommendedPlayers() {
        return recommendedPlayers;
    }

    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public boolean isEnforceMaximumPlayers() {
        return enforceMaximumPlayers;
    }

    public String[] getDescription() {
        return desc;
    }

    public boolean isDoRespawn() {
        return doRespawn;
    }

    public int getRespawnTimer() {
        return respawnTimer;
    }

    public boolean isJoinMidgame() {
        return joinMidgame;
    }

    public void setAllowBreak(List<Material> allowBreak) {
        this.allowBreak = allowBreak;
    }

    public void setAllowPlace(List<Material> allowPlace) {
        this.allowPlace = allowPlace;
    }

    public void setDenyBreak(List<Material> denyBreak) {
        this.denyBreak = denyBreak;
    }

    public void setDenyPlace(List<Material> denyPlace) {
        this.denyPlace = denyPlace;
    }

    public void setAllowBlockBreak(boolean allowBlockBreak) {
        this.allowBlockBreak = allowBlockBreak;
    }

    public void setAllowBlockPlace(boolean allowBlockPlace) {
        this.allowBlockPlace = allowBlockPlace;
    }

    public void setFrozenWhilePrepare(boolean frozenWhilePrepare) {
        this.frozenWhilePrepare = frozenWhilePrepare;
    }

    public void setInventoryEdit(boolean inventoryEdit) {
        this.inventoryEdit = inventoryEdit;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public void setPickupItems(boolean pickupItems) {
        this.pickupItems = pickupItems;
    }

    public void setDeathDropItems(boolean deathDropItems) {
        this.deathDropItems = deathDropItems;
    }

    public void setDoRespawn(boolean doRespawn) {
        this.doRespawn = doRespawn;
    }

    public void setRespawnTimer(int respawnTimer) {
        this.respawnTimer = respawnTimer;
    }

    public void setHungerSet(int hungerSet) {
        this.hungerSet = hungerSet;
    }

    public void setPvpDamage(boolean pvpDamage) {
        this.pvpDamage = pvpDamage;
    }

    public void setPveDamage(boolean pveDamage) {
        this.pveDamage = pveDamage;
    }

    public void setEvpDamage(boolean evpDamage) {
        this.evpDamage = evpDamage;
    }

    public void setFallDamage(boolean fallDamage) {
        this.fallDamage = fallDamage;
    }

    public void setTeamSelfDamage(boolean teamSelfDamage) {
        this.teamSelfDamage = teamSelfDamage;
    }

    public void setTeamOtherDamage(boolean teamOtherDamage) {
        this.teamOtherDamage = teamOtherDamage;
    }

    public void setMinimumPlayers(int minimumPlayers) {
        this.minimumPlayers = minimumPlayers;
    }

    public void setRecommendedPlayers(int recommendedPlayers) {
        this.recommendedPlayers = recommendedPlayers;
    }

    public void setMaximumPlayers(int maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    public void setEnforceMaximumPlayers(boolean enforceMaximumPlayers) {
        this.enforceMaximumPlayers = enforceMaximumPlayers;
    }

    public void setJoinMidgame(boolean joinMidgame) {
        this.joinMidgame = joinMidgame;
    }

    public void setDescription(String... desc) {
        this.desc = desc;
    }
}
