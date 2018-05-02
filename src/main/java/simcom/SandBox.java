package simcom;

public class SandBox {

    private static long pascalTriangle(int r, int k) {
        if(r == 1 || k <= 1 || k >= r) return 1L;
        return pascalTriangle(r - 1, k - 1) + pascalTriangle(r - 1, k);
    }

    public static void main(String[] args) {

        System.out.println("PascalTriangle(5, 3) = " + pascalTriangle(5, 3));
    }
}
