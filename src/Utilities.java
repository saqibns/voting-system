/**
 * The MIT License (MIT)
Copyright (c) 2015 Saqib Nizam Shamsi
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Utilities 
{
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] encrypted = encryptPassword("colostart", Constants.SYMMETRIC_KEY);
		byte[] encoded = Base64.encodeBase64(encrypted);
		setPassword(new String(encoded));
		System.out.println(new String(encoded));
		System.out.println(decryptPassword(encrypted, Constants.SYMMETRIC_KEY));
		//setPassword("colovote");
		/*Console cons;
		 char[] passwd;
		 if ((cons = System.console()) != null &&
		     (passwd = cons.readPassword("[%s]", "Password:")) != null) {
			 if(validatePassword(new String(passwd)))
			 {
				 //System.out.print("Enter New Password: ");
				 if((passwd = cons.readPassword("[%s]", "New Password:")) != null)
				 {
					 char[] repeat;
					 System.out.println("Confirm: ");
					 if((repeat = cons.readPassword("[%s]", "Confirm:")) != null)
					 {
						 if(new String(passwd).equals(new String(repeat)))
							 setPassword(new String(repeat));
					 }
				 }
			 }
		 }
		 */
	}
	
	public static boolean all(boolean[] array)
	{
		boolean result = true;
		for(int i = 0; i < array.length; i++)
			result = result && array[i];
		return result;
	}
	
	public static String getTableName(String id)
	{
		char ch;
		ch = id.charAt(0);
		if(ch == 'C' || ch == 'c')
			return "external";
		else
			return "college";
	}

	public static int[] getIDRange() throws NumberFormatException, IOException
	{
		String filePath = new File("").getAbsolutePath();
		filePath += "//res//" + Files.getNAME_OF_THE_FILE_CONTAINING_COLLEGE_ID_RANGE();
		File f = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] range = new int[2];
		range[0] = Integer.parseInt(br.readLine());
		range[1] = Integer.parseInt(br.readLine());
		br.close();
		return range;
	}
	
	public static String[] getExtIDRange() throws NumberFormatException, IOException
	{
		String filePath = new File("").getAbsolutePath();
		filePath += "//res//" + Files.getNAME_OF_THE_FILE_CONTAINING_EXTERNAL_ID_RANGE();
		File f = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String[] range = new String[3];
		range[0] = br.readLine(); 
		range[1] = br.readLine();
		range[2] = br.readLine();
		br.close();
		return range;
	}
	
	public static String readPassword() throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		String filePath = new File("").getAbsolutePath();
		filePath += "//res//" + Files.getNAME_OF_THE_FILE_CONTAINING_PASSWORD();
		File f = new File(filePath);
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		String password = (String) ois.readObject();
		byte[] decoded = Base64.decodeBase64(password);
		String decrypted = decryptPassword(decoded, Constants.SYMMETRIC_KEY);
		ois.close();
		return decrypted;
		
	}
	
	public static void setPassword(String password) throws IOException
	{
		String filePath = new File("").getAbsolutePath();
		filePath += "//res//" + Files.getNAME_OF_THE_FILE_CONTAINING_PASSWORD();
		FileOutputStream fos = new FileOutputStream(filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(password);
		oos.close();
	}
	
	public static boolean validatePassword(String password) throws ClassNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		String correctPassword = readPassword();
		if(correctPassword.equals(password))
			return true;
		else
			return false;
	}
	
	public static byte[] encryptPassword(String password, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
	{
		Key K = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, K);
		byte[] output = cipher.doFinal(password.getBytes());
		return output;
	}
	
	public static String decryptPassword(byte[] bytes, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
	{
		Key K = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, K);
		byte[] output = cipher.doFinal(bytes);
		return new String(output, "UTF-8");
				
	}
	
	
}
