create table department (
	id integer primary key autoincrement
	, title varchar(40) not null unique
	, description varchar(200) not null
)
/

create table employee (
	id integer primary key autoincrement
	, name varchar(40) not null
	, department_id integer foreign key references department(id)
)
/
