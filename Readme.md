# MetricsAgent

## Overview
MetricsAgent is a Java-based project designed to monitor and analyze application metrics. This agent leverages the Javassist library to perform bytecode manipulation, allowing users to instrument applications dynamically.



## Dependencies
The project relies on the following dependency:

```xml
<dependency>
    <groupId>org.javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.29.2-GA</version>
</dependency>
```



## How to Build
To build the project, ensure Maven is installed and run the following command:
```bash
mvn clean package
```
This will generate a shaded JAR file in the `target/` directory.

## Requirements
- **Java Version**: 22 or higher
- **Maven Version**: 3.6.0 or higher

## Usage

### take argument of package to be tracker


## static loading
```bash
java -javaagent:Agent.jar=org.va,org.report-mgmt -jar Service-1.0-SNAPSHOT.jar
```

## dynamic loading
 Refer Code: src/test/java/DynamicAgentTest.java
```java
 public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException {
    // your pid of remote jvm application
    String pid = "2841";
    URL agentUrl = DynamicAgentTest.class.getClassLoader()
            .getResource("MetricsAgent-1.0-SNAPSHOT.jar");
    VirtualMachine vm = VirtualMachine.attach(pid);
    try {
        vm.loadAgent( agentUrl.getPath(),"org.va");
    } catch (AgentInitializationException e) {
        System.err.println("Agent initialization failed: " + e.getMessage());
        e.printStackTrace();
    }
    vm.detach();

}
```


