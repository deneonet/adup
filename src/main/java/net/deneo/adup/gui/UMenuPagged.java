package net.deneo.adup.gui;

import org.mineacademy.fo.menu.MenuPagged;

import java.util.List;

public abstract class UMenuPagged<T> extends MenuPagged<T> {
    public UMenuPagged(List<T> list) {
        super(list);
    }

    public abstract void update();
}