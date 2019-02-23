create sequence seq_notification_id;

create table notification (
  id bigint primary key,
  status varchar(4) not null,
  chat_id bigint not null,
  message varchar(100) not null,
  date_time timestamp not null default now()
);

--create index idx_notification_new on notification (date_time) where status = 'new';
