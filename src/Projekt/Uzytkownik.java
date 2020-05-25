package Projekt;

import java.util.Scanner;

import Projekt.KlientSerwer.Polecenia;

public class Uzytkownik {

	public static void main(String[] args) throws Exception {
		
		int wybor = 0;
		String adresIp = null;
		int adresPort = 0;
		String informacjeDodatkowe = null;
		Klient klient = null;
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("W jakim systemie chcesz pracowac?");
		System.out.println("= 1 - TCP");
		System.out.println("= 2 - UDP");
		System.out.println("v");
		System.out.println("");
		//				Tak zwane GUI dla całego projektu
		wybor = scanner.nextInt();
		if(wybor == 1) {					//Wersja TCP
			Serwer serwer = new Serwer();
			new Thread(serwer, "serwer").start();
			while (true) {
				System.out.println(serwer.myIp + "                 " + serwer.myPort);
				System.out.println("===========Obsluga============");
				System.out.println("= 1 - Pokaz klientow w sieci =");
				System.out.println("= 2 - Pokaz pliki klienta    =");
				System.out.println("= 3 - Pobierz plik           =");
				System.out.println("= 4 - Wyslij plik            =");
				System.out.println("= 0 - Wyloguj                =");
				System.out.println("=============TCP==============");
				serwer.getPliki();													//Wyswietlanie listy plikow jakie sa aktualnie dostepne w folderze udostepnione
				Thread.sleep(500);
				System.out.println("v");
				try {
					wybor = scanner.nextInt();
					switch (wybor) {
					case 1:
						System.out.println("IP       Port");
						System.out.println("_______________________________________");
						serwer.wyswietlanieUzytkownikow(); 							//wyswietla uzytkownikow ktorych zna, zczytuje z pliku ktory jest aktualizowany w trakcie laczenia sie
						System.out.println("_______________________________________");
						break;
					case 2:
						System.out.println("_______________________________________");
						System.out.println("Ktorego klienta chcesz zobaczyc pliki?");
						System.out.println("Podaj IP");
						adresIp = scanner.next();
						System.out.println("Podaj port");
						adresPort = scanner.nextInt();
						klient = new Klient(adresIp, adresPort);
						klient.setPolecenia(Polecenia.PLIKI);
						new Thread(klient, "klient").start();
						System.out.println("_______________________________________");
						break;
					case 3:
						System.out.println("_______________________________________");
						System.out.println("Podaj nazwę pliku");
						informacjeDodatkowe = scanner.next();
						klient = new Klient(adresIp, adresPort);
						klient.setInfo(informacjeDodatkowe);						//Okreslanie nazwy pliku
						klient.setPolecenia(Polecenia.POBIERANIE);
						new Thread(klient, "klient").start();
						System.out.println("_______________________________________");
						break;
					case 4:
						System.out.println("_______________________________________");
						System.out.println("Do kogo chcesz wyslac plik?");
						System.out.println("Podaj IP");
						adresIp = scanner.next();
						System.out.println("Podaj port");
						adresPort = scanner.nextInt();
						klient = new Klient(adresIp, adresPort);
						System.out.println("Podaj nazwę pliku");
						informacjeDodatkowe = scanner.next();
						klient.setInfo(informacjeDodatkowe);
						klient.setPolecenia(Polecenia.WYSYLANIE);
						new Thread(klient, "klient").start();
						System.out.println("_______________________________________");
						break;
					case 0:
						System.out.println("Wylogowano uzytkownik na " + serwer.myIp + ", porcie " + serwer.myPort);
						scanner.close();
						System.exit(0);
						break;
					default:
						System.out.println("Nie ma takiego polecenia");
					}
					
				} catch (Exception ex) {
					System.out.println("Cos poszlo nie tak :/");
					scanner.close();
					scanner = new Scanner(System.in);
				}
				
			}
		}
		else {								//Wersja UDP
			
			System.out.println("Wkrótce....");
			
		}
		scanner.close();
	}

}