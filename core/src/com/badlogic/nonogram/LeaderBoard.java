package com.badlogic.nonogram;

import java.util.Vector;

public class LeaderBoard
{
    public Vector<String> names;
    public Vector<String> times;

    public void sort() {
        for (int i = 0; i < this.times.size(); i++)
            for (int j = 0; j < this.times.size()-i-1; j++)
                if (Integer.parseInt(this.times.get(j)) > Integer.parseInt(this.times.get(j+1)))
                {
                    String tmp = this.times.get(j);
                    this.times.set(j, this.times.get(j + 1));
                    this.times.set(j + 1, tmp);

                    tmp = this.names.get(j);
                    this.names.set(j, this.names.get(j + 1));
                    this.names.set(j + 1, tmp);
                }
    }
}