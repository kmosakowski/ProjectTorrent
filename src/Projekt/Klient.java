package Projekt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Klient extends KlientSerwer {

	private String ip;
	private int port;
	private BufferedReader odbior;
	private BufferedWriter wysylanie;
	private Socket klient;
	private File pliki[];

	public Klient(String ip, int port) throws IOException {

		this.ip = ip;
		this.port = port;
		this.klient = new Socket(ip, port);
		this.aktualizacjaUzytkownikow(ip, port); 										//Aktualizowanie pliku o nowego uzytkownika czyli siebie
		super.sumyKontrolne();															//Generowanie sm kontrolnych w tle swoich plikow w folderze udostepnione
	}

	@Override
	public void run() {
		try {
			System.out.println("Wysylam prosbe o " + super.getStan());					//prosba o to co wybral uzytkownik, czy pobieranie/wysylanie/podglad plikow
			this.uwierzytelnianiePolaczenia();											//Podstawowe czynnosci potrzebne do polaczenia. Otwieranie strumieni itd.
			switch (super.getStan()) {
			case PLIKI:
				this.wysylanie.write("\n");
				String tresc = null;
				this.aktualizacjaUzytkownikow(this.odbior.readLine());					//Aktualizacja pliku o nowego uzytkownika, czyli serwer z ktorym sie lacze
				while ((tresc = this.odbior.readLine()) != null) {						//wyswietlanie listy plikow serwera
					System.out.println(tresc);
					System.out.flush();
				}
				break;
			case POBIERANIE:															//Pobieranie pliku
				int proby = 0;
				int licznik = 0;
				this.pliki = new File[5];												// 3 pola ktore uzywam do retransmisji. tablica 5 elementowa okresla nam liczbe retransmisji
				while(true) {
					try {
						if(proby != 0) {												//jesli polaczenie zostanie przerwane to tworze nowego klienta i znowu robie podstawowe czynnosci laczenia sie ktore robilem w lini 42
							this.klient = new Socket(this.ip, this.port);				
							this.uwierzytelnianiePolaczenia();
						}
						this.wysylanie.write(this.getInfo() + "\n\n");					//Wysylanie nazwy pliku jaki chcemy pobrac
						this.wysylanie.flush();
						String suma = this.odbior.readLine();			
						int stanPobierania;
						byte buffor[] = new byte[1028];
						if (!super.czyPlikInstenieje(super.getInfo()) && proby == 0) {	//Sprawdzamy czy plik juz istnieje i czy to jest pierwsza proba pobrania pliku czy moze rentransmijsa?
							this.pliki[0] = new File(super.getPlikiUdostepnione().getPath() + "\\" + super.getInfo() + ".part" + proby);	//tworze plik o nazwie.part0
							InputStream download = klient.getInputStream();
							OutputStream zapisDysk = new FileOutputStream(pliki[0]);
							
							while ((stanPobierania = download.read(buffor)) != -1) {
								zapisDysk.write(buffor, 0, stanPobierania);
								licznik++;
							}
							download.close();
							zapisDysk.close();
							this.pliki[0].renameTo(new File(super.getPlikiUdostepnione().getPath() + "\\" + super.getInfo()));
							this.pliki[0].delete();										//jesli sie wszystko fajnie pobrało to zmieniam nazwe z nazwa.part0 na nazwe taka jak powinna byc
							this.pliki[0] = new File(super.getPlikiUdostepnione().getPath() + "\\" + super.getInfo());
							super.sprawdzaniePobranychPlikow(suma, pliki[0]);			//Sprawdzam sumy kontrolne jesli dobre to bedzie odpowiedni komunikat, jesli zle tez 
							break;
						} 
						else if(proby != 0) {											//retransmisja
							System.out.println("Retransmisja");
							pliki[proby] = new File(super.getPlikiUdostepnione().getPath() + "\\" + super.getInfo() + ".part" + proby);		//Tworze nowy plik o nazwie part1 lub ktora to tam proba jest
							InputStream download = klient.getInputStream();
							OutputStream zapisDysk = new FileOutputStream(pliki[proby]);
							
							int tmp = 0;
							while ((stanPobierania = download.read(buffor)) != -1) {	//kontynuuje zapis pliku dopiero wtedy gdy liczniki beda sie zgadzać
								++tmp;
								if(tmp > licznik) {
									zapisDysk.write(buffor, 0, stanPobierania);
									licznik++;
								}
							}	
							System.out.println(licznik);
							download.close();
							zapisDysk.close();
							
							super.sprawdzaniePobranychPlikow(suma, super.laczeniePlikow(pliki, proby));		//lacze pliki ktore pobralem i sprawdzam sume polaczonego pliku
							
							for(File element : pliki) {									//usuwam party juz nie potrzebne
								element.delete();
							}
							break;							
						}
						else {
							System.out.println("Plik o takiej nazwie już istnieje!");
							System.out.println("Pobieranie anulowane");
							break;
						}
					}catch(SocketException se) {									//Przypadek przerwania polaczenia
						if(proby == 5) {											//liczba prob wyniesie 5 konczymy zabawe
							System.out.println("Polaczenie zakonczono");
							break;
						}
						System.out.println("Polaczenie przerwane, zostanie wznowione za 5sek");
						klient.close();
						Thread.sleep(5000);
						System.out.println("Wznawianie polaczenia, proba " + ++proby + " z 5");	//cofamy sie do lini 57 i teraz bedzie robił sie inny kod zwarzywszy na to ze proby ma wartość !=0
					}
				}
				break;
			case WYSYLANIE:																	//Wysylanie pliku na serwer
				this.wysylanie.write(super.getInfo() + "\n");								//Wysylam informacje jaki plik wysylam - nazwa
				this.wysylanie.flush();
				this.wysylanie.write(super.getSumyKontrolne(super.getInfo()) + "\n\n");		//Wysylam sume kontrolna do sprawdzenia po pobraniu po stronie serwera
				this.wysylanie.flush();
				OutputStream upload = klient.getOutputStream();
				InputStream plikDoWyslania = new FileInputStream(super.getPlikiUdostepnione().getPath() + "\\" + super.getInfo());	//Tworze strumien do pliku
				byte buffor[] = new byte[1028];
				int iloscByte = 0;
				while ((iloscByte = plikDoWyslania.read(buffor)) != -1) {
					upload.write(buffor, 0, iloscByte);
					upload.flush();
				}
				upload.close();
				plikDoWyslania.close();
				System.out.println("Koniec wysylania");
				break;
			}
			super.sumyKontrolne();															//sprawdzanie sum w przypadku gdy jakies nowe pliki sie pobiora albo cos
			wysylanie.close();
			odbior.close();
		} catch (Exception ex) {
			System.out.println("blad klienta " + ex);
			ex.printStackTrace();
			System.out.println("Polaczenie przerwane");
		}
	}
	
	protected void aktualizacjaUzytkownikow(String ip, int port) throws IOException {			//aktualizacja pliku z lista znanych uzytkownikow o nowego uzytkownika

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
	
	private void aktualizacjaUzytkownikow(String lista) throws Exception{					//aktualizacja pliku z lista uzytkownikow, lecz teraz aktualizzuje cała liste ktora dostałem z serwera a nie pojedynczego uzytkownika

		String wyrazy[] = lista.split("\t");
		for( String element : wyrazy) {
			Pattern wyrazenie = Pattern.compile(element);
			StringBuilder plik = new StringBuilder();
			BufferedReader odbiorKlientow = new BufferedReader(new FileReader(super.getPlikiPrywatne()+"\\BazaKlientow.txt"));
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
				BufferedWriter wysylanieKlientow = new BufferedWriter(new FileWriter(super.getPlikiPrywatne()+"\\BazaKlientow.txt"));
				wysylanieKlientow.write(plik.toString());
				wysylanieKlientow.close();
			}
		}
		
	}

	private void uwierzytelnianiePolaczenia() throws Exception{								//Postawowe czynnosci inicjujace polaczenie
		this.wysylanie = new BufferedWriter(new OutputStreamWriter(this.klient.getOutputStream()));
		this.odbior = new BufferedReader(new InputStreamReader(this.klient.getInputStream()));
		this.wysylanie.write(this.getStan() + "\n");
		this.wysylanie.flush();
	}

}
