# building angular webview
FROM node:9.11.2 as builder
WORKDIR /webview
COPY . .
RUN npm install
RUN npm run build --prod

# deploying angular webview
FROM nginx:alpine
COPY --from=builder /webview/dist/webview /usr/share/nginx/html
