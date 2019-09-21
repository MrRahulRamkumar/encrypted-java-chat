package crypto;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Random;

public class RabinPKC {

    private static BigInteger THREE = BigInteger.valueOf(3);
    private static BigInteger FOUR = BigInteger.valueOf(4);

    public static String addPadding(String str) {
        return ("~" + str + "~");
    }

    public static BigInteger[] genKey(int bitLength, Random random) {
        BigInteger p = getPrime(bitLength/2, random);
        BigInteger q = getPrime(bitLength/2, random);
        BigInteger N = p.multiply(q);
        return new BigInteger[]{N,p,q};
    }

    //p = 11, q = 13, n = 143 = p*q
    public static String encrypt(String plainText, BigInteger N) {

        plainText = addPadding(plainText);

        BigInteger m = new BigInteger(plainText.getBytes(Charset.forName("ascii")));

        return m.multiply(m).mod(N).toString();
    }

    public static String decrypt(String cipherText, BigInteger p, BigInteger q) {

        BigInteger c = new BigInteger(cipherText);

        BigInteger N = p.multiply(q);
        BigInteger m_p1 = c.modPow(p.add(BigInteger.ONE).divide(FOUR), p);
        BigInteger m_p2 = p.subtract(m_p1);
        BigInteger m_q1 = c.modPow(q.add(BigInteger.ONE).divide(FOUR), q);
        BigInteger m_q2 = q.subtract(m_q1);

        BigInteger[] ext = extendedGCD(p,q);
        BigInteger y_p = ext[0];
        BigInteger y_q = ext[1];

        //y_p*p*m_q + y_q*q*m_p (mod n)
        BigInteger d1 = y_p.multiply(p).multiply(m_q1).add(y_q.multiply(q).multiply(m_p1)).mod(N);
        BigInteger d2 = y_p.multiply(p).multiply(m_q2).add(y_q.multiply(q).multiply(m_p1)).mod(N);
        BigInteger d3 = y_p.multiply(p).multiply(m_q1).add(y_q.multiply(q).multiply(m_p2)).mod(N);
        BigInteger d4 = y_p.multiply(p).multiply(m_q2).add(y_q.multiply(q).multiply(m_p2)).mod(N);

        BigInteger [] d = {d1,d2,d3,d4};
        for(int i = 0; i<d.length; i++) {
            String dec = new String(d[i].toByteArray(), Charset.forName("ascii"));
            System.out.println("possible: " + dec);
            if(isPadding(dec)) {
                return dec.substring(1,dec.length()-1);
            }
        }
        return "";
    }

    public static boolean isPadding(String str) {
        return (str.charAt(0) == '~' && str.charAt(str.length()-1) == '~');
    }

    public static BigInteger[] extendedGCD(BigInteger a, BigInteger b) {

        BigInteger s2 = BigInteger.ZERO;
        BigInteger s1 = BigInteger.ONE;

        BigInteger t2 = BigInteger.ONE;
        BigInteger t1 = BigInteger.ZERO;

        BigInteger r2 = b;
        BigInteger r1 = a;
        while(!r2.equals(BigInteger.ZERO)) {
            BigInteger q = r1.divide(r2);

            BigInteger r = r1.subtract(q.multiply(r2));
            r1 = r2;
            r2 = r;

            BigInteger s = s1.subtract(q.multiply(s2));
            s1 = s2;
            s2 = s;

            BigInteger t = t1.subtract(q.multiply(t2));
            t1 = t2;
            t2 = t;

        }
        //gcd, x,y
        //x,y such that ax+by=gcd(a,b)
        return new BigInteger[]{s1, t1};
    }


    public static BigInteger getPrime(int bitLength, Random random) {
        BigInteger p;
        do {
            p = BigInteger.probablePrime(bitLength,random);
        }
        while(!p.mod(FOUR).equals(THREE));
        return p;
    }
}