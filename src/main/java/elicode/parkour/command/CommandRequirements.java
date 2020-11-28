package elicode.parkour.command;

public class CommandRequirements {

    //コマンドの実行に必要な権限
    public Permission permission;

    // プレーヤーである必要があります
    public boolean playerOnly;
    // メンバーである必要があります
    public boolean memberOnly;

    // Edge case handling
    public boolean errorOnManyArgs;
    public boolean disableOnLock;

    private CommandRequirements(Permission permission, boolean playerOnly, boolean memberOnly) {
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.memberOnly = memberOnly;
    }

    public boolean computeRequirements(Sender sender, boolean informIfNot) {
        // パーミッションが入力されていないなら trueを返す。
        if (permission == null) {
            return true;
        }

     /*   if (sender != null) {
            // playerであるか？
            if (!context.fPlayer.hasFaction() && memberOnly) {
                if (informIfNot) context.msg(TL.GENERIC_MEMBERONLY);
                return false;
            }

            if (context.fPlayer.isAdminBypassing()) return true;


            if (!FactionsPlugin.getInstance().perm.has(context.sender, permission.node, informIfNot)) return false;

        } else {
            if (playerOnly) {
                if (informIfNot) context.sender.sendMessage(TL.GENERIC_PLAYERONLY.toString());
                return false;
            }
            return sender.hasPermission(permission.node);
        }*/
        return false;
    }

    public static class Builder {

        private Permission permission;

        private boolean playerOnly = false;
        private boolean memberOnly = false;

        private boolean errorOnManyArgs = true;
        private boolean disableOnLock = true;

        public Builder(Permission permission) {
            this.permission = permission;
        }

        public Builder playerOnly() {
            playerOnly = true;
            return this;
        }

        public Builder memberOnly() {
            playerOnly = true;
            memberOnly = true;
            return this;
        }

        public CommandRequirements build() {
            CommandRequirements requirements = new CommandRequirements(permission, playerOnly, memberOnly);
            requirements.errorOnManyArgs = errorOnManyArgs;
            requirements.disableOnLock = disableOnLock;
            return requirements;
        }

        public Builder noErrorOnManyArgs() {
            errorOnManyArgs = false;
            return this;
        }

        public Builder noDisableOnLock() {
            disableOnLock = false;
            return this;
        }

    }

}