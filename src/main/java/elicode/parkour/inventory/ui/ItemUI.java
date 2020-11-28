package elicode.parkour.inventory.ui;

import elicode.parkour.cosmetics.hat.Hat;
import elicode.parkour.cosmetics.hat.Hats;
import elicode.parkour.cosmetics.item.Item;
import elicode.parkour.cosmetics.item.Items;
import elicode.parkour.function.parkour.ControlFunctionalItem;
import elicode.parkour.function.parkour.ItemType;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.inventory.ui.menu.MenuCategory;
import elicode.parkour.user.PurchasedHatCollection;
import elicode.parkour.user.PurchasedTeleportItem;
import elicode.parkour.user.User;
import elicode.parkour.util.format.CoinFormat;
import elicode.parkour.util.item.ItemStackBuilder;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ItemUI extends AbstractUI {

    private static final SoundMetadata BUY_SE = new SoundMetadata(Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.75f);
    private static final SoundMetadata ERROR_SE = new SoundMetadata(Sound.BLOCK_ANVIL_PLACE, 1f, 1.75f);

    private static final SoundMetadata WEAR_SE = new SoundMetadata(Sound.ITEM_ARMOR_EQUIP_CHAIN, 5f, 0.25f);
    private static final SoundMetadata PUT_ON_SE = new SoundMetadata(Sound.ENTITY_CHICKEN_EGG, 1.5f, 1f);
    private static final ItemStack AIR = new ItemStack(Material.AIR);

    private final PurchasedTeleportItem purchaseditems;
    private int page = 0;

    public ItemUI(User user){
        super(user);
        this.purchaseditems = user.items;
    }

    @Override
    public Function<Player, InventoryLayout> layout() {
        //未購入の帽子のリスト
        List<Item> items = Items.ITEMS;

        Player player = user.asBukkitPlayer();

        return build(54, l -> {
            String title = this.page + 1 > 1 ? BilingualText.stream("アイテム (Page $page)", "Items (Page $page)")
                    .setAttribute("$page", this.page + 1)
                    .textBy(player)
                    .toString() : BilingualText.stream("アイテム", "Items")
                    .textBy(player)
                    .toString();

            l.title = title;
            l.itemsPerPage = 10;
            l.square = 1;
            l.setPage(page);
            int lastSlotIndex = l.option.size - 1;

            for(int index = 0; index < items.size(); index++){
                Item item = items.get(index);
                int value = item.value;
                String hatName = item.name;
                Material clonedHatItem = item.item;

                //同じ帽子であれば処理しない
                /*if(isSameHat(hatName, player.getInventory().getHelmet())) continue;*/

                if (l.getCurrentPage() < l.getPageAmount()) {
                    l.put((s) -> {

                        s.onClick(e -> {
                            this.page ++;
                            user.inventoryUserInterfaces.openItemUI();
                        });

                        s.icon(Material.ARROW, i -> i.displayName = BilingualText.stream("§a次のページ", "§aNext Page")
                                .textBy(player)
                                .toString());

                    }, 53);
                }
                if (l.getCurrentPage() > 1) {
                    l.put((s) -> {

                        s.onClick(e -> {
                            this.page --;
                            user.inventoryUserInterfaces.openItemUI();
                        });

                        s.icon(Material.ARROW, i -> i.displayName = BilingualText.stream("§aのページ", "§aPrevious Page")
                                .textBy(player)
                                .toString());

                    }, 45);
                }
                l.put((s) -> {

                    s.onClick(e -> {
                        user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.MYPROFILE);
                    });


                    s.icon(Material.ARROW, i -> i.displayName = BilingualText.stream("§a戻る", "§aGo Back")
                            .textBy(player)
                            .toString());
                }, 48);

                l.put((s) -> s.icon(Material.EMERALD, i -> i.displayName = BilingualText.stream("§6総計コイン: $coin", "§6Total Coins: $coin")
                        .setAttribute("$coin", CoinFormat.format(user.coins()))
                        .textBy(player)
                        .toString()), 49);

                l.put((s) -> {

                    s.onClick(e -> {
                        /*PlayerInventory inventory = player.getInventory();
                        ItemStack helmet = inventory.getHelmet();

                        //帽子を被っていなければ戻る
                        if(helmet == null) return;

                        inventory.setHelmet(AIR);
                        e.clickedInventory.setItem(lastSlotIndex, AIR);*/

                        PUT_ON_SE.play(player);

                        BilingualText.stream("&c-アイテムをデフォルトにしました", "&c-Reset your hat")
                                .color()
                                .setReceiver(player)
                                .sendActionBarMessage();

                        openInventory(player);
                    });


                    s.icon(Material.BARRIER, i -> i.displayName = BilingualText.stream("&c-アイテムをデフォルトにしました", "&c-Reset your hat")
                            .textBy(player)
                            .toString());
                }, 50);



                l.page(s -> {

                    if(purchaseditems.has(item)){
                        Material hatItem = item.item;

                        s.onClick(e -> {

                            /*PlayerInventory inventory = player.getInventory();

                            //帽子を被らせる
                            inventory.setHelmet(new ItemStack((hatItem)));

                            //被った帽子はNMS側でclone()されているので取得した物を書き換える
                            ItemStack equippedHelmet = inventory.getHelmet();

                            //表示名を帽子のプレイヤー名にする
                            ItemMeta meta = equippedHelmet.getItemMeta();
                            meta.setDisplayName(hatName);
                            equippedHelmet.setItemMeta(meta);*/

                            String displayName = BilingualText.stream("&c-帽子を仕舞う", "&c-ResetHat")
                                    .textBy(player)
                                    .color()
                                    .toString();

                            ItemStack button = new ItemStackBuilder(Material.CHEST)
                                    .setDisplayName(displayName)
                                    .build();

                            e.clickedInventory.setItem(lastSlotIndex, button);

                            WEAR_SE.play(player);

                            BilingualText.stream("&c-$nameの帽子を被りました", "&c-You wore a $name hat")
                                    .setAttribute("$name", hatName)
                                    .color()
                                    .setReceiver(player)
                                    .sendActionBarMessage();
                            user.teleporterItem = hatItem.name();

                            ControlFunctionalItem.updateSlot(player, ItemType.CHERCKPOINT_TELEPORTER);
                            openInventory(player);
                        });


                        s.icon(i -> {
                            i.material = hatItem;
                            i.displayName = "§e" + hatName;

                            List<String> lore = new ArrayList<>();

                            lore.add(0,BilingualText.stream("&8-アイテム", "&8-Item")
                                    .textBy(player)
                                    .setAttribute("$coins", value)
                                    .color()
                                    .toString());
                            lore.add(1,"");

                            String bottomText = hatItem.toString().toUpperCase() == user.teleporterItem ? BilingualText.stream("&a-選択済み", "&a-SELECTED")
                                    .textBy(player)
                                    .color()
                                    .toString() : BilingualText.stream("&7-クリックして選択", "&e-Click to select!")
                                    .textBy(player)
                                    .color()
                                    .toString();
                            lore.add(2,bottomText);
                            i.lore =  lore;
                        });

                    }else if(purchaseditems.canBuy(item)){
                        s.onClick(e -> {
                            purchaseditems.buy(item);
                            BUY_SE.play(player);

                            BilingualText.stream("&b-$nameの帽子を購入しました", "&b-You bought a $name hat")
                                    .setAttribute("$name", hatName)
                                    .color()
                                    .setReceiver(player)
                                    .sendActionBarMessage();

                            //表示を更新する
                            user.inventoryUserInterfaces.openItemUI();
                        });

                        s.icon(i -> {
                            i.material = clonedHatItem;

                            i.displayName = BilingualText.stream("&c-$name", "&c-$name")
                                    .textBy(player)
                                    .setAttribute("$name", hatName)
                                    .color()
                                    .toString();

                            List<String> lore = new ArrayList<>();


                            lore.add(0,BilingualText.stream("&8-Item", "&8-Item")
                                    .textBy(player)
                                    .setAttribute("$coins", value)
                                    .color()
                                    .toString());
                            lore.add(1,"");
                            lore.add(2,BilingualText.stream("&7-コスト: &6-$coins", "&7-Cost: &6-$coins")
                                    .textBy(player)
                                    .setAttribute("$coins", value)
                                    .color()
                                    .toString());
                            lore.add(3,"");
                            lore.add(4,
                                    BilingualText.stream("&e-クリックで購入", "&e-Click to purchase!")
                                            .textBy(player)
                                            .color()
                                            .toString()
                            );

                            i.lore = lore;
                        });
                    }else{
                        s.onClick(e -> ERROR_SE.play(player));

                        s.icon(i -> {
                            i.material = clonedHatItem;

                            i.displayName = BilingualText.stream("&a-$name", "&a-$name")
                                    .textBy(player)
                                    .setAttribute("$name", hatName)
                                    .color()
                                    .toString();

                            List<String> lore = new ArrayList<>();

                            lore.add(0,BilingualText.stream("&8-Item", "&8-Item")
                                    .textBy(player)
                                    .setAttribute("$coins", value)
                                    .color()
                                    .toString());
                            lore.add(1,"");
                            lore.add(2,BilingualText.stream("&7-コスト: &6-$coins", "&7-Cost: &6-$coins")
                                    .textBy(player)
                                    .setAttribute("$coins", value)
                                    .color()
                                    .toString());
                            lore.add(3,"");
                            lore.add(4,
                                    BilingualText.stream("&c-コインが足りないため購入出来ません。", "&c-You don't have enough coins!")
                                            .textBy(player)
                                            .color()
                                            .toString()
                            );


                            i.lore = lore;
                        });
                    }

                });
            }
        },21,10,1);
    }

    private boolean isSameHat(String hatName, ItemStack helmet){
        return helmet != null /*&& helmet.getType() == Material.PLAYER_HEAD*/ /*&& helmet.hasItemMeta()*/ && hatName.endsWith(helmet.getItemMeta().getDisplayName());
    }

}
