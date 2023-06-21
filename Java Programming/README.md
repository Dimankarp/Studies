
### Java Programming

#### **ITMO Software Engineering 1 semester - Lab 1 of Java Programming course**

Introducing students to 
the basic syntax of *Java* and primitive programming
concepts.

 - [lab1/](./lab1/)
	- [ReportJavaProgLab1.docx](./lab1/ReportJavaProgLab1.docx) (!In Russian!) - The laboratory work report containing code and task
	 - [LabOne.java](./lab1/LabOne.java) - Java code Lab.1

#### **ITMO Software Engineering 1 semester - Lab 2 of Java Programming Course**

Introducing students to the
*OOP*, working with jars and external libs
with a simple Pokemon autofighting game.

 - [lab2/](./lab2/)
	- [ReportJavaProgLab2.docx](./lab2/ReportJavaProgLab2.docx) (!In Russian!) - The laboratory work report containing code and task
	- [extension/](./lab2/extension/) - the task solution package - Rewritten and new classes
	- [Pokemon.jar](./lab2/Pokemon.jar) - external classes of the game provided with the task
	... - not so important

#### **ITMO Software Engineering 1 semester - Lab 3-4 of Java Programming Course**

Introducing students to advance Java features, different
class types, exceptions, reflection API with a task of creating
a sizable project.

My project is a ship autofighting game with high
ship destructability.

 - [lab3-4/](./lab3-4/)
	- [ReportJavaProgLab3.docx](./lab3-4/ReportJavaProgLab3.docx) (!In Russian!) - The laboratory work report containing code and task
	- [src/](./lab3-4/src/) - The game package

#### **ITMO Software Engineering 2 semester - Lab 5 of Java Programming Course**

Introducing students to *Java Collections* and *Stream API *
along with object *serialization* and storing.

 - [lab5/](./lab5/)
	- [ReportJavaProgLab5.docx](./lab5/ReportJavaProgLab5.docx) (!In Russian!) - The laboratory work report containing code and task
	- [manager/](./lab5/manager/) - The App package
	- [SpaceMarineManager-1.2-Release.jar](./lab5/SpaceMarineManager-1.2-Release.jar) - FatJar with the App
	- [marines.xml](./lab5/marines.xml) - Serialized array of object stored as an xml file
	- [script.txt](./lab5/script.txt) - Basic script example that can be executed in the app

Dependency list as a Gradle dependencies:
```
dependencies {
    implementation 'com.sun.xml.bind:jaxb-core:4.0.2'
    implementation 'com.sun.xml.bind:jaxb-ri:4.0.2'
    implementation 'jakarta.xml.bind:jakarta.xml.bind-api:4.0.0'
}

```

#### **ITMO Software Engineering 2 semester - Lab 6 of Java Programming Course**

Introducing students to *Sockets and Datagrams, TCP and UDP protocols*
along with Client-Server application building.

 - [lab6/](./lab6/)
	- [MarineLibrary/](./lab6/MarineLibrary) - Java Library that Server and Client depend on
	- [MarineManagerClient/](./lab6/MarineManagerClient/) - The Client side of the Application
	- [MarineManagerServer](./lab6/MarineManagerServer) - The Server side of the Application
	- [marines.xml](./lab6/marines.xml) - Serialized array of object stored as an xml file
	- [script.txt](./lab6/script.txt) - Basic script example that can be executed in the app

#### **ITMO Software Engineering 2 semester - Lab 7 of Java Programming Course**

Introducing students to *Realtional DB (PostgreSQL) and Multithreading in Java*.

 - [lab7/](./lab7/)
	- [MarineLibrary/](./lab7/MarineLibrary) - Java Library that Server and Client depend on
	- [MarineManagerClient/](./lab7/MarineManagerClient/) - The Client side of the Application
	- [MarineManagerServer](./lab7/MarineManagerServer) - The Server side of the Application
		
