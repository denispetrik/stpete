create sequence seq_subscription_id;

create table subscription (
  id bigint primary key,
  user_id bigint not null,
  user_name varchar(30) not null,
  chat_id bigint not null,
  chat_type varchar(10) not null
);

insert into subscription(id, user_id, user_name, chat_id, chat_type)
  values(1, 165925974, 'petrique', 165925974, 'private');
