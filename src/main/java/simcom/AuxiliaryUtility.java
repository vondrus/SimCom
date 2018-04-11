package simcom;


class AuxiliaryUtility {

    static String ByteArrayAsBinLittleEndian (byte[] a) {
        StringBuilder rv = new StringBuilder();

        if (a.length > 0) {
            for (int i = a.length; i > 0; i--) {

                byte mask = 1;
                StringBuilder s = new StringBuilder();
                for (int j = 0; j < 8; j++) {
                    if ((a[i - 1] & mask) == 0) {
                        s.insert(0, '0');
                    } else {
                        s.insert(0, '1');
                    }
                    mask <<= 1;
                }

                rv.append(s.toString());

                if (i > 1) {
                    rv.append(' ');
                }

            }
            return rv.toString();
        } else {
            return "";
        }
    }

    static String ByteArrayAsHexBigEndian (byte[] a) {
        StringBuilder rv = new StringBuilder();

        if (a.length > 0) {
            for (byte b : a) {
                rv.append(String.format("%02X", b));
            }
            return rv.toString();
        } else {
            return "";
        }
    }

    static String ByteArrayAsHexLittleEndian (byte[] a) {
        StringBuilder rv = new StringBuilder();

        if (a.length > 0) {
            for (int i = a.length; i > 0; i--) {
                rv.append(String.format("%02X", a[i - 1]));
            }
            return rv.toString();
        } else {
            return "";
        }
    }

}
