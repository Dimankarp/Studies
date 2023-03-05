
### Java Programming

#### **ITMO Software Engineering 2 semester - Lab 6 MarineManagerServer of Java Programming course**

The server side of the MarineManager application
	- [manager/](./manager/) - The source code of the Serverside
	 - [MarineManagerServer-1.2-Release](./MarineManagerServer-1.2-Release.jar) - FatJar of the Server Application
Dependecy list as a Gradle dependencies:
```
dependencies {
    implementation 'com.sun.xml.bind:jaxb-core:4.0.2'
    implementation 'com.sun.xml.bind:jaxb-ri:4.0.2'
    implementation 'jakarta.xml.bind:jakarta.xml.bind-api:4.0.0'
    implementation 'com.tersesystems.logback:logback-core:1.1.1'
    implementation 'org.slf4j:slf4j-api:2.0.5'
    implementation files("libs/MarineLibrary-5.2-Release.jar")
    implementation 'ch.qos.logback:logback-classic:1.4.5'
}

```
