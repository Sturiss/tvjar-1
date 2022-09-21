package net.gnim.crypto;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BASE64编解码
 *
 * @author ming
 */
public class BASE64 {

    /**
     * 标准BASE64
     */
    public static final int TYPE_STANDARD = 0;
    /**
     * URL安全的BASE64编码
     */
    public static final int TYPE_URL_SAFE = 1;
    /**
     * ORDERED的BASE64编码
     */
    public static final int TYPE_ORDERED = 2;
    /**
     * 多行编码
     */
    public static final int ENCODE_BREAKLINE = 4;
    private static final byte[] STANDARD_ENCODE_MAP = {
        (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
        (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
        (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
        (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
        (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
        (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
        (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
        (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
        (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
        (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/'
    };
    private static final byte[] STANDARD_DECODE_MAP = {
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -2, -2, -3, -3, -2, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -2, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, 62, -3, -3, -3, 63,
        52, 53, 54, 55, 56, 57, 58, 59,
        60, 61, -3, -3, -3, -1, -3, -3,
        -3, 0, 1, 2, 3, 4, 5, 6,
        7, 8, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22,
        23, 24, 25, -3, -3, -3, -3, -3,
        -3, 26, 27, 28, 29, 30, 31, 32,
        33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48,
        49, 50, 51, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3
    };
    private static final byte[] URL_SAFE_ENCODE_MAP = {
        (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
        (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
        (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
        (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
        (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
        (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
        (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
        (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
        (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
        (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '-', (byte) '_'
    };
    private static final byte[] URL_SAFE_DECODE_MAP = {
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -2, -2, -3, -3, -2, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -2, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, 45, -3, -3,
        48, 49, 50, 51, 52, 53, 54, 55,
        56, 57, -3, -3, -3, -1, -3, -3,
        -3, 65, 66, 67, 68, 69, 70, 71,
        72, 73, 74, 75, 76, 77, 78, 79,
        80, 81, 82, 83, 84, 85, 86, 87,
        88, 89, 90, -3, -3, -3, -3, 95,
        -3, 97, 98, 99, 100, 101, 102, 103,
        104, 105, 106, 107, 108, 109, 110, 111,
        112, 113, 114, 115, 116, 117, 118, 119,
        120, 121, 122, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3
    };
    private static final byte[] ORDERED_ENCODE_MAP = {
        (byte) '-',
        (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
        (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
        (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
        (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
        (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
        (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
        (byte) '_',
        (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
        (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
        (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
        (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z'
    };
    private static final byte[] ORDERED_DECODE_MAP = {
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -2, -2, -3, -3, -2, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -2, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, 45, -3, -3,
        48, 49, 50, 51, 52, 53, 54, 55,
        56, 57, -3, -3, -3, -1, -3, -3,
        -3, 65, 66, 67, 68, 69, 70, 71,
        72, 73, 74, 75, 76, 77, 78, 79,
        80, 81, 82, 83, 84, 85, 86, 87,
        88, 89, 90, -3, -3, -3, -3, 95,
        -3, 97, 98, 99, 100, 101, 102, 103,
        104, 105, 106, 107, 108, 109, 110, 111,
        112, 113, 114, 115, 116, 117, 118, 119,
        120, 121, 122, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3
    };
    private static final byte ENCODE_END = (byte) '=';
    private static final byte DECODE_END = -1;
    private static final byte DECODE_SPACE = -2;
    private static final byte DECODE_ERR = -3;
    private static final byte NEW_LINE = (byte) '\n';
    private static final int LINE_MAX = 76;
    private static final int BUFF_DECODE_BLOCK = 1024;

    /**
     * 获取编码映射表
     *
     * @param options 模式
     * @return 编码映射表
     */
    private static byte[] getEncodeMap(int options) {
        switch (options & 0x03) {
            case TYPE_URL_SAFE:
                return URL_SAFE_ENCODE_MAP;
            case TYPE_ORDERED:
                return ORDERED_ENCODE_MAP;
            default:
                return STANDARD_ENCODE_MAP;
        }
    }

    /**
     * 获取解码映射表
     *
     * @param options 模式
     * @return 解码映射表
     */
    private static byte[] getDecodeMap(int options) {
        switch (options & 0x03) {
            case TYPE_URL_SAFE:
                return URL_SAFE_DECODE_MAP;
            case TYPE_ORDERED:
                return ORDERED_DECODE_MAP;
            default:
                return STANDARD_DECODE_MAP;
        }
    }

    /**
     * 3字节到4字节转换函数
     *
     * @param encodeMap 编码映射表
     * @param data 数据
     * @param index 起始索引
     * @param len 数据长度
     * @param buff 缓存数组
     * @param pos 写入缓存起始索引
     */
    private static void byte3to4(byte[] encodeMap, byte[] data, int index, int len, byte[] buff, int pos) {
        switch (len) {
            case 1:
                buff[pos] = encodeMap[data[index] >> 2];
                buff[pos + 1] = encodeMap[(data[index] & 0x03) << 4];
                buff[pos + 2] = ENCODE_END;
                buff[pos + 3] = ENCODE_END;
                break;
            case 2:
                buff[pos] = encodeMap[data[index] >> 2];
                buff[pos + 1] = encodeMap[((data[index] & 0x03) << 4) | (data[index + 1] >> 4)];
                buff[pos + 2] = encodeMap[(data[index + 1] & 0x0F) << 2];
                buff[pos + 3] = ENCODE_END;
                break;
            default:
                buff[pos] = encodeMap[data[index] >> 2];
                buff[pos + 1] = encodeMap[((data[index] & 0x03) << 4) | (data[index + 1] >> 4)];
                buff[pos + 2] = encodeMap[((data[index + 1] & 0x0F) << 2) | (data[index + 2] >> 6)];
                buff[pos + 3] = encodeMap[data[index + 2] & 0x3F];
        }
    }

    /**
     * 4字节到3字节转换函数
     *
     * @param decodeMap 解码映射表
     * @param data 数据
     * @param len 数据长度
     * @param buff 缓存数组
     * @param pos 写入缓存起始索引
     */
    private static void byte4to3(byte[] decodeMap, byte[] data, int len, byte[] buff, int pos) {
        switch (len) {
            case 1:
                //error
                break;
            case 2:
                buff[pos] = (byte) ((decodeMap[data[0]] << 2) | (decodeMap[data[1]] >> 4));
                break;
            case 3:
                buff[pos] = (byte) ((decodeMap[data[0]] << 2) | (decodeMap[data[1]] >> 4));
                buff[pos + 1] = (byte) (((decodeMap[data[1]] & 0x0F) << 4) | (decodeMap[data[2]] >> 2));
                break;
            default:
                buff[pos] = (byte) ((decodeMap[data[0]] << 2) | (decodeMap[data[1]] >> 4));
                buff[pos + 1] = (byte) (((decodeMap[data[1]] & 0x0F) << 4) | (decodeMap[data[2]] >> 2));
                buff[pos + 2] = (byte) (((decodeMap[data[2]] & 0x03) << 6) | decodeMap[data[3]]);
        }
    }

    /**
     * BASE64编码数据
     *
     * @param data 数据
     * @return BASE64字符串
     */
    public static String encode(byte[] data) {
        return encode(data, TYPE_STANDARD);
    }

    /**
     * BASE64编码数据
     *
     * @param data 数据
     * @param options 编码参数
     * @return BASE64字符串
     */
    public static String encode(byte[] data, int options) {
        byte[] encodeMap = getEncodeMap(options);
        boolean breakline = (options & ENCODE_BREAKLINE) != 0;
        int len = (data.length + 2) / 3;
        int lineSize = LINE_MAX / 4;
        int lenAdd = breakline ? (len / lineSize) : 0;
        byte[] buff = new byte[len * 4 + lenAdd];
        int blCnt = 0;
        for (int i = 0; i < len; i++) {
            if (breakline && i > 0 && i % lineSize == 0) {
                buff[i * 4 + blCnt++] = NEW_LINE;
            }
            byte3to4(encodeMap, data, i * 3, data.length - i * 3, buff, i * 4 + blCnt);
        }
        return new String(buff);
    }

    /**
     * 使用输入输出流编码
     *
     * @param in 输入流
     * @param out 输出流
     * @return 是否成功，使用或操作并BASE64.[X]
     */
    public static boolean encode(InputStream in, OutputStream out) {
        return encode(in, out, TYPE_STANDARD);
    }

    /**
     * 使用输入输出流编码
     *
     * @param in 输入流
     * @param out 输出流
     * @param options 编码参数，使用或操作并BASE64.[X]
     * @return 是否成功
     */
    public static boolean encode(InputStream in, OutputStream out, int options) {
        byte[] encodeMap = getEncodeMap(options);
        boolean breakline = (options & ENCODE_BREAKLINE) != 0;
        int lineSize = LINE_MAX / 4;
        byte[] buff = new byte[4];
        try {
            BufferedInputStream bin = new BufferedInputStream(in);
            BufferedOutputStream bout = new BufferedOutputStream(out);
            byte[] data = new byte[3];
            int readCnt;
            int solveCnt = 0;
            while ((readCnt = bin.read(data)) != -1) {
                if (readCnt > 0) {
                    if (breakline && solveCnt > 0 && solveCnt % lineSize == 0) {
                        bout.write(NEW_LINE);
                    }
                    byte3to4(encodeMap, data, 0, readCnt, buff, 0);
                    bout.write(buff);
                    solveCnt++;
                }
            }
            bout.flush();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(BASE64.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * BASE64解码数据
     *
     * @param str BASE64字符串
     * @return 数据，错误返回null
     */
    public static byte[] decode(String str) {
        return decode(str, TYPE_STANDARD);
    }

    /**
     * BASE64解码数据
     *
     * @param str BASE64字符串
     * @param options 解码参数
     * @return 数据，错误返回null
     */
    public static byte[] decode(String str, int options) {
        byte[] decodeMap = getDecodeMap(options);
        byte[] data = new byte[4];
        byte[] buff = new byte[3 * BUFF_DECODE_BLOCK];
        ArrayList<byte[]> buffs = new ArrayList<byte[]>();
        int dataCnt = 0;
        int buffCnt = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch > 255) {
                return null;
            }
            byte b = (byte) ch;
            byte db = decodeMap[b];
            if (db == DECODE_ERR) {
                return null;//error char
            } else if (db == DECODE_SPACE) {
                continue;//skip space
            } else if (db == DECODE_END) {
                continue;//skip end char
            } else {
                if (dataCnt == 4) {
                    byte4to3(decodeMap, data, dataCnt, buff, buffCnt);
                    dataCnt = 0;
                    buffCnt += 3;
                    if (buffCnt >= buff.length) {
                        buffs.add(buff);
                        buffCnt = 0;
                    }
                }
                data[dataCnt++] = b;
            }
        }
        if (dataCnt > 0) {
            byte4to3(decodeMap, data, dataCnt, buff, buffCnt);
            buffCnt += dataCnt - 1;
        }
        int totalLen = buffCnt;
        for (byte[] bs : buffs) {
            totalLen += bs.length;
        }
        byte[] result = new byte[totalLen];
        for (int i = 0; i < buffs.size(); i++) {
            System.arraycopy(buffs.get(i), 0, result, i * 3 * BUFF_DECODE_BLOCK, 3 * BUFF_DECODE_BLOCK);
        }
        if (buffCnt > 0) {
            System.arraycopy(buff, 0, result, buffs.size() * 3 * BUFF_DECODE_BLOCK, buffCnt);
        }
        return result;
    }

    /**
     * 使用输入输出流解码
     *
     * @param in 输入流
     * @param out 输出流
     * @return 是否成功
     */
    public static boolean decode(InputStream in, OutputStream out) {
        return decode(in, out, TYPE_STANDARD);
    }

    /**
     * 使用输入输出流解码
     *
     * @param in 输入流
     * @param out 输出流
     * @param options 解码参数
     * @return 是否成功
     */
    public static boolean decode(InputStream in, OutputStream out, int options) {
        byte[] decodeMap = getDecodeMap(options);
        byte[] data = new byte[4];
        byte[] buff = new byte[3 * BUFF_DECODE_BLOCK];
        int dataCnt = 0;
        int buffCnt = 0;
        try {
            BufferedInputStream bin = new BufferedInputStream(in);
            BufferedOutputStream bout = new BufferedOutputStream(out);
            int read;
            while ((read = bin.read()) != -1) {
                char ch = (char) read;
                if (ch > 255) {
                    return false;
                }
                byte b = (byte) ch;
                byte db = decodeMap[b];
                if (db == DECODE_ERR) {
                    return false;//error char
                } else if (db == DECODE_SPACE) {
                    continue;//skip space
                } else if (db == DECODE_END) {
                    continue;//skip end char
                } else {
                    if (dataCnt == 4) {
                        byte4to3(decodeMap, data, dataCnt, buff, buffCnt);
                        dataCnt = 0;
                        buffCnt += 3;
                        if (buffCnt >= buff.length) {
                            bout.write(buff, 0, buffCnt);
                            buffCnt = 0;
                        }
                    }
                    data[dataCnt++] = b;
                }
            }
            if (dataCnt > 0) {
                byte4to3(decodeMap, data, dataCnt, buff, buffCnt);
                buffCnt += dataCnt - 1;
            }
            if (buffCnt > 0) {
                bout.write(buff, 0, buffCnt);
            }
            bout.flush();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(BASE64.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
