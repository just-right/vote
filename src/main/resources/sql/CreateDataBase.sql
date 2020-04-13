--net start/stop mysql
--set global time_zone = '+8:00';
--redis-server.exe redis.windows.conf
--redis-cli -h 127.0.0.1 -p 6379
--redis-server /etc/redis/redis.conf

create table vote_activity
(
    id            int auto_increment
        primary key,
    name          varchar(50) null,
    begindatetime datetime    null,
    enddatetime   datetime    null
);

create table vote_option
(
    id           int auto_increment
        primary key,
    serialnumber int         null,
    name         varchar(50) null,
    aid          int         null,
    score        int         null,
    constraint vote_option_vote_activity_id_fk
        foreign key (aid) references vote_activity (id)
);



