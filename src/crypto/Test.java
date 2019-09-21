package crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;

public class Test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        BigInteger[] key = RabinPKC.genKey(128, new SecureRandom());
        BigInteger N = key[0];
        BigInteger p = key[1];
        BigInteger q = key[2];

//        BigInteger N = new BigInteger("172799355431972674226537494037595869837");
//        BigInteger p = new BigInteger("13429502747926392427");
//        BigInteger q = new BigInteger("12867144724226969831");

        System.out.println(N);
        System.out.println(p+","+q);

        String plainText = "my name is rahul";

        String cipherText = RabinPKC.encrypt(plainText, N);
        System.out.println(cipherText);

        String out = RabinPKC.decrypt(cipherText, p, q);
        System.out.println(out);

    }
}
