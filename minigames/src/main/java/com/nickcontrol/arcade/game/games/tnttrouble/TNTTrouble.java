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

package com.nickcontrol.arcade.game.games.tnttrouble;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.Game;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.game.GameType;
import com.nickcontrol.arcade.game.Gamemode;
import com.nickcontrol.arcade.game.games.tnttrouble.kit.BomberKit;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.arcade.team.Team;
import com.nickcontrol.core.combat.CombatDeathEvent;
import com.nickcontrol.core.scoreboard.ncScoreboard;
import com.nickcontrol.core.utils.*;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class TNTTrouble extends Game
{
    private int scoreToReach = 5; //TODO: Change to 10 or something in live game
    private HashMap<GamePlayer, Integer> scores = new HashMap<>();

    public TNTTrouble(GameType type) {
        super(Gamemode.TNT_TROUBLE, type);

        getProperties().setJoinMidgame(true);

        getProperties().setDoRespawn(true);
        getProperties().setRespawnTimer(5);

        getProperties().setDescription("§fUse the power of §cTNT §fto kill the others!",
                "§eRight-Click§f to throw §cThrowable TNT",
                "§aTap §fspace twice to use §eDouble Jump",
                "§aFirst Player§f reaching §e" + scoreToReach + " Kills §fwins!");

        getProperties().setPvpDamage(true);

        addTeams(new Team("Players", ChatColor.YELLOW, "Yellow"));
        addKits(new BomberKit());
    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void preparePlayer(GamePlayer player)
    {
        if (!scores.containsKey(player))
            scores.put(player, 0);
    }

    @EventHandler
    public void TNTThrow(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (GameManager.Instance.getState() != GameState.GAME_IN_PROGRESS)
            return;

        if (player.getItemInHand().getType() != Material.TNT)
            return;

        if (event.getAction() == Action.PHYSICAL)
            return;

        event.setCancelled(true);

        TNTPrimed tnt = (TNTPrimed) player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), TNTPrimed.class);
        tnt.setMetadata("owner", new FixedMetadataValue(GameManager.Instance.getPlugin(), player.getUniqueId().toString()));
        tnt.setFuseTicks(20);

        UtilInv.remove(player, Material.TNT, (byte) 0, 1);

        UtilAlg.velocity(tnt, player.getLocation().getDirection(), 0.7, false, 0, 0.1, 10, false);

        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 3.0F, 1.0F);

    }

    @Override
    public void winCheck()
    {
        if (getPlayersPlaying().size() == 0)
        {
            GameManager.Instance.updateState(GameState.DEAD);
            return;
        }

        if (getPlayersPlaying().size() == 1)
        {
            ArrayList<List<GamePlayer>> winners = new ArrayList<>();
            winners.add(0, new ArrayList<GamePlayer>());
            winners.get(0).add(getPlayersPlaying().get(0));

            GameManager.Instance.updateState(GameState.ENDING);
            announceWinner(winners);
        }

        if (!(getSortedScores().isEmpty()) && getSortedScores().entrySet().iterator().next().getValue() >= scoreToReach)
        {
            HashMap<Integer, List<GamePlayer>> scoresFinal = new HashMap<>();
            for (Map.Entry<GamePlayer, Integer> entry : getSortedScores().entrySet())
            {
                if (scoresFinal.containsKey(entry.getValue()))
                {
                    scoresFinal.get(entry.getValue()).add(entry.getKey());
                    continue;
                }

                scoresFinal.put(entry.getValue(), new ArrayList<>());
                scoresFinal.get(entry.getValue()).add(entry.getKey());
            }

            scoresFinal = scoresFinal.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1 ,e2) -> e2, LinkedHashMap::new));

            announceWinner(new ArrayList<>(scoresFinal.values()));
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event)
    {
        event.blockList().clear();
    }

    public LinkedHashMap<GamePlayer, Integer> getSortedScores()
    {
        return scores.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1 ,e2) -> e2, LinkedHashMap::new)); // TODO: Find solution for this ( player being null ) -> CONSIDER USING OFFLINE PLAYERS?!
    }

    @EventHandler
    public void preventSelfDamage(EntityDamageByEntityEvent e)
    {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
        {
            if (e.getDamager().getType() == EntityType.PRIMED_TNT && e.getEntity().getType() == EntityType.PLAYER) {
                if (!e.getDamager().getMetadata("owner").isEmpty()) {
                    Player attacker = (Player) Bukkit.getPlayer(UUID.fromString(e.getDamager().getMetadata("owner").get(0).asString()));

                    if (attacker != null && attacker.getUniqueId().equals(e.getEntity().getUniqueId())) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void regenTNT(TickEvent event)
    {
        if (event.getType() != TickType.TWOSEC)
            return;

        if (GameManager.Instance.getState() != GameState.GAME_IN_PROGRESS)
            return;

        for (GamePlayer player : getPlayersAlive())
        {
            if (UtilInv.contains(player.getPlayer(), Material.TNT, (byte) 0, 5))
                continue;

            player.getPlayer().getInventory().addItem(new ItemBuilder(Material.TNT).setTitle(C.Red + "Throwable TNT").build());
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 1f, 1f);
        }
    }

    @EventHandler
    public void doJump(PlayerToggleFlightEvent event)
    {
        Player player = event.getPlayer();

        if (GameManager.Instance.getState() != GameState.GAME_IN_PROGRESS)
            return;

        if (!isAlive(event.getPlayer()))
            return;

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        event.setCancelled(true);
        player.setFlying(false);

        player.setAllowFlight(false);

        UtilAlg.velocity(player, player.getLocation().getDirection(), 1.4f, true, 0.6f, 0, 5f, true);

        player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);

        UtilCooldown.Instance.addCooldown(player, "Double Jump", 250, false, true);
    }

    @EventHandler
    public void updateDoubleJump(TickEvent event)
    {
        if (event.getType() != TickType.TICK)
            return;

        if (GameManager.Instance.getState() != GameState.GAME_IN_PROGRESS)
            return;

        for (Player player : UtilServer.getPlayers())
        {
            if (!isAlive(player))
                continue;

            if (UtilCooldown.Instance.onCooldown(player, "Double Jump"))
                continue;

            if (player.isOnGround() || !player.getLocation().getBlock().getRelative(BlockFace.DOWN).isEmpty())
                player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void fallCancel(EntityDamageEvent event)
    {
        if (event.getEntity().getType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }


    @EventHandler
    public void onDeath(CombatDeathEvent event)
    {
        Player attacker = UtilPlayer.searchOnline(event.getEntity(), event.getLastDamage().Attacker, false);

        if (attacker == null)
            return;

        if (getGamePlayer(attacker) == null)
            return;

        int kills = 0;
        if (scores.containsKey(getGamePlayer(attacker)))
        {
            kills = scores.remove(getGamePlayer(attacker));
        }

        kills++;
        scores.put(getGamePlayer(attacker), kills);
    }

    @Override
    public void scoreboard(ncScoreboard scoreboard)
    {
        scoreboard.writeNL();
        scoreboard.write(C.White + "» " + C.Yellow + C.Bold + "First to §f§l" + scoreToReach + "§e§l Kills!");
        scoreboard.writeNL();
        scoreboard.write("§c§lSCORES §c»");

        for (Map.Entry<GamePlayer, Integer> score : getSortedScores().entrySet())
            scoreboard.write("§7" + score.getValue() + " §f" + score.getKey().getPlayer().getName());
        scoreboard.writeNL();
    }
}
