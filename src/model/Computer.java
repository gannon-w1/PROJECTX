package model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class Computer extends Player {
    private final boolean[][] tried;
    private final Random rand;

    // targeting state
    private final Deque<int[]> targets; // candidate targets when in target mode
    private final List<int[]> hits; // consecutive hits for current target ship
    private int orientation; // -1 unknown, 0 horizontal, 1 vertical

    public Computer() {
        super();
        tried = new boolean[10][10];
        rand = new Random();
        targets = new ArrayDeque<>();
        hits = new ArrayList<>();
        orientation = -1;
        placeShipsRandomly();
    }

    // place ships randomly onto this computer player's own board
    private void placeShipsRandomly() {
        int[] lengths = {5, 4, 3, 2, 1};
        for (int i = 0; i < lengths.length; i++) {
            int len = lengths[i];
            int type = i + 1;
            boolean placed = false;
            while (!placed) {
                boolean horizontal = rand.nextBoolean();
                int r = horizontal ? rand.nextInt(10) : rand.nextInt(10 - len + 1);
                int c = horizontal ? rand.nextInt(10 - len + 1) : rand.nextInt(10);
                PlacedShip s = new PlacedShip(r, c, len, horizontal, type);
                placed = placeShip(s);
            }
        }
    }

    // Select next target. Prefer queued targets (from hits), else random hunt.
    public int[] selectTarget() {
        int[] t = null;
        // pop until we find an untried candidate
        while (!targets.isEmpty()) {
            int[] cand = targets.pollFirst();
            if (!tried[cand[0]][cand[1]]) {
                t = cand;
                break;
            }
        }

        if (t == null) {
            // random hunt
            int r, c;
            do {
                r = rand.nextInt(10);
                c = rand.nextInt(10);
            } while (tried[r][c]);
            t = new int[]{r, c};
        }

        tried[t[0]][t[1]] = true;
        return t;
    }

    // Called by UI after firing to inform AI of the result so it can update targets
    public void notifyResult(int row, int col, String result) {
        if (result == null) return;

        switch (result) {
            case "hit":
                hits.add(new int[]{row, col});
                if (hits.size() == 1) {
                    // add neighbors to try
                    addNeighborCandidates(row, col);
                } else if (hits.size() >= 2) {
                    // determine orientation if unknown
                    if (orientation == -1) determineOrientation();
                    // rebuild targets along orientation
                    rebuildOrientationTargets();
                }
                break;
            case "sunk":
                // clear current target state
                hits.clear();
                targets.clear();
                orientation = -1;
                break;
            case "miss":
            default:
                // miss -> nothing to add (already marked tried in selectTarget)
                break;
        }
    }

    private void addNeighborCandidates(int r, int c) {
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        List<int[]> list = new ArrayList<>();
        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (isValid(nr, nc) && !tried[nr][nc]) list.add(new int[]{nr, nc});
        }
        // randomize neighbor order to avoid predictable behavior
        Collections.shuffle(list, rand);
        for (int[] p : list) targets.addLast(p);
    }

    private void determineOrientation() {
        if (hits.size() < 2) return;
        int[] a = hits.get(0);
        int[] b = hits.get(1);
        if (a[0] == b[0]) orientation = 0; // same row -> horizontal
        else if (a[1] == b[1]) orientation = 1; // same col -> vertical
    }

    private void rebuildOrientationTargets() {
        if (orientation == -1 || hits.isEmpty()) return;
        targets.clear();
        // sort hits along orientation
        hits.sort((p1,p2) -> orientation == 0 ? Integer.compare(p1[1], p2[1]) : Integer.compare(p1[0], p2[0]));
        // try extending both ends
        int[] first = hits.get(0);
        int[] last = hits.get(hits.size()-1);
        if (orientation == 0) {
            // horizontal: try left of first and right of last
            int leftCol = first[1] - 1;
            if (isValid(first[0], leftCol) && !tried[first[0]][leftCol]) targets.addLast(new int[]{first[0], leftCol});
            int rightCol = last[1] + 1;
            if (isValid(last[0], rightCol) && !tried[last[0]][rightCol]) targets.addLast(new int[]{last[0], rightCol});
        } else {
            // vertical: try above first and below last
            int upRow = first[0] - 1;
            if (isValid(upRow, first[1]) && !tried[upRow][first[1]]) targets.addLast(new int[]{upRow, first[1]});
            int downRow = last[0] + 1;
            if (isValid(downRow, last[1]) && !tried[downRow][last[1]]) targets.addLast(new int[]{downRow, last[1]});
        }
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < 10 && c >= 0 && c < 10;
    }

    public void markTried(int r, int c) {
        if (r >= 0 && r < 10 && c >= 0 && c < 10) tried[r][c] = true;
    }
}