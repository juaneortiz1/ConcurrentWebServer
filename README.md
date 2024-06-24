Sure, here's the updated README file including the author and mentioning what was built in.

---

# ConcurrentTestWebApp

## Overview
ConcurrentTestWebApp is a simple threaded web server application written in Java. It serves HTML files and image files encoded in Base64, and is capable of handling multiple client connections concurrently. If the number of concurrent clients exceeds the set limit, it responds with a service unavailable message from a pre-defined text file.

## Features
- Handles concurrent client connections using threads.
- Serves HTML files and image files (PNG, JPG, JPEG) encoded in Base64.
- Limits the maximum number of concurrent clients and provides a response if this limit is exceeded.

## Project Structure
```
ConcurrentTestWebApp/
│
├── src/
│   └── main/
│       └── resources/
│           ├── index.html
│           ├── exceeded.txt
│           └── ... (other resources)
│
├── ConcurrentTestWebApp.java
└── README.md
```



4. **Access the web server**:
    - Open a web browser and navigate to `http://localhost:45000`.
    - For search requests, navigate to `http://localhost:45000/search?file=filename`, where `filename` is the name of the file you want to search in the `resources` directory.

## Configuration
- **Default HTML file**: The default HTML file served is `index.html` located in `src/main/resources/`.
- **Exceeded response file**: If the maximum number of clients is exceeded, the content from `exceeded.txt` located in `src/main/resources/` is served.
- **Port**: The server listens on port `45000` by default.
- **Maximum Clients**: The maximum number of concurrent clients is set to `3`.

## What Was Built
This project includes the following built-in functionalities:
- A simple HTTP server capable of handling requests for HTML and image files.
- Concurrent handling of client requests using Java threads.
- Limitation on the number of concurrent clients with a custom response for exceeding clients.
- Base64 encoding for image files served through the HTTP response.

## Comparison with Initial Sequential Version
The initial version of this web server was sequential, meaning it could handle only one client at a time. Here are some key differences:

### Initial Sequential Version
- **Sequential Handling**: The server handled one client at a time. The server socket accepted a connection and processed it fully before accepting another.
- **Blocking**: While handling a client request, the server was blocked and could not handle other incoming connections.
- **Simple Implementation**: Easier to implement but not efficient for handling multiple clients.

### Concurrent Version (Current)
- **Concurrent Handling**: The server can handle multiple clients at the same time using threads.
- **Non-Blocking**: By using threads, the server can accept new connections while processing other requests, improving responsiveness and throughput.
- **Synchronization**: Introduces complexity due to the need to manage concurrent access to shared resources (e.g., client count).
- **Client Limit**: Includes a mechanism to limit the number of concurrent clients and respond appropriately if the limit is exceeded.

### Key Benefits of the Concurrent Version
- **Improved Performance**: Can handle multiple clients simultaneously, improving overall performance and user experience.
- **Scalability**: More scalable as it can handle more clients efficiently up to the set limit.
- **User Experience**: Users are less likely to experience delays due to blocking, as requests are processed concurrently.


## Author
This project was developed by Juan Esteban Ortiz.



