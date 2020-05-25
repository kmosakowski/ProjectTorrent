package Projekt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;

public abstract class KlientSerwer implements Runnable {

	protected int myPort = 10000;
	protected String myIp = "localhost";
	private File plikiPrywatne = new File("C:\\Users\\mosak\\OneDrive\\Studia\\Java\\Eclipse_Workspace\\Projekt Torrent\\Uzytkownik3\\Prywatne");
	private File plikiUdostepnione = new File("C:\\Users\\mosak\\OneDrive\\Studia\\Java\\Eclipse_Workspace\\Projekt Torrent\\Uzytkownik3\\Udostepnione");
	private volatile Polecenia stan;
	private String info;
	private HashMap<String, String> sumyKontrolne;
	public enum Polecenia {
		PLIKI, POBIERANIE, WYSYLANIE
	};
	
	abstract public void run();
	
	abstract protected void aktualizacjaUzytkownikow(String ip, int port) throws IOException;
	
	protected String generujSume(File plik) throws Exception{							//Generuje sume kontrolna plikow
			
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
		
	protected boolean czyPlikInstenieje(String nazwaPliku) throws Exception{			//sprawdzanie czy plik istnieje. Uzywane w Klient.java 68
		boolean wynik = false;
		for (String element : this.plikiUdostepnione.list()) {
			if(element.equals(nazwaPliku)) {
				wynik = true;
			}
		}
		return wynik;
		
	}

	protected void sprawdzaniePobranychPlikow(String suma, File plik) throws Exception{	//Sprawdzanie sum kontrolnych pobranych plikow. Zazwyczaj uzywane w kliencie i serwerze po pobieraniu
		System.out.println("Sprawdzanie sum kontrolnych ...");
		if(suma.equals(this.generujSume(plik))) {
			System.out.println("Pobieranie zakonczone pomyslnie");
			System.out.println("Sumy kontrolne zgodne");
		}
		else {
			plik.delete();
			System.out.println("Sumy kontrolne niezgodne");
			System.out.println("Pobieranie anulowane");
		}
	}

	protected void sumyKontrolne() {													//generowanie sum kontrolnych w tle. Zazwyczaj uruchamiane w konstruktorze klienta i serwera
		new Thread(() -> {
			try{
				String tabPliki[] = this.plikiUdostepnione.list();
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
	
	protected void sumyKontrolne(String plik) {													//generowanie sum kontrolnych w tle. Zazwyczaj uruchamiane w konstruktorze klienta i serwera
		new Thread(() -> {
			try{
					sumyKontrolne.put(plik, this.generujSume(new File(this.plikiUdostepnione.toString()+"\\"+plik)));					
			}catch(Exception exc) {
				System.out.println("Blad sum " + exc);
				return;
			}
    	}).start();
	}

	protected File laczeniePlikow(File pliki[], int proby) throws Exception{			//Lączy pobrane party plikow w jeden cały piękny i okazały pliczek
		
		System.out.println("Laczenie plikow...");
		File polaczony = new File(this.plikiUdostepnione.getPath() + "\\" + this.getInfo());
		OutputStream plikPolaczony = new FileOutputStream(polaczony);
		
		for(int i = 0; i <= proby; i++) {
			FileInputStream plik = new FileInputStream(pliki[i]);
			byte buffor[] = new byte[1028];
			int iloscByte = 0;
			if(i != proby) {
				while ((iloscByte = plik.read(buffor)) == buffor.length) {
					plikPolaczony.write(buffor, 0, iloscByte);
					plikPolaczony.flush();
				}
			}
			else {
				while ((iloscByte = plik.read(buffor)) != -1) {
					plikPolaczony.write(buffor, 0, iloscByte);
					plikPolaczony.flush();
				}
			}
			plik.close();			
		}
		plikPolaczony.close();
		System.out.println("Plik polaczony");
		return polaczony;
	}

	protected String getInfo() {
		return info;
	}

	protected void setInfo(String info) {
		this.info = info;
	}

	protected Polecenia getStan() {
		return stan;
	}

	protected void setPolecenia(Polecenia stan) {
		this.stan = stan;
	}
	
	protected String getSumyKontrolne(String klucz) {
		
		if(sumyKontrolne.containsKey(klucz)) {
			return sumyKontrolne.get(klucz);
		}
		else {
			return null;
		}
	}
	
	protected File getPlikiPrywatne() {
		return plikiPrywatne;
	}
	
	protected File getPlikiUdostepnione() {
		return plikiUdostepnione;
	}

}


