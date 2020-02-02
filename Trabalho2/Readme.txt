Readme
Docker concepts that i used: VOLUME, NETWORK,DOCKERFILE (RUN, ENTRYPOINT,COPY)

We have 2 interfaces (Client Monitor and Servidor) that are the interfaces to connect RMI server to client and vice-versa. The images contains the JAVA RMI code, Dockerfile with Entrypoints and RUN

Servidor is Server in Portuguese and Cliente is Client. The code comments are in Portuguese, some of the variables too....

The Server for each Client will create a Random File in the serverdata folder in the servervol VOLUME, and md5 and send it to the Connected Client every 5 seconds. If you follow the guide below, you will be able to run it detached (-dit) and you can inspect later.

To execute the program you will need to create a custom bridge network:

docker network create --driver bridge alonso

docker run -dit -p 1099:1099 --name server --network alonso -v servervol:/serverdata servidorrmi ash

docker run -dit --name client --network alonso -v clientvol:/clientdata clientermi ash

-------------------- Portuguese --------------------

O Servidor para Cada Cliente conectado cria um arquivo randômico na pasta serverdata no volume servervol, e envia aos clientes a cada 5 segundos.

Para executar o programa deve ser criada uma rede bridge customizada:

docker network create --driver bridge alonso

docker run -dit -p 1099:1099 --name servidor --network alonso -v servervol:/serverdata servidorrmi ash

docker run -dit --name cliente --network alonso -v clientvol:/clientdata clientermi ash

D - Detached (Em backGroud)
I - Interactive (Posso escrever se combinar com o Terminal)
T - Terminal (Com Bash aberto)