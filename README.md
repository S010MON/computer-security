## 1. Docker
### 1.a. Building Docker Image
To build the docker container (skip if no changes have been made) navigate to the top level directory `.../server` and run:
```bash
docker build -t cs_server .
```
This will name your image `cs_server` which is how you can refer to it later when you want to run it.  The tag `:latest` is automatically added.

**Note the full stop at the end of the command, this is important to use the current working directory!**

### 1.b. Running Docker Container
To run the docker container:
```bash
docker run --name my_server_name -p 80:80 cs_server:latest
```

+ `--name my_server_name` This assigns the name to the container, if you forget it you need to use the hexadecimal id 
+ `-p 80:80` Sets the input and output ports for the image
+ `cs_server:latest` Tells docker which image to run (which we named above)

You should see the following output:
```
INFO:     Started server process [1]
INFO:     Waiting for application startup.
INFO:     Application startup complete.
INFO:     Uvicorn running on http://0.0.0.0:80 (Press CTRL+C to quit)
```
To see the swagger UI to interact with the server, use your browser and navigate to the address below:
```
localhost/docs
```
You should see this:
![](https://github.com/S010MON/computer-security/blob/main/screenshots/server_swagger.png)

### 1.c. Troubleshooting

#### 1.c.1 Permissions
If you get a permission error while running docker commands like the one below, then make sure you run any commands using the `sudo` prefix
```commandline
Got permission denied while trying to connect to the Docker daemon socket
```

#### 1.c.2 Seeing available containers/images
To see all the containers available use:
```
docker ps -a
```
outputs the list:
```
CONTAINER ID   IMAGE           COMMAND                  CREATED          STATUS                      PORTS     NAMES
b9daf9cd39f4   bronze:latest   "uvicorn app.main:ap…"   8 minutes ago    Exited (0) 28 seconds ago             bronze-1
47415521dfc6   78c0bafd00fe    "uvicorn app.main:ap…"   11 minutes ago   Exited (0) 9 minutes ago              bronze
```

To see all the avialable images that you can run use:
```
docker image ls -a
```
outputs the list:
```
REPOSITORY   TAG       IMAGE ID       CREATED          SIZE
bronze       latest    b939a4b05b95   11 minutes ago   972MB
python       3.9       3c6a9a896255   2 days ago       915MB
```

#### 1.c.3 Stopping containers
```
docker stop <CONTAINER_NAME:TAG>
```

#### 1.c.4 Removing containers
To remove an image, identify the image name from the ps and use the command:
```
docker rm <IMAGE_NAME>
```


