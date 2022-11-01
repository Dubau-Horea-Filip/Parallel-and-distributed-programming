public class Vector {
    private int j, i, k;

    public Vector(int i, int j, int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }

    public int scalarProduct(Vector v) {
        return this.i * v.getI() + this.j * v.getJ() + this.k * v.getK();
    }

    public int getJ() {
        return j;
    }

    public int getI() {
        return i;
    }

    public int getK() {
        return k;
    }
}
