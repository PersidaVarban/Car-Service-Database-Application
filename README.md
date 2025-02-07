# Auto Service Management Application (PD Service)
The application manages data related to clients, vehicles, repairs, employees, and appointments for the PD Service auto center. Users can schedule appointments, generate invoices, and manage all related information through an integrated database system.
## Key Features
- Access and manage the list of clients, vehicles, employees, and appointments
- Filter appointments by month and year
- Add, edit, and delete clients, vehicles, employees, and appointments
- View detailed client pages, including associated vehicles and invoices
- Track repair history and future appointments for each vehicle
- Assign employees to repairs and track ongoing activities
## SQL Queries Examples
- Add a client: `INSERT INTO clienti (nume, prenume, telefon) VALUES (?, ?, ?)`
- Edit a client: `UPDATE clienti SET nume = ?, prenume = ?, telefon = ? WHERE id_client = ?`
- Add a vehicle: `INSERT INTO autoturisme (id_client, marca, model, ...) VALUES (?, ?, ?, ...)`
## Technologies Used
- **Backend:** Java (Spring Boot)
- **Frontend:** Thymeleaf, CSS, JavaScript
- **Database:** MySQL (via phpMyAdmin)
- **IDE:** IntelliJ IDEA 2024.3
- **API Testing:** Postman
