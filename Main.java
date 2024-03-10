import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        Kuznyechik kuznyechik = new Kuznyechik();
        KuznyechikTests kuznyechikTests = new KuznyechikTests(kuznyechik);
        kuznyechikTests.runAllTests();
    }
}
