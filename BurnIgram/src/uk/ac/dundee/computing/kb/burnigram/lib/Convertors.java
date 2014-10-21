package uk.ac.dundee.computing.kb.burnigram.lib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
//import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

import uk.ac.dundee.computing.kb.burnigram.beans.Globals;

public final class Convertors {
    public static final int DISPLAY_ORIGINAL_IMAGE=0;
    public static final int DISPLAY_THUMB=1;
    public static final int DISPLAY_PROCESSED=2;
    
    public Convertors() {

    }

    public static java.util.UUID getTimeUUID() {
        return java.util.UUID.fromString(new com.eaio.uuid.UUID().toString());
    }
    
    

    public static byte[] asByteArray(java.util.UUID uuid) {

        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return buffer;
    }

    public static byte[] longToByteArray(long value) {
        byte[] buffer = new byte[8]; //longs are 8 bytes I believe
        for (int i = 7; i >= 0; i--) { //fill from the right
            buffer[i] = (byte) (value & 0x00000000000000ff); //get the bottom byte

            //System.out.print(""+Integer.toHexString((int)buffer[i])+",");
            value = value >>> 8; //Shift the value right 8 bits
        }
        return buffer;
    }

    public static long byteArrayToLong(byte[] buffer) {
        long value = 0;
        long multiplier = 1;
        for (int i = 7; i >= 0; i--) { //get from the right

            //System.out.println(Long.toHexString(multiplier)+"\t"+Integer.toHexString((int)buffer[i]));
            value = value + (buffer[i] & 0xff) * multiplier; // add the value * the hex mulitplier
            multiplier = multiplier << 8;
        }
        return value;
    }
    
	public static byte[] bufferedImageToByteArray(BufferedImage bufferedImage,
			String formatString) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] imageInByte = null;
		try {
			if (!ImageIO.write(bufferedImage, formatString, baos)) {
				System.err
						.println("Error in bufferdImageToByteArray no ImageIO reader found "
								+ "for type " + formatString);
			}
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
		} catch (IOException exception) {
			System.err.println("IOExecption in bufferedImageToByte Array");
			exception.printStackTrace();
		}
		return imageInByte;

	}

	public static BufferedImage byteArrayToBufferedImage(byte[] byteArray) {
		BufferedImage buffImage;
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(byteArray);
			buffImage = ImageIO.read(is);
			is.close();
		} catch (IOException ioEx) {
			System.err
					.println("byteArrayToBufferedImage cannot read from input stream ");
			ioEx.printStackTrace();
			buffImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		}
		return buffImage;
	}


    public static void displayByteArrayAsHex(byte[] buffer) {
        int byteArrayLength = buffer.length;
        for (int i = 0; i < byteArrayLength; i++) {
            int val = (int) buffer[i];
            System.out.print(Integer.toHexString(val)+",");
        }

	  //System.out.println();
    }

//From: http://www.captain.at/howto-java-convert-binary-data.php
    public static long arr2long(byte[] arr, int start) {
        int i = 0;
        int len = 4;
        int cnt = 0;
        byte[] tmp = new byte[len];
        for (i = start; i < (start + len); i++) {
            tmp[cnt] = arr[i];
            cnt++;
        }
        long accum = 0;
        i = 0;
        for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
            accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
            i++;
        }
        return accum;
    }

    public static String[] SplitTags(String Tags) {
        String args[] = null;

        StringTokenizer st = Convertors.SplitTagString(Tags);
        args = new String[st.countTokens() + 1];  //+1 for _No_Tag_
        //Lets assume the number is the last argument

        int argv = 0;
        while (st.hasMoreTokens()) {;
            args[argv] = new String();
            args[argv] = st.nextToken();
            argv++;
        }
        args[argv] = "_No-Tag_";
        return args;
    }

    private static StringTokenizer SplitTagString(String str) {
        return new StringTokenizer(str, ",");

    }

    public static String[] SplitFiletype(String type) {
        String args[] = null;

        StringTokenizer st = SplitString(type);
        args = new String[st.countTokens()];
		//Lets assume the number is the last argument

        int argv = 0;
        while (st.hasMoreTokens()) {;
            args[argv] = new String();

            args[argv] = st.nextToken();
            try {
                //System.out.println("String was "+URLDecoder.decode(args[argv],"UTF-8"));
                args[argv] = URLDecoder.decode(args[argv], "UTF-8");

            } catch (Exception et) {
                System.out.println("Bad URL Encoding" + args[argv]);
            }
            argv++;
        }

	//so now they'll be in the args array.  
        // argv[0] should be the user directory
        return args;
    }
    /**
     * splits request Path into String array. 
     * e.g. /Burnigram/Images/kai
     * into {"Burnigram","Images","kai"}
     * @param request
     * @return String Array with Path components
     */
    public static String[] SplitRequestPath(HttpServletRequest request) {
        String args[] = null;
        StringTokenizer st = SplitString(request.getRequestURI());
        args = new String[st.countTokens()];
		//Lets assume the number is the last argument

        int argv = 0;
        while (st.hasMoreTokens()) {
            args[argv] = new String();

            args[argv] = st.nextToken();
            try {
            	if(Globals.DEBUG)
            		System.out.println("String was "+URLDecoder.decode(args[argv],"UTF-8"));
                args[argv] = URLDecoder.decode(args[argv], "UTF-8");

            } catch (UnsupportedEncodingException et) {
                System.out.println("Bad URL Encoding" + args[argv]);
            }
            argv++;
        }

	//so now they'll be in the args array.  
        // argv[0] should be the user directory
        return args;
    }

    private static StringTokenizer SplitString(String str) {
        return new StringTokenizer(str, "/");

    }

}
