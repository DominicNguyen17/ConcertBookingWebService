Project - A Concert Booking Service
==========

The aim of this project is to build a Web service for concert booking. 

System description
----------
A booking service is required for a small venue, with a capacity of 120 seats. The venue's seats are classified into three price bands, Silver, Gold and Platinum. A concert may run on multiple dates, and each concert has at least one performer. Additionally, a performer may feature in multiple concerts.

The service is to allow clients to retrieve information about concerts and performers, to make and enquire about seats and reservations, and to authenticate users.

When making a reservation, clients may enquire about available seats. They should be informed of any unbooked seats, along with their pricing. Clients may select any number of seats to book. When making a reservation request, double-bookings are not permitted. Only one client should be allowed to book a given seat for a given concert on a given date. Clients may not make partial bookings - either *all* of a client's selected seats are booked, or *none* of them are.

Clients may view concert and performer information, and enquire about available seating, whether or not they are authenticated. When authenticated, clients gain the ability to make reservations, and to view their own reservations. Clients should never be able to view the reservation information for other guests.

In addition, clients should be able to subscribe to information regarding concert bookings. Specifically, they should be able to subscribe to "watch" one or more concerts / dates, and be notified when those concerts / dates are about to sell out.

A particular quality attribute that the service must satisfy is scalability. It is expected that the service will experience high load when concert tickets go on sale. 


Tasks
----------
#### 1) Construct a domain model
Develop an object-oriented domain model for the concert service. The model should include appropriate classes and relationships that allow concerts, performers, reservations and users to be represented. The classes should be suitably annotated with JPA annotations to define their mapping to a relational schema.

#### 2) Develop a RESTful Web service
Develop a JAX-RS Web service that exposes the required functionality. In developing the service, you should define an appropriate REST interface in terms of resource URIs and HTTP methods.

#### 3) Document some design choices and the organisation of your team
Create a document named `Organisation.md` containing the following details:

- Summary of what each team member did and how the team was organised.
For example, the team may discuss the domain model together but only
one person implements. This will mean we might only see commits from that
person, and wonder if the other members were involved. A short explanation
in `Organisation.md` will help this. It could be a couple of sentences for each member or a simple *All members participated about the same for everything* (in which case the logs should reflect this).
- Short description of the strategy used to minimise the chance of
concurrency errors in program execution (2-3 sentences)
- Short description of how the domain model is organised (2-3 sentences)

Points to consider
----------

To check your understanding of the various concepts used in this project, you should consider the following:

1. How have you made an effort to improve the *scalability* of your web service?

2. What (implicit and explicit) uses of *lazy loading* and *eager fetching* are used within your web service. Why those uses are appropriate in the context of this web service? 

3. How have you made an effort to remove the possibility of issues arising from concurrent access, such as double-bookings?

4. How would you extend your web service to add support for the following new features?:

   - Support for different ticket prices per concert (currently ticket prices are identical for each concert)

   - Support for multiple venues (currently all concerts are assumed to be at the same venue with an identical seat layout)
   
   - Support for "held" seats (where, after a user selects their seats, they are reserved for a period of time to allow the user time to pay. If the user cancels payment, or the time period elapses, the seats are automatically released, able to be booked again by other users).

**Important Note:** Recording the above information as well as your overall project development experience will help in completing the Assignment 1 tasks too. Assignment 1 will be released in the second half of the course.

Resources
----------
A multi-module Maven project, `project-concert`, is supplied and comprises 3 modules: 

- `concert-client`. A module that contains resources for building a concert client application.

- `concert-common`. This module defines several entities that are shared by the `client` and `service` modules.

- `concert-service`. The `service` module is responsible for implementing the Web service and persistence aspects of the concert application.

The parent project's POM includes common dependencies and properties that are inherited by the 3 modules.

#### System architecture
The system as a whole is a *tiered* architecture, in which a browser application, driven by a *web app*, communicates via HTTP with the *web service* you will create for this project. A high-level overview can be seen in the below diagram:

![](./spec/system-architecture.png)

#### Client module
The client module contains a *web application*, built using technologies such as HTML5, CSS, JavaScript, JSP, and JSTL. This web application is capable of acting as a *client* to the web service which you will create for this project, as shown in the diagram above. The client contains both browser-based and server-based code (written in JavaScript and Java, respectively) which communicate with the `service` layer over HTTP.

The client is intended to be a demonstration of the kind of web application that can be powered by the web service you will create. You will not need to modify the application, other than potentially altering the value of `Config.WEB_SERVICE_URI`, but the application can be used as part of your testing.

#### Common module
This module includes a number of packages:

- `proj.concert.common.dto`. This package contains complete implementations for 7 DTO classes. The `client` webapp above, and the integration tests (see below), are defined in terms of the DTO classes.
	
- `proj.concert.common.jackson`. This package includes the Jackson serializer and deserializer implementations for Java's `LocalDateTime` class.

- `proj.concert.common.types`. This package defines basic data types that are common to the `client` and `service` modules. The types comprise 2 enumerations: `Genre` (for performers), and `BookingStatus` (for querying available / unavailable seats).

#### Service module
The `service` module will contain your completed Web service for the project. It contains the following packages:

- `proj.concert.service.domain`. This package will contain the web service's completed domain model. Currently it includes two **incomplete** classes - `Concert` and `Seat`. These two classes must be completed, and any other domain classes you deem necessary should be added.

- `proj.concert.service.jaxrs`. This package contains the `LocalDateTimeParam` class. This class allows us to receive `LocalDateTime` instances as *query* or *path* parameters, which may prove useful when implementing your web service. For a detailed description, be sure to read the class's comments.

- `proj.concert.service.mapper`. This package is intended to contain mapper classes, which map between your domain and DTO classes.

- `proj.concert.service.services`. This package contains the `ConcertResource` class, which you must implement for this project. It also contains a `PersistenceManager` class which can be used to obtain `EntityManager` instances when required, a complete `ConcertApplication` class, and a `TestResource` class which implements a web service endpoint to reset the database. This is used for testing purposes.

- `proj.concert.service.util`. This package contains the `ConcertUtils` class, which can re-initialize the database, and the `TheatreLayout` class, which can be used to generate `Seat`s for a particular concert date.

Beyond packages and source files, the `service` module contains the JPA `persistence.xml` file, a database initialise script (`db-init.sql`) and a `log4j.properties` file (in `src/main/resources`). Finally, integration tests are included in the `src/test/java` folder. As usual, these tests may be run by executing Maven's `verify` goal on the parent project, and a 100% pass-rate (along with correct client functionality) is a good indication that your service implementation is correct.

The `persistence.xml` file includes a `javax.persistence.sql-load-script-source` element whose value is the supplied`db-init.sql` script. The effect of this element is to run the script when the `EntityManagerFactory` is created.`db-init.sql` file populates the database with concert and performer data.


Appendix: The client webapp.
----------
The `concert-client` project contains a complete web application which is designed to communicate with your web service from both its server-side (Java) and client-side (JavaScript) code, to deliver its functionality.

#### App usage
Using the application, browsing to `/Concerts` or simply `/` (within the webapp's *context*) will present the "homepage" of the app, allowing the user to view concerts. On this page, users may scroll through the list of concerts on the left side of the page. Clicking on a concert will cause that concert's info to be displayed in the detail view.

![](./spec/concerts-page.PNG)

On the detail view, users can see more information about the currently selected concert, as well as a list of performers and dates for that concert.

Clicking on a performer's image will display a modal dialog containing more information about that performer:

![](./spec/view-performer.PNG)

If the user hasn't logged in, then clicking on the "Book!" button next to one of a concert's dates will prompt the user to sign in:

![](./spec/please-login.PNG)

If the user has already signed in, they will be redirected to the seat selection page for their selected concert and date. See `concert-service/src/main/resources/db-init.sql` for the data that the server is initialised with, including possible users and their passwords.

![](./spec/booking-page.PNG)

Here, users can see a visual seat map of the venue, along with an indicator of which seats have already been booked. Clicking unbooked seats will toggle a blue halo around that seat - an indicator that the seat has been selected for booking, as can be seen in the above screenshot. A list of selected seats - along with the total ticket price - can be seen below the seat map.

Once the user has made their selection, they can click the "Book!" button. Assuming they have "sufficient funds" (which will always be true for this test case), and that no other user has booked the same seats in the meantime, the user will be presented with a success dialog box:

![](./spec/booking-success.PNG)

Dismissing the dialog will redirect the user back to the concerts page, where they may continue to book tickets at other concerts / dates. (or more tickets for the same concert / date).

#### Limitations
The webapp currently *partially* exercises the web service you are required to create for this project. Functionality for a user to view their bookings is not implemented, though this is required by the web service specification. Additionally, the webapp does not participate in the *publish / subscribe* functionality required of the service.

#### Running the complete system
The only way to run the complete system is to package both the `client` and `service` projects into `WAR` files, and deploy them to a running servlet container such as Tomcat.

Fortunately, running Maven's `package` goal on the parent project will build both WARs as required. However, we will still need to set up a Tomcat (or similar) instance to run the application.

Whichever IDE you use, the first step will be to download a Tomcat installation, if you don't already have one. You can find them [here](https://tomcat.apache.org/download-80.cgi). Get version `8.5.x` as an archive (the installer isn't required), download and unzip it somewhere on your machine, and then follow the steps below according to your IDE.

**Warning** There seem to be a number of variations in IDE setup and installation, so what's below may not work for your particular setup. You may need to explore google to find workable alternatives. [Tomcat-CL](Tomcat-CL.md) has a simplified attempt for doing it from the command line.


#### From IntelliJ
**Note: This ONLY works in IntelliJ Ultimate edition.** Community edition does not support this. Having said that, Ultimate edition is available for free for educational use. Simply sign up for a new account using your University email address.

1. Within your project, open the *Run Configurations* dialog (usually available in the top-right corner, or open the search box by tapping double-shift, then type "run configurations"). Either way, pick *Edit Configurations*.

2. Click on the **+** button on the top-left of the dialog, then choose *Tomcat server* &rarr; *Local*.

3. Name the configuration whatever you like. For the *HTTP Port*, pick one which is available on your machine, such as *10000*.

4. For the *application server*, pick one from the list. If none are available, click *configure*. You'll be able to add one (by browsing to the location where you downloaded Tomcat earlier).

5. Under the *Deployment* tab, select both WAR artifacts (`client` and `service`) for deployment - these would have been created by IntelliJ's Maven integration (you shouldn't select the *exploded* versions, but it probably doesn't matter). For the application context, set the `service` WAR's context to `/webservice`, and the `client` WAR's context to `/concert-webapp`.

6. Go back to the *Server* tab. Edit the *URL* to be `http://localhost:10000/concert-webapp/` (altering the port as necessary). Then, click `OK`.

7. In the `client` project, open `src/main/java/.../Config.java`, and edit the `WEB_SERVICE_URI` field as appropriate (assuming port `10000`, and the contexts identified in step 5 above, you won't need to change this).

8. In the `client` project, open `src/main/webapp/js/fetch-api.js` and edit `WEB_URI` as appropriate (assuming the contexts identified in step 5 above, you won't need to change this).

9. From the *run configurations* dropdown, slect your new configuration and click the *play* button. The webapp and web service should start, and the browser should eventually open at the URL identified in step 6 above. If it doesn't, you can browse there manually.

10. When you're done, hit the *stop* button. Sometimes you may need to click it twice (do so until the stop button is greyed out, and you see a *Disconnected from server* message in the server output).
