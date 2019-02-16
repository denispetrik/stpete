create sequence seq_forecast_id;

create table forecast(
  id bigint primary key,
  epoch_time bigint not null,
  clouds integer not null
);

--create index idx_forecast_epoch_time on forecast (epoch_time);
