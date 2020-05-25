package Projekt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Serwer extends KlientSerwer {

	private ServerSocket serwer;
	private BufferedReader odbior;
	private BufferedWriter wysylanie;
	
	public Serwer() throws Exception {
		this.serwer = new ServerSocket(super.myPort);
		this.aktualizacjaUzytkownikow(super.myIp, super.myPort); 										//aktualizowanie pliku ze znanymi uzytkownikami w sieci
		super.sumyKontrolne();																			//generowanie sum kontrolnych w tle swoich plikow. Duże pliki potrzebuja wiecej czasu
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);//
				System.out.println("Serwer nasluchuje");
				Socket socket = this.serwer.accept();
				System.out.println("Nowy klient sie polaczyl...");
				this.aktualizacjaUzytkownikow(socket.getInetAddress().toString(), socket.getPort());	//aktualizuje liste uzytkownikow o nowo polaczonego
				this.odbior = new BufferedReader(new InputStreamReader(socket.getInputStream()));		//przygotowywuje strumienie do pracy
				this.wysylanie = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				switch (this.odbior.readLine()) {														//odbieram pierwszy komunikat czego chce ode mnie klient
				case "PLIKI": 																			// wysylanie mojej listy plikow
					this.wysylanie.write(this.wymianaUzytkownikow()+"\n");								//wysylanie mojej listy uzytkownikow w celu aktualizacji po stronie klienta
					this.wysylanie.flush();
					String tabPliki[] = super.getPlikiUdostepnione().list();							//wysylanie listy moich plikow
					for (int i = 0; i < tabPliki.length; i++) {
						this.wysylanie.write(tabPliki[i] + "\n");
						if(super.getSumyKontrolne(tabPliki[i]) != null) {								//Pobieranie sumy kontrolnej wczejsniej wygenerowanej w lini 31 i wysylanie do klienta
							this.wysylanie.write(super.getSumyKontrolne(tabPliki[i]) + "\n");
							this.wysylanie.flush();
						}
						else {																			//Jesli sie jeszcze nie wygenerowaly sumy kontrolne
							this.wysylanie.write("Suma kontrolna w trakcie generowania, sprobuj pozniej" + "\n");
							this.wysylanie.flush();
						}
					}
					break;
				case "POBIERANIE":																		//Wysylanie plikow do klienta (nazwa troche myląca :D )
					try {
						String jaki = this.odbior.readLine();
						this.wysylanie.write(super.getSumyKontrolne(jaki)+"\n");						//wysylanie sumy kontrolnej do klienta w celu weryfikacji
						this.wysylanie.flush();
						OutputStream upload = socket.getOutputStream();									// v okreslanie pliku w folderze
						InputStream plik = new FileInputStream(super.getPlikiUdostepnione().getPath() + "\\" + jaki);
						byte buffor[] = new byte[1028];
						int iloscByte = 0;
						while ((iloscByte = plik.read(buffor)) != -1) {
							upload.write(buffor, 0, iloscByte);
							upload.flush();
						}
						upload.close();
						plik.close();
						System.out.println("Koniec wysylania");
						break;
					}catch(SocketException se) {
						System.out.println("Polaczenie przerwane");
					}
				case "WYSYLANIE":																		//Odbieranie pliku od klienta (nazwa mylaca ale po stronie klienta jest ok)
					File plikDoZapisu = new File(super.getPlikiUdostepnione().getPath() + "\\" + odbior.readLine());//Tworzenie pliku o podanej nazwie
					String suma = odbior.readLine(); 													//odbieranie sumy kontrolnej
					if (!plikDoZapisu.exists()) {														//Sprawdzanie czy plik istnieje i jesli nie to pobieraj go!
						plikDoZapisu.createNewFile();
						int stanPobierania;
						InputStream download = socket.getInputStream();
						OutputStream zapisDysk = new FileOutputStream(plikDoZapisu);
						byte bufforZapisu[] = new byte[1024];
						while ((stanPobierania = download.read(bufforZapisu)) != -1) {
							zapisDysk.write(bufforZapisu, 0, stanPobierania);
						}
						download.close();
						zapisDysk.close();
						if(suma.equals(super.generujSume(plikDoZapisu))) {								//Sprawdzanie sum kontrolnych czy sie zgadzaja
							System.out.println("Pobieranie zakonczone pomyslnie");
							System.out.println("Sumy kontrolne zgodne");
						}
						else {
							plikDoZapisu.delete();
							System.out.println("Sumy kontrolne niezgodne");
							System.out.println("Pobieranie anulowane");
						}
					} else {
						System.out.println("Plik o takiej nazwie już istnieje!");
						System.out.println("Pobieranie anulowane");
					}
					break;
				default:																				//Opcja gdy cos sie wcisnie nie tak
					this.wysylanie.write("Nieznane polecenie");
					System.out.println("Nieznane polecenie");
				}
				socket.close();
			} catch (SocketException se) {																//W przypadku przerwania polaczenia wyswietli sie ten komunikat i serwer znowu bedzie nasluchiwal
				System.out.println("Przerwano przez klienta");
			}catch (FileNotFoundException fnfe) {
				System.out.println("Nie ma takiego pliku, klient nie wie czego chce");
			}catch (Exception ex) {
				System.out.println("blad serwera " + ex);
				ex.printStackTrace();
			}
		}

	}

	protected void aktualizacjaUzytkownikow(String ip, int port) throws IOException {						//Aktualizacja pliku z lista uzytkownikow. Dodaje tylko tych ktorych nie ma na liscie
		Pattern wyrazenie = Pattern.compile(ip + " " + port);
		StringBuilder plik = new StringBuilder();
		this.odbior = new BufferedReader(new FileReader(super.getPlikiPrywatne()+"\\BazaKlientow.txt"));
		String linia = null;
		while ((linia = this.odbior.readLine()) != null) {
			plik.append(linia + "\n");
		}
		this.odbior.close();
		boolean czyJest = false;
		Matcher dopasowanie = wyrazenie.matcher(plik);
		while (dopasowanie.find()) {
			czyJest = true;
			break;
		}
		if (!czyJest) {
			plik.append(ip + " " + port + "\n");
			this.wysylanie = new BufferedWriter(new FileWriter(super.getPlikiPrywatne()+"\\BazaKlientow.txt"));
			this.wysylanie.write(plik.toString());
			this.wysylanie.close();
		}
	}

	protected void wyswietlanieUzytkownikow() throws IOException {											//Wyswietla zawartosc pliku z uzytkownikami. Uzywane w Uzytkownik.java
		this.odbior = new BufferedReader(new FileReader(super.getPlikiPrywatne()+"\\BazaKlientow.txt"));
		String linia = null;
		while ((linia = this.odbior.readLine()) != null) {
			System.out.print(linia + "\n");
		}
		this.odbior.close();

	}

	protected void getPliki() throws IOException {															//Wyswietlanie plikow jakie sa w folderze udostepnione. forma GUI uzywane w Uzytkownik.java
		String tabPliki[] = super.getPlikiUdostepnione().list();
		System.out.println("\t\t\t| Moje pliki:");
		for (String element : tabPliki) {
			System.out.println("\t\t\t|> "+element);
		}
		System.out.println("\t\t\t|______________");
		String pliki[] = super.getPlikiUdostepnione().list();							//wysylanie listy moich plikow
		for (int i = 0; i < pliki.length; i++) {
			if(super.getSumyKontrolne(pliki[i]) != null) {								//Pobieranie sumy kontrolnej wczejsniej wygenerowanej w lini 31 i wysylanie do klienta
				super.sumyKontrolne(pliki[i]);
			}
		}
	}

	private String wymianaUzytkownikow() throws Exception{												//Generowanie listy uzytkownikow z pliku, po to by wysylac do klientow w celu aktualizacji
		StringBuilder lista = new StringBuilder();
		this.odbior = new BufferedReader(new FileReader(super.getPlikiPrywatne()+"\\BazaKlientow.txt"));
		String linia = null;
		while ((linia = this.odbior.readLine()) != null) {
			lista.append("\t"+linia);
		}
		return lista.toString();
		
	}

}