# Battleship Backend API

This document defines the backend “contract” for the Battleship game.
It lists all classes, methods, their purposes, inputs, outputs, and notes for frontend integration.

---

## Classes

### 1. Battleship

Manages the overall game state, players, turns, and winner detection.

| Method                     | Purpose                                   | Input              | Output                                   | Notes                                             |
| -------------------------- | ----------------------------------------- | ------------------ | ---------------------------------------- | ------------------------------------------------- |
| `getCurrentPlayer()`       | Returns the player whose turn it is       | none               | `Player`                                 | GUI uses this to highlight current player         |
| `getOpponent()`            | Returns the opposing player               | none               | `Player`                                 | GUI can access opponent's board for firing        |
| `nextTurn()`               | Switches turns between players            | none               | void                                     | Call after a valid move                           |
| `isGameOver()`             | Checks if the game has ended              | none               | boolean                                  | True if all ships of a player are sunk            |
| `getWinner()`              | Returns the winning player (if game over) | none               | `Player` or `null`                       | Null if game not over                             |
| `fireAt(int row, int col)` | Current player fires at opponent          | `int row, int col` | `"hit"`, `"miss"`, `"sunk"`, `"already"` | Optional helper for GUI to make moves in one call |

---

### 2. Player

Represents a player and delegates board operations.

| Method                                      | Purpose                             | Input                        | Output                                   | Notes                              |
| ------------------------------------------- | ----------------------------------- | ---------------------------- | ---------------------------------------- | ---------------------------------- |
| `placeShip(PlacedShip ship)`                | Places a ship on the player’s board | `PlacedShip`                 | boolean                                  | Returns false if placement invalid |
| `fireAt(Player opponent, int row, int col)` | Fires at a cell on opponent’s board | `Player`, `int row, int col` | `"hit"`, `"miss"`, `"sunk"`, `"already"` | GUI can call this per move         |
| `allShipsSunk()`                            | Checks if all ships are sunk        | none                         | boolean                                  | Used to check game over            |
| `getBoard()`                                | Returns the player’s board          | none                         | `Board`                                  | GUI can access board grid          |

---

### 3. Board

Represents the 10x10 grid and tracks ships and hits/misses.

| Method                       | Purpose                                   | Input              | Output                                   | Notes                                                       |
| ---------------------------- | ----------------------------------------- | ------------------ | ---------------------------------------- | ----------------------------------------------------------- |
| `placeShip(PlacedShip ship)` | Adds ship to the board                    | `PlacedShip`       | boolean                                  | Returns false if invalid or overlapping                     |
| `canPlace(PlacedShip ship)`  | Checks if a ship can be placed            | `PlacedShip`       | boolean                                  | GUI can optionally validate placement                       |
| `fireAt(int row, int col)`   | Fires at a cell                           | `int row, int col` | `"hit"`, `"miss"`, `"sunk"`, `"already"` | Updates grid and ship hit state                             |
| `allShipsSunk()`             | Checks if all ships on the board are sunk | none               | boolean                                  |                                                             |
| `getGrid()`                  | Returns board grid                        | none               | `int[][]`                                | Grid values: `0 = empty`, `1 = ship`, `2 = hit`, `3 = miss` |
| `getShips()`                 | Returns list of ships                     | none               | `List<PlacedShip>`                       |                                                             |

---

### 4. PlacedShip

Represents a single ship and tracks hits.

| Method                       | Purpose                        | Input              | Output                                       | Notes                      |
| ---------------------------- | ------------------------------ | ------------------ | -------------------------------------------- | -------------------------- |
| `contains(int row, int col)` | Checks if ship occupies a cell | `int row, int col` | boolean                                      | Used for hit detection     |
| `hit()`                      | Registers a hit on the ship    | none               | void                                         | Updates internal hit count |
| `isSunk()`                   | Checks if ship is sunk         | none               | boolean                                      | True if hits >= length     |
| Getters                      | Returns ship properties        | none               | `row`, `col`, `length`, `horizontal`, `type` |                            |

---

## Notes for GUI Integration

* GUI calls `Battleship.getCurrentPlayer()` to highlight whose turn it is.
* GUI calls `Battleship.fireAt(row,col)` or `Player.fireAt(opponent,row,col)` to make moves.
* After a valid move, GUI calls `Battleship.nextTurn()` to switch turns.
* GUI checks `Battleship.isGameOver()` and `getWinner()` to display results.
* Ship images are matched using the `type` from `PlacedShip` (`1 = Carrier`, `2 = Battleship`, etc.).
* Grid values (`0-3`) from `Board.getGrid()` can be used to render hits/misses in the GUI.

---

## Example Usage

```java
Battleship game = new Battleship();

// Place ships for player 1
game.getCurrentPlayer().placeShip(new PlacedShip(0, 0, 5, true, 1));

// Player fires at opponent
String result = game.fireAt(0, 0);
if(result.equals("hit")) {
    System.out.println("Hit successful!");
}

// Switch turn
game.nextTurn();

// Check if game is over
if(game.isGameOver()) {
    Player winner = game.getWinner();
    System.out.println("Winner: " + (winner == game.getCurrentPlayer() ? "Player 1" : "Player 2"));
}
```
