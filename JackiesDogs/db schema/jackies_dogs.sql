SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS schema_changes;

CREATE TABLE schema_changes
(
   id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
   major_release_number VARCHAR(2) NOT NULL,
   minor_release_number VARCHAR(2) NOT NULL,
   point_release_number VARCHAR(4) NOT NULL,
   script_name VARCHAR(50) NOT NULL,
   date_applied DATETIME NOT NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS product;

CREATE TABLE product /* table to store products*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , product_name VARCHAR(128) NOT NULL
  , price FLOAT NOT NULL
  , order_by_unit_id INT
  , bill_by_unit_id INT
  , estimated_weight INT NOT NULL
  , notes VARCHAR(2048)
  , vendor_id VARCHAR(8)
  , last_modified_date DATETIME

  , FOREIGN KEY (bill_by_unit_id) REFERENCES unit(id) 
		ON DELETE SET NULL
  , FOREIGN KEY (order_by_unit_id) REFERENCES unit(id) 
		ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS vendor;

CREATE TABLE vendor /* table to store vendors*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , vendor_name varchar(16) NOT NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS product_group;

CREATE TABLE product_group /* table to store product group information*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , url varchar(256)
  , description VARCHAR(8192)
  , website_id INT
  , vendor_type INT
  , last_modified_date DATETIME

  , FOREIGN KEY (vendor_type) REFERENCES vendor(id) 
		ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS product_group_member;

CREATE TABLE product_group_member /* table to store product group members*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , product_group_id INT
  , product_id INT
  , last_modified_date DATETIME
	
  , FOREIGN KEY (product_id) REFERENCES product(id) 
		ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS product_group_category;

CREATE TABLE product_group_category /* table to store categories for each product group*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , product_group_id INT
  , category_id INT
  , last_modified_date DATETIME

  , FOREIGN KEY (product_group_id) REFERENCES product_group(id) 
		ON DELETE SET NULL
  , FOREIGN KEY (category_id) REFERENCES category(id) 
		ON DELETE SET NULL 
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS product_group_image;

CREATE TABLE product_group_image /* table to store product group images*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , product_group_id INT
  , product_image_url VARCHAR(256)
  , last_modified_date DATETIME

  , FOREIGN KEY (product_group_id) REFERENCES product_group(id) 
		ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS unit;

CREATE TABLE unit /* table to store units*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , unit_name varchar(16) NOT NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS status;

CREATE TABLE status /* table to store statuses*/
( 
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , status_name VARCHAR(32) NOT NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS category;

CREATE TABLE category /* table to store categories*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , category_name varchar (32) NOT NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS inventory;

CREATE TABLE inventory /*table to store inventory in stock*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , product_id INT
  , quantity INT NOT NULL
  , total_weight INT NOT NULL
  , cost FLOAT NOT NULL
  , special_quantity INT NOT NULL
  , special_cost FLOAT NOT NULL
  , notes VARCHAR(2048)
  , last_modified_date DATETIME
	
  , FOREIGN KEY (product_id) REFERENCES product(id) 
		ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS order_info;

CREATE TABLE order_info /* table to store info for each order*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , order_date DATETIME NOT NULL
  , customer_id INT
  , delivery_date_time DATETIME
  , delivery_address VARCHAR(256)
  , delivery_phone VARCHAR(32)
  , discount INT
  , credit FLOAT
  , delivery_fee FLOAT
  , toll_expense FLOAT
  , total_cost FLOAT
  , status_id INT
  , change_due FLOAT
  , delivered BOOLEAN
  , notes VARCHAR (2048)
  , personal BOOLEAN
  , last_modified_date DATETIME
	
  , FOREIGN KEY (status_id) REFERENCES status(id) 
		ON DELETE SET NULL
  , FOREIGN KEY (customer_id) REFERENCES customer(id)
		ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS order_item;

CREATE TABLE order_item /* table to store items for each order*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , order_id INT
  , product_id INT
  , quantity INT NOT NULL
  , weight FLOAT NOT NULL
  , notes VARCHAR (2048)
  , deleted BOOLEAN
  , last_modified_date DATETIME

  , FOREIGN KEY (order_id) REFERENCES order_info(id) 
		ON DELETE SET NULL
  , FOREIGN KEY (product_id) REFERENCES product(id) 
		ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS customer;

CREATE TABLE customer /*table to store customers*/
(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
  , first_name VARCHAR (64) NOT NULL
  , last_name VARCHAR(64) NOT NULL
  , street_address VARCHAR (128)
  , apt_address VARCHAR (32)
  , city VARCHAR (64)
  , state CHAR(2)
  , zip CHAR(5)
  , phone VARCHAR(32)
  , email VARCHAR (128)
  , notes VARCHAR (2048)
  , inactive BOOLEAN
  , last_modified_date DATETIME
);

SET FOREIGN_KEY_CHECKS=1;

/*insert categories*/
INSERT 	category 
SELECT 	0 
	  , "Chicken";

INSERT	category 
SELECT 	0
	  , "Turkey";

INSERT 	category 
SELECT 	0
	  , "Beef, Buffalo, and Pork";

INSERT 	category 
SELECT 	0
      , "Lamb";

INSERT 	category 
SELECT 	0
	  , "Bones";

INSERT 	category 
SELECT 	0
	  , "Fish";

INSERT 	category 
SELECT 	0 
	  , "Fruit and Vegetables";

INSERT 	category 
SELECT 	0
	  , "Mixes";

INSERT 	category 
SELECT 	0
	  , "Other Poultry";

INSERT 	category 
SELECT 	0
	  , "Exotics";

INSERT 	category 
SELECT 	0
	  , "Cat Friendly";

INSERT 	category 
SELECT 	0
	  , "Tripe and Etc";

INSERT 	category 
SELECT 	0
	  , "Treats and Chews";

INSERT 	category 
SELECT 	0
	  , "Supplements";

INSERT 	category 
SELECT 	0
	  , "Dr. Harveys";		

INSERT 	category 
SELECT 	0
	  , "Canidae and Felidae";

INSERT 	category 
SELECT 	0
	  , "Freeze Dried- 9Pk";

INSERT 	category 
SELECT 	0
	  , "Freeze Dried";		

INSERT 	category 
SELECT 	0
	  , "Smoked Treats";

INSERT 	category 
SELECT 	0
	  , "Cat Friendly Treats";		

INSERT 	category 
SELECT 	0
	  , "Best Sellers";

INSERT 	category 
SELECT 	0
	  , "Freeze Dried- 2oz";

INSERT 	category 
SELECT 	0
	  , "Freeze Dried- 4oz";

INSERT 	category 
SELECT 	0
	  , "Freeze Dried- Bulk";	

/*insert units*/
INSERT 	unit 
SELECT 	0
	  , "Each";

INSERT 	unit 
SELECT 	0
	  , "Pound";

INSERT 	unit 
SELECT 	0
	  , "Case";

INSERT 	unit 
SELECT 	0
	  , "Bag";

INSERT 	unit 
SELECT 	0
	  , "Pouch";

INSERT 	unit 
SELECT 	0
	  , "Tube";

INSERT 	unit 
SELECT 	0
	  , "Package";

INSERT 	unit 
SELECT 	0
	  , "Piece";

INSERT 	unit 
SELECT 	0
	  , "Tub";

/* insert statuses*/
INSERT 	status 
SELECT 	0
	  , "Pending";

INSERT 	status 
SELECT 	0
	  , "Open";

INSERT 	status 
SELECT 	0
	  , "Paid (Cash)";

INSERT 	status 
SELECT 	0
	  , "Paid (Credit Card)";

INSERT 	status 
SELECT 	0
	  , "Paid (Check)";

INSERT 	status 
SELECT 	0
	  , "Paid (Paypal)";

INSERT 	status 
SELECT 	0
	  , "Cancelled";

/* insert vendors*/
INSERT 	vendor 
SELECT 	0
	  , "Omas";

INSERT 	schema_changes
SELECT	0
	  , '01'
	  , '00'
	  , '0000'
	  , 'initial install'
	  , NOW();

DROP PROCEDURE IF EXISTS product_group_update; 

DELIMITER //
CREATE PROCEDURE product_group_update
/* check to see if product group with given website_id exists, if not, insert, otherwise make sure last modified date is up to date*/
(
	IN in_url varchar(256)
  , IN in_description VARCHAR(8192)
  , IN in_website_id INT
  , IN in_vendor_type INT
)
BEGIN

	DECLARE	current_last_modified_date DATETIME;
	DECLARE current_id INT;

	/*select last modified date and id for given product group*/
	SELECT 	last_modified_date
		  , id
	INTO 	current_last_modified_date
		  , current_id
	FROM 	product_group 
	WHERE 	website_id = in_website_id;

	/*exists in database*/
	IF current_id IS NOT NULL THEN

		/*out of date, refresh date*/
		IF current_last_modified_date < DATE_SUB(NOW(), INTERVAL 1 HOUR) THEN

			UPDATE 	product_group 
			SET 	url = in_url
				  , description = in_description
				  , vendor_type = in_vendor_type
				  , last_modified_date = NOW() 
			WHERE 	id = current_id;

		END IF;

		/* return id*/
		SELECT 	current_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	product_group 
		SELECT 	0 
			  , in_url
			  , in_description
			  , in_website_id
			  , in_vendor_type
			  , NOW();

		SELECT 	LAST_INSERT_ID();

	END IF;

END
//
DELIMITER ;


DROP PROCEDURE IF EXISTS product_group_member_update; 

DELIMITER //
CREATE PROCEDURE product_group_member_update 
/* check to see if given vendor id is already attached to product group, if not insert, if so, make sure last modified date is up to date*/
(
	IN in_product_group_id INT
  , IN in_vendor_id varchar(8)
)
BEGIN

	DECLARE	current_last_modified_date DATETIME;
	DECLARE current_id INT;

	/*select last modified date and id for given product group and product vendor id*/
	SELECT 	pgm.last_modified_date
	      , pgm.id 
	INTO 	current_last_modified_date
          , current_id 
	FROM 	product_group_member pgm
	      , product p 
	WHERE 	p.id = pgm.product_id 
		AND product_group_id = in_product_group_id 
	    AND vendor_id = in_vendor_id;

	/*exists in database*/
	IF current_id IS NOT NULL THEN

	    /*out of date, refresh date*/
		IF current_last_modified_date < DATE_SUB(NOW(), INTERVAL 1 HOUR) THEN

			UPDATE 	product_group_member 
			SET 	last_modified_date = NOW()
			WHERE 	id = current_id;

		END IF;

		/* return id*/
		SELECT 	current_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	product_group_member 
		SELECT 	0 
			  , in_product_group_id
			  , p.id
			  , NOW() 
		FROM 	product 
		WHERE 	vendor_id = in_vendor_id;

		/* capture auto_increment value*/
		SELECT 	LAST_INSERT_ID(); 

	END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS product_group_category_update;

DELIMITER //
CREATE PROCEDURE product_group_category_update 
/*check to see if given group and category combination exists, if not insert, if so, make sure last_modified_date is up to date*/
(
	IN in_product_group_id INT
  , IN in_category_id INT
)
BEGIN

	DECLARE current_last_modified_date DATETIME;
	DECLARE current_id INT;

	/* select last modified date and id from database for given product group and category*/
	SELECT 	last_modified_date 
		  , id 
	INTO 	current_last_modified_date
	      , current_id 
	FROM 	product_group_category 
	WHERE 	product_group_id = in_product_group_id 
	    AND category_id = in_category_id;

	/*exists in database*/
	IF current_id IS NOT NULL THEN

	    /*out of date, refresh date*/
		IF current_last_modified_date < DATE_SUB(NOW(), INTERVAL 1 HOUR) THEN

			UPDATE 	product_group_category 
			SET 	last_modified_date = NOW()
			WHERE 	id = current_id;

		END IF;

		/* return id*/
		SELECT 	current_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	product_group_category 
		SELECT 	0 
			  , in_product_group_id 
		      , in_category_id 
		      , NOW();

		/* capture auto_increment value*/
		SELECT 	LAST_INSERT_ID(); 

	END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS product_group_image_update;

DELIMITER //
CREATE PROCEDURE product_group_image_update 
/*check to see if given image url exists, if not insert it along with the group it belongs to, if so, make sure last_modified_date is up to date*/
(
	IN in_product_group_id INT
  , IN in_product_image_url VARCHAR(256)
)
BEGIN

	DECLARE current_last_modified_date DATETIME;
	DECLARE current_id INT;

	/* select last modified date and id from database for given image*/
	SELECT 	last_modified_date 
		  , id 
	INTO 	current_last_modified_date
	      , current_id 
	FROM 	product_group_image 
	WHERE 	product_image_url = in_product_image_url;

	/*exists in database*/
	IF current_id IS NOT NULL THEN

	    /*out of date, refresh date*/
		IF current_last_modified_date < DATE_SUB(NOW(), INTERVAL 1 HOUR) THEN

			UPDATE 	product_group_image 
			SET 	last_modified_date = NOW()
			WHERE 	id = current_id;

		END IF;

		/* return id*/
		SELECT 	current_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	product_group_image 
		SELECT 	0 
			  , in_product_group_id 
		      , in_product_image_url
		      , NOW();

		/* capture auto_increment value*/
		SELECT 	LAST_INSERT_ID(); 

	END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS inventory_update;

DELIMITER //
CREATE PROCEDURE inventory_update 
/*check to see if inventory exists for product, if not insert record, if so, update record*/
(
	IN in_id INT
  , IN in_product_id INT
  , IN in_quantity INT
  , IN in_total_weight INT
  , IN in_cost FLOAT
  , IN in_special_quantity INT
  , IN in_special_cost FLOAT
  , IN in_notes VARCHAR(2048)
  , IN in_vendor_id VARCHAR(8)
)
BEGIN

	/*if we have vendor id instead of product id then do lookup for product id*/
	IF in_vendor_id IS NOT NULL THEN

		SELECT 	id 
		INTO 	in_product_id
		FROM 	product 
		WHERE  	vendor_id = in_vendor_id;

	END IF;

	/*we aren't passed inventory id so we'll try to look it up*/
	IF in_id IS NULL THEN
		SELECT 	id
		INTO 	in_id
		FROM 	inventory
		WHERE  	product_id = in_product_id;

	END IF;

	/* if we passed an id or vendor, this is an update*/
	IF in_id IS NOT NULL THEN

		UPDATE 	inventory 
		SET 	special_quantity = in_special_quantity + quantity
			  , quantity = quantity + in_quantity
			  , total_weight = in_total_weight
			  , special_cost = cost
			  , cost = in_cost
			  , notes = in_notes
			  , last_modified_date = NOW()
		WHERE 	(in_id IS NULL OR id = in_id)
			AND (in_vendor_id IS NULL OR id = (SELECT id FROM product WHERE vendor_id = in_vendor_id));

		/* return id*/
		SELECT 	in_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	inventory
		SELECT 	0
			  , in_product_id
			  , in_quantity
			  , in_total_weight
			  , in_cost
			  , in_special_quantity
			  , in_special_cost
			  , in_notes
			  , NOW();

		/* capture auto_increment value*/
		SELECT 	LAST_INSERT_ID(); 

END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS product_update;

DELIMITER //
CREATE PROCEDURE product_update 
/*check to see if product exists, if not insert record, if so, update record*/
(	
	IN in_id INT
  , IN in_product_name VARCHAR(128)
  , IN in_price FLOAT
  , IN in_order_by_unit_id INT
  , IN in_bill_by_unit_id INT
  , IN in_estimated_weight INT
  , IN in_notes VARCHAR(2048)
  , IN in_vendor_id VARCHAR(8)

)
BEGIN

	/*we aren't passed product id so we'll try to look it up*/
	IF in_id IS NULL THEN
		SELECT 	id
		INTO 	in_id
		FROM 	product
		WHERE  	vendor_id = vendor_id;

	END IF;

	/* if we passed or looked up a product id, this is an update*/
	IF in_id IS NOT NULL THEN

		UPDATE 	product 
		SET 	product_name = in_product_name
			  , price = in_price
			  , order_by_unit_id = in_order_by_unit_id
			  , bill_by_unit_id = in_bill_by_unit_id
			  , estimated_weight = in_estimated_weight
			  , notes = in_notes
			  , vendor_id = in_vendor_id
		      , last_modified_date = NOW() 
		WHERE 	id = in_id;

		/* return id*/
		SELECT 	in_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	product
		SELECT 	0
				in_product_name
			  , in_price
			  , in_order_by_unit_id
			  , in_bill_by_unit_id
			  , in_estimated_weight
			  , in_notes
			  , in_vendor_id
			  , NOW();

		/* capture auto_increment value*/
		SELECT 	LAST_INSERT_ID(); 

	END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS product_retrieve;

DELIMITER //
CREATE PROCEDURE product_retrieve 
/*retrieve product(s)*/
(	
	IN in_id INT
  , IN in_match VARCHAR(32)
  , IN in_limit INT
)
BEGIN

	SELECT 		p.id AS product_id
			  , p.product_name
			  , pc.description
			  , pc.vendor_name 
			  , pc.url
			  , pc.website_id
			  , p.price
			  , p.estimated_weight
			  , p.notes AS product_notes
			  , p.vendor_id
			  , p.notes AS product_notes
			  , pc.categories
			  , pi.images
			  , u1.unit_name AS bill_by_unit_name
			  , u2.unit_name AS order_by_unit_name
			  , i.id AS inventory_id
			  , i.quantity
			  , i.special_quantity
			  , i.cost
			  , i.special_cost
			  , i.total_weight 
			  , i.notes AS inventory_notes
	FROM   		product p
		      , inventory i
		      , unit u1
			  , unit u2
			  , (	SELECT 		product_id
							  , GROUP_CONCAT(product_image_url SEPARATOR '|') AS images 
					FROM 		product_group_image pgi
							  , product_group pg 
							  , product_group_member pgm
					WHERE 		pgi.product_group_id = pg.id 
							AND pgm.product_group_id = pg.id
					GROUP BY 	product_id) pi 
			  , (	SELECT 		product_id
							  , description
							  , url
							  , website_id
							  , vendor_name
							  , GROUP_CONCAT(category_name SEPARATOR '|') AS categories 
					FROM 		category c
							  , product_group_category pgc
							  , product_group pg 
							  , vendor v
					WHERE 		pgc.product_group_id = pg.id 
							AND v.id = pg.vendor_type
							AND pgc.category_id = c.id 
					GROUP BY 	product_id, description, url, website_id, vendor_name) pc 
	WHERE 		p.id=i.product_id 
			AND p.bill_by_unit_id = u1.id 
			AND	p.order_by_unit_id = u2.id 
			AND pc.product_id = p.id 
			AND pi.product_id = p.id 
			AND (in_match IS NULL OR p.product_name LIKE CONCAT('%',in_match,'%'))
			AND (in_id IS NULL OR id = in_id)
	ORDER BY 	product_name 
	LIMIT 		in_limit;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS order_info_retrieve;

DELIMITER //
CREATE PROCEDURE order_info_retrieve
/*retrieve order record(s)*/
(	
	IN in_id INT
  , IN in_start_order_date DATETIME
  , IN in_end_order_date DATETIME
  , IN in_customer_ids VARCHAR(256)
  , IN in_status_ids VARCHAR(256)
  , IN in_personal BOOLEAN
  , IN in_delivered BOOLEAN
)
BEGIN

	SELECT 		oi.id
			  , oi.order_date
			  , oi.customer_id
			  , oi.delivery_date_time
			  , oi.delivery_address
			  , oi.delivery_phone
			  , oi.discount
			  , oi.credit
			  , oi.delivery_fee
			  , oi.toll_expense
			  , oi.total_cost
			  , oi.change_due
			  , oi.delivered
			  , oi.personal
			  , oi.notes
			  , s.status_name 
			  , c.first_name
			  , c.last_name
	FROM 		order_info oi
			  , customer c
	WHERE 		c.id = oi.customer_id
				(in_id IS NULL OR id = in_id)
			AND (in_start_order_date IS NULL OR order_date > in_start_order_date)
			AND (in_end_order_date IS NULL OR order_date < in_end_order_date)
			AND (in_customer_id IS NULL OR customer_id IN (in_customer_id))
			AND (in_status IS NULL OR status_id IN (in_status))
			AND (in_personal IS NULL OR personal = in_personal)	
			AND (in_delivered IS NULL OR delivered = in_delivered)	
	ORDER BY 	order_date;

	/*If this is a single order to be retrieved and we need full customer information*/
	IF in_id IS NOT NULL THEN
		
		CALL customer_retrieve(NULL, NULL, 1, in_id);
	
	END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS order_info_update;

DELIMITER //
CREATE PROCEDURE order_info_update 
/*check to see if info exists for order, if not insert record, if so, update record*/
(
	IN in_id INT
  , IN in_order_date DATETIME
  , IN in_customer_id INT
  , IN in_delivery_date_time DATETIME
  , IN in_delivery_address VARCHAR(256)
  , IN in_delivery_phone VARCHAR(32)
  , IN in_discount INT
  , IN in_credit FLOAT
  , IN in_delivery_fee FLOAT
  , IN in_toll_expense FLOAT
  , IN in_total_cost FLOAT
  , IN in_status_id INT
  , IN in_change_due FLOAT
  , IN in_delivered BOOLEAN
  , IN in_notes VARCHAR (2048)
  , IN in_personal BOOLEAN
  , IN in_delete_flag BOOLEAN
)
BEGIN

	/* if we passed an id, this is an update or delete*/
	IF in_id IS NOT NULL THEN

		/* we want to "delete" this order by setting the status to "Cancelled"*/
		IF in_delete_flag = TRUE THEN

			UPDATE 	order_info oi, status s 
			SET 	oi.payment_method_id = s.id
			WHERE 	id = in_id
				AND s.status_name = 'Cancelled';			
		
		/*this is an update*/
		ELSE
			
			UPDATE 	order_info 
			SET 	customer_id = in_customer_id
				  , delivery_date_time = in_delivery_date_time
				  , delivery_address = in_delivery_address
				  , delivery_phone = in_delivery_phone
				  , discount = in_discount
				  , credit = in_credit
				  , delivery_fee = in_delivery_fee
				  , toll_expense = in_toll_expense
				  , total_cost = in_total_cost
				  , status_id = in_status_id
				  , change_due = in_change_due
				  , delivered = in_delivered
				  , notes = in_notes
				  , personal = in_personal
				  , last_modified_date = NOW()
			WHERE 	id = in_id;

		END IF;

		/* return id*/
		SELECT 	in_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	order_info
		SELECT 	0
			  , in_order_date
			  , in_customer_id
			  , in_delivery_date_time
			  , in_delivery_address
			  , in_delivery_phone
			  , in_discount
			  , in_credit
			  , in_delivery_fee
			  , in_toll_expense
			  , in_total_cost
			  , in_status_id
			  , in_change_due
			  , in_delivered
			  , in_notes
			  , in_personal
			  , NOW();

		/* capture auto_increment value*/
		SELECT 	LAST_INSERT_ID(); 

END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS order_item_retrieve;

DELIMITER //
CREATE PROCEDURE order_item_retrieve
/*retrieve order item record(s)*/
(	
	IN in_id INT
)
BEGIN

	SELECT 		oi.id AS order_item_id
			  , oi.quantity
			  , oi.weight
			  , oi.notes, p.id AS product_id
			  , p.product_name
			  , pc.description
			  , p.price
			  , u.unit_name
			  , p.estimated_weight
			  , p.vendor_id
			  , pc.vendor_name
			  , pc.categories 
	FROM 		order_item oi
			  , product p
			  , unit u
			  , (SELECT 	product_id
						  , description
						  , vendor_name
						  , GROUP_CONCAT(category_name SEPARATOR '|') AS categories 
				 FROM		category c
						  , product_group_category pgc
						  , product_group_member pgm
						  , product_group pg 
						  , vendor v
				WHERE 		pgc.product_group_id = pg.id 
						AND pgm.product_group_id = pg.id
						AND v.id = pg.vendor_type
						AND pgc.category_id = c.id 
				GROUP BY 	product_id, description, vendor_name) pc 
	WHERE 		pc.product_id = p.id 
			AND oi.product_id = p.id 
			AND p.bill_by_unit_id = u.id 
			AND order_id = in_id 
			AND deleted = FALSE
	ORDER BY 	p.product_name;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS order_item_update;

DELIMITER //
CREATE PROCEDURE order_item_update 
/*check to see if item exists for order, if not insert record, if so, update record*/
(
	IN in_id INT
  , IN in_order_id INT
  , IN in_product_id INT
  , IN in_quantity INT
  , IN in_weight FLOAT
  , IN in_notes VARCHAR (2048)
  , IN in_deleted BOOLEAN
)
BEGIN

	/* if we passed an id, this is an update or delete*/
	IF in_id IS NOT NULL THEN

		/* we want to "delete" this order item by setting the deleted column to "true"*/
		IF in_delete = TRUE THEN

			UPDATE 	order_item
			SET 	deleted = TRUE
			WHERE 	id = in_id;
		
		/*this is an update*/
		ELSE
			
			UPDATE 	order_item 
			SET 	order_id = in_order_id
				  , product_id = in_product_id
				  , quantity = in_quantity
				  , weight = in_weight
				  , notes = in_notes 
				  , deleted = FALSE
				  , last_modified_date = NOW()
			WHERE 	id = in_id;

		END IF;

		/* return id*/
		SELECT 	in_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	order_item
		SELECT 	0
			  , in_order_id
			  , in_product_id
			  , in_quantity
			  , in_weight
			  , in_notes
			  , FALSE
			  , NOW();

		/* capture auto_increment value*/
		SELECT 	LAST_INSERT_ID(); 

END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS customer_retrieve;

DELIMITER //
CREATE PROCEDURE customer_retrieve 
/*retrieve product(s)*/
(	
	IN in_id INT
  , IN in_match VARCHAR(32)
  , IN in_limit INT
  , IN in_order_id INT
)
BEGIN

	SELECT	 	id
			  , first_name
			  , last_name
			  , street_address
			  , apt_address
			  , city
			  , state
			  , zip
			  , phone
			  , email
			  , notes 
	FROM 		customer
	WHERE 		((in_match IS NULL OR first_name LIKE CONCAT('%',in_match,'%'))
			 OR (in_match IS NULL OR last_name LIKE CONCAT('%',in_match,'%'))
			 OR (in_match IS NULL OR email LIKE CONCAT('%',in_match,'%'))
			 OR (in_match IS NULL OR phone LIKE CONCAT('%',in_match,'%')))
			AND (in_id IS NULL OR id = in_id)
			AND (in_order_id IS NULL OR id IN (SELECT 	customer_id 
											   FROM 	order_info
											   WHERE 	id = in_order_id))
			AND inactive = FALSE
	ORDER BY 	last_name 
	LIMIT 		in_limit;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS customer_update;

DELIMITER //
CREATE PROCEDURE customer_update 
/*check to see if customer exists, if not insert record, if so, update record*/
(
	IN in_id INT
  , IN in_first_name VARCHAR (64)
  , IN in_last_name VARCHAR(64)
  , IN in_street_address VARCHAR (128)
  , IN in_apt_address VARCHAR (32)
  , IN in_city VARCHAR (64)
  , IN in_state CHAR(2)
  , IN in_zip CHAR(5)
  , IN in_phone VARCHAR(32)
  , IN in_email VARCHAR (128)
  , IN in_notes VARCHAR (2048)
  , IN in_inactive BOOLEAN
)
BEGIN

	/* if we passed an id, this is an update or delete*/
	IF in_id IS NOT NULL THEN

		/* we want to "delete" this customer by setting the inactive column to "true"*/
		IF in_inactive = TRUE THEN

			UPDATE 	customer
			SET 	inactive = TRUE
			WHERE 	id = in_id;
		
		/*this is an update*/
		ELSE
			
			UPDATE 	customer 
			SET 	first_name = in_first_name
				  , last_name = in_last_name
				  , street_address = in_street_address
				  , apt_address = in_apt_address
				  , city = in_city
				  , state = in_state
				  , zip = in_zip
				  , phone = in_phone
				  , email = in_email
				  , notes = in_notes
				  , inactive = FALSE
				  , last_modified_date = NOW()
			WHERE 	id = in_id;

		END IF;

		/* return id*/
		SELECT 	in_id; 

	/* doesn't exist, insert into database*/
	ELSE 

		INSERT 	order_item
		SELECT 	0
			  , in_first_name
			  , in_last_name
			  , in_street_address
			  , in_apt_address
			  , in_city
			  , in_state
			  , in_zip
			  , in_phone
			  , in_email
			  , in_notes
			  , FALSE
			  , NOW();

		/* capture auto_increment value*/
		SELECT 	LAST_INSERT_ID(); 

END IF;

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS product_report;

DELIMITER //
CREATE PROCEDURE product_report 
/*retrieve products that haven't been updated*/
(	
)
BEGIN

	SELECT 		p.id
			  , p.product_name
			  , v.vendor_name 
			  , pg.url
			  , pg.website_id
			  , p.vendor_id
			  , p.last_modified_date
	FROM   		product p
			  , product_group pg 
			  , vendor v
	WHERE 		pg.product_id = p.id 
			AND v.id = pg.vendor_type
			AND last_modified_date < DATE_SUB(NOW(), INTERVAL 1 HOUR)
	ORDER BY 	product_name; 

END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS product_group_report;

DELIMITER //
CREATE PROCEDURE product_group_report 
/*retrieve product group information that is missing or hasn't been updated*/
(	
)
BEGIN

	/* select products that aren't in any product groups*/
	SELECT 		p.id
			  , product_name
			  , vendor_id
			  , last_modified_date
	FROM   		product
	WHERE 		id NOT IN (	SELECT 	DISTINCT product_id
							FROM 	product_group_member
							WHERE 	last_modified_date > DATE_SUB(NOW(), INTERVAL 1 HOUR))
	ORDER BY 	product_name; 

	/* select product groups that are out of date*/
	SELECT		pg.id 
			  , pg.url
			  , pg.website_id
			  , v.vendor_name
			  , pg.last_modified_date
	FROM   		product_group pg
			  , vendor v
	WHERE 		pg.vendor_id = v.id 
			AND pg.last_modified_date < DATE_SUB(NOW(), INTERVAL 1 HOUR)
	ORDER BY 	pg.id; 

	/* select product groups without up to date images*/
	SELECT		pg.id 
			  , pg.url
			  , pg.website_id
			  , v.vendor_name
			  , pg.last_modified_date
	FROM   		product_group pg
			  , vendor v	
	WHERE 		pg.vendor_id = v.id 
			AND pg.id NOT IN (	SELECT 	DISTINCT product_group_id 
								FROM 	product_group_image 
								WHERE   last_modified_date > DATE_SUB(NOW(), INTERVAL 1 HOUR))
	ORDER BY 	pg.id; 
	
	/*select product groups without up to date categories*/
	SELECT		pg.id 
			  , pg.url
			  , pg.website_id
			  , v.vendor_name
			  , pg.last_modified_date
	FROM   		product_group pg
			  , vendor v	
	WHERE 		pg.vendor_id = v.id 
			AND pg.id NOT IN (	SELECT 	DISTINCT product_group_id 
								FROM 	product_group_category 
								WHERE   last_modified_date > DATE_SUB(NOW(), INTERVAL 1 HOUR))
	ORDER BY 	pg.id; 

	/*select out of date product group images*/
	SELECT 	pgi.product_group_id
		  , pgi.product_image_url
		  , pgi.last_modified_date
	FROM 	product_group_image pgi
	WHERE   last_modified_date < DATE_SUB(NOW(), INTERVAL 1 HOUR);

	/*select out of date product group categories*/
	SELECT 	pgc.product_group_id
		  , pgc.category_id
		  , c.category_name
		  , pgc.last_modified_date
	FROM 	product_group_image pgc
		  , category c
	WHERE   c.id = pgc.category_id 
		AND last_modified_date < DATE_SUB(NOW(), INTERVAL 1 HOUR);

END
//
DELIMITER ;
