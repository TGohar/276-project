# Trackour

Trackour is a web application that assists both beginner and experienced music creators with planning their mix or mashup projects. Users will be able to search for music through Trackour using harmonic details of the songs, such as key, tempo, time signature, etcetera. The search function will give users the option to narrow their search down to harmonically compatible songs, which will be determined using a system based on the Camelot Wheel. Additionally, Trackour will provide collaborative functionality, allowing users to plan, save, and share their mix projects with other users.

## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Deploy using Heroku

login to Heroku with

```
heroku login
```

you can create a new Heroku app simply with

```
heroku create herokuappname
```

To build the jar file, run

```
mvn clean package -Pproduction
```

Once it's built, deploy the jar to Heroku with
```
heroku deploy:jar target\trackour-1.0-SNAPSHOT.jar --app herokuappname
```


## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `security` folder in `src/main/java/` contains the Spring Security logic for authentication and authorization.
- `models` folder in `src/main/java/` contains the entities and their respective services.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links from Vaadin

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorial at [vaadin.com/docs/latest/tutorial/overview](https://vaadin.com/docs/latest/tutorial/overview).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/docs/latest/components](https://vaadin.com/docs/latest/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes). 
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).

