public class Program {
    public static void main(String[] args) {
        var solution = new Solution();
        System.out.println(solution.canReachCorner(
                500000000, 500000000, new int[][] {
                        new int[] { 499980000, 699999999, 200000000 },
                        new int[] { 500020000, 300000001, 200000000 } }) == false ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                8, 6, new int[][] {
                        new int[] { 6, 1, 1 },
                        new int[] { 7, 2, 1 },
                        new int[] { 6, 3, 1 },
                        new int[] { 3, 4, 2 },
                        new int[] { 6, 2, 2 },
                        new int[] { 4, 4, 2 },
                        new int[] { 7, 3, 1 } }) == false ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                20, 100, new int[][] {
                        new int[] { 1, 102, 18 },
                        new int[] { 50, 60, 48 } }) == false ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                3, 4, new int[][] {
                        new int[] { 2, 1, 1 } }) == true ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                3, 3, new int[][] {
                        new int[] { 1, 1, 2 } }) == false ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                3, 3, new int[][] {
                        new int[] { 2, 1, 1 }, new int[] { 1, 2, 1 } }) == false ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                4, 4, new int[][] {
                        new int[] { 5, 5, 1 } }) == true ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                5, 9, new int[][] {
                        new int[] { 4, 7, 1 },
                        new int[] { 2, 1, 1 },
                        new int[] { 4, 7, 1 },
                        new int[] { 3, 7, 1 },
                        new int[] { 4, 1, 1 },
                        new int[] { 4, 7, 1 },
                        new int[] { 1, 5, 1 } }) == true ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                6, 13, new int[][] {
                        new int[] { 1, 5, 1 },
                        new int[] { 1, 5, 1 },
                        new int[] { 5, 7, 1 },
                        new int[] { 3, 7, 2 },
                        new int[] { 5, 5, 1 },
                        new int[] { 2, 10, 1 },
                        new int[] { 2, 1, 1 } }) == false ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                15, 15, new int[][] {
                        new int[] { 1, 99, 85 },
                        new int[] { 99, 1, 85 } }) == true ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                5, 8, new int[][] {
                        new int[] { 4, 7, 1 } }) == false ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                13, 13, new int[][] {
                        new int[] { 10, 5, 3 },
                        new int[] { 1, 2, 1 },
                        new int[] { 3, 8, 1 },
                        new int[] { 2, 12, 1 },
                        new int[] { 10, 1, 1 },
                        new int[] { 7, 4, 1 },
                        new int[] { 4, 5, 3 } }) == true ? "CORRECT" : "INCORRECT");

        System.out.println(solution.canReachCorner(
                283239, 179963,
                new int[][] {
                        new int[] { 248866, 18768, 15302 },
                        new int[] { 118187, 107493, 44573 },
                        new int[] { 108498, 120943, 43664 },
                        new int[] { 153333, 112887, 34787 },
                        new int[] { 177345, 57622, 13897 },
                        new int[] { 110613, 49502, 49502 },
                        new int[] { 55969, 48432, 13190 },
                        new int[] { 77476, 58814, 35515 },
                        new int[] { 143118, 79684, 31 } }) == true ? "CORRECT" : "INCORRECT");

    }
}