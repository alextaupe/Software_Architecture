##########################################################
#
# FINAL ONE-LINE COMMAND:
#
# dependencies: 
#   files:
#     2018-austria-latest.osm, DD.sql, Dockerfile, Graz.poly, graz_poi_complete.csv, .dockerignore
#   tools:
#     osmctools (including osmfilter, osmconvert), grep, docker
#
##########################################################

osmfilter 2018-austria-latest.osm --keep="shop" | osmconvert - -B=Graz.poly --all-to-nodes --csv="@lat @lon shop name opening_hours website" --csv-separator=";" --csv-headline | grep -E "([0-9\.]+;[0-9\.]+;[^;]*;[^;]+;[^;]*;[^;]*)" > final.csv && docker build . --tag=maria:latest && docker run --rm -it maria:latest
