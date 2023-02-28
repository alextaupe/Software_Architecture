FROM node:9.11.2

WORKDIR /angular

VOLUME ["/angular"]

ENTRYPOINT [ "npm" ]
CMD ["--help"]

# Use this to resolve npm dep. before deployment
#
# 1) build npmbuider
# docker build . --tag=npmbuild:latest
#
# 2) install dep
# docker run --rm -v /ABSOLUTE/PATH/TO/sa_18_04_unofficial_code/task4/webview:/angular -it npmbuild:latest install
#
#

