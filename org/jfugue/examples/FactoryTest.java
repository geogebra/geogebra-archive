package org.jfugue.examples;

import org.jfugue.*;
import org.jfugue.extras.*;

public class FactoryTest
{
    public static void main(String[] args)
    {
        Player player = new Player();


        RockPatternFactory rpf = new RockPatternFactory();
        Pattern p = rpf.getDemo();
        System.out.println(p.getMusicString());
        player.play(p);
//        player.save(p,"rockdemo.mid");

//Pattern p = new Pattern("V0 A5q");
//DurationPatternTool dpt = new DurationPatternTool();
//dpt.execute(p);
//System.out.println(dpt.getResult());
//
//p.setMusicString("V0 A5q V1 A5q");
//dpt.execute(p);
//System.out.println(dpt.getResult());

        // Exit the program
        System.exit(0);
    }
}


