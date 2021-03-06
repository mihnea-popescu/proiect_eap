package service_architecture.main;
import service_architecture.model.*;
import service_architecture.service.*;
import service_architecture.service.fileio.GetCSVData;
import service_architecture.service.fileio.WriteCSVData;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private String basePath = "D:\\facultate\\an2\\EAP\\Proiect_EAP\\src\\storage\\";

    private ArrayList<Client> clients;
    private ArrayList<Organiser> organisers;
    private ArrayList<Venue> venues;

    private ArrayList<Event> events;

    public static void main(String[] args) {
        Main main = new Main();
        main.getData();
        main.menu();
    }

    private void menu() {
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("E-TICKETING SYSTEM");
        System.out.println("1. LOGIN AS CLIENT");
        System.out.println("2. LOGIN AS ORGANISER");
        System.out.println("3. SYSTEM ADMINISTRATION");
        System.out.println("0. EXIT");
        System.out.println("Enter your input...");
        Integer option;
        Scanner scan = new Scanner(System.in);
        option = scan.nextInt();
        switch(option) {
            case 1: {
                // LOGIN AS CLIENT
                this.clientMenu();
                break;
            }
            case 2: {
                // LOGIN AS ORGANISER
                this.organiserMenu();
                break;
            }
            case 3: {
                // LOGIN AS SYSADMIN
                this.adminMenu();
                break;
            }
            case 0: { break; }
            default: {
                System.out.println("IMPOSSIBLE OPERATION");
                this.menu();
            }
        }
    }

    private void organiserMenu() {
        ArrayList<Organiser> organisers = this.getOrganisers();
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("ORGANISER MENU");
        for(Organiser organiser : organisers) {
            System.out.println((organisers.indexOf(organiser) + 1) + ". " + organiser.getName() + " - " + organiser.getCompany() + " - " + organiser.getEmail());
        }
        System.out.println((organisers.size() + 1) + ". Register a new organiser");
        System.out.println("0. PREVIOUS MENU");
        System.out.println("Enter your input...");
        int option;
        Scanner scan = new Scanner(System.in);
        option = scan.nextInt();
        if(option == 0) {
            this.menu();
        }
        else if(option == (organisers.size() + 1)) {
            // register new organiser
            organisers.add(CreateOrganiser.Create());
            this.organiserMenu();
        }
        else if(option >= 1 && option <= organisers.size()) {
            // select a organiser
            Organiser organiser = organisers.get(option - 1);
            SelectOrganiser so = new SelectOrganiser(organiser, organisers, this.getClients(), this.getVenues(), this.getEvents());
            so.main();
            this.organiserMenu();
        }
        else {
            // unexpected command
            System.out.println("IMPOSSIBLE OPERATION");
            this.organiserMenu();
        }
    }

    private void clientMenu() {
        ArrayList<Client> clients = this.getClients();
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("CLIENT MENU");
        for(Client client : clients) {
            System.out.println((clients.indexOf(client)+1) + ". " + client.getName() + " " + client.getEmail() + " " + client.getPhone());
        }
        System.out.println((clients.size() + 1) + ". Register a new client");
        System.out.println("0. PREVIOUS MENU");
        System.out.println("Enter your input...");
        Integer option;
        Scanner scan = new Scanner(System.in);
        option = scan.nextInt();
        if(option == 0) {
            this.menu();
        }
        else if(option >= 1 && option <= clients.size()) {
            // select a client
            Client client = clients.get(option - 1);
            SelectClient sc = new SelectClient(client, this.getEvents(), this.getVenues(), this.getOrganisers());
            Client newClient = sc.ManageClient();
            clients.set(option - 1, newClient);
            clientMenu();
        }
        else if(option == (clients.size() + 1)) {
            // register a new client
            CreateClient cc = new CreateClient();
            Client client = cc.CreateClient();
            clients.add(client);
            clientMenu();
        }
        else {
            // unexpected command
            System.out.println("IMPOSSIBLE OPERATION");
            this.clientMenu();
        }
    }

    private void adminMenu() {
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("SYSTEM ADMINISTRATION");
        System.out.println("1. SAVE CHANGES");
        System.out.println("2. MANAGE VENUES");
        System.out.println("3. PREVIOUS MENU");
        System.out.println("0. EXIT");
        System.out.println("Enter your input...");
        Integer option;
        Scanner scan = new Scanner(System.in);
        option = scan.nextInt();
        switch(option) {
            case 1: {
                // save changes
                this.saveData();
                this.adminMenu();
                break;
            }
            case 2: {
                // manage venues
                this.manageVenues();
                break;
            }
            case 3: {
                // previous menu
                this.menu();
                break;
            }
            case 0: { break; }
            default: {
                System.out.println("IMPOSSIBLE OPERATION");
                this.menu();
            }
        }
    }

    private void manageVenues() {
        ArrayList<Venue> venues = this.getVenues();
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("MANAGE VENUES");
        for(Venue venue : venues) {
            System.out.println((venues.indexOf(venue) + 1) + ". " + venue.getName() + " - " + venue.getAddress() + " - open: " + venue.getOpened().toString());
        }
        System.out.println((venues.size() + 1) + ". Create a new venue");
        System.out.println("0. PREVIOUS MENU");
        System.out.println("Enter your input...");
        Integer option;
        Scanner scan = new Scanner(System.in);
        option = scan.nextInt();
        if(option == 0) {
            this.adminMenu();
        }
        else if(option == (venues.size() + 1)) {
            // create a new venue
            CreateVenue cv = new CreateVenue();
            Venue venue = cv.CreateVenue();
            ArrayList<Venue> list = this.getVenues();
            list.add(venue);
            this.setVenues(list);
            this.manageVenues();

        }
        else if(option >= 1 && option <= venues.size()) {
            // selected a venue (index = option-1)
            Venue original = venues.get(option - 1);
            EditVenue ev = new EditVenue(original);
            Venue newVenue = ev.Edit();
            for(Event event : this.getEvents()) {
                // modify all events to have the new hashcode
                if(event.getVenue() == original.hashCode()) {
                    Integer oldHash = event.hashCode();
                    event.setVenue(newVenue.hashCode());
                    Integer newHash = event.hashCode();

                    // modify all the tickets for this event to have the new hashcode
                    for(Client client : this.getClients()) {
                        for(Ticket ticket : client.getTickets()) {
                            if(ticket.getEvent() == oldHash) {
                                ticket.setEvent(newHash);
                            }
                        }
                    }
                    for(Organiser organiser : this.getOrganisers()) {
                        for(Ticket ticket: organiser.getTickets()) {
                            if(ticket.getEvent() == oldHash) {
                                ticket.setEvent(newHash);
                            }
                        }
                    }
                }
            }

            venues.set(option - 1, newVenue);
            this.setVenues(venues);
            manageVenues();
        }
        else {
            System.out.println("IMPOSSIBLE OPERATION");
            this.manageVenues();
        }
    }

    private void getData() {
        System.out.println("Loading the data...");
        String basePath = this.basePath;

        //inputs
        GetCSVData singleton = GetCSVData.getInstance();

        Map<String, ArrayList> map = singleton.getCSVInputData(basePath);

        if(map.containsKey("venues")) {
            this.setVenues(map.get("venues"));
        }
        System.out.println("Found " + this.getVenues().size() +" venues.");

        if(map.containsKey("organisers")) {
            this.setOrganisers(map.get("organisers"));
        }
        System.out.println("Found " + this.getOrganisers().size() + " organisers.");

        if(map.containsKey("events")) {
            this.setEvents(map.get("events"));
        }
        System.out.println("Found "+ this.getEvents().size() + " events.");

        if(map.containsKey("clients")) {
            this.setClients(map.get("clients"));
        }
        System.out.println("Found " + this.getClients().size() + " clients.");

        if(map.containsKey("tickets")) {
            ArrayList<Ticket> tickets = map.get("tickets");

            for(Client client : this.getClients()) {
                ArrayList<Ticket> clientTickets = new ArrayList<Ticket>();
                for(Ticket ticket : tickets) {
                    if(ticket.getUser() == client.hashCode()) {
                        clientTickets.add(ticket);
                    }
                }
                client.setTickets(clientTickets);
            }

            for(Organiser organiser : this.getOrganisers()) {
                ArrayList<Ticket> organiserTickets = new ArrayList<Ticket>();
                for(Ticket ticket : tickets) {
                    if(ticket.getUser() == organiser.hashCode()) {
                        organiserTickets.add(ticket);
                    }
                }
                organiser.setTickets(organiserTickets);
            }

            System.out.println("Found " + tickets.size() + " tickets.");
        }
    }

    public void saveData() {
        WriteCSVData singleton = WriteCSVData.getInstance();
        singleton.saveData(basePath, this.getVenues(), this.getOrganisers(), this.getEvents(), this.getClients());
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public ArrayList<Organiser> getOrganisers() {
        return organisers;
    }

    public void setOrganisers(ArrayList<Organiser> organisers) {
        this.organisers = organisers;
    }

    public ArrayList<Venue> getVenues() {
        return venues;
    }

    public void setVenues(ArrayList<Venue> venues) {
        this.venues = venues;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
