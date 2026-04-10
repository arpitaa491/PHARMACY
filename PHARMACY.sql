CREATE database pharmacy_db;
USE pharmacy_db;

CREATE TABLE medicines (
   med_id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100),
quantity INT,
price DECIMAL(8,2),
expiry_date DATE
);
CREATE TABLE sales (
sale_id INT PRIMARY KEY AUTO_INCREMENT,
med_id INT,
qty_sold INT,
sold_at DATETIME DEFAULT NOW(),
FOREIGN KEY (med_id) REFERENCES medicines(med_id)
);
TRUNCATE TABLE medicines;