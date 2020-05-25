package Projekt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerwerUDP extends KlientSerwerUDP{

	DatagramSocket serwerSocket;
	DatagramPacket receivePacket;
	byte[] receiveData;
	DatagramPacket sendPacket;
	byte[] sendData;
	String[] informacjeKlienta;
	
	
	public SerwerUDP() throws Exception{ 															// tworzenie tablic i obiektow niezbedynych do dzialania serwera
		receiveData = new byte[1024];
		sendData = new byte[1024];
		serwerSocket = new DatagramSocket(super.myPort);
		this.aktualizacjaUzytkownikow(super.myIp, super.myPort); 									//aktualizuje plik z uzytkownikami w sieci
		super.sumyKontrolne(); 																		// generuje w tle sumy kontrolne swoich plikow
	}
	
	public void run() {
		while(true) {
			try {																					//krok 1 przygotowywanie do odebrania pliku
				this.receivePacket = new DatagramPacket(receiveData, receiveData.length);
				this.serwerSocket.receive(receivePacket);
				System.out.println("Nowy klient się polaczyl...");
				this.informacjeKlienta = this.odszyfrowywanie(receiveData); 						// odczytywanie opcji do wyboru i ip/portu klienta i ewentualnie dodatkowych informacji 0-wybor 1-ip 2-port 3-dodatkowe informacje
				switch (Integer.parseInt(this.informacjeKlienta[0])) {
				case 1:																				//Wysylanie klientowi listy moich plikow
					this.sendData = wymianaUzytkownikow().getBytes();								//wysylanie wszystkich uzytkownikow jakich znam do klienta by zsynchronizowac liste uzytkownikow					
					this.sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(this.informacjeKlienta[1]), Integer.parseInt(this.informacjeKlienta[2]));
					this.serwerSocket.send(sendPacket);
					String tabPliki[] = super.plikiUdostepnione.list();								//generowanie listy plikow do wyslania
					int licznik = 0;
					for(int i = 0; i < tabPliki.length; i++) {
						byte[] tmp = tabPliki[i].getBytes();
						for(int j = 0; j < tmp.length; j++) {
							sendData[licznik] = tmp[i];
							licznik++;
						}
					}
					this.sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(this.informacjeKlienta[1]), Integer.parseInt(this.informacjeKlienta[2]));
					this.serwerSocket.send(sendPacket);
					break;
				case 2:																				//Pobieranie
					
					
					//String sentence = new String(receivePacket.getData());
					FileOutputStream plik = new FileOutputStream("C:\\Users\\mosak\\Desktop\\Studia\\Java\\Pliki\\odebrany.jpg");
					//plik.write(receiveData.array());
					plik.close();
					
					InetAddress IPAddress = receivePacket.getAddress();
					
					int port = receivePacket.getPort();
					
					//String capitalizedSentence = sentence.toUpperCase();
					
					//sendData = capitalizedSentence.getBytes();
					
					//DatagramPacket sendPacket = new DatagramPacket(sendData.array(), sendData.limit(), IPAddress, port);
					
					serwerSocket.send(sendPacket);
					break;
				case 3://Wysylanie
					break;
				default:
					//this.wysylanie.write("Nieznane polecenie");
					System.out.println("Nieznane polecenie");
				}			
			
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
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
	
	public void getPliki() throws IOException {
		String tabPliki[] = super.plikiUdostepnione.list();
		System.out.println("\t\t\t| Moje pliki:");
		for (String element : tabPliki) {
			System.out.println("\t\t\t|> "+element);
		}
		System.out.println("\t\t\t|______________");
	}
	
	public void wyswietlanieUzytkownikow() throws IOException {
		BufferedReader odbior = new BufferedReader(new FileReader(super.plikiPrywatne+"\\BazaKlientow.txt"));
		String linia = null;
		while ((linia = odbior.readLine()) != null) {
			System.out.print(linia + "\n");
		}
		odbior.close();

	}
	
	public String wymianaUzytkownikow() throws Exception{
		StringBuilder lista = new StringBuilder();
		BufferedReader odbior = new BufferedReader(new FileReader(super.plikiPrywatne+"\\BazaKlientow.txt"));
		String linia = null;
		while ((linia = odbior.readLine()) != null) {
			lista.append("\t"+linia);
		}
		odbior.close();
		return lista.toString();
		
	}
	
	public String[] odszyfrowywanie(byte[] dane) {
		
		String kod = "";
		for(int i = 0; i < dane.length; i++) {
			kod += (char)dane[i];
		}
		System.out.println("Oszyfrowywanie :");
		String[] tmp = kod.split("-");
		for(int i = 0; i < tmp.length; i++) {
			System.out.println(tmp[i]);
		}
		System.out.print(":koniec:");
		return kod.split("-");
	}
	
}
