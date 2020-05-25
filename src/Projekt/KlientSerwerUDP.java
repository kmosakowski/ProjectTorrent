package Projekt;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;

public abstract class KlientSerwerUDP implements Runnable{

	int myPort = 6666;
	String myIp = "localhost";
	File plikiPrywatne = new File("C:\\Users\\mosak\\OneDrive\\Studia\\Java\\Eclipse_Workspace\\Projekt Torrent\\Uzytkownik2\\Prywatne");
	File plikiUdostepnione = new File("C:\\Users\\mosak\\OneDrive\\Studia\\Java\\Eclipse_Workspace\\Projekt Torrent\\Uzytkownik2\\Udostepnione");
	String info;
	HashMap<String, String> sumyKontrolne;
	
	abstract public void run();
	
	public void sumyKontrolne() {
		new Thread(() -> {
			try{
				String tabPliki[] = plikiUdostepnione.list();
				sumyKontrolne = new HashMap<String, String>();
				for (int i = 0; i < tabPliki.length; i++) {
					sumyKontrolne.put(tabPliki[i], this.generujSume(new File(this.plikiUdostepnione.toString()+"\\"+tabPliki[i])));					
				}
			}catch(Exception exc) {
				System.out.println("Blad sum " + exc);
				return;
			}
    	}).start();
	}
	
	public String generujSume(File plik) throws Exception{
		
		MessageDigest md5 = MessageDigest.getInstance("MD5"); 
		
		InputStream strumien = Files.newInputStream(plik.toPath());
		DigestInputStream dis = new DigestInputStream(strumien, md5); 
		StringBuffer tekst = new StringBuffer();
		
		while(dis.read() != -1)
			;

		byte[] tabliceBajtow = md5.digest();
		
		for(byte b : tabliceBajtow) {
			tekst.append(String.format("%02x", b));
		}

		return tekst.toString();	
	}
}
