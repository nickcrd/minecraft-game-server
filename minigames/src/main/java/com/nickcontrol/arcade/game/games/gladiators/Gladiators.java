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

package com.nickcontrol.arcade.game.games.gladiators;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.Game;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.game.GameType;
import com.nickcontrol.arcade.game.Gamemode;
import com.nickcontrol.arcade.game.games.gladiators.kits.GladiatorKit;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.arcade.team.Team;
import com.nickcontrol.core.combat.CombatDeathEvent;
import com.nickcontrol.core.scoreboard.ncScoreboard;
import com.nickcontrol.core.utils.ItemBuilder;
import com.nickcontrol.core.utils.UtilPlayer;
import com.nickcontrol.core.utils.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Gladiators extends Game {
    private HashMap<GamePlayer, Integer> scores = new HashMap<>();
    private HashMap<GamePlayer, Integer> killstreaks = new HashMap<>();

    private long time = 1000 * 60 * 5;

    public Gladiators(GameType type) {
        super(Gamemode.GLADIATORS, type);

        getProperties().setJoinMidgame(true);

        getProperties().setDoRespawn(true);
        getProperties().setRespawnTimer(5);

        getProperties().setDescription("§cKill§f other players to earn §ePoints",
                "§aUpgrade §fyour Gear by having §bKill Streaks",
                "You get §61 Golden Apple §fafter each §cKill",
                "§fWin by having the §ahighest amount§f of §ePoints§f after 5 Minutes!");

        getProperties().setPvpDamage(true);
        getProperties().setFallDamage(false);

        addTeams(new Team("Players", ChatColor.YELLOW, "Yellow"));
        addKits(new GladiatorKit());
    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void preparePlayer(GamePlayer player) {
        if (!scores.containsKey(player))
            scores.put(player, 0);
        if (!killstreaks.containsKey(player))
            killstreaks.put(player, 0);
    }

    @Override
    public void winCheck() {
        if (getPlayersPlaying().size() == 0) {
            GameManager.Instance.updateState(GameState.DEAD);
            return;
        }

        if (getPlayersPlaying().size() == 1) {
            ArrayList<List<GamePlayer>> winners = new ArrayList<>();
            winners.add(0, new ArrayList<>());
            winners.get(0).add(getPlayersPlaying().get(0));

            announceWinner(winners);
        }

        if (!UtilTime.elapsed(GameManager.Instance.getStateTime(), time))
            return;


        HashMap<Integer, List<GamePlayer>> scoresFinal = new HashMap<>();
        for (Map.Entry<GamePlayer, Integer> entry : getSortedScores().entrySet()) {
            if (scoresFinal.containsKey(entry.getValue())) {
                scoresFinal.get(entry.getValue()).add(entry.getKey());
                continue;
            }

            scoresFinal.put(entry.getValue(), new ArrayList<>());
            scoresFinal.get(entry.getValue()).add(entry.getKey());
        }

        scoresFinal = scoresFinal.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        announceWinner(new ArrayList<>(scoresFinal.values()));
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }


    public LinkedHashMap<GamePlayer, Integer> getSortedScores() {
        return scores.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)); // TODO: Find solution for this ( player being null ) -> CONSIDER USING OFFLINE PLAYERS?!
    }

    @EventHandler
    public void fallCancel(EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }


    @EventHandler
    public void onDeath(CombatDeathEvent event) {
        Player attacker = UtilPlayer.searchOnline(event.getEntity(), event.getLastDamage().Attacker, false);

        if (attacker == null)
            return;

        if (getGamePlayer(attacker) == null)
            return;

        int kills = 0;
        if (scores.containsKey(getGamePlayer(attacker))) {
            kills = scores.remove(getGamePlayer(attacker));
        }

        kills++;

        int ks = 0;
        if (killstreaks.containsKey(getGamePlayer(attacker))) {
            ks = killstreaks.remove(getGamePlayer(attacker));
        }

        ks++;

        scores.put(getGamePlayer(attacker), kills);
        killstreaks.put(getGamePlayer(attacker), ks);

        attacker.getInventory().addItem(new ItemBuilder(Material.GOLDEN_APPLE).build());

        getGamePlayer(attacker).addDiamondsEarned(10);
        getGamePlayer(attacker).addStat("Kills", 1);


        upgradeKillstreak(getGamePlayer(attacker), ks);

        // Reset killstreak
        if (killstreaks.containsKey(getGamePlayer(event.getEntity()))) {
            killstreaks.remove(getGamePlayer(event.getEntity()));
        }

        killstreaks.put(getGamePlayer(event.getEntity()), 0);
    }

    public void upgradeKillstreak(GamePlayer player, int newStreak)
    {
        switch (newStreak)
        {
            case 2:
                player.getPlayer().getInventory().addItem(new ItemStack(Material.BOW));
                player.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 3));
                break;
            case 3:
                player.getPlayer().getInventory().remove(Material.STONE_SWORD);
                player.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                player.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 2));
                break;
            case 4:
                Potion pot = new Potion(PotionType.SPEED, 1, false);
                player.getPlayer().getInventory().addItem(pot.toItemStack(1));
                break;
            case 5:
                player.getPlayer().getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                player.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 1));
                break;
        }
    }


    @Override
    public void scoreboard(ncScoreboard sb) {
        sb.writeNL();
        if (GameManager.Instance.getState() == GameState.GAME_IN_PROGRESS) {
            sb.write("Time Remaining: §a" + UtilTime.makeString(Math.max(0, time - (System.currentTimeMillis() - GameManager.Instance.getStateTime()))));
            sb.writeNL();
        }
        sb.write("Kill Streak: §e" + (killstreaks.containsKey(getGamePlayer(sb.getPlayer())) ? killstreaks.get(getGamePlayer(sb.getPlayer())) : "None")); // TODO:
        sb.writeNL();
        sb.write("§c§lSCORES §c»");

        for (Map.Entry<GamePlayer, Integer> score : getSortedScores().entrySet())
            sb.write("§7" + score.getValue() + " §f" + score.getKey().getPlayer().getName());
        sb.writeNL();
    }
}
