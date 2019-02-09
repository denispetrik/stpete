create sequence seq_interaction_id;

create table interaction (
  id bigint primary key,
  update_id bigint not null,
  user_id bigint not null,
  user_name varchar(30) not null,
  chat_id bigint not null,
  chat_type varchar(10) not null,
  date_time timestamp not null,
  text varchar(100) not null
);
