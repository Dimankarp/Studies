
### Java Programming

#### **ITMO Software Engineering 2 semester - Lab 6 MarineManagerClient of Java Programming course**

The client side of the MarineManager application
- [manager/](./manager/) - The source code of the Client side
- [MarineManagerClient-1.2-Release](./MarineManagerClient-1.2-Release.jar) - FatJar of the Client Application
Dependecy list as a Gradle dependencies:
```
dependencies {
    implementation 'com.sun.xml.bind:jaxb-core:4.0.2'
    implementation 'com.sun.xml.bind:jaxb-ri:4.0.2'
    implementation 'jakarta.xml.bind:jakarta.xml.bind-api:4.0.0'
    implementation files("libs/MarineLibrary-5.2-Release.jar")
}

```
