merge into MPA (MPA_id, name)
values (1, 'G'), (2, 'PG'), (3, 'PG_13'), (4, 'R'), (5, 'NC_17');

merge into Genre (genre_id, name)
values (1, 'DRAMA'), (2, 'COMEDY'), (3, 'ACTION'), (4, 'HORROR'), (5, 'THRILLER'), (6, 'SPORT');

CREATE TABLE IF NOT EXISTS Friendship_status
(
status_id int not null primary key auto_increment,
name varchar(255) not null,
constraint Status_PK primary key (status_id)
);

merge into Friendship_status (status_id, name)
values (1, 'CONFIRMED'), (2, 'UNCONFIRMED');
