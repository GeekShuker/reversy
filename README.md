# Reversi Game

A fully playable implementation of the classic **Reversi (Othello)** board game, built in Java with a Swing GUI. Players compete on an 8×8 board to end with the most discs of their color by surrounding and flipping the opponent's discs. This project extends the standard rules with three disc types and two AI strategies, and is built around an interface-driven architecture that cleanly separates game logic from the GUI.

---

## Features

- **Game modes** — Human vs Human, Human vs AI, AI vs AI (configured at startup via player-selection dialog)
- **AI strategies**
  - `RandomAI` — picks a random legal move, optionally using special disc types
  - `GreedyAI` — always picks the move that flips the most opponent discs; breaks ties by rightmost column then lowest row
- **Special disc types**
  - `SimpleDisc` (`⬤`) — standard disc, flips normally
  - `UnflippableDisc` (`⭕`) — cannot be flipped once placed; each player gets 2 per game
  - `BombDisc` (`💣`) — when flipped, triggers a chain explosion that flips all surrounding non-unflippable discs, including other bombs recursively; each player gets 3 per game
- **Undo** — reverts the last move including all flipped discs and restores special disc counts (Human vs Human only)
- **Reset** — restarts the game from the initial board position
- **Win tracking** — cumulative win counter per player across resets

---

## Project Structure

| File | Role |
|------|------|
| `Main.java` | Entry point; launches GUI, shows player-selection dialog, wires players to game logic |
| `PlayableLogic.java` | Interface defining the full game API (place disc, valid moves, flip count, undo, reset, …) |
| `GameLogic.java` | Implements `PlayableLogic`: board state, move validation, disc flipping, bomb chain reaction, undo stack |
| `GUI_for_chess_like_games.java` | Swing GUI: renders the board, handles click events, calls into `PlayableLogic` |
| `Player.java` | Abstract base: tracks wins and special disc inventory |
| `HumanPlayer.java` | Concrete player; moves driven by GUI clicks |
| `AIPlayer.java` | Abstract AI base with a registry pattern for dynamically registering AI types |
| `RandomAI.java` | AI strategy: selects a random position and random disc type |
| `GreedyAI.java` | AI strategy: maximizes immediate flips |
| `Disc.java` | Abstract disc base class |
| `SimpleDisc.java` | Standard flippable disc |
| `UnflippableDisc.java` | Special disc immune to flipping |
| `BombDisc.java` | Special disc that triggers chain explosions when flipped |
| `Position.java` | Immutable (row, col) value type with correct `equals`/`hashCode` |
| `Move.java` | Pairs a `Position` with the `Disc` placed there; used in the undo history |

---

## How to Compile and Run

All source files are in `src/`. The `lib/` directory contains the JUnit JARs needed to compile (no runtime dependency).

**Compile:**
```bash
# From the project root
javac -cp "lib/*" -d out src/*.java
```

**Run:**
```bash
java -cp out Main
```

A player-selection dialog appears on startup. Choose Human or an AI type (RandomAI / GreedyAI) for each player and click OK.

---

## Design Notes

**`PlayableLogic` interface** — the GUI talks only to this interface, so the game logic is fully swappable without touching the UI.

**AI registry** — `AIPlayer` uses a `Map<String, Class<? extends AIPlayer>>` registry. New AI types can be added by calling `registerAIPlayerType(name, class)` in `registerAllAIPlayers()` and they automatically appear in the player-selection dropdown.

**Bomb chain reaction** — implemented as a recursive DFS (`trigerbomb`) that marks visited positions in the flip set to avoid infinite loops when two bombs are neighbors.

**Undo stack** — every call to `locate_disc` pushes the set of flipped positions onto a `Stack<HashSet<Position>>` (`LastMoves`). Undo pops this set and re-flips all positions back to the opponent, then restores any special disc that was consumed.

**Special disc inventory** — each player starts with 3 bombs and 2 unflippable discs per game. The counts are decremented on placement and restored on undo.
