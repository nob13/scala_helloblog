# --- First database schema

# --- !Ups
create table post (
    id          bigint not null primary key,
    title       varchar (255) not null,
    content     text not null,
    date        datetime not null,
);

create sequence post_seq start with 1000;

# --- !Downs
drop table if exists post;
drop sequence if exists post_seq;
