# 🎵 Java Music Transfer
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![GitHub Stars](https://img.shields.io/github/stars/nikryan-cpu/Java_Music_transfer?style=social)](https://github.com/nikryan-cpu/Java_Music_transfer/stargazers)
## Educational project in Java and Web
### 🔥About
**Effortlessly migrate your music universe** from Yandex Music to Spotify with this elegant web platform. Powered by the **Spotify API** and a custom **Yandex Music parser**, platform ensures your playlists and tracks transition seamlessly between platforms while preserving metadata and order.



<img src="https://via.placeholder.com/800x400?text=Demo+Interface" align="right" width="45%">

![image_2025-02-09_19-34-34 (2)](https://github.com/user-attachments/assets/f3051e4f-6a2e-4372-8962-3aa593ae29f2)
![image_2025-02-09_20-09-37](https://github.com/user-attachments/assets/3ec2b02b-6dbb-4788-a352-9fc810d00d50)
![image (1)](https://github.com/user-attachments/assets/87318d9e-d352-432e-8e4a-97ff50d7e099)



## 🛠 Tech Stack
| Component       | Technologies                                                                 |
|-----------------|-----------------------------------------------------------------------------|
| **Backend**     | Java, RestAPI                                           |
| **Frontend**    | React.js                                        |
| **APIs**        | [Spotify Web API](https://developer.spotify.com/documentation/web-api), Custom Yandex Music Parser |
| **Auth**        | OAuth 2.0 (Spotify)                                             |     

## 🚀 Fast Start

Spotify Developer Setup

Go to Spotify Developer Dashboard.

Log in with your Spotify account.

Click Create App in the dashboard.

Enter any app name and description.

Under Redirect URIs, add: http://localhost:8080/api/callback.

Select Web API and click Save.

Get API Credentials

In the app's Settings, copy the Client ID and Client Secret.

Paste these credentials into the application.properties file located at:
transfer/src/main/resources/application.properties.


Clone the Repository

git clone https://github.com/nikryan-cpu/Java_Music_transfer.git


Backend Setup

Open a terminal and navigate to the transfer folder:


cd Java_Music_transfer/transfer
Build and run the Spring Boot application:


mvnw.cmd clean install
mvnw.cmd spring-boot:run
Frontend Setup

Open a second terminal and navigate to the frontend folder:

cd Java_Music_transfer/transfer/frontend

Install dependencies and start the development server:

npm install
npm run dev

Access the Platform
Open your browser and go to:
http://localhost:8080
