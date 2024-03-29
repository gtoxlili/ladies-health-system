package com.system.ladiesHealth.utils;

public class XXHash {

    public static int PRIME32_1 = 0x9E3779B1;
    public static int PRIME32_2 = 0x85EBCA77;
    public static int PRIME32_3 = 0xC2B2AE3D;
    public static int PRIME32_4 = 0x27D4EB2F;
    public static int PRIME32_5 = 0x165667B1;

    public static int hash32(byte[] b, int off, int len, int seed) {
        int h32 = 0;
        int p = off;
        int end = off + len;

        if (len >= 16) {
            int v1 = seed + PRIME32_1 + PRIME32_2;
            int v2 = seed + PRIME32_2;
            int v3 = seed + 0;
            int v4 = seed - PRIME32_1;

            for (int limit = end - 16; p <= limit; ) {
                v1 += get32bits(b, p) * PRIME32_2;
                v1 = rotl32(v1, 13);
                v1 *= PRIME32_1;
                p += 4;
                v2 += get32bits(b, p) * PRIME32_2;
                v2 = rotl32(v2, 13);
                v2 *= PRIME32_1;
                p += 4;
                v3 += get32bits(b, p) * PRIME32_2;
                v3 = rotl32(v3, 13);
                v3 *= PRIME32_1;
                p += 4;
                v4 += get32bits(b, p) * PRIME32_2;
                v4 = rotl32(v4, 13);
                v4 *= PRIME32_1;
                p += 4;
            }

            h32 = rotl32(v1, 1) + rotl32(v2, 7) + rotl32(v3, 12)
                    + rotl32(v4, 18);
        } else {
            h32 = seed + PRIME32_5;
        }

        h32 += len;

        while (p + 4 <= end) {
            h32 += get32bits(b, p) * PRIME32_3;
            h32 = rotl32(h32, 17) * PRIME32_4;
            p += 4;
        }

        while (p < end) {
            h32 += ((int) b[p] & 0xFF) * PRIME32_5;
            h32 = rotl32(h32, 11) * PRIME32_1;
            p += 1;
        }

        h32 ^= h32 >>> 15;
        h32 *= PRIME32_2;
        h32 ^= h32 >>> 13;
        h32 *= PRIME32_3;
        h32 ^= h32 >>> 16;

        return h32;
    }

    public static int rotl32(int v, int r) {
        return (v << r) | (v >>> (32 - r));
    }

    public static int get32bits(byte[] b, int off) {
        return (((int) b[off + 3] & 0xFF) << 24)
                | (((int) b[off + 2] & 0xFF) << 16)
                | (((int) b[off + 1] & 0xFF) << 8)
                | ((int) b[off] & 0xFF);
    }

    public static long PRIME64_1 = 0x9E3779B185EBCA87L;
    public static long PRIME64_2 = 0xC2B2AE3D27D4EB4FL;
    public static long PRIME64_3 = 0x165667B19E3779F9L;
    public static long PRIME64_4 = 0x85EBCA77C2B2AE63L;
    public static long PRIME64_5 = 0x27D4EB2F165667C5L;

    public static long hash64(byte[] b, int off, int len, long seed) {
        long h64 = 0;
        int p = off, end = off + len;

        if (len >= 32) {
            long v1 = seed + PRIME64_1 + PRIME64_2;
            long v2 = seed + PRIME64_2;
            long v3 = seed + 0;
            long v4 = seed - PRIME64_1;

            for (int limit = end - 32; p <= limit; ) {
                v1 += get64bits(b, p) * PRIME64_2;
                v1 = rotl64(v1, 31);
                v1 *= PRIME64_1;
                p += 8;
                v2 += get64bits(b, p) * PRIME64_2;
                v2 = rotl64(v2, 31);
                v2 *= PRIME64_1;
                p += 8;
                v3 += get64bits(b, p) * PRIME64_2;
                v3 = rotl64(v3, 31);
                v3 *= PRIME64_1;
                p += 8;
                v4 += get64bits(b, p) * PRIME64_2;
                v4 = rotl64(v4, 31);
                v4 *= PRIME64_1;
                p += 8;
            }

            h64 = rotl64(v1, 1) + rotl64(v2, 7) + rotl64(v3, 12)
                    + rotl64(v4, 18);

            v1 *= PRIME64_2;
            v1 = rotl64(v1, 31);
            v1 *= PRIME64_1;
            h64 ^= v1;
            h64 = h64 * PRIME64_1 + PRIME64_4;

            v2 *= PRIME64_2;
            v2 = rotl64(v2, 31);
            v2 *= PRIME64_1;
            h64 ^= v2;
            h64 = h64 * PRIME64_1 + PRIME64_4;

            v3 *= PRIME64_2;
            v3 = rotl64(v3, 31);
            v3 *= PRIME64_1;
            h64 ^= v3;
            h64 = h64 * PRIME64_1 + PRIME64_4;

            v4 *= PRIME64_2;
            v4 = rotl64(v4, 31);
            v4 *= PRIME64_1;
            h64 ^= v4;
            h64 = h64 * PRIME64_1 + PRIME64_4;
        } else {
            h64 = seed + PRIME64_5;
        }

        h64 += len;

        while (p + 8 <= end) {
            long k1 = get64bits(b, p);
            k1 *= PRIME64_2;
            k1 = rotl64(k1, 31);
            k1 *= PRIME64_1;
            h64 ^= k1;
            h64 = rotl64(h64, 27) * PRIME64_1 + PRIME64_4;
            p += 8;
        }

        if (p + 4 <= end) {
            h64 ^= ((long) get32bits(b, p) & 0xFFFFFFFFL) * PRIME64_1;
            h64 = rotl64(h64, 23) * PRIME64_2 + PRIME64_3;
            p += 4;
        }

        while (p < end) {
            h64 ^= ((long) b[p] & 0xFFL) * PRIME64_5;
            h64 = rotl64(h64, 11) * PRIME64_1;
            p += 1;
        }

        h64 ^= h64 >>> 33;
        h64 *= PRIME64_2;
        h64 ^= h64 >>> 29;
        h64 *= PRIME64_3;
        h64 ^= h64 >>> 32;

        return h64;
    }

    public static long rotl64(long v, int r) {
        return (v << r) | (v >>> (64 - r));
    }

    public static long get64bits(byte[] b, int off) {
        return (((long) b[off + 7] & 0xFF) << 56)
                | (((long) b[off + 6] & 0xFF) << 48)
                | (((long) b[off + 5] & 0xFF) << 40)
                | (((long) b[off + 4] & 0xFF) << 32)
                | (((long) b[off + 3] & 0xFF) << 24)
                | (((long) b[off + 2] & 0xFF) << 16)
                | (((long) b[off + 1] & 0xFF) << 8)
                | ((long) b[off] & 0xFF);
    }

}
