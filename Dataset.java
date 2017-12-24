public class Dataset {

    private double[][] data;

    public Dataset(int row, int col) {
        data = new double[row][];
        for (int i = 0; i < row; i++) {
            data[i] = new double[col];
        }
    }

    public double[][] get() {
        // return reference
        return data;
    }

    public double get(int row, int col) {
        if (row > data.length || col > data[0].length) {
            throw new IndexOutOfBoundsException("Index problem; row, col: " + row + ", " + col);
        }
        return data[row][col];
    }

    public void set(int row, int col, double item) {
        if (row > data.length || col > data[0].length) {
            throw new IndexOutOfBoundsException("Index problem; row, col: " + row + ", " + col);
        }
        data[row][col] = item;
    }

    public void standardize() {
        throw new UnsupportedOperationException("standardize not implemented yet!");
    }
}