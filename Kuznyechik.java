import java.util.Arrays;

import static java.lang.Math.min;

public class Kuznyechik {
    //табличка для нелинейного биективного преобразования
    private static final short[] pi = new short[] {
            252, 238, 221, 17, 207, 110, 49, 22, 251, 196, 250, 218, 35, 197, 4, 77,
            233, 119, 240, 219, 147, 46, 153, 186, 23, 54, 241, 187, 20, 205, 95, 193,
            249, 24, 101, 90, 226, 92, 239, 33, 129, 28, 60, 66, 139, 1, 142, 79,
            5, 132, 2, 174, 227, 106, 143, 160, 6, 11, 237, 152, 127, 212, 211, 31,
            235, 52, 44, 81, 234, 200, 72, 171, 242, 42, 104, 162, 253, 58, 206, 204,
            181, 112, 14, 86, 8, 12, 118, 18, 191, 114, 19, 71, 156, 183, 93, 135,
            21, 161, 150, 41, 16, 123, 154, 199, 243, 145, 120, 111, 157, 158, 178, 177,
            50, 117, 25, 61, 255, 53, 138, 126, 109, 84, 198, 128, 195, 189, 13, 87,
            223, 245, 36, 169, 62, 168, 67, 201, 215, 121, 214, 246, 124, 34, 185, 3,
            224, 15, 236, 222, 122, 148, 176, 188, 220, 232, 40, 80, 78, 51, 10, 74,
            167, 151, 96, 115, 30, 0, 98, 68, 26, 184, 56, 130, 100, 159, 38, 65,
            173, 69, 70, 146, 39, 94, 85, 47, 140, 163, 165, 125, 105, 213, 149, 59,
            7, 88, 179, 64, 134, 172, 29, 247, 48, 55, 107, 228, 136, 217, 231, 137,
            225, 27, 131, 73, 76, 63, 248, 254, 141, 83, 170, 144, 202, 216, 133, 97,
            32, 113, 103, 164, 45, 43, 9, 91, 203, 155, 37, 208, 190, 229, 108, 82,
            89, 166, 116, 210, 230, 244, 180, 192, 209, 102, 175, 194, 57, 75, 99, 182
    };

    //табличка для обратного нелинейного биективного преобразования
    private static final short[] piInv = new short[] {
            165, 45, 50, 143, 14, 48, 56, 192, 84, 230, 158, 57, 85, 126, 82, 145,
            100, 3, 87, 90, 28, 96, 7, 24, 33, 114, 168, 209, 41, 198, 164, 63,
            224, 39, 141, 12, 130, 234, 174, 180, 154, 99, 73, 229, 66, 228, 21, 183,
            200, 6, 112, 157, 65, 117, 25, 201, 170, 252, 77, 191, 42, 115, 132, 213,
            195, 175, 43, 134, 167, 177, 178, 91, 70, 211, 159, 253, 212, 15, 156, 47,
            155, 67, 239, 217, 121, 182, 83, 127, 193, 240, 35, 231, 37, 94, 181, 30,
            162, 223, 166, 254, 172, 34, 249, 226, 74, 188, 53, 202, 238, 120, 5, 107,
            81, 225, 89, 163, 242, 113, 86, 17, 106, 137, 148, 101, 140, 187, 119, 60,
            123, 40, 171, 210, 49, 222, 196, 95, 204, 207, 118, 44, 184, 216, 46, 54,
            219, 105, 179, 20, 149, 190, 98, 161, 59, 22, 102, 233, 92, 108, 109, 173,
            55, 97, 75, 185, 227, 186, 241, 160, 133, 131, 218, 71, 197, 176, 51, 250,
            150, 111, 110, 194, 246, 80, 255, 93, 169, 142, 23, 27, 151, 125, 236, 88,
            247, 31, 251, 124, 9, 13, 122, 103, 69, 135, 220, 232, 79, 29, 78, 4,
            235, 248, 243, 62, 61, 189, 138, 136, 221, 205, 11, 19, 152, 2, 147, 128,
            144, 208, 36, 52, 203, 237, 244, 206, 153, 16, 68, 64, 146, 58, 1, 38,
            18, 26, 72, 104, 245, 129, 139, 199, 214, 32, 10, 8, 0, 76, 215, 116
    };

    //элементы поля F
    private static final short[] linearValues = new short[] {
            148, 32, 133, 16, 194, 192, 1, 251, 1, 192, 194, 16, 133, 32, 148, 1
    };

    private static final int mod = Integer.parseUnsignedInt("111000011", 2);

    //вспомогательные функции для работы с массивами типа long, так как блочный шифр работает со 128 битными векторами
    //проверка на нулевой вектор
    private static boolean isZero(long[] x) {
        for (long l : x) {
            if (l != 0) {
                return false;
            }
        }
        return true;
    }

    //xor двух массивов типа long
    private static long[] xor(long[] x, long[] y) {
        int i = 1;
        int minLen = min(x.length, y.length);
        long[] z = new long[minLen];
        while (minLen - i >= 0) {
            z[minLen - i] = x[x.length - i] ^ y[y.length - i];
            i++;
        }
        return z;
    }

    //сдвиг вправо массива типа long как единого вектора 128/256 бит, plaintext/key
    private static long[] shiftRight(long[] x, int n) {
        long[] result = Arrays.copyOf(x, x.length);
        int nInv = 64 - n;
        long localMask = -1L >>> nInv;
        long buf = 0;
        long bufSup;
        for (int i = 0; i < result.length; i++) {
            bufSup = (result[i] & localMask) << nInv;
            result[i] >>>= n;
            result[i] ^= buf;
            buf = bufSup;
        }
        return result;
    }

    //сдвиг влево массива типа long как единого вектора 128/256 бит, plaintext/key
    private static long[] shiftLeft(long[] x, int n) {
        long[] result = Arrays.copyOf(x, x.length);
        int nInv = 64 - n;
        long localMAsk = -1L << nInv;
        long buf = 0;
        long bufSup;
        for (int i = result.length - 1; i >= 0; i--) {
            bufSup = (result[i] & localMAsk) >>> nInv;
            result[i] <<= n;
            result[i] ^= buf;
            buf = bufSup;
        }
        return result;
    }

    //вспомогательная функция перемножения двух векторов (длины 8 бит) как полиномы
    private static int multiplyIntsAsPolynomials(int x, int y) {
        if (x == 0 || y == 0) {
            return 0;
        }
        int z = 0;
        while (x != 0) {
            if ((x & 1) == 1) {
                z ^= y;
            }
            y <<= 1;
            x >>>= 1;
        }
        return z;
    }

    //подсчет длины вектора в x, так как работа идет с векторами длины 8/16 бит
    private static int numberBits(int x) {
        int nb = 0;
        while (x != 0) {
            nb += 1;
            x >>>= 1;
        }
        return nb;
    }

    //модуль, ограничивающий значения в поле F
    private static int modIntAsPolynomial(int x, int m) {
        int nbm = numberBits(m);
        while (true) {
            int nbx = numberBits(x);
            if (nbx < nbm) {
                return x;
            }
            int mshift = m << (nbx - nbm);
            x ^= mshift;
        }
    }

    //перемножение элемента поля F с нашими блоками из сообщения, находящемся в шифраторе
    //произведение просиходит по модулю
    private static int kuznyechikMultiplication(int x, int y) {
        int z = multiplyIntsAsPolynomials(x, y);
        return modIntAsPolynomial(z, mod);
    }

    //линейное раундовое преобразование
    //вход - 128 бит
    //выход - 8 бит
    private static int kuznyechikLinearFunctional(long[] x) {
        int y = 0;
        int i = 15;
        while (!isZero(x)) {
            y ^= kuznyechikMultiplication((int) (x[1] & 0xff), linearValues[i]);
            x = shiftRight(x, 8);
            i--;
        }
        return y;
    }

    //вспомогательная функция для нелинейного биективного преобразования
    //вход - 128 бит
    //выход - 128 бит
    private long[] SSupport(long[] x, short[] neededPi) {
        long[] result = new long[2];
        for (int i = 0; i < 2; i++) {
            long y = 0L;
            for (int j = 7; j >= 0; j--) {
                y <<= 8;
                y ^= neededPi[(short) ((x[i] >>> (8 * j)) & 0xff)];
            }
            result[i] = y;
        }
        return result;
    }

    //нелинейное преобразование
    private long[] S(long[] x) {
        return SSupport(x, pi);
    }

    //обратное нелинейное преобразование
    private long[] SInv(long[] x) {
        return SSupport(x, piInv);
    }

    //Один раунд линейного преобразования
    //вход - 128 бит
    //выход - 128 бит
    private long[] R(long[] x) {
        long l = kuznyechikLinearFunctional(x);
        long[] lAtBeginning = new long[]{l << (8 * 7), 0L};
        return xor(lAtBeginning, shiftRight(x, 8));
    }

    //Один раунд обратного линейного преобразования
    //вход - 128 бит
    //выход - 128 бит
    private long[] RInv(long[] x) {
        long[] a = new long[]{0L, x[0] >>> (7 * 8)};
        x = shiftLeft(x, 8);
        int b = kuznyechikLinearFunctional(xor(x, a));
        x[1] ^= b;
        return x;
    }

    //Вызов линейного преобразования
    //вход - 128 бит
    //выход - 128 бит
    private long[] L(long[] x) {
        for (int i = 0; i < 16; i ++) {
            x = R(x);
        }
        return x;
    }

    //Вызов обратного линейного преобразования
    //вход - 128 бит
    //выход - 128 бит
    private long[] LInv(long[] x) {
        for (int i = 0; i < 16; i ++) {
            x = RInv(x);
        }
        return x;
    }

    //генерация ключей
    //вход - 256 бит
    //выход - 128 бит (раундовый ключ) x10
    private long[][] kuznyechikKeySchedule(long[] key) {
        long[][] roundKeys = new long[10][2];
        long[] a = new long[]{key[0], key[1]};
        long[] b = new long[]{key[2], key[3]};
        roundKeys[0] = Arrays.copyOf(a, a.length);
        roundKeys[1] = Arrays.copyOf(b, b.length);
        long[] buf;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                long[] c = L(new long[]{0L, 8 * i + j + 1});
                buf = Arrays.copyOf(a, a.length);
                a = xor(L(S(xor(a, c))), b);
                b = buf;
            }
            roundKeys[2 * (i + 1)] = Arrays.copyOf(a, a.length);
            roundKeys[2 * (i + 1) + 1] = Arrays.copyOf(b, b.length);
        }
        return roundKeys;
    }

    public long[] encrypt(long[] x, long[] key) {
        long[][] keys = kuznyechikKeySchedule(key);
        for (int round = 0; round < 9; round++) {
            x = L(S(xor(x, keys[round])));
        }
        return xor(x, keys[9]);
    }

    public long[] decrypt(long[] x, long[] key) {
        long[][] keys = kuznyechikKeySchedule(key);
        for (int round = 9; round > 0; round--) {
            x = SInv(LInv(xor(x, keys[round])));
        }
        return xor(x, keys[0]);
    }
}