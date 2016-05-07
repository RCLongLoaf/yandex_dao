//Задание 4

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Task4 {
    private static int[] count;
    private static int[] par;
    private static int sum;
    private static Scanner in;
    private static PrintWriter out;
    private static String[] tokens;
    private static boolean isQuit = false;

    private static void init(int[] arg) {
        in = new Scanner(System.in);
        out = new PrintWriter(System.out);
        Arrays.sort(arg, 0, arg.length - 1);
        par = arg;
        count = new int[par.length];
        for (int a : count) {
            a = 0;
        }
        sum = 0;
    }

    private static void readLine() {
        tokens = in.nextLine().trim().split("[ ]+");
    }

    private static boolean isInteger(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    private static void put() {
        if (tokens.length == 3 && isInteger(tokens[1]) && isInteger(tokens[2])) {
            int d = Integer.valueOf(tokens[1]);
            int c = Integer.valueOf(tokens[2]);
            int ind = Arrays.binarySearch(par, d);
            if (ind >= 0) {
                count[ind] += c;
                sum += d * c;
                out.println("всего " + sum);
            } else {
                out.println("купюры с номиналом " + d + " не существует");
            }
        } else {
            out.println("аргументы некорректны");
        }
    }


    private static void get() {
        if (tokens.length == 2 && isInteger(tokens[1])) {
            int need = Integer.valueOf(tokens[1]);
            boolean[][] dp = new boolean[par.length][need + 1];
            for (int i = 0; i < par.length; i++) {
                for (int j = 0; j <= need; j++) {
                    dp[i][j] = false;
                }
            }
            for (int i = 0; i <= Math.min(need, count[0] * par[0]); i += par[0]) {
                dp[0][i] = true;
            }
            for (int i = 1; i < par.length; i++) {
                for (int j = 0; j <= need; j++) {
                    if (dp[i - 1][j]) {
                        for (int k = j; k <= Math.min(need, j + count[i] * par[i]); k += par[i]) {
                            dp[i][k] = true;
                        }
                    }
                }
            }
            int ans = 0;
            for (int i = need; i >= 0; i--) {
                if (dp[par.length - 1][i]) {
                    ans = i;
                    break;
                }
            }
            int ind = ans;
            int tmp;
            for (int i = par.length - 1; i > 0; i--) {
                for (int j = Math.max(ind % par[i], ind - count[i] * par[i]); j <= ind; j += par[i]) {
                    if (dp[i][j]) {
                        tmp = (ind - j) / par[i];
                        if (tmp != 0) {
                            out.print(par[i] + "=" + tmp + ",");
                            count[i] -= tmp;
                            sum -= tmp * par[i];
                        }
                        ind = j;
                        break;
                    }
                }
            }
            tmp = ind / par[0];
            if (tmp != 0) {
                out.print(par[0] + "=" + (tmp) + ",");
                count[0] -= tmp;
                sum -= tmp * par[0];
            }
            out.println(((ind != ans) ? " " : "") + "всего " + ans);
            if (need != ans) {
                out.println("без " + (need - ans));
            }
        } else {
            out.println("аргументы некорректны");
        }
    }

    private static void dump() {
        if (tokens.length == 1) {
            for (int i = par.length - 1; i >= 0; i--) {
                out.println(par[i] + "=" + count[i]);
            }
        } else {
            out.println("неизвестные аргументы");
        }
    }

    private static void state() {
        if (tokens.length == 1) {
            out.println("всего " + sum);
        } else {
            out.println("неизвестные аргументы");
        }
    }

    private static void quit() {
        if (tokens.length == 1) {
            isQuit = true;
        } else {
            out.println("неизвестные аргументы");
        }
    }

    public static void main(String[] args) {
        init(new int[]{1, 3, 5, 10, 25, 50, 100, 500, 1000, 5000});
        while (!isQuit && in.hasNext()) {
            readLine();
            switch (tokens[0]) {
                case "put":
                    put();
                    break;
                case "get":
                    get();
                    break;
                case "dump":
                    dump();
                    break;
                case "state":
                    state();
                    break;
                case "quit":
                    quit();
                    break;
                default:
                    if (!tokens[0].isEmpty()) {
                        out.println("неизвестная функция");
                    }
            }
            out.flush();
        }
        in.close();
        out.close();
    }
}
