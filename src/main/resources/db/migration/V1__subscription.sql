create sequence seq_subscription_id;

create table subscription (
  id bigint primary key,
  user_id bigint not null,
  user_name varchar(30) not null,
  chat_id bigint not null,
  chat_type varchar(10) not null
);
