DROP TABLE post;

CREATE TABLE post (
	id serial primary key,
	name text,
	text text,
	link text,
	created timestamp unique
);