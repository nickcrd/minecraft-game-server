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

package com.nickcontrol.arcade.game.games.oneinthechamber;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.event.GameStateChangedEvent;
import com.nickcontrol.arcade.game.Game;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.game.GameType;
import com.nickcontrol.arcade.game.Gamemode;
import com.nickcontrol.arcade.game.games.bowspleef.kits.ArcherKit;
import com.nickcontrol.arcade.game.games.bowspleef.kits.JumperKit;
import com.nickcontrol.arcade.game.games.bowspleef.kits.RepulsorKit;
import com.nickcontrol.arcade.game.games.bowspleef.stats.ArrowsFiredStatsTracker;
import com.nickcontrol.arcade.game.games.bowspleef.stats.DoubleJumpsStatsTracker;
import com.nickcontrol.arcade.game.games.bowspleef.stats.PlayersRepulsedStatsTracker;
import com.nickcontrol.arcade.game.games.oneinthechamber.kits.ChamberJumperKit;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.arcade.player.PlayerState;
import com.nickcontrol.arcade.player.PlayerStateUpdateEvent;
import com.nickcontrol.arcade.team.Team;
import com.nickcontrol.core.combat.CombatDeathEvent;
import com.nickcontrol.core.scoreboard.ncScoreboard;
import com.nickcontrol.core.utils.*;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

public class OneInTheChamber extends Game{
    private HashMap<GamePlayer, Integer> scores = new HashMap<>();
    private long time = 1000 * 60 * 10;

    private int scoreToReach = 5;

    public Set<GamePlayer> allowDoubleShot = new HashSet<>();

    public OneInTheChamber(GameType type)
    {
        super(Gamemode.ONE_IN_THE_CHAMBER, type);

        getProperties().setFallDamage(false);
        getProperties().setJoinMidgame(true);
        getProperties().setFrozenWhilePrepare(false);
        getProperties().setPvpDamage(true);

        getProperties().setMinimumPlayers(2);
        getProperties().setRecommendedPlayers(4);
        getProperties().setMaximumPlayers(16);
        getProperties().setEnforceMaximumPlayers(false);

        getProperties().setDescription(
                "Use your §bBow§f to §cinsta-kill§f other players",
                "You get §b+1 Point§f for every kill!",
                "First player to reach §e" + scoreToReach + " Points§f wins!");

        getProperties().setDoRespawn(true);
        getProperties().setRespawnTimer(2);

        addTeams(new Team( "Players", ChatColor.YELLOW, "Yellow"));
        addKits(new ChamberJumperKit());
    }

    @EventHandler
    public void onGameUpdate(GameStateChangedEvent event)
    {
        if (event.getNewState() == GameState.PREPARING_PLAYERS)
        {
            scoreToReach = 5;

            if (getPlayersPlaying().size() >= 2)
                scoreToReach = 10;
            if (getPlayersPlaying().size() >= 5)
                scoreToReach = 15;
            if (getPlayersPlaying().size() >= 8)
                scoreToReach = 20;

            getProperties().setDescription(
                    "Use your §bBow§f to §cinsta-kill§f other players",
                    "You get §b+1 Point§f for every kill!",
                    "First player to reach §e" + scoreToReach + " Points§f wins!");

        }
    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void preparePlayer(GamePlayer player) {
        if (!scores.containsKey(player))
            scores.put(player, 0);
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

        if (!UtilTime.elapsed(GameManager.Instance.getStateTime(), time) && scoresFinal.keySet().iterator().next() < scoreToReach)
            return;

        announceWinner(new ArrayList<>(scoresFinal.values()));
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event)
    {
       if (event.getDamager() instanceof Arrow)
       {
           if (((Arrow) event.getDamager()).getShooter().equals(event.getEntity()))
               event.setCancelled(true);

           event.setDamage(9999);
       }
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
        scores.put(getGamePlayer(attacker), kills);
        attacker.getInventory().addItem(new ItemBuilder(Material.ARROW).build());

        getGamePlayer(attacker).addDiamondsEarned(10);
        getGamePlayer(attacker).addStat("Kills", 1);

        allowDoubleShot.remove(getGamePlayer(event.getEntity()));
    }

    @Override
    public void scoreboard(ncScoreboard sb) {
        sb.writeNL();
        if (GameManager.Instance.getState() == GameState.GAME_IN_PROGRESS) {
            sb.write("Time Remaining: §a" + UtilTime.makeString(Math.max(0, time - (System.currentTimeMillis() - GameManager.Instance.getStateTime()))));
            sb.writeNL();
        }
        sb.write("§c§lSCORES §c»");

        for (Map.Entry<GamePlayer, Integer> score : getSortedScores().entrySet())
            sb.write("§7" + score.getValue() + " §f" + score.getKey().getPlayer().getName());
        sb.writeNL();
    }

    @EventHandler
    public void spawnPowerup(TickEvent event)
    {
        if (event.getType() != TickType.SLOWER)
            return;

        if (!GameManager.Instance.getState().isLive())
            return;


        Location spawn = getMapData().getDataPoint("powerup").get(UtilMath.r(0, getMapData().getDataPoint("powerup").size()-1));

        PowerupType powerup = PowerupType.randomPowerup();

        Hologram holo = HologramsAPI.createHologram(GameManager.Instance.getPlugin(), spawn);
        holo.appendTextLine("§b§lCOLLECT POWERUP");
        holo.appendTextLine("§l" + powerup.getDisplayName());
        ItemLine itemLine = holo.appendItemLine(new ItemStack(powerup.getDisplay()));
        itemLine.setPickupHandler(player -> {
            holo.delete();
            powerup.collect.accept(player);
        });
        itemLine.setTouchHandler(player -> {
            holo.delete();
            powerup.collect.accept(player);
        });

        UtilFirework.playFirework(spawn, FireworkEffect.builder().flicker(false).withColor(Color.LIME).with(FireworkEffect.Type.BALL).trail(false).build());


    }



    public static enum PowerupType {

        EXTRA_ARROW("Extra Arrow", Material.ARROW, (player) -> { player.getInventory().addItem(new ItemBuilder(Material.ARROW).build());}),
        IRON_SWORD("Iron Sword Upgrade", Material.IRON_SWORD, player -> { player.getInventory().remove(Material.STONE_SWORD); player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));}),
        SPEED_ONE("Speed I", Material.POTION, player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0))),
        INVISIBILITY("Invisibility", Material.GLASS, player -> player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0))),
        CHAINMAIL_ARMOR("Chainmail Armor", Material.CHAINMAIL_CHESTPLATE, player -> {
            player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.CHAINMAIL_BOOTS), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_HELMET)});
        }),
        GOLDEN_APPLE("Golden Apple", Material.GOLDEN_APPLE, player -> player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE))),
        STRENGTH("Strength Potion", Material.IRON_INGOT, player -> {
            Potion pot = new Potion(PotionType.STRENGTH, 1, false);
            player.getPlayer().getInventory().addItem(pot.toItemStack(1));
        })

        ;
        private String displayName;
        private Material display;
        private Consumer<Player> collect;

        PowerupType(String displayName, Material display, Consumer<Player> collect) {
            this.displayName = displayName;
            this.display = display;
            this.collect = collect;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getDisplay() {
            return display;
        }

        public Consumer<Player> getCollect() {
            return collect;
        }

        public static PowerupType randomPowerup()
        {
            return values()[UtilMath.r(0, values().length-1)];
        }
    }
}
