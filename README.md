# apip2

Nombre de desarrollador: Deisy Jaque

API con convertidor de tiempo en formato UTC

Se emplea IDE "intellij IDEA" versión community para el proyecto. 

Framework Spring Boot versión 2.1.6.RELEASE con JAVA

Versión de Java: 8

Dependencias usadas:


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web-services</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>2.9.3</version>
        </dependency>

        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.1</version>
        </dependency>

        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
            <version>2.10.2</version>
        </dependency>

        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.5</version>
        </dependency>

        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.8.7</version>
        </dependency>


Para ejecutar de forma local, se debe configurar en: 

    "Edit Configurations" - "+" - "Maven" -  En campo "Command Line" escribir spring-boot:run

Una vez realizado el paso anterior, se ejecutará el API por el puerto 8080

Si se desea generar el jar, se debe configurar lo siguiente:
   
    "Edit Configurations" - "+" - "Maven" -  En campo "Command Line" clean compile package

Si el build se realiza exitosamente, se busca el .jar dentro de la carpeta "target" del proyecto y se ejecuta con el comando
    
    java -jar nombredeljar.jar 
   
Link en la nube del API: http://djaque.prueba.mooo.com/api

Ruta del servicio: /timezone-utc

Ruta completa para la llamada del servicio: http://djaque.prueba.mooo.com/api/timezone-utc

Recibe un body json con dos parámetros:

    {
        "time": "23:37:20",
        "timezone": "-4"
    }

El mismo devuelve un objeto tipo ResponseEntity<byte[]> con el archivo json y el contenido del resultado

        byte[] responsefile = response.getBytes();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=response.json")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(responsefile.length)
                .body(responsefile);

Los headers que ayudan con la respuesta del archivo en el response: 
     HttpHeaders.CONTENT_DISPOSITION
     HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS
     "attachment;filename=response.json"
     
Archivo que contiene las propiedades del proyecto: application.properties

Archivo de mensajes para respuestas de errores: messages.properties

NOTA: Al installar el proyecto, debe permitir importar los paquetes para el correcto funcionamiento del API