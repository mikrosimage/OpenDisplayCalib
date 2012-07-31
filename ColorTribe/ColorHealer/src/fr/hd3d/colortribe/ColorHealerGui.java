package fr.hd3d.colortribe;

import fr.hd3d.colortribe.core.probes.K10Probe;
import fr.hd3d.colortribe.gui.HealerMainWindow;
import fr.hd3d.colortribe.gui.HealerWaitingWindow;


/**
 * 
 * @author mfe
 * 
 */

public class ColorHealerGui
{
    public static HealerWaitingWindow waitingFrame;
    public static HealerMainWindow mainWindow;

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if(args.length == 1){
            K10Probe.useManualPort = true;
            K10Probe.manualPort = args[0];
            System.out.println("/!\\ use manual port "+ K10Probe.manualPort +" for K10 !");
        }
        waitingFrame = HealerWaitingWindow._instance;
        mainWindow = HealerMainWindow._instance;
        waitingFrame.open();
    }
}
