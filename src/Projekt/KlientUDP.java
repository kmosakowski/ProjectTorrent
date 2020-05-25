package Projekt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KlientUDP extends KlientSerwerUDP{

	int port;
	InetAddress ip;
	DatagramSocket klientSocket;
	DatagramPacket sendPacket;
	DatagramPacket receivePacket;
	byte[] receiveData;
	byte[] sendData;
	int polecenie;

	public KlientUDP(String ip, int port) throws Exception{
		
		this.ip = InetAddress.getByName(ip);
		this.port = port;
		receiveData = new byte[1024];
		this.klientSocket = new DatagramSocket();
		this.aktualizacjaUzytkownikow(this.ip.getHostAddress(), port);
		super.sumyKontrolne();
	}
	
	public void run() {
		
		try {																								//krok1 - Przygotowywanie paczki bitow do wsylania
			System.out.println("Wysylam prosbe o polaczenie");
			sendData = new String(this.getPolecenie()+"-"+super.myIp+"-"+super.myPort+"-").getBytes();
			this.sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
			klientSocket.send(sendPacket);
			switch (this.getPolecenie()) {																	//krok2 - wybranie opcji
			case 1:																							// odebranie listy plikow uzytkownika
				String tresc = null;
				System.out.println("jestem");
				this.receivePacket = new DatagramPacket(receiveData, receiveData.length);
				System.out.println("nie jestem");
				this.klientSocket.receive(receivePacket);
				
				this.aktualizacjaUzytkownikow(receiveData);
				
				
				/*while (this.klientSocket.receive(receivePacket) != null) {
					System.out.println(tresc);
					System.out.flush();
				}*/
				break;
			case 2://Pobieranie
				
				break;
			case 3://Wysylanie
				File plik = new File("C:\\Users\\mosak\\Desktop\\Studia\\Java\\Pliki\\photo.jpg");//
				
				BufferedReader klient = new BufferedReader(new InputStreamReader(System.in));
				
				DatagramSocket klientSocket = new DatagramSocket();
				
				
				
				 byte sendPhoto[] = new byte[(int) plik.length()];//
				 sendPhoto = Files.readAllBytes(plik.toPath());//
				
				byte[] wyslaneDane = new byte[1024];
				byte[] odebraneDane = new byte[1024];
				
				//String sentence = klient.readLine();
				
				//wyslaneDane = sentence.getBytes();
				
				//DatagramPacket sendPacket = new DatagramPacket(sendPhoto, sendPhoto.length, ipAdres, 9876);
				
				klientSocket.send(sendPacket);
				
				DatagramPacket receivePacket = new DatagramPacket(odebraneDane, odebraneDane.length);
				
				klientSocket.receive(receivePacket);
				
				String modifiedSentence = new String(receivePacket.getData());
				System.out.println("Z serwera: " + modifiedSentence);
				klientSocket.close();
				
				break;
			}
			super.sumyKontrolne();
			//wysylanie.close();
			//odbior.close();
		} catch (Exception ex) {
			System.out.println("blad klienta " + ex);
			ex.printStackTrace();
			System.out.println("Polaczenie przerwane");
		}

	}
	
	public int getPolecenie() {
		return polecenie;
	}

	public void setPolecenie(int polecenie) {
		this.polecenie = polecenie;
	}
	
	public void aktualizacjaUzytkownikow(String ip, int port) throws IOException {

		Pattern wyrazenie = Pattern.compile(ip + " " + port);
		StringBuilder plik = new StringBuilder();
		BufferedReader odbior = new BufferedReader(new FileReader(super.plikiPrywatne+"\\BazaKlientow.txt"));
		String linia = null;
		while ((linia = odbior.readLine()) != null) {
			plik.append(linia + "\n");
		}
		odbior.close();
		boolean czyJest = false;
		Matcher dopasowanie = wyrazenie.matcher(plik);
		while (dopasowanie.find()) {
			czyJest = true;
			break;
		}

		if (!czyJest) {
			plik.append(ip + " " + port + "\n");
			BufferedWriter wysylanie = new BufferedWriter(new FileWriter(super.plikiPrywatne+"\\BazaKlientow.txt"));
			wysylanie.write(plik.toString());
			wysylanie.close();
		}
	}
	
	public void aktualizacjaUzytkownikow(byte[] lista) throws Exception{

		String kod = "";
		for(int i = 0; i < lista.length; i++) {
			kod += (char)lista[i];
		}
		System.out.println("Mam ten kod " + kod);
	//	for(String element : kod.split("-")) {
	//		System.out.println(element);
	//	}
		
		
	/*	String wyrazy[] = lista.split("\t");
		for( String element : wyrazy) {
			Pattern wyrazenie = Pattern.compile(element);
			StringBuilder plik = new StringBuilder();
			BufferedReader odbiorKlientow = new BufferedReader(new FileReader(super.plikiPrywatne+"\\BazaKlientow.txt"));
			String linia = null;
			while ((linia = odbiorKlientow.readLine()) != null) {
				plik.append(linia + "\n");
			}
			odbiorKlientow.close();
			boolean czyJest = false;
			Matcher dopasowanie = wyrazenie.matcher(plik);
			while (dopasowanie.find()) {
				czyJest = true;
				break;
			}

			if (!czyJest) {
				plik.append(element + "\n");
				BufferedWriter wysylanieKlientow = new BufferedWriter(new FileWriter(super.plikiPrywatne+"\\BazaKlientow.txt"));
				wysylanieKlientow.write(plik.toString());
				wysylanieKlientow.close();
			}
		}*/
		
	}
	
}
