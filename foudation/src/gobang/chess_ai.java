package gobang;

public class chess_ai {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    chess_ai(int x, int y){
        this.x=x;
        this.y=y;
    }
}
