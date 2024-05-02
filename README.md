PadelApp

Se trata de un proyecto realizado para el Trabajo Fin de Grado del grado de GITI en la ETSI, Universidad de Sevilla

Podemos definir a groso modo nuestro proyecto como: diseño de una aplicación móvil para Android cuya finalidad es poder conectarse a un robot lanza pelotas de pádel y poder mandar desde la App diferentes órdenes de lanzamiento para que el robot las ejecute.
Esta conexión y comunicación entre la App y el robot se hará mediante sockets TCP, ya que ambos estarán conectados a la misma red WiFi.

El proyecto se compone entonces de:

1) Cliente

   Diseño de una aplicación móvil mediante lenguaje Kotlin (Android) y Java usando como IDE la aplicación de escritorio Android Studio.

2) Servidor

   En otra máquina, concretamente en una raspberry pi, se simulará el robot lanza pelotas mediante un programa en lenguaje Python
