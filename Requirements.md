# Requirements
## UI requirements
- [ ] Textual UI
- [ ] Running in parallel

## Architecture Requirements
- [ ] Strict layering
- [ ] MVC Architecture
- [ ] Test coverage
- [ ] Components 
- [ ] Flexible exchange of these (DI)

# Task 1: Create a game project
- [ ] Install Scala
- [ ] Install IDEA with Scala Plugin
- [ ] Create a Scala project in IDEA (with sbt as build system)
- [ ] Create an Object that prints out a string representation of your game

# Task 2a: Write Tests
Take the Classes from your domain model and start developing them in Scala
- [ ] Use Behaviour Driven Development
- [ ] Use ScalaTest
- [ ] Write a Spec using WordSpec 
- [ ] Use the IDEA ScalaTest Runner 
- [ ] Achieve 100% code coverage. And maintain it!

# Task 2b: Set up a Continuous Development Process
- [ ] Make your development independant of the local platform using a build mechanism like sbt
- [ ] Initiate a Continuous Integration Server on Travis
- [ ] Set up tests and code coverage using Coveralls
- [ ] Integrate Badges into your git repository

# Task 3: Monads
- [ ] Avoid the use of null and Null. Use Option instead. 
- [ ] Also avoid using try-catch, use the Try-Monad instead.
- [ ] Use the For-Comprehension to un-pack monads

# Task 4: Write an internal DSL
Create an API that can be read and written by an expert of the domain but not of programming. 
You can do that for the game or some other topic. 
For the game you could write a DSL as a text notation for a set of moves or an entire game, like the chess notation.

# Task 5: Actors
Use Actors to implement and solve a concurrency problem.
This could be within your game, like a parallel search for a solution. 
Or you can create a new project just focused on Actors.
