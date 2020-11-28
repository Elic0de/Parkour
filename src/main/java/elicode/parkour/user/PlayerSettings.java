package elicode.parkour.user;

import elicode.parkour.function.ToggleHideMode;
import elicode.parkour.function.ToggleNightVisionMode;
import elicode.parkour.util.yaml.Yaml;


public class PlayerSettings {

    public boolean hideMode;
    public boolean nightVisionMode;
    public boolean particle;
    public boolean checkArenaNotification;

    public PlayerSettings(Yaml yaml){

        hideMode = yaml.getBoolean("Hide mode");
        nightVisionMode = yaml.getBoolean("NighiVision mode");
        particle = yaml.getBoolean("Particle");
        checkArenaNotification = yaml.getBoolean("CheckArea Notification");


    }

    public void loadScoreboard(User user) {
        ToggleNightVisionMode.getInstance().update(user);
        ToggleHideMode.getInstance().update(user);
    }

    public void save(Yaml yaml){


        yaml.set("Particle", particle);
        yaml.set("Hide mode", hideMode);
        yaml.set("NightVision mode", nightVisionMode);
        yaml.set("CheckArea Notification", checkArenaNotification);
    }


}
