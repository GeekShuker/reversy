

public class Move {
    private Position position;
    private Disc owner;

    public Move(Position position, Disc owner) {
        this.position = position;
        this.owner = owner;
    }
    public Move(int row,int col  ,Disc owner) {
        this.position = new Position(row,col);
        this.owner = owner;
    }

    public Position position() {
        return position;
    }

    public Disc disc() {
        return owner;
    }

    @Override
    public String toString() {
        return   "Position "+ position().toString() + "Disc " + disc().getType();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move move = (Move) obj;

            return position().equals(move.position()) && disc().equals(move.disc());
        }
        return false;
    }
}
