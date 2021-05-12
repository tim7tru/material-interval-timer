# Material Interval Timer - Timmy Truong

This project acts as a playground for me to explore my interest in Android development. The goal of this project was never to release/publish the application but to demonstrate my ever-evolving capabilities in the competent use of Android libraries/technologies throughout my university career. 

## Libraries

### User Interface
- Material Design Components
- Fragment

### Architecture
- Jetpack Libraries (ViewModel, DataBinding, Navigation/NavigationUI, Room) 
- Kotlin Coroutines
- Dagger Hilt

### Unit Testing
- Turbine
- Mockito-Kotlin
- KoTest


## Architecture

I have a particular interest in Android application architectures evident by the amount of refactoring in this project to update the code architecture. I explored a majority of the **MVx** architectures and consider the current state of this application's architecture to be a mix of MVVM and MVI. 

Initially, this project began as a poorly designed MVVM architecture, utilizing Jetpack libraries such as **ViewModel** and **LiveData**. When I began, I was quite new to the idea of the reactive UI pattern and did not know how to implement it very well. 

Over the course of a couple of months and a very helpful internship, I began to understand the importance of a reactive UI's role in concise development and testing. With my learnings, the project evolved to adapt an MVVM/MVI architecture.

### Screens
One of my biggest gripes with DataBinding and MVVM was the amount of code that needed to be implemented when creating a new Fragment. Every dynamic change received by an observer needed to be bound to the binding in the Fragment file. Instead, I assigned this logic to a data class, called a screen, that holds observable data types. Thus, within a Fragment, I only need to bind the screen once and every change made can be within the ViewModel. 

Although far from perfect, this was a simple solution, that proved to be testable in unit testing, something that couldn't be done in the traditional method.

## Material Design

I initially designed the layouts for this application in **Figma** as an attempt to apply **Material Design** principles. I'm far from a UX/UI designer but I felt that it was important to at least have a simple understanding of where to acquire the knowledge to implement these principles.

This has so far proven useful when discussing designs for new features in past internships.

##  Unit Testing

This is an area where I don't have too much experience but would love to learn more. As of April 2021, I began adding unit tests using **KoTest**, **Mockito-Kotlin**, and **Turbine** to test my view model logic. 
