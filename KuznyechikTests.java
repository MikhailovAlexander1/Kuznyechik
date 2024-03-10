import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class KuznyechikTests {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String[] testSInput = new String[]{
            "ffeeddccbbaa99881122334455667700", "b66cd8887d38e8d77765aeea0c9a7efc",
            "559d8dd7bd06cbfe7e7b262523280d39", "0c3322fed531e4630d80ef5c5a81c50b"};
    private static final String[] testSOutput = new String[] {
            "b66cd8887d38e8d77765aeea0c9a7efc", "559d8dd7bd06cbfe7e7b262523280d39",
            "0c3322fed531e4630d80ef5c5a81c50b", "23ae65633f842d29c5df529c13f5acda"};
    private static final String[] testRInput = new String[] {
            "00000000000000000000000000000100", "94000000000000000000000000000001",
            "a5940000000000000000000000000000", "64a59400000000000000000000000000"};
    private static final String[] testROutput = new String[] {
            "94000000000000000000000000000001", "a5940000000000000000000000000000",
            "64a59400000000000000000000000000", "0d64a594000000000000000000000000"};
    private static final String[] testLInput = new String[] {
            "64a59400000000000000000000000000", "d456584dd0e3e84cc3166e4b7fa2890d",
            "79d26221b87b584cd42fbc4ffea5de9a", "0e93691a0cfc60408b7b68f66b513c13"};
    private static final String[] testLOutput = new String[] {
            "d456584dd0e3e84cc3166e4b7fa2890d", "79d26221b87b584cd42fbc4ffea5de9a",
            "0e93691a0cfc60408b7b68f66b513c13", "e6a8094fee0aa204fd97bcb0b44b8580"};
    private static final String[] testKeyGenerateInput = new String[] {
            "8899aabbccddeeff0011223344556677fedcba98765432100123456789abcdef"};
    private static final String[] testKeyGenerateOutput = new String[] {
            "8899aabbccddeeff0011223344556677", "fedcba98765432100123456789abcdef",
            "db31485315694343228d6aef8cc78c44", "3d4553d8e9cfec6815ebadc40a9ffd04",
            "57646468c44a5e28d3e59246f429f1ac", "bd079435165c6432b532e82834da581b",
            "51e640757e8745de705727265a0098b1", "5a7925017b9fdd3ed72a91a22286f984",
            "bb44e25378c73123a5f32f73cdb6e517", "72e9dd7416bcf45b755dbaa88e4a4043"};
    private static final String[] testEncryptInput = new String[] {
            "1122334455667700ffeeddccbbaa9988",
            "8899aabbccddeeff0011223344556677fedcba98765432100123456789abcdef"};
    private static final String[] testEncryptOutput = new String[] {
            "7f679d90bebc24305a468d42b9d4edcd"};
    private final Kuznyechik kuznyechik;

    private static long[] convertToLongArr(String s, int longVectorLength) {
        long[] result = new long[longVectorLength];
        int l = 0;
        int r = 16;
        for (int i = 0; i < longVectorLength; i++) {
            result[i] = Long.parseUnsignedLong(s.substring(l, r), 16);
            l = r;
            r = r + 16;
        }
        return result;
    }

    private static boolean areEqual(long[] a, long[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (Long.compareUnsigned(a[i], b[i]) != 0) {
                return false;
            }
        }
        return true;
    }

    private static void show(long[] x) {
        for (long l : x) {
            String vector = Long.toUnsignedString(l, 16);
            System.out.printf("%s%s", "0".repeat(16 - vector.length()), vector);
        }
    }

    private static void showComparative(long[] actualValue, long[] expectedValue) {
        show(actualValue);
        System.out.printf(" - actual value%n");
        show(expectedValue);
        System.out.printf(" - expected value%n");
    }

    private int runTest(String methodToTest, String[] inputValues, String[] outputValues) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method testMethod = Kuznyechik.class.getDeclaredMethod(methodToTest, long[].class);
        testMethod.setAccessible(true);
        System.out.printf("=========================%s=========================%n%n", methodToTest + " TEST");
        int fails = 0;
        for (int i = 0; i < inputValues.length; i++) {
            boolean isEqual = true;
            if (!methodToTest.equals("kuznyechikKeySchedule")) {
                long[] input = convertToLongArr(inputValues[i], 2);
                show(input);
                System.out.printf(" - input%n");
                long[] actualValue = (long[]) testMethod.invoke(kuznyechik, (Object) input);
                long[] expectedValue = convertToLongArr(outputValues[i], 2);
                isEqual = areEqual(actualValue, expectedValue);
                showComparative(actualValue, expectedValue);
                System.out.printf("Are they equal? - %s.%n%n", isEqual);
            } else {
                long[][] actualValue = (long[][]) testMethod.invoke(kuznyechik, (Object) convertToLongArr(inputValues[i], 4));
                for (int j = 0; j < 10; j++) {
                    long[] expectedValue = convertToLongArr(outputValues[10 * i + j], 2);
                    isEqual = areEqual(actualValue[j], expectedValue);
                    System.out.printf("%d round key: ", j + 1);
                    show(actualValue[j]);
                    System.out.printf(" - actual value%n             ");
                    show(expectedValue);
                    System.out.printf(" - expected value%n");
                    System.out.printf("Are they equal? - %s.%n%n", isEqual);
                }
            }
            if (!isEqual) {
                fails++;
            }
        }
        System.out.printf("Test count = %s; %sSuccess tests =%s %s; %sTests failed =%s %s;%n",
                inputValues.length, GREEN, RESET, inputValues.length - fails, RED, RESET, fails);
        if (fails == 0) {
            System.out.printf("%s test - Ok!%n", methodToTest);
            return 1;
        } else {
            System.out.printf("%s test - Failed!%n", methodToTest);
        }
        return 0;
    }

    @TestOrder(1)
    public int runSTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return runTest("S", testSInput, testSOutput);
    }
    @TestOrder(2)
    public int runSInvTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return runTest("SInv", testSOutput, testSInput);
    }
    @TestOrder(3)
    public int runRTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return runTest("R", testRInput, testROutput);
    }
    @TestOrder(4)
    public int runRInvTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return runTest("RInv", testROutput, testRInput);
    }
    @TestOrder(5)
    public int runLTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return runTest("L", testLInput, testLOutput);
    }
    @TestOrder(6)
    public int runLInvTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return runTest("LInv", testLOutput, testLInput);
    }
    @TestOrder(7)
    public int runKeyGeneratorTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return runTest("kuznyechikKeySchedule", testKeyGenerateInput, testKeyGenerateOutput);
    }
    public int rrunEncryptDecryptTest() {
        System.out.printf("=========================%s=========================%n%n", "encrypt and decrypt TEST");
        int fails = 0;
        long[] toEncrypt;
        long[] toDecrypt;
        long[] key;
        boolean encryptSuccess;
        boolean decryptSuccess;
        long[] actualValue;
        long[] expectedValue;
        for (int i = 0; i < testEncryptInput.length; i += 2) {
            toEncrypt = convertToLongArr(testEncryptInput[i], 2);
            toDecrypt = convertToLongArr(testEncryptOutput[i / 2], 2);
            key = convertToLongArr(testEncryptInput[i + 1], 4);
            show(toEncrypt); System.out.printf(" - plaintext%n");
            show(toDecrypt); System.out.printf(" - ciphertext%n");
            show(key); System.out.printf(" - key%n%n");

            actualValue = kuznyechik.encrypt(toEncrypt, key);
            expectedValue = toDecrypt;
            encryptSuccess = areEqual(actualValue, expectedValue);
            System.out.printf("Encrypt:%n");
            showComparative(actualValue, expectedValue);
            System.out.printf("Are they equal? - %s.%n%n", encryptSuccess);

            actualValue = kuznyechik.decrypt(actualValue, key);
            expectedValue = toEncrypt;
            decryptSuccess = areEqual(actualValue, expectedValue);
            System.out.printf("Decrypt:%n");
            showComparative(actualValue, expectedValue);
            System.out.printf("Are they equal? - %s.%n%n", decryptSuccess);

            if (!(encryptSuccess & decryptSuccess)) {
                fails++;
            }
        }
        System.out.printf("Test count = %s; %sSuccess tests =%s %s; %sTests failed =%s %s;%n",
                testEncryptOutput.length, GREEN, RESET, testEncryptOutput.length - fails, RED, RESET, fails);
        if (fails == 0) {
            System.out.printf("%s test - Ok!%n", "encrypt");
            return 1;
        } else {
            System.out.printf("%s test - Failed!%n", "encrypt");
        }
        return 0;
    }

    public void runAllTests() throws InvocationTargetException, IllegalAccessException {
        List<Method> tests = Arrays.stream(KuznyechikTests.class.getMethods())
                .filter(x -> x.getName().startsWith("run") && x.getName().endsWith("Test"))
                .sorted(Comparator.comparingInt(m -> m.getAnnotation(TestOrder.class).value()))
                .toList();
        int count = tests.size() + 1;
        int result;
        ArrayList<String> failedTests = new ArrayList<>();
        for (Method test: tests) {
            result = (int) test.invoke(this);
            if (result == 0) {
                failedTests.add(test.getName().substring(3, test.getName().length() - 4));
            }
            count -= result;
        }
        count -= rrunEncryptDecryptTest();
        System.out.printf("%n%s tests failed! %s", count, String.join(", ", failedTests));
    }

    public KuznyechikTests(Kuznyechik kuznyechik) {
        this.kuznyechik = kuznyechik;
    }
}
