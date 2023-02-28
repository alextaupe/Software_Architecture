FROM node:9.11.2

WORKDIR /angular

VOLUME ["/angular"]

RUN npm install -g @angular/cli

EXPOSE 4200

ENTRYPOINT [ "ng" ]
CMD ["--help"]

# ok guys.
# this produces a node container to serve the webview (angluar),
# so that it is easier to work with it (can see changes immediatly).
# Using this Dockerfile, it is also not needed to install node and npm yourself
#
# 1) building "builder": step needed ONCE!
#
# change to this directory and run:
# docker build . --tag=ngbuild:latest
#
# This will produce a "builder" that we can use for serving
#
# 2) serving webview: always do on system start (if you want to work)
#
# docker run -d --rm --name=ngwebview -it -v /ABSOLUTE/PATH/TO/sa_18_04_unofficial_code/task4/webview:/angular -p 4200:4200 ngbuild:latest serve --host=0.0.0.0
#
# Now open your browser on "localhost:4200" and get to it :)
