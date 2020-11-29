package elicode.parkour.function.creative;

import elicode.location.ImmutableLocation;
import elicode.parkour.function.parkour.ClickType;
import elicode.parkour.function.parkour.FunctionalItem;
import elicode.parkour.util.Optional;
import elicode.parkour.util.enchantment.GleamEnchantment;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.CheckpointSet;
import elicode.parkour.user.User;
import elicode.parkour.util.tuplet.Tuple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CheckPointItem implements FunctionalItem {

    ItemStack item;

    @Override
    public void onClick(User user, ClickType click) {
        Player player = user.asBukkitPlayer();

        CheckpointSet checkpoints = user.checkpoints;

        //右クリックしたのであればチェックポイントを設定、左クリックしたのであれば最新チェックポイント

        if (click == ClickType.RIGHT) {
            Optional<Tuple<Integer, ImmutableLocation>> wrappedCheckpoint = checkpoints.getLatestCheckpoint(user.asBukkitPlayer().getName());
            if(!checkpoints.hasCheckpoint(user.asBukkitPlayer().getName())) {
                BilingualText.stream("&c-チェックポイントが設定されていないためテレポート出来ません",
                        "&c-You can't teleport because you have not set any checkpoints")
                        .color()
                        .setReceiver(player)
                        .sendActionBarMessage();
                return;

            }
            Tuple<Integer, ImmutableLocation> checkpoint = wrappedCheckpoint.forcedUnwrapping();

            //チェックポイントにテレポートさせる
           player.teleport(checkpoint.second.asBukkit());

            BilingualText.stream("チェックポイントにテレポートしました", "Teleported to checkpoint")
                    .setReceiver(player)
                    .sendActionBarMessage();
        } else if(click == ClickType.LEFT){
            user.checkpoints.setCheckpoint(user,1,new ImmutableLocation(player.getLocation()));
            BilingualText.stream("チェックポイントを設定しました", "Set checkpoint")
                    .color()
                    .setReceiver(player)
                    .sendActionBarMessage();
        }
    }

    @Override
    public ItemStack build(User user) {

        item = new ItemStack(Material.PRISMARINE_SHARD);

        ItemMeta meta = item.getItemMeta();

        //最新@左 最終@右
        String displayName = BilingualText.stream("&b-チェックポイント アイテム &7-(CP設定 @ 左 / テレポート @ 右)",
                "&b-Checkpoint Item&7-(Set Cp @ L / Teleport @ R)")
                .textBy(user.asBukkitPlayer())
                .color()
                .toString();


        meta.setDisplayName(displayName);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean isSimilar(ItemStack item, User user) {

        String displayName = BilingualText.stream("&b-チェックポイント アイテム &7-(CP設定 @ 左 / テレポート @ 右)",
                "&b-Checkpoint Item&7-(Set Cp @ L / Teleport @ R)")
                .textBy(user.asBukkitPlayer())
                .color()
                .toString();

        return item != null && item.getType() == Material.PRISMARINE_SHARD && item.getItemMeta().getDisplayName().equals(displayName);
    }

}
