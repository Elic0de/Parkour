package elicode.parkour.party;

import elicode.parkour.Main;
import elicode.parkour.game.fight.Fight;
import elicode.parkour.game.games.PartyFight;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Party {

    private Player owner;
    private Party opponent;
    private final Main plugin = Main.getPlugin();
    private HashSet<Player> invited = new HashSet<Player>();
    private HashSet<Player> members = new HashSet<>();
    private Fight fight;
    private boolean privateParty = true;
    private Set<UUID> bannedPlayers = new HashSet<>();
    private PartyFight partyFight;
    private final PartyManager partyManager = Main.getPlugin().getPartyManager();
    //private PartySettings settings = new PartySettings();
    private BukkitTask broadcastTask;

    public Party(Player p) {
        this.owner = p;
        getMembers().add(p);


    }

    public Party getOpponent() {
        return opponent;
    }

    public void setPartyFight(PartyFight partyFight) {
        this.partyFight = partyFight;
    }

    public PartyFight getPartyFight() {
        return partyFight;
    }

    public void setOpponent(Party opponent) {
        this.opponent = opponent;
    }

    public HashSet<Player> getInvited() {
        return invited;
    }

    public void setInvited(HashSet<Player> invited) {
        this.invited = invited;
    }

    public boolean isInFight() {
        return fight != null;
    }


    public void setFight(Fight fight) {
        this.fight = fight;
    }

    public Fight getFight() {
        return fight;
    }

    public Player getOwner() {
        return owner;
    }

    public HashSet<Player> getMembers() {
        return members;
    }

    public void setMembers(HashSet<Player> members) {
        this.members = members;
    }

    public void addMember(Player player) {
        this.members.add(player);
    }

   /* public PartySettings getSettings() {
        return settings;
    }*/

    public boolean isBanned(Player pPlayer) {
        return this.bannedPlayers.contains(pPlayer.getUniqueId());
    }

    public void setBanned(Player pPlayer, boolean pIsBanned) {
        if (pIsBanned) {
            this.bannedPlayers.add(pPlayer.getUniqueId());
        } else {
            this.bannedPlayers.remove(pPlayer.getUniqueId());
        }
    }
    public Player getPlayer(Player player) {
        for(Player player1 : members) {
            if(player1.equals(player)){
                return player1;
            }
        }
        return null;
    }

    public boolean isPrivate() {
        return this.privateParty;
    }

    public void setPrivateState(boolean pIsPrivate) {
        this.privateParty = pIsPrivate;
    }

    protected boolean isAMember(Player pPlayer) {
        return this.members.contains(pPlayer);
    }

    public BukkitTask getBroadcastTask() {
        return broadcastTask;
    }

    public void setBroadcastTask(BukkitTask task){
        this.broadcastTask = task;
    }

    public void setOwner(Player player) {
        if(!getMembers().contains(player)) {
            getMembers().add(player);
        }
        if(broadcastTask != null) {
            broadcastTask.cancel();
            broadcastTask = null;
        }
        owner = player;
    }

    public void disbandParty() {

        for(Player member : getPlayers()) {
        BilingualText.stream("$player&eがパーティーを解散しました!","$player &ehas disbanded the party!")
                .color()
                .setAttribute("$player", getOwner().getDisplayName())
                .setReceiver(member.getPlayer())
                .sendChatMessage();
            getPlayers().remove(member);
            if(member != null) {
              /*  if(isInFight()) {
                    Fight fight = Fight.getCurrentFight(member);
                        *//*if(fight instanceof PartyVsParty) {
                            ((PartyVsParty)fight).forceEnd(this);
                        }
                        else {*//*
                            fight.forceEnd(ChatColor.RED + "The fight was forced to end because the party has been disbanded!");
                        //}

                }
                if(Fight.getCurrentFight(member) == null) {

                }*/
            }
        }

        partyManager.deleteParty(this);
    }


    public boolean addPlayer(Player pPlayer) {
        System.out.println(this.members.contains(pPlayer) +"" +this.invited.contains(pPlayer )+  isLeader(pPlayer) + this.privateParty + isBanned(pPlayer));
        if (!this.members.contains(pPlayer) && (this.invited.contains(pPlayer) || isLeader(pPlayer) || !this.privateParty) && !isBanned(pPlayer)) {
            this.members.add(pPlayer);
            partyManager.addPlayerToParty(pPlayer, this);
            removeFromInvited(pPlayer);
            return true;
        }
        return false;
    }

    protected void addToInvited(Player pPlayer) {
        this.invited.add(pPlayer);
    }

    protected void removePlayerSilent(Player pPlayer) {
        this.members.remove(pPlayer);
        partyManager.removePlayerFromParty(pPlayer);
    }

    public int getInviteListSize() {
        return this.invited.size();
    }

    protected boolean needsNewLeader(Player pPlayer) {
        if (isLeader(pPlayer)) {
            //this.owner = null;
            return true;
        }
        return false;
    }

    protected void findNewLeader() {
        Player newLeader = getPlayers().get(0);
        removePlayerSilent(newLeader);
        setOwner(newLeader);
        /*sendMessage(PartyCommand.getInstance().getPrefix() + PatterCollection.NEW_LEADER_PATTERN.matcher(Main.getInstance().getMessages()
                .getString("Party.Command.Leave.NewLeaderIs")).replaceAll(Matcher.quoteReplacement(getLeader().getDisplayName())));*/
    }

    public void invite(Player pPlayer) {
        setBanned(pPlayer, false);
        addToInvited(pPlayer);

    }
    public boolean isLeader(Player player) {
        return isOwner(player);
    }

    public boolean isInParty(Player pPlayer) {
        return (isAMember(pPlayer) || pPlayer.getUniqueId().equals(getOwner().getUniqueId()));
    }

    public boolean isNobodyInvited() {
        return getInvited().isEmpty();
    }

    public boolean isAlreadyInvited(Player pPlayer, Player pToInvite) {
        if (!isPrivate())
            return false;
        if (isInvited(pToInvite)) {
            /*pPlayer.sendMessage(this.PREFIX + PatterCollection.PLAYER_PATTERN

                    .matcher(Main.getInstance().getMessages()
                            .getString("Party.Command.Invite.AlreadyInYourParty"))
                    .replaceAll(Matcher.quoteReplacement(pToInvite.getDisplayName())));*/
            return true;
        }
        return false;
    }

    public boolean canInvite(Player pPlayer) {
        if (!true &&
                10 > 1 &&
                10 < getAllPlayers().size() +
                        getInviteListSize() + 1) {
            /*pPlayer.sendMessage(this.PREFIX + PatterCollection.MAX_PLAYERS_IN_PARTY_PATTERN
                    .matcher(Main.getInstance().getMessages()
                            .getString("Party.Command.Invite.MaxPlayersInPartyReached"))
                    .replaceAll(Matcher.quoteReplacement(
                            Main.getInstance().getGeneralConfig().getInt("General.MaxPlayersInParty") + "")));*/
            return false;
        }
        return true;
    }
    public boolean allowsInvitation(Player pPlayer, Player pQueryPlayer) {
        /*if (Main.getInstance().getGeneralConfig().getBoolean("Commands.Friends.SubCommands.Settings.Settings.PartyInvite.Enabled") && pQueryPlayer.getSettingsWorth(1) == 1 && !pPlayer.isAFriendOf((PAFPlayer)pQueryPlayer)) {
            pPlayer.sendMessage(this.PREFIX + Main.getInstance().getMessages().getString("Party.Command.Invite.CanNotInviteThisPlayer"));
            return false;
        }*/
        return true;
    }

    public boolean isAlreadyInAParty(Player pPlayer, Player pToInvite) {
        if (partyManager.getParty(pToInvite.getUniqueId()) != null) {
            BilingualText.stream("$playerは既にパーティーに参加しています！","$player &cis already in your party!")
                    .color()
                    .setAttribute("$player", pToInvite.getDisplayName())
                    .setReceiver(pPlayer)
                    .sendChatMessage();
            return true;
        }
        return false;
    }

    public List<Player> getAllPlayers() {
        List<Player> allPlayers = getPlayers();
        Player onlinePAFPlayer = getOwner();
        if (onlinePAFPlayer != null)
            allPlayers.add(onlinePAFPlayer);
        return allPlayers;
    }

    private void removePlayer(Player pPlayer) {
        removePlayerSilent(pPlayer);
        /*sendMessage(PartyCommand.getInstance().getPrefix() + PatterCollection.PLAYER_PATTERN
                .matcher(Main.getInstance().getMessages().getString("Party.Command.General.PlayerHasLeftTheParty")).replaceAll(Matcher.quoteReplacement(pPlayer.getDisplayName())));*/
    }

    public void leaveParty(Player pPlayer) {

       /* boolean needsNewLeader = needsNewLeader(pPlayer);*/
        if (deleteParty()) return;
        /*if (needsNewLeader) findNewLeader();*/

        BilingualText.stream("$player&aが退出しました","$player &aleft the party")
                .color()
                .setAttribute("$player", pPlayer.getDisplayName())
                .setReceivers(getPlayers())
                .sendChatMessage();

        removePlayer(pPlayer);
    }

    public void kickPlayer(Player pPlayer) {
        removePlayerSilent(pPlayer);
        /*pPlayer.sendMessage(Main.getInstance().getMessages()
                .get(PartyCommand.getInstance().getPrefix(), "Party.Command.Kick.KickedPlayerOutOfThePartyKickedPlayer"));
        sendMessage(PartyCommand.getInstance().getPrefix() + PatterCollection.PLAYER_PATTERN
                .matcher(Main.getInstance().getMessages().getString("Party.Command.Kick.KickedPlayerOutOfThePartyOthers"))
                .replaceAll(Matcher.quoteReplacement(pPlayer.getDisplayName())));*/
        deleteParty();
    }
    public void removeFromInvited(Player pPlayer) {
        this.invited.remove(pPlayer.getUniqueId());
    }

    private boolean isPartyEmpty() {
        return (getPlayers().isEmpty() && isNobodyInvited());
    }

    public boolean isInvited(Player pPlayer) {
        return (!isPrivate() || getInvited().contains(pPlayer.getUniqueId()));
    }

   /* public void sendMessage(TextComponent pText) {
        for (OnlinePAFPlayer player : getAllPlayers())
            player.sendMessage(pText);
    }

    public void sendMessage(String pText) {
        for (OnlinePAFPlayer player : getAllPlayers())
            player.sendMessage(pText);
    }*/
   public boolean hasNoParty(Player player) {
       if (this == null) {
         /*  pPlayer.sendMessage(this.PREFIX +
                   Main.getInstance().getMessages().getString("Party.Command.Join.PlayerHasNoParty"));*/
           return true;
       }
       return false;
   }

    private boolean deleteParty() {
        int partyMemberCount = getPlayers().size();
        if ((partyMemberCount <= 2 && isPrivate()) || partyMemberCount < 2) {
            BilingualText.stream("&eすべての招待が期限切れになり、すべてのメンバーが去ったため、パーティーは解散されました","&eThe party was disbanded because all invites have expired and all members have left")
                    .color()
                    .setReceivers(getPlayers())
                    .sendChatMessage();
            partyManager.deleteParty(this);
            for (Player player : getAllPlayers()) removePlayerSilent(player);

            return true;
        }
        return false;
    }

    public boolean isOwner(Player player) {
        return this.owner.equals(player);
    }


    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<Player>();
        for(Player p : members) {
            if(p != null) {
                players.add(p);
            }
        }
        return players;
    }

}
