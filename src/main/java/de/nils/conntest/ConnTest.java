package de.nils.conntest;

import de.nils.conntest.exception.UncaughtHandler;
import de.nils.conntest.gui.UIStart;

public class ConnTest
{
    public static void main(String[] args)
    {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler());

        new UIStart();
    }
}