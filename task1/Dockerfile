FROM mariadb:10
ENV MYSQL_ROOT_PASSWORD=root
COPY ./final.csv /tmp/final.csv
COPY ./graz_poi_complete.csv /tmp/graz_poi_complete.csv
ADD DD.sql /docker-entrypoint-initdb.d
EXPOSE 3306
