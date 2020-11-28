package elicode.parkour.command;

public enum Permission {

    COIN("coin"),
    LINK("link"),
    PARKOUR("parkour");

    public final String node;

    Permission(final String node) {
        this.node = "parkour." + node;
    }

    /*public boolean has(Sender sender, boolean informSenderIfNot) {
        return FactionsPlugin.getInstance().perm.has(sender, this.node, informSenderIfNot);
    }

    public boolean has(Sender sender) {
        return has(sender, false);
    }*/

}
