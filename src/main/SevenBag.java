package main;

import mino.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SevenBag {
    private ArrayList<Mino> bag = new ArrayList<>();

    public SevenBag() {
        refillBag();
    }

    private void refillBag() {
        bag.clear();
        bag.add(new Mino_L1());
        bag.add(new Mino_L2());
        bag.add(new Mino_Square());
        bag.add(new Mino_Bar());
        bag.add(new Mino_T());
        bag.add(new Mino_Z1());
        bag.add(new Mino_Z2());
        Collections.shuffle(bag, new Random());
    }

    public Mino getNextMino() {
        if (bag.isEmpty()) {
            refillBag();
        }
        return bag.remove(0);
    }
}