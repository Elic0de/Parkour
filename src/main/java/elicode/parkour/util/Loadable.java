package elicode.parkour.util;

public interface Loadable {

    void handleLoad() throws Exception;

    void handleUnload() throws Exception;
}
